/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011-2018 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.keywordsearch;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.concurrent.GuardedBy;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.coreutils.Version;
import org.sleuthkit.autopsy.keywordsearch.KeywordQueryFilter.FilterType;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Highlights hits for a given document. Knows about pages and such for the
 * content viewer.
 */
class HighlightedText implements IndexedText {

    private static final Logger logger = Logger.getLogger(HighlightedText.class.getName());

    private static final boolean DEBUG = (Version.getBuildType() == Version.Type.DEVELOPMENT);

    private static final BlackboardAttribute.Type TSK_KEYWORD_SEARCH_TYPE = new BlackboardAttribute.Type(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD_SEARCH_TYPE);
    private static final BlackboardAttribute.Type TSK_KEYWORD = new BlackboardAttribute.Type(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD);
    static private final BlackboardAttribute.Type TSK_ASSOCIATED_ARTIFACT = new BlackboardAttribute.Type(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_ASSOCIATED_ARTIFACT);
    static private final BlackboardAttribute.Type TSK_KEYWORD_REGEXP = new BlackboardAttribute.Type(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD_REGEXP);

    private static final String HIGHLIGHT_PRE = "<span style='background:yellow'>"; //NON-NLS
    private static final String HIGHLIGHT_POST = "</span>"; //NON-NLS
    private static final String ANCHOR_PREFIX = HighlightedText.class.getName() + "_"; //NON-NLS

    final private Server solrServer = KeywordSearch.getServer();

    private final long solrObjectId;
    /*
     * The keywords to highlight
     */
    private final Set<String> keywords = new HashSet<>();

    private int numberPages;
    private Integer currentPage = 0;

    @GuardedBy("this")
    private boolean isPageInfoLoaded = false;

    /*
     * map from page/chunk to number of hits. value is 0 if not yet known.
     */
    private final TreeMap<Integer, Integer> numberOfHitsPerPage = new TreeMap<>();
    /*
     * set of pages, used for iterating back and forth. Only stores pages with
     * hits
     */
    private final Set<Integer> pages = numberOfHitsPerPage.keySet();
    /*
     * map from page/chunk number to current hit on that page.
     */
    private final HashMap<Integer, Integer> currentHitPerPage = new HashMap<>();

    private QueryResults hits = null; //original hits that may get passed in
    private BlackboardArtifact artifact;
    private KeywordSearch.QueryType qt;
    private boolean isLiteral;

    /**
     * This constructor is used when keyword hits are accessed from the ad-hoc
     * search results. In that case we have the entire QueryResults object and
     * need to arrange the paging.
     *
     * @param solrObjectId The solrObjectId of the content whose text will be
     *                     highlighted.
     * @param QueryResults The QueryResults for the ad-hoc search from whose
     *                     results a selection was made leading to this
     *                     HighlightedText.
     */
    HighlightedText(long solrObjectId, QueryResults hits) {
        this.solrObjectId = solrObjectId;
        this.hits = hits;
    }

    /**
     * This constructor is used when keyword hits are accessed from the "Keyword
     * Hits" node in the directory tree in Autopsy. In that case we have the
     * keyword hit artifact which has the chunks (as
     * TSK_KEYWORD_HIT_DOCUMENT_IDS attribute) to use to work out the paging.
     *
     * @param artifact The artifact that was selected.
     */
    HighlightedText(BlackboardArtifact artifact) throws TskCoreException {
        this.artifact = artifact;
        BlackboardAttribute attribute = artifact.getAttribute(TSK_ASSOCIATED_ARTIFACT);
        if (attribute != null) {
            this.solrObjectId = attribute.getValueLong();
        } else {
            this.solrObjectId = artifact.getObjectID();
        }

    }

    /**
     * This method figures out which pages / chunks have hits. Invoking it a
     * second time has no effect.
     */
    synchronized private void loadPageInfo() throws TskCoreException, KeywordSearchModuleException, NoOpenCoreException {
        if (isPageInfoLoaded) {
            return;
        }

        this.numberPages = solrServer.queryNumFileChunks(this.solrObjectId);

        if (artifact != null) {
            loadPageInfoFromArtifact();
        } else if (numberPages != 0) {
            // if the file has chunks, get pages with hits, sorted
            loadPageInfoFromHits();
        } else {
            //non-artifact, no chunks, everything is easy.
            this.numberPages = 1;
            this.currentPage = 1;
            numberOfHitsPerPage.put(1, 0);
            currentHitPerPage.put(1, 0);
            isPageInfoLoaded = true;
        }
    }

    /**
     * Figure out the paging info from the artifact that was used to create this
     * HighlightedText
     *
     * @throws TskCoreException
     */
    synchronized private void loadPageInfoFromArtifact() throws TskCoreException, KeywordSearchModuleException, NoOpenCoreException {
        final String keyword = artifact.getAttribute(TSK_KEYWORD).getValueString();
        this.keywords.add(keyword);

        //get the QueryType (if available)
        final BlackboardAttribute queryTypeAttribute = artifact.getAttribute(TSK_KEYWORD_SEARCH_TYPE);
        qt = (queryTypeAttribute != null)
                ? KeywordSearch.QueryType.values()[queryTypeAttribute.getValueInt()] : null;

        Keyword keywordQuery = null;
        switch (qt) {
            case LITERAL:
            case SUBSTRING:
                keywordQuery = new Keyword(keyword, true, true);
                break;
            case REGEX:
                String regexp = artifact.getAttribute(TSK_KEYWORD_REGEXP).getValueString();
                keywordQuery = new Keyword(regexp, false, false);
                break;
        }
        KeywordSearchQuery chunksQuery = KeywordSearchUtil.getQueryForKeyword(keywordQuery, new KeywordList(Arrays.asList(keywordQuery)));
        // Run a query to figure out which chunks for the current object have
        // hits for this keyword.

        chunksQuery.addFilter(new KeywordQueryFilter(FilterType.CHUNK, this.solrObjectId));

        hits = chunksQuery.performQuery();
        loadPageInfoFromHits();
    }

    /**
     * Load the paging info from the QueryResults object.
     */
    synchronized private void loadPageInfoFromHits() {
        isLiteral = hits.getQuery().isLiteral();

        /**
         * Organize the hits by page, filter as needed. We process *every*
         * keyword here because in the case of a regular expression search there
         * may be multiple different keyword hits located in different chunks
         * for the same file/artifact.
         */
        for (Keyword k : hits.getKeywords()) {
            for (KeywordHit hit : hits.getResults(k)) {
                int chunkID = hit.getChunkId();
                if (artifact != null) {
                    if (chunkID != 0 && this.solrObjectId == hit.getSolrObjectId()) {
                        String hit1 = hit.getHit();
                        if (keywords.stream().anyMatch(hit1::contains)) {
                            numberOfHitsPerPage.put(chunkID, 0); //unknown number of matches in the page
                            currentHitPerPage.put(chunkID, 0); //set current hit to 0th

                        }
                    }
                } else {
                    if (chunkID != 0 && this.solrObjectId == hit.getSolrObjectId()) {

                        numberOfHitsPerPage.put(chunkID, 0); //unknown number of matches in the page
                        currentHitPerPage.put(chunkID, 0); //set current hit to 0th

                        if (StringUtils.isNotBlank(hit.getHit())) {
                            this.keywords.add(hit.getHit());
                        }
                    }
                }
            }
        }

        //set page to first page having highlights
        this.currentPage = pages.stream().findFirst().orElse(1);

        isPageInfoLoaded = true;
    }

    /**
     * Constructs a complete, escaped Solr query that is ready to be used.
     *
     * @param query         keyword term to be searched for
     * @param fields field to query
     *
     * @return Solr query string
     */
    static private String constructEscapedSolrQuery(String query, List<Server.Schema> fields) {
        return fields.stream().map(f ->
            f.toString() + ":" + "\"" + KeywordSearchUtil.escapeLuceneQuery(query) + "\""
        ).collect(Collectors.joining(" OR "));
    }

    private int getIndexOfCurrentPage() {
        return Iterators.indexOf(pages.iterator(), this.currentPage::equals);
    }

    @Override
    public int getNumberPages() {
        //return number of pages that have hits
        return this.numberPages;
    }

    @Override
    public int getCurrentPage() {
        return this.currentPage;
    }

    @Override
    public boolean hasNextPage() {
        return getIndexOfCurrentPage() < pages.size() - 1;
    }

    @Override
    public boolean hasPreviousPage() {
        return getIndexOfCurrentPage() > 0;
    }

    @Override
    public int nextPage() {
        if (hasNextPage()) {
            currentPage = Iterators.get(pages.iterator(), getIndexOfCurrentPage() + 1);
            return currentPage;
        } else {
            throw new IllegalStateException("No next page.");
        }
    }

    @Override
    public int previousPage() {
        if (hasPreviousPage()) {
            currentPage = Iterators.get(pages.iterator(), getIndexOfCurrentPage() - 1);
            return currentPage;
        } else {
            throw new IllegalStateException("No previous page.");
        }
    }

    @Override
    public boolean hasNextItem() {
        if (!this.currentHitPerPage.containsKey(currentPage)) {
            return false;
        }
        return this.currentHitPerPage.get(currentPage) < this.numberOfHitsPerPage.get(currentPage);
    }

    @Override
    public boolean hasPreviousItem() {
        if (!this.currentHitPerPage.containsKey(currentPage)) {
            return false;
        }
        return this.currentHitPerPage.get(currentPage) > 1;
    }

    @Override
    public int nextItem() {
        if (!hasNextItem()) {
            throw new IllegalStateException("No next item.");
        }
        int cur = currentHitPerPage.get(currentPage) + 1;
        currentHitPerPage.put(currentPage, cur);
        return cur;
    }

    @Override
    public int previousItem() {
        if (!hasPreviousItem()) {
            throw new IllegalStateException("No previous item.");
        }
        int cur = currentHitPerPage.get(currentPage) - 1;
        currentHitPerPage.put(currentPage, cur);
        return cur;
    }

    @Override
    public int currentItem() {
        if (!this.currentHitPerPage.containsKey(currentPage)) {
            return 0;
        }
        return currentHitPerPage.get(currentPage);
    }

    @Override
    public String getText() {
        String chunkID = "";
        try {
            loadPageInfo(); //inits once
            SolrQuery q = new SolrQuery();
            q.setShowDebugInfo(DEBUG); //debug

            String contentIdStr = Long.toString(this.solrObjectId);
            if (numberPages != 0) {
                chunkID = Integer.toString(this.currentPage);
                contentIdStr += "0".equals(chunkID) ? "" : "_" + chunkID;
            }
            final String filterQuery = Server.Schema.ID.toString() + ":" + KeywordSearchUtil.escapeLuceneQuery(contentIdStr);

            if (isLiteral) {
                //if the query is literal try to get solr to do the highlighting
                String highlightQuery = keywords.stream()
                    .map(s -> constructEscapedSolrQuery(s, LuceneQuery.CONTENT_FIELDS))
                    .collect(Collectors.joining(" "));
                q.setQuery(highlightQuery);
                for (Server.Schema field : LuceneQuery.CONTENT_FIELDS) {
                    q.addField(field.toString());
                }
                q.addFilterQuery(filterQuery);
                for (Server.Schema field : LuceneQuery.CONTENT_FIELDS) {
                    q.addHighlightField(field.toString());
                }

                q.setHighlightFragsize(0); // don't fragment the highlight, works with original highlighter, or needs "single" list builder with FVH

                //tune the highlighter
                q.setParam("hl.useFastVectorHighlighter", "on"); //fast highlighter scales better than standard one NON-NLS
                q.setParam("hl.tag.pre", HIGHLIGHT_PRE); //makes sense for FastVectorHighlighter only NON-NLS
                q.setParam("hl.tag.post", HIGHLIGHT_POST); //makes sense for FastVectorHighlighter only NON-NLS
                q.setParam("hl.fragListBuilder", "single"); //makes sense for FastVectorHighlighter only NON-NLS

                //docs says makes sense for the original Highlighter only, but not really
                q.setParam("hl.maxAnalyzedChars", Server.HL_ANALYZE_CHARS_UNLIMITED); //NON-NLS
            } else {
                /*
                 * if the query is not literal just pull back the text. We will
                 * do the highlighting in autopsy.
                 */
                q.setQuery(filterQuery);
                for (Server.Schema field : LuceneQuery.CONTENT_FIELDS) {
                    q.addField(field.toString());
                }
            }

            QueryResponse response = solrServer.query(q, METHOD.POST);

            // There should never be more than one document since there will 
            // either be a single chunk containing hits or we narrow our
            // query down to the current page/chunk.
            if (response.getResults().size() > 1) {
                logger.log(Level.WARNING, "Unexpected number of results for Solr highlighting query: {0}", q); //NON-NLS
            }
            String highlightedContent;
            Map<String, Map<String, List<String>>> responseHighlight = response.getHighlighting();

            // even if not found, we need field name here. so use CONTENT_GENERAL as a default value.
            // attemptManualHighlighting will use empty string for its field value.
            String highlightField = getContentFieldName(response.getResults().get(0)).map(Server.Schema::toString)
                .orElse(Server.Schema.CONTENT_GENERAL.toString());

            if (responseHighlight == null) {
                highlightedContent = HighlightedTextUtil.attemptManualHighlighting(response.getResults(), highlightField, keywords);
            } else {
                Map<String, List<String>> responseHighlightID = responseHighlight.get(contentIdStr);

                if (responseHighlightID == null) {
                    highlightedContent = HighlightedTextUtil.attemptManualHighlighting(response.getResults(), highlightField, keywords);
                } else {
                    List<String> contentHighlights = getHighlightResult(responseHighlightID);
                    if (contentHighlights == null) {
                        highlightedContent = HighlightedTextUtil.attemptManualHighlighting(response.getResults(), highlightField, keywords);
                    } else {
                        // extracted content (minus highlight tags) is HTML-escaped
                        highlightedContent = contentHighlights.get(0).trim();
                    }
                }
            }
            highlightedContent = insertAnchors(highlightedContent);

            return "<html><pre>" + highlightedContent + "</pre></html>"; //NON-NLS
        } catch (TskCoreException | KeywordSearchModuleException | NoOpenCoreException ex) {
            logger.log(Level.SEVERE, "Error getting highlighted text for Solr doc id " + solrObjectId + ", chunkID " + chunkID, ex); //NON-NLS
            return Bundle.IndexedText_errorMessage_errorGettingText();
        }
    }

    @Override
    public String toString() {
        return NbBundle.getMessage(this.getClass(), "HighlightedMatchesSource.toString");
    }

    @Override
    public boolean isSearchable() {
        return true;
    }

    @Override
    public String getAnchorPrefix() {
        return ANCHOR_PREFIX;
    }

    @Override
    public int getNumberHits() {
        if (!this.numberOfHitsPerPage.containsKey(this.currentPage)) {
            return 0;
        }
        return this.numberOfHitsPerPage.get(this.currentPage);

    }

    private static Optional<Server.Schema> getContentFieldName(SolrDocument document) {
        for (Server.Schema field : LuceneQuery.CONTENT_FIELDS) {
            if (document.containsKey(field.toString())) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    private static List<String> getHighlightResult(Map<String, List<String>> highlight) {
        for (Server.Schema field : LuceneQuery.CONTENT_FIELDS) {
            if (highlight.containsKey(field.toString())) {
                return highlight.get(field.toString());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Anchors are used to navigate back and forth between hits on the same page
     * and to navigate to hits on the next/previous page.
     *
     * @param searchableContent
     *
     * @return
     */
    private String insertAnchors(String searchableContent) {
        StringBuilder buf = new StringBuilder(searchableContent);
        final String searchToken = HIGHLIGHT_PRE;
        final int indexSearchTokLen = searchToken.length();
        final String insertPre = "<a name='" + ANCHOR_PREFIX; //NON-NLS
        final String insertPost = "'></a>"; //NON-NLS
        int count = 0;
        int searchOffset = 0;
        int index = buf.indexOf(searchToken, searchOffset);
        while (index >= 0) {
            String insertString = insertPre + Integer.toString(count + 1) + insertPost;
            int insertStringLen = insertString.length();
            buf.insert(index, insertString);
            searchOffset = index + indexSearchTokLen + insertStringLen; //next offset past this anchor
            ++count;
            index = buf.indexOf(searchToken, searchOffset);
        }

        //store total hits for this page, now that we know it
        this.numberOfHitsPerPage.put(this.currentPage, count);
        if (this.currentItem() == 0 && this.hasNextItem()) {
            this.nextItem();
        }

        return buf.toString();
    }

}

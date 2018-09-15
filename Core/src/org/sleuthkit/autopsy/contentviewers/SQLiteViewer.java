/*
 * Autopsy Forensic Browser
 *
 * Copyright 2018 Basis Technology Corp.
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
package org.sleuthkit.autopsy.contentviewers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.autopsy.coreutils.MessageNotifyUtil;
import org.sleuthkit.autopsy.tabulardatareader.AbstractReader;
import org.sleuthkit.autopsy.tabulardatareader.AbstractReader.FileReaderException;
import org.sleuthkit.autopsy.tabulardatareader.AbstractReader.FileReaderInitException;
import org.sleuthkit.autopsy.tabulardatareader.FileReaderFactory;

/**
 * A file content viewer for SQLite database files.
 */
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
class SQLiteViewer extends javax.swing.JPanel implements FileTypeViewer {

    private static final long serialVersionUID = 1L;
    public static final String[] SUPPORTED_MIMETYPES = new String[]{"application/x-sqlite3"};
    private static final int ROWS_PER_PAGE = 100;
    private static final Logger logger = Logger.getLogger(FileViewer.class.getName());
    private final SQLiteTableView selectedTableView = new SQLiteTableView();
    private AbstractFile sqliteDbFile;
    private File tmpDbFile;
    private AbstractReader sqliteReader;
    private int numRows;    // num of rows in the selected table
    private int currPage = 0; // curr page of rows being displayed

    /**
     * Constructs a file content viewer for SQLite database files.
     */
    public SQLiteViewer() {
        initComponents();
        jTableDataPanel.add(selectedTableView, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jHdrPanel = new javax.swing.JPanel();
        tablesDropdownList = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        numEntriesField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        currPageLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        numPagesLabel = new javax.swing.JLabel();
        prevPageButton = new javax.swing.JButton();
        nextPageButton = new javax.swing.JButton();
        exportCsvButton = new javax.swing.JButton();
        jTableDataPanel = new javax.swing.JPanel();

        jHdrPanel.setPreferredSize(new java.awt.Dimension(536, 40));

        tablesDropdownList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        tablesDropdownList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tablesDropdownListActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.jLabel1.text")); // NOI18N

        numEntriesField.setEditable(false);
        numEntriesField.setText(org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.numEntriesField.text")); // NOI18N
        numEntriesField.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(currPageLabel, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.currPageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(numPagesLabel, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.numPagesLabel.text")); // NOI18N

        prevPageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(prevPageButton, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.prevPageButton.text")); // NOI18N
        prevPageButton.setBorderPainted(false);
        prevPageButton.setContentAreaFilled(false);
        prevPageButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back_disabled.png"))); // NOI18N
        prevPageButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        prevPageButton.setPreferredSize(new java.awt.Dimension(23, 23));
        prevPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevPageButtonActionPerformed(evt);
            }
        });

        nextPageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(nextPageButton, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.nextPageButton.text")); // NOI18N
        nextPageButton.setBorderPainted(false);
        nextPageButton.setContentAreaFilled(false);
        nextPageButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward_disabled.png"))); // NOI18N
        nextPageButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        nextPageButton.setPreferredSize(new java.awt.Dimension(23, 23));
        nextPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPageButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(exportCsvButton, org.openide.util.NbBundle.getMessage(SQLiteViewer.class, "SQLiteViewer.exportCsvButton.text")); // NOI18N
        exportCsvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportCsvButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jHdrPanelLayout = new javax.swing.GroupLayout(jHdrPanel);
        jHdrPanel.setLayout(jHdrPanelLayout);
        jHdrPanelLayout.setHorizontalGroup(
            jHdrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jHdrPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablesDropdownList, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(numEntriesField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currPageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numPagesLabel)
                .addGap(18, 18, 18)
                .addComponent(prevPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(nextPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(exportCsvButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jHdrPanelLayout.setVerticalGroup(
            jHdrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jHdrPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jHdrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportCsvButton)
                    .addComponent(nextPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prevPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jHdrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tablesDropdownList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(numEntriesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(currPageLabel)
                        .addComponent(jLabel3)
                        .addComponent(numPagesLabel)))
                .addContainerGap())
        );

        jTableDataPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jHdrPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
            .addComponent(jTableDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jHdrPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTableDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nextPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPageButtonActionPerformed
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        currPage++;
        if (currPage * ROWS_PER_PAGE > numRows) {
            nextPageButton.setEnabled(false);
        }
        currPageLabel.setText(Integer.toString(currPage));
        prevPageButton.setEnabled(true);

        // read and display a page of rows
        String tableName = (String) this.tablesDropdownList.getSelectedItem();
        readTable(tableName, (currPage - 1) * ROWS_PER_PAGE + 1, ROWS_PER_PAGE);
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_nextPageButtonActionPerformed

    private void prevPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevPageButtonActionPerformed

        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        currPage--;
        if (currPage == 1) {
            prevPageButton.setEnabled(false);
        }
        currPageLabel.setText(Integer.toString(currPage));
        nextPageButton.setEnabled(true);

        // read and display a page of rows
        String tableName = (String) this.tablesDropdownList.getSelectedItem();
        readTable(tableName, (currPage - 1) * ROWS_PER_PAGE + 1, ROWS_PER_PAGE);
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_prevPageButtonActionPerformed

    private void tablesDropdownListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tablesDropdownListActionPerformed
        JComboBox<?> cb = (JComboBox<?>) evt.getSource();
        String tableName = (String) cb.getSelectedItem();
        if (null == tableName) {
            return;
        }
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        selectTable(tableName);
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tablesDropdownListActionPerformed

    /**
     * The action when the Export Csv button is pressed. The file chooser window will pop
     * up to choose where the user wants to save the csv file. The default location is case export directory.
     *
     * @param evt the action event
     */

    @NbBundle.Messages({"SQLiteViewer.csvExport.fileName.empty=Please input a file name for exporting.",
                        "SQLiteViewer.csvExport.title=Export to csv file",
                        "SQLiteViewer.csvExport.confirm.msg=Do you want to overwrite the existing file?"})
    private void exportCsvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportCsvButtonActionPerformed
        Case openCase = Case.getCurrentCase();
        File caseDirectory = new File(openCase.getExportDirectory());        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDragEnabled(false);
        fileChooser.setCurrentDirectory(caseDirectory);
        //Set a filter to let the filechooser only work for csv files
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("*.csv", "csv");
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(csvFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String defaultFileName = (String) this.tablesDropdownList.getSelectedItem();
        fileChooser.setSelectedFile(new File(defaultFileName));
        int choice = fileChooser.showSaveDialog((Component) evt.getSource()); //TODO
        if (JFileChooser.APPROVE_OPTION == choice) {
            File file = fileChooser.getSelectedFile();
            if (file.exists() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
                        Bundle.SQLiteViewer_csvExport_confirm_msg(), 
                        Bundle.SQLiteViewer_csvExport_title(), 
                        JOptionPane.YES_NO_OPTION)) {
                } else {
                    return;
                }            
            }
         
            exportTableToCsv(file);
        }
    }//GEN-LAST:event_exportCsvButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currPageLabel;
    private javax.swing.JButton exportCsvButton;
    private javax.swing.JPanel jHdrPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jTableDataPanel;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JTextField numEntriesField;
    private javax.swing.JLabel numPagesLabel;
    private javax.swing.JButton prevPageButton;
    private javax.swing.JComboBox<String> tablesDropdownList;
    // End of variables declaration//GEN-END:variables

    @Override
    public List<String> getSupportedMIMETypes() {
        return Arrays.asList(SUPPORTED_MIMETYPES);
    }

    @Override
    public void setFile(AbstractFile file) {
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        sqliteDbFile = file;
        processSQLiteFile();
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void resetComponent() {
        tablesDropdownList.setEnabled(true);
        tablesDropdownList.removeAllItems();
        numEntriesField.setText("");

        // close DB connection to file
        if (null != sqliteReader) {
            sqliteReader.close();
            sqliteReader = null;
        }
        
        sqliteDbFile = null;
    }

    /**
     * Process the given SQLite DB file.
     */
    @NbBundle.Messages({
        "SQLiteViewer.comboBox.noTableEntry=No tables found",
        "SQLiteViewer.errorMessage.interrupted=The processing of the file was interrupted.",
        "SQLiteViewer.errorMessage.noCurrentCase=The case has been closed.",
        "SQLiteViewer.errorMessage.failedToExtractFile=The file could not be extracted from the data source.",
        "SQLiteViewer.errorMessage.failedToQueryDatabase=The database tables in the file could not be read.",
        "SQLiteViewer.errorMessage.failedToinitJDBCDriver=The JDBC driver for SQLite could not be loaded.",
        "# {0} - exception message", "SQLiteViewer.errorMessage.unexpectedError=An unexpected error occurred:\n{0).",})
    private void processSQLiteFile() {       
        tablesDropdownList.removeAllItems();
        try {
            String localDiskPath = Case.getCurrentCaseThrows().getTempDirectory() + 
                    File.separator + sqliteDbFile.getName();
            
            sqliteReader = FileReaderFactory.createReader(SUPPORTED_MIMETYPES[0], sqliteDbFile, localDiskPath);
            
            Map<String, String> dbTablesMap = sqliteReader.getTableSchemas();
            
            if (dbTablesMap.isEmpty()) {
                tablesDropdownList.addItem(Bundle.SQLiteViewer_comboBox_noTableEntry());
                tablesDropdownList.setEnabled(false);
            } else {
                dbTablesMap.keySet().forEach((tableName) -> {
                    tablesDropdownList.addItem(tableName);
                });
            }
        } catch (NoCurrentCaseException ex) {
            logger.log(Level.SEVERE, "Current case has been closed", ex); //NON-NLS
            MessageNotifyUtil.Message.error(Bundle.SQLiteViewer_errorMessage_noCurrentCase());
        } catch (FileReaderException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to get tables from DB file  '%s' (objId=%d)", //NON-NLS
                    sqliteDbFile.getName(), sqliteDbFile.getId()), ex);
            MessageNotifyUtil.Message.error(
                    Bundle.SQLiteViewer_errorMessage_failedToQueryDatabase());
        } catch (FileReaderInitException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to create a SQLiteReader '%s' (objId=%d)", //NON-NLS
                    sqliteDbFile.getName(), sqliteDbFile.getId()), ex);
        }
    }

    @NbBundle.Messages({"# {0} - tableName",
        "SQLiteViewer.selectTable.errorText=Error getting row count for table: {0}"
    })
    private void selectTable(String tableName) {
        try {
            numRows = sqliteReader.getRowCountFromTable(tableName);
            numEntriesField.setText(numRows + " entries");

            currPage = 1;
            currPageLabel.setText(Integer.toString(currPage));
            numPagesLabel.setText(Integer.toString((numRows / ROWS_PER_PAGE) + 1));

            prevPageButton.setEnabled(false);

            if (numRows > 0) {
                exportCsvButton.setEnabled(true);
                nextPageButton.setEnabled(((numRows > ROWS_PER_PAGE)));
                readTable(tableName, (currPage - 1) * ROWS_PER_PAGE + 1, ROWS_PER_PAGE);
            } else {
                exportCsvButton.setEnabled(false);
                nextPageButton.setEnabled(false);
                selectedTableView.setupTable(Collections.emptyList());
            }
            
        } catch (FileReaderException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to load table %s from DB file '%s' (objId=%d)", tableName, //NON-NLS
                    sqliteDbFile.getName(), sqliteDbFile.getId()), ex);
            MessageNotifyUtil.Message.error(
                    Bundle.SQLiteViewer_selectTable_errorText(tableName));
        }
    }

    @NbBundle.Messages({"# {0} - tableName",
        "SQLiteViewer.readTable.errorText=Error getting rows for table: {0}"})
    private void readTable(String tableName, int startRow, int numRowsToRead) {

        try {
            List<Map<String, Object>> rows = sqliteReader.getRowsFromTable(
                    tableName, startRow, numRowsToRead);
            if (Objects.nonNull(rows)) {
                selectedTableView.setupTable(rows);
            } else {
                selectedTableView.setupTable(Collections.emptyList());
            }
        } catch (FileReaderException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to read table %s from DB file '%s' (objId=%d)", tableName, //NON-NLS
                    sqliteDbFile.getName(), sqliteDbFile.getId()), ex);
            MessageNotifyUtil.Message.error(
                    Bundle.SQLiteViewer_readTable_errorText(tableName));
        }
    }
    
    /**
     * Converts a sqlite table into a CSV file.
     * 
     * @param file
     * @param tableName
     * @param rowMap A list of rows in the table, where each row is represented as a column-value
     * map.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    @NbBundle.Messages({
        "SQLiteViewer.exportTableToCsv.FileName=File name: ",
        "SQLiteViewer.exportTableToCsv.TableName=Table name: "
    })
    public void exportTableToCSV(File file, String tableName, 
            List<Map<String, Object>> rowMap) throws FileNotFoundException, IOException{
        
        File csvFile;
        String fileName = file.getName();
        if (FilenameUtils.getExtension(fileName).equalsIgnoreCase("csv")) {
            csvFile = file;
        } else {
            csvFile = new File(file.toString() + ".csv");
        }

        try (FileOutputStream out = new FileOutputStream(csvFile, false)) {

            out.write((Bundle.SQLiteViewer_exportTableToCsv_FileName() + csvFile.getName() + "\n").getBytes());
            out.write((Bundle.SQLiteViewer_exportTableToCsv_TableName() + tableName + "\n").getBytes());
            
            String header = createColumnHeader(rowMap.get(0)).concat("\n");
            out.write(header.getBytes());

            for (Map<String, Object> maps : rowMap) {
                String row = maps.values()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
                        .concat("\n");
                out.write(row.getBytes());
            }
        }
    }
    
    @NbBundle.Messages({
        "SQLiteViewer.exportTableToCsv.write.errText=Failed to export table content to csv file.",
    })
    private void exportTableToCsv(File file) {
        String tableName = (String) this.tablesDropdownList.getSelectedItem();
        try {
            List<Map<String, Object>> currentTableRows = 
                    sqliteReader.getRowsFromTable(tableName);

            if (Objects.isNull(currentTableRows) || currentTableRows.isEmpty()) {
                logger.log(Level.INFO, String.format(
                        "The table %s is empty. (objId=%d)", tableName, //NON-NLS
                        sqliteDbFile.getId()));
            } else {
                exportTableToCSV(file, tableName, currentTableRows);
            }
        } catch (FileReaderException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to read table %s from DB file '%s' (objId=%d)", //NON-NLS
                    tableName, sqliteDbFile.getName(), sqliteDbFile.getId()), ex); 
            MessageNotifyUtil.Message.error(
                    Bundle.SQLiteViewer_readTable_errorText(tableName));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, String.format(
                    "Failed to export table %s to file '%s'", tableName, file.getName()), ex); //NON-NLS
            MessageNotifyUtil.Message.error(
                    Bundle.SQLiteViewer_exportTableToCsv_write_errText());
        }
    }
    
    /**
     * Returns a comma seperated header string from the keys of the column
     * row map.
     * 
     * @param row column header row map
     * @return comma seperated header string
     */
    private String createColumnHeader(Map<String, Object> row) {
        return row.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));
    }
}

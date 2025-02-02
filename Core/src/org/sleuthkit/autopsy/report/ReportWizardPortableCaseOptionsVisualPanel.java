/*
 * Autopsy Forensic Browser
 *
 * Copyright 2019 Basis Technology Corp.
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
package org.sleuthkit.autopsy.report;

import java.awt.GridLayout;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.PlatformUtil;
import org.sleuthkit.autopsy.report.PortableCaseReportModule.ChunkSize;
import org.sleuthkit.autopsy.report.PortableCaseReportModule.PortableCaseOptions;

/**
 * The UI portion of the Portable Case config panel
 */
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
class ReportWizardPortableCaseOptionsVisualPanel extends javax.swing.JPanel {

    private final ReportWizardPortableCaseOptionsPanel wizPanel;
    private final PortableCaseOptions options = new PortableCaseOptions();
    
    /**
     * Creates new form ReportWizardPortableCaseOptionsVisualPanel
     */
    ReportWizardPortableCaseOptionsVisualPanel(ReportWizardPortableCaseOptionsPanel wizPanel) {
        this.wizPanel = wizPanel;
        initComponents();
        customizeComponents();
    }
    
    private void customizeComponents() {
        
        if ( ! PlatformUtil.isWindowsOS()) {
            errorLabel.setVisible(true);
            compressCheckbox.setEnabled(false);
        } else {
            errorLabel.setVisible(false);
        }
        
        for (ChunkSize chunkSize:ChunkSize.values()) {
            chunkSizeComboBox.addItem(chunkSize);
        }
        chunkSizeComboBox.setSelectedItem(ChunkSize.NONE);
        chunkSizeComboBox.setEnabled(false);
        options.updateCompression(false, ChunkSize.NONE);        
        
        listPanel.setLayout(new GridLayout(1,2));
        listPanel.add(new PortableCaseTagsListPanel(wizPanel, options));
        listPanel.add(new PortableCaseInterestingItemsListPanel(wizPanel, options));
    }
    
    @NbBundle.Messages({
        "ReportWizardPortableCaseOptionsVisualPanel.getName.title=Choose Portable Case settings",  
    })  
    @Override
    public String getName() {
        return Bundle.ReportWizardPortableCaseOptionsVisualPanel_getName_title();
    }
    
    /**
     * Get the selected chunk size
     * 
     * @return the chunk size that was selected
     */
    private ChunkSize getChunkSize() {
        return (ChunkSize) chunkSizeComboBox.getSelectedItem();
    }
    
    /**
     * Update the selected compression options and enable/disable the finish button
     */
    private void updateCompression() {
        options.updateCompression(compressCheckbox.isSelected(), getChunkSize());
        wizPanel.setFinish(options.isValid());
    }
    
    /**
     * Get the user-selected settings.
     *
     * @return the current settings
     */
    PortableCaseOptions getPortableCaseReportOptions() {
        return options;
    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        chunkSizeComboBox = new javax.swing.JComboBox<>();
        compressCheckbox = new javax.swing.JCheckBox();
        errorLabel = new javax.swing.JLabel();
        listPanel = new javax.swing.JPanel();

        chunkSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chunkSizeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(compressCheckbox, org.openide.util.NbBundle.getMessage(ReportWizardPortableCaseOptionsVisualPanel.class, "ReportWizardPortableCaseOptionsVisualPanel.compressCheckbox.text")); // NOI18N
        compressCheckbox.setToolTipText(org.openide.util.NbBundle.getMessage(ReportWizardPortableCaseOptionsVisualPanel.class, "ReportWizardPortableCaseOptionsVisualPanel.compressCheckbox.toolTipText")); // NOI18N
        compressCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compressCheckboxActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(ReportWizardPortableCaseOptionsVisualPanel.class, "ReportWizardPortableCaseOptionsVisualPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout listPanelLayout = new javax.swing.GroupLayout(listPanel);
        listPanel.setLayout(listPanelLayout);
        listPanelLayout.setHorizontalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        listPanelLayout.setVerticalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 217, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(compressCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chunkSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(41, Short.MAX_VALUE))
            .addComponent(listPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(listPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(compressCheckbox)
                    .addComponent(chunkSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(errorLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chunkSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chunkSizeComboBoxActionPerformed
        updateCompression();
    }//GEN-LAST:event_chunkSizeComboBoxActionPerformed

    private void compressCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compressCheckboxActionPerformed
        chunkSizeComboBox.setEnabled(compressCheckbox.isSelected());
        updateCompression();
    }//GEN-LAST:event_compressCheckboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ChunkSize> chunkSizeComboBox;
    private javax.swing.JCheckBox compressCheckbox;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel listPanel;
    // End of variables declaration//GEN-END:variables
}

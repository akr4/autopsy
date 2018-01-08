/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sleuthkit.autopsy.livetriage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.LocalDisk;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.coreutils.PlatformUtil;

/**
 *
 */
class SelectDriveDialog extends javax.swing.JDialog {

    private List<LocalDisk> disks = new ArrayList<>();
    private final LocalDiskModel model = new LocalDiskModel();
    private final java.awt.Frame parent;
    private String drivePath = "";
    
    /**
     * Creates new form SelectDriveDialog
     */
    @NbBundle.Messages({"SelectDriveDialog.title=Create Live Triage Drive"})
    SelectDriveDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        
        model.loadDisks();
        bnOk.setEnabled(false);
        diskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (diskTable.getSelectedRow() >= 0 && diskTable.getSelectedRow() < disks.size()) {
                    bnOk.setEnabled(true);
                } else {  //The selection changed to nothing valid being selected, such as with ctrl+click
                    bnOk.setEnabled(false);
                }
            }
        });
    }
    
    void display(){        
        this.setTitle(Bundle.SelectDriveDialog_title());
        
        final Dimension parentSize = parent.getSize();
        final Point parentLocationOnScreen = parent.getLocationOnScreen();
        final Dimension childSize = this.getSize();
        int x;
        int y;
        x = (parentSize.width - childSize.width) / 2;
        y = (parentSize.height - childSize.height) / 2;
        x += parentLocationOnScreen.x;
        y += parentLocationOnScreen.y;

        setLocation(x, y);
        setVisible(true);
    }
    
    String getSelectedDrive(){
        return this.drivePath;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        diskTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        bnRefresh = new javax.swing.JButton();
        bnOk = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        bnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        diskTable.setModel(model);
        jScrollPane1.setViewportView(diskTable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bnRefresh, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.bnRefresh.text")); // NOI18N
        bnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnRefreshActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bnOk, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.bnOk.text")); // NOI18N
        bnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnOkActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.errorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bnCancel, org.openide.util.NbBundle.getMessage(SelectDriveDialog.class, "SelectDriveDialog.bnCancel.text")); // NOI18N
        bnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel4)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(1, 1, 1)
                .addComponent(jLabel3)
                .addGap(1, 1, 1)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(1, 1, 1)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bnRefresh)
                    .addComponent(bnCancel)
                    .addComponent(bnOk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnRefreshActionPerformed
        model.loadDisks();
    }//GEN-LAST:event_bnRefreshActionPerformed

    private void bnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnOkActionPerformed
        if (diskTable.getSelectedRow() >= 0 && diskTable.getSelectedRow() < disks.size()) {
            LocalDisk selectedDisk = disks.get(diskTable.getSelectedRow());
            drivePath = selectedDisk.getPath();
        } else {
            drivePath = "";
        }
        dispose();
    }//GEN-LAST:event_bnOkActionPerformed

    private void bnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_bnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bnCancel;
    private javax.swing.JButton bnOk;
    private javax.swing.JButton bnRefresh;
    private javax.swing.JTable diskTable;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables


    /**
     * Table model for displaying information from LocalDisk Objects in a table.
     */
    @NbBundle.Messages({"SelectDriveDialog.localDiskModel.loading.msg=",
        "SelectDriveDialog.localDiskModel.nodrives.msg=Executable could not be found",
        "SelectDriveDialog.diskTable.column1.title=Disk Name",
        "SelectDriveDialog.diskTable.column2.title=Disk Size",
        "SelectDriveDialog.errLabel.disksNotDetected.text=Disks were not detected. On some systems it requires admin privileges",
        "SelectDriveDialog.errLabel.disksNotDetected.toolTipText=Disks were not detected. On some systems it requires admin privileges",
        "SelectDriveDialog.errLabel.drivesNotDetected.text=Local drives were not detected. Auto-detection not supported on this OS  or admin privileges required",
        "SelectDriveDialog.errLabel.drivesNotDetected.toolTipText=Local drives were not detected. Auto-detection not supported on this OS  or admin privileges required",
        "SelectDriveDialog.errLabel.someDisksNotDetected.text=Some disks were not detected. On some systems it requires admin privileges",
        "SelectDriveDialog.errLabel.someDisksNotDetected.toolTipText=Some disks were not detected. On some systems it requires admin privileges"
            
    })
    private class LocalDiskModel implements TableModel {

        private LocalDiskThread worker = null;
        private boolean ready = false;
        private volatile boolean loadingDisks = false;

        //private String SELECT = "Select a local disk:";
        private final String LOADING = NbBundle.getMessage(this.getClass(), "SelectDriveDialog.localDiskModel.loading.msg");
        private final String NO_DRIVES = NbBundle.getMessage(this.getClass(), "SelectDriveDialog.localDiskModel.nodrives.msg");

        private void loadDisks() {

            // if there is a worker already building the lists, then cancel it first.
            if (loadingDisks && worker != null) {
                worker.cancel(false);
            }

            // Clear the lists
            errorLabel.setText("");
            diskTable.setEnabled(false);
            ready = false;
            loadingDisks = true;
            worker = new LocalDiskThread();
            worker.execute();
        }

        @Override
        public int getRowCount() {
            if (disks.isEmpty()) {
                return 0;
            }
            return disks.size();
        }

        @Override
        public int getColumnCount() {
            return 2;

        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return NbBundle.getMessage(this.getClass(), "SelectDriveDialog.diskTable.column1.title");
                case 1:
                    return NbBundle.getMessage(this.getClass(), "SelectDriveDialog.diskTable.column2.title");
                default:
                    return "Unnamed"; //NON-NLS  
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (ready) {
                if (disks.isEmpty()) {
                    return NO_DRIVES;
                }
                switch (columnIndex) {
                    case 0:
                        return disks.get(rowIndex).getName();
                    case 1:
                        return disks.get(rowIndex).getReadableSize();
                    default:
                        return disks.get(rowIndex).getPath();
                }
            } else {
                return LOADING;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            //setter does nothing they should not be able to modify table
        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }

        /**
         * Gets the lists of physical drives and partitions and combines them
         * into a list of disks.
         */
        class LocalDiskThread extends SwingWorker<Object, Void> {

            private final Logger logger = Logger.getLogger(LocalDiskThread.class.getName());
            private List<LocalDisk> physicalDrives = new ArrayList<>();
            private List<LocalDisk> partitions = new ArrayList<>();

            @Override
            protected Object doInBackground() throws Exception {
                // Populate the lists
                //physicalDrives = new ArrayList<>();
                partitions = new ArrayList<>();
                //physicalDrives = PlatformUtil.getPhysicalDrives();
                partitions = PlatformUtil.getPartitions();
                return null;
            }

            /**
             * Display any error messages that might of occurred when getting
             * the lists of physical drives or partitions.
             */
            private void displayErrors() {
                if (physicalDrives.isEmpty() && partitions.isEmpty()) {
                    if (PlatformUtil.isWindowsOS()) {
                        errorLabel.setText(
                                NbBundle.getMessage(this.getClass(), "SelectDriveDialog.errLabel.disksNotDetected.text"));
                        errorLabel.setToolTipText(NbBundle.getMessage(this.getClass(),
                                "SelectDriveDialog.errLabel.disksNotDetected.toolTipText"));
                    } else {
                        errorLabel.setText(
                                NbBundle.getMessage(this.getClass(), "SelectDriveDialog.errLabel.drivesNotDetected.text"));
                        errorLabel.setToolTipText(NbBundle.getMessage(this.getClass(),
                                "SelectDriveDialog.errLabel.drivesNotDetected.toolTipText"));
                    }
                    errorLabel.setVisible(true);
                    diskTable.setEnabled(false);
                }/* else if (physicalDrives.isEmpty()) {
                    errorLabel.setText(
                            NbBundle.getMessage(this.getClass(), "SelectDriveDialog.errLabel.someDisksNotDetected.text"));
                    errorLabel.setToolTipText(NbBundle.getMessage(this.getClass(),
                            "SelectDriveDialog.errLabel.someDisksNotDetected.toolTipText"));
                    errorLabel.setVisible(true);
                }*/
            }

            @Override
            protected void done() {
                try {
                    super.get(); //block and get all exceptions thrown while doInBackground()
                } catch (CancellationException ex) {
                    logger.log(Level.INFO, "Loading local disks was canceled."); //NON-NLS
                } catch (InterruptedException ex) {
                    logger.log(Level.INFO, "Loading local disks was interrupted."); //NON-NLS
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Fatal error when loading local disks", ex); //NON-NLS
                } finally {
                    if (!this.isCancelled()) {
                        displayErrors();
                        worker = null;
                        loadingDisks = false;
                        disks = new ArrayList<>();
                        //disks.addAll(physicalDrives);
                        disks.addAll(partitions);
                        if (disks.size() > 0) {
                            diskTable.setEnabled(true);
                            diskTable.clearSelection();
                        }
                        ready = true;
                    }
                }
                diskTable.revalidate();
            }
        }
    }
}

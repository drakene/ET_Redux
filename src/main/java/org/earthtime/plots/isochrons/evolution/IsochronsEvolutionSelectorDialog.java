/*
 * IsochronsSelectorDialog.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.plots.isochrons.evolution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.plots.isochrons.IsochronModel;
import org.earthtime.utilities.CheckBoxList;

/**
 *
 * @author James F. Bowring
 */
public class IsochronsEvolutionSelectorDialog extends DialogEditor {

    // Fields    
    private SortedSet<IsochronModel> selectedIsochrons;

    private CheckBoxList isochronCheckBoxes;

    /**
     * Creates new form IsochronsSelectorDialog
     *
     * @param parent
     * @param modal
     * @param selectedIsochrons
     */
    public IsochronsEvolutionSelectorDialog(//
            java.awt.Frame parent,
            boolean modal,
            SortedSet<IsochronModel> selectedIsochrons) {
        super(parent, modal);

        this.selectedIsochrons = selectedIsochrons;
        if (this.selectedIsochrons == null) {
            this.selectedIsochrons = new TreeSet<>();
        }
        
        initComponents();

        isochronCheckBoxes = new CheckBoxList();
        isochronsLayeredPane.add(isochronCheckBoxes);

        initLists();

    }

    private class IsochronModelList extends AbstractListModel {

        List<IsochronModel> isochronModels;

        public IsochronModelList() {
            isochronModels = new ArrayList<>();
        }

        @Override
        public int getSize() {
            return isochronModels.size();
        }

        @Override
        public IsochronModel getElementAt(int index) {
            return isochronModels.get(index);
        }

        public void addElement(IsochronModel im) {
            isochronModels.add(im);
            Collections.sort(isochronModels);
        }

        public void removeElement(IsochronModel im) {
            isochronModels.remove(im);
            Collections.sort(isochronModels);
        }
    }

    private void initLists() {
        JCheckBox[] isochronCheckBoxArray = new JCheckBox[selectedIsochrons.size()];

        // used to get quick access to isochrons
        IsochronModel[] isochrons = new IsochronModel[selectedIsochrons.size()];
        int count = 0;
        Iterator<IsochronModel> isochronIterator = selectedIsochrons.iterator();
        while (isochronIterator.hasNext()) {
            IsochronModel isochronModel = isochronIterator.next();
            isochrons[count] = isochronModel;

            JCheckBox checkBox = new JCheckBox(isochronModel.prettyPrintI());
            checkBox.setSelected(isochronModel.isVisible());
            // track index
            checkBox.setName(String.valueOf(count));
            checkBox.addChangeListener((ChangeEvent e) -> {
                isochrons[Integer.parseInt(((JCheckBox) e.getSource()).getName())]
                        .setVisible(((JCheckBox) e.getSource()).isSelected());
            });
            isochronCheckBoxArray[count] = checkBox;
            count++;
        }

        isochronCheckBoxes.removeAll();
        isochronCheckBoxes.setBounds(100, 50, 100, count * 20);
        isochronCheckBoxes.setListData(isochronCheckBoxArray);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonsPanel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        isochronsLayeredPane = new javax.swing.JLayeredPane();
        specifyIsochron_label = new javax.swing.JLabel();
        availableListLabel = new javax.swing.JLabel();
        dateInKaText = new javax.swing.JTextField();
        specifyDateka_label = new javax.swing.JLabel();
        defaultSetButton = new ET_JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Available Sample Date Interpretation Models");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(245, 236, 206));
        setForeground(java.awt.Color.white);
        setName(getTitle());
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        buttonsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Done");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(close_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 4, 270, 25));

        getContentPane().add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 570, 340, 30));

        isochronsLayeredPane.setBackground(new java.awt.Color(255, 237, 255));
        isochronsLayeredPane.setOpaque(true);

        specifyIsochron_label.setText("Manage Evolution Plot Isochrons:");
        isochronsLayeredPane.add(specifyIsochron_label);
        specifyIsochron_label.setBounds(10, 10, 240, 16);

        availableListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        availableListLabel.setText("Use CheckBoxes to toggle Isochron visibility:");
        isochronsLayeredPane.add(availableListLabel);
        availableListLabel.setBounds(20, 30, 300, 16);

        dateInKaText.setText("0");
        isochronsLayeredPane.add(dateInKaText);
        dateInKaText.setBounds(150, 520, 50, 26);

        specifyDateka_label.setText("Enter Date in ka:");
        isochronsLayeredPane.add(specifyDateka_label);
        specifyDateka_label.setBounds(30, 530, 110, 16);

        defaultSetButton.setForeground(new java.awt.Color(255, 51, 0));
        defaultSetButton.setText("Default Set");
        defaultSetButton.setMargin(new java.awt.Insets(0, 1, 0, 1));
        defaultSetButton.setPreferredSize(new java.awt.Dimension(140, 23));
        defaultSetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultSetButtonActionPerformed(evt);
            }
        });
        isochronsLayeredPane.add(defaultSetButton);
        defaultSetButton.setBounds(0, 550, 100, 23);

        getContentPane().add(isochronsLayeredPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void defaultSetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultSetButtonActionPerformed
        selectedIsochrons.clear();
        selectedIsochrons.addAll(IsochronModel.generateDefaultEvolutionIsochronModels());
        initLists();
        repaint();
    }//GEN-LAST:event_defaultSetButtonActionPerformed

    private void updateList(JList<IsochronModel> jlist, AbstractListModel<IsochronModel> listModel) {
        jlist.setModel(new IsochronModelList());
        jlist.setModel(listModel);
        jlist.validate();
    }

    private void OK() {

    }

    /**
     * @return the selectedIsochrons
     */
    public SortedSet<IsochronModel> getSelectedIsochrons() {
        return selectedIsochrons;
    }

    /**
     * @param selectedIsochrons the selectedIsochrons to set
     */
    public void setSelectedIsochrons(SortedSet<IsochronModel> selectedIsochrons) {
        this.selectedIsochrons = selectedIsochrons;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel availableListLabel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JTextField dateInKaText;
    private javax.swing.JButton defaultSetButton;
    private javax.swing.JLayeredPane isochronsLayeredPane;
    private javax.swing.JLabel specifyDateka_label;
    private javax.swing.JLabel specifyIsochron_label;
    // End of variables declaration//GEN-END:variables

}

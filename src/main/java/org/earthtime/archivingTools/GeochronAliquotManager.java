/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.archivingTools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.SampleI;
import org.earthtime.archivingTools.forSESAR.SesarSample;
import org.earthtime.archivingTools.forSESAR.SesarSampleManager;
import org.earthtime.beans.ET_JButton;
import org.earthtime.projects.ProjectI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeochronAliquotManager extends JPanel {

    private ProjectI project;
    private SampleI sample;
    private final String userName;
    private final String password;
    private String userCode;
    private String sampleIGSN;
    private final int height;
    private final int width;
    private final static int ROW_HEIGHT = 25;
    private final int LEFT_MARGIN = 5;
    private final int TOP_MARGIN = 5;
    private JTextField sampleNameText;
    private JTextField sampleIGSNText;
    private SesarSample sesarSample;
    private SesarSample[] sesarAliquots;
    private JButton saveButton;
    private JButton registerNewSampleButton;
    private JButton viewSampleRecordButton;
    private JLabel checkMarkForValidSampleIGSN;
    private JLabel xMarkForInValidSampleIGSN;
    // aliquot fields
    private Vector<Aliquot> activeAliquots;
    private JTextField[] aliquotName_TextFields;
    private JTextField[] aliquotIGSN_TextFields;
    private JLabel[] checkMarkForValidAliquotIGSNs;
    private JLabel[] xMarkForInValidAliqutIGSNs;
    private JButton[] aliquotUploadButtons;
    private String[] aliquotIGSNs;
    private JButton[] registerNewAliquotButtons;
    private JButton[] viewAliquotRecordButtons;

    public GeochronAliquotManager(ProjectI project, SampleI sample, String userName, String password, String userCode, int x, int y, int width, int height) {
        this.project = project;
        this.sample = sample;
        this.userName = userName;
        this.password = password;
        this.userCode = userCode;
        this.height = height;
        this.width = width;

        this.sampleIGSN = "IGSN";
        this.sesarSample = new SesarSample(userCode, userName, password, false);

        setOpaque(true);
        setBackground(Color.white);
        setBorder(null);
        setBounds(x, y, this.width, this.height);

        setLayout(null);

        initSampleView();
    }

    private void initSampleView() {
        String sampleName = sample.getSampleName();
        sampleIGSN = sample.getSampleIGSN();

        int cumulativeWidth = LEFT_MARGIN;
        add(labelFactory("Sample Name:", cumulativeWidth, TOP_MARGIN, 100));
        cumulativeWidth += 100;

        sampleNameText = new JTextField(sampleName);
        sampleNameText.setBounds(cumulativeWidth, TOP_MARGIN, 150, ROW_HEIGHT);
        sampleNameText.setFont(ReduxConstants.sansSerif_12_Bold);
        add(sampleNameText);
        sampleNameText.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                if (textField.getText().trim().length() == 0) {
                    textField.setText(sample.getSampleName());
                }
                return true;
            }
        });
        cumulativeWidth += 150;

        add(labelFactory("IGSN:", cumulativeWidth, TOP_MARGIN, 35));
        cumulativeWidth += 35;

        sampleIGSNText = new JTextField(sampleIGSN);
        sampleIGSNText.setBounds(cumulativeWidth, TOP_MARGIN, 90, ROW_HEIGHT);
        sampleIGSNText.setFont(ReduxConstants.sansSerif_12_Bold);
        add(sampleIGSNText);
        sampleIGSNText.setInputVerifier(new SampleIGSNVerifier());
        sampleIGSNText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                    sampleIGSNText.getInputVerifier().verify(sampleIGSNText);
                }
            }
        }
        );
        cumulativeWidth += 90;

        // next two occupy same space and show depending on condition
        checkMarkForValidSampleIGSN = new JLabel();
        checkMarkForValidSampleIGSN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png")));
        checkMarkForValidSampleIGSN.setToolTipText("Sample IGSN is VALID.");
        checkMarkForValidSampleIGSN.setIconTextGap(0);
        checkMarkForValidSampleIGSN.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        checkMarkForValidSampleIGSN.setVisible(false);
        add(checkMarkForValidSampleIGSN);

        xMarkForInValidSampleIGSN = new JLabel();
        xMarkForInValidSampleIGSN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForInValidSampleIGSN.setToolTipText("Sample IGSN is NOT valid.");
        xMarkForInValidSampleIGSN.setIconTextGap(0);
        xMarkForInValidSampleIGSN.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        xMarkForInValidSampleIGSN.setVisible(false);
        add(xMarkForInValidSampleIGSN);
        cumulativeWidth += 35;

        // next two occupy same space and show depending on condition
        registerNewSampleButton = new ET_JButton("Register New Sample");
        registerNewSampleButton.setBounds(cumulativeWidth, TOP_MARGIN, 135, 25);
        registerNewSampleButton.setFont(ReduxConstants.sansSerif_12_Bold);
        registerNewSampleButton.setVisible(false);
        add(registerNewSampleButton);
        registerNewSampleButton.addActionListener((ActionEvent e) -> {
            saveSample();
            sesarSample.setIGSN(userCode);
            sesarSample.setName(sample.getSampleName());
            DialogEditor sesarSampleManager = //
                    new SesarSampleManager(null, true, sesarSample, true);
            sesarSampleManager.setVisible(true);
            sample.setSampleIGSN(sesarSample.getIGSN());
            sampleIGSNText.setText(sesarSample.getIGSN());
            sampleIGSNText.getInputVerifier().verify(sampleIGSNText);
            saveSample();
        });

        viewSampleRecordButton = new ET_JButton("View Existing Record");
        viewSampleRecordButton.setBounds(cumulativeWidth, TOP_MARGIN, 135, 25);
        viewSampleRecordButton.setFont(ReduxConstants.sansSerif_12_Bold);
        viewSampleRecordButton.setVisible(false);
        add(viewSampleRecordButton);
        viewSampleRecordButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveSample();
                // get the igsn record and create a SesarSample to view
                SesarSample mySesarSample = SesarSample.createSesarSampleFromSesarRecord(sample.getSampleIGSN());
                if (mySesarSample != null) {
                    sesarSample = mySesarSample;
                    sesarSample.setNameOfLocalSample(sample.getSampleName());
                    DialogEditor sesarSampleManager = //
                            new SesarSampleManager(null, true, sesarSample, false);
                    sesarSampleManager.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{"Could not retrieve sample details."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        cumulativeWidth += 135;

        saveButton = new ET_JButton("Save");
        saveButton.setBounds((int) (getBounds().getWidth() - 75.0), TOP_MARGIN, 73, 25);
        saveButton.setFont(ReduxConstants.sansSerif_12_Bold);
        saveButton.setVisible(false);
        add(saveButton);
        saveButton.addActionListener((ActionEvent e) -> {
            saveSample();
        });

        sampleIGSNText.getInputVerifier().verify(sampleIGSNText);

        initAliquotsOfSampleViews();
    }

    private void initAliquotsOfSampleViews() {
        // aliquots
        // the initial working assumption is one aliquot per sample, but we will eventually support n aliquots per sample
        activeAliquots = sample.getActiveAliquots();
        int aliquotCount = activeAliquots.size();
        aliquotName_TextFields = new JTextField[aliquotCount];
        aliquotUploadButtons = new JButton[aliquotCount];
        aliquotIGSN_TextFields = new JTextField[aliquotCount];
        checkMarkForValidAliquotIGSNs = new JLabel[aliquotCount];
        xMarkForInValidAliqutIGSNs = new JLabel[aliquotCount];
        aliquotIGSNs = new String[aliquotCount];
        registerNewAliquotButtons = new JButton[aliquotCount];
        viewAliquotRecordButtons = new JButton[aliquotCount];
        sesarAliquots = new SesarSample[aliquotCount];

        for (int i = 0; i < aliquotCount; i++) {
            System.out.println(sample.getSampleName() + "  >  " + activeAliquots.get(i).getAliquotName());
            Aliquot aliquot = activeAliquots.get(i);
            String aliquotName = aliquot.getAliquotName();
            aliquotIGSNs[i] = aliquot.getAliquotIGSN();
            sesarAliquots[i] = new SesarSample(userCode, userName, password, true);
            final SesarSample sesarAliquot = sesarAliquots[i];

            int cumulativeWidth = LEFT_MARGIN;
            add(labelFactory("Aliquot Name:", cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 100));
            cumulativeWidth += 100;

            aliquotName_TextFields[i] = new JTextField(aliquotName);
            aliquotName_TextFields[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 150, ROW_HEIGHT);
            aliquotName_TextFields[i].setFont(ReduxConstants.sansSerif_12_Bold);
            add(aliquotName_TextFields[i]);
            aliquotName_TextFields[i].setInputVerifier(new InputVerifier() {

                @Override
                public boolean verify(JComponent input) {
                    JTextField textField = (JTextField) input;
                    if (textField.getText().trim().length() == 0) {
                        textField.setText(aliquotName);
                    }
                    return true;
                }
            });
            cumulativeWidth += 150;

            JTextField aliquotName_TextField = aliquotName_TextFields[i];

            add(labelFactory("IGSN:", cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35));
            cumulativeWidth += 35;

            aliquotIGSN_TextFields[i] = new JTextField(aliquotIGSNs[i]);
            aliquotIGSN_TextFields[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 90, ROW_HEIGHT);
            aliquotIGSN_TextFields[i].setFont(ReduxConstants.sansSerif_12_Bold);
            add(aliquotIGSN_TextFields[i]);
            aliquotIGSN_TextFields[i].setInputVerifier(new AliquotIGSNVerifier(i));
            aliquotIGSN_TextFields[i].addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    JTextField textField = (JTextField) e.getSource();
                    int key = e.getKeyCode();
                    if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                        textField.getInputVerifier().verify(textField);
                    }
                }
            }
            );
            JTextField aliquotIGSN_TextField = aliquotIGSN_TextFields[i];
            cumulativeWidth += 90;

            // next two occupy same space and show depending on condition
            checkMarkForValidAliquotIGSNs[i] = new JLabel();
            checkMarkForValidAliquotIGSNs[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png")));
            checkMarkForValidAliquotIGSNs[i].setToolTipText("Aliquot IGSN is VALID.");
            checkMarkForValidAliquotIGSNs[i].setIconTextGap(0);
            checkMarkForValidAliquotIGSNs[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35, 25);
            checkMarkForValidAliquotIGSNs[i].setVisible(false);
            add(checkMarkForValidAliquotIGSNs[i]);

            xMarkForInValidAliqutIGSNs[i] = new JLabel();
            xMarkForInValidAliqutIGSNs[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
            xMarkForInValidAliqutIGSNs[i].setToolTipText("Aliquot IGSN is NOT valid.");
            xMarkForInValidAliqutIGSNs[i].setIconTextGap(0);
            xMarkForInValidAliqutIGSNs[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35, 25);
            xMarkForInValidAliqutIGSNs[i].setVisible(false);
            add(xMarkForInValidAliqutIGSNs[i]);
            cumulativeWidth += 35;

            // next two occupy same space and show depending on condition
            registerNewAliquotButtons[i] = new ET_JButton("Register New Aliquot");
            registerNewAliquotButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 135, 25);
            registerNewAliquotButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            registerNewAliquotButtons[i].setVisible(false);
            add(registerNewAliquotButtons[i]);
            registerNewAliquotButtons[i].addActionListener((ActionEvent e) -> {
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
                sesarAliquot.setIGSN(userCode);
                sesarAliquot.setName(aliquot.getAliquotName());
                DialogEditor sesarSampleManager = //
                        new SesarSampleManager(null, true, sesarAliquot, true);
                sesarSampleManager.setVisible(true);
                aliquot.setAliquotIGSN(sesarAliquot.getIGSN());
                aliquotIGSN_TextField.setText(sesarAliquot.getIGSN());
                aliquotIGSN_TextField.getInputVerifier().verify(aliquotIGSN_TextField);
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
            });

            viewAliquotRecordButtons[i] = new ET_JButton("View Existing Record");
            viewAliquotRecordButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 135, 25);
            viewAliquotRecordButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            viewAliquotRecordButtons[i].setVisible(false);
            add(viewAliquotRecordButtons[i]);
            viewAliquotRecordButtons[i].addActionListener((ActionEvent e) -> {
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
                // get the igsn record and create a SesarSample to view
                SesarSample mySesarAliquot = SesarSample.createSesarSampleFromSesarRecord(aliquot.getAliquotIGSN());
                if (mySesarAliquot != null) {
                    //sesarAliquot = mySesarAliquot;
                    mySesarAliquot.setNameOfLocalSample(aliquot.getAliquotName());
                    DialogEditor sesarSampleManager = //
                            new SesarSampleManager(null, true, mySesarAliquot, false);
                    sesarSampleManager.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{"Could not retrieve aliquot details."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            cumulativeWidth += 135;

//
//            
//            
//            
//            
//            
//            
            aliquotUploadButtons[i] = new ET_JButton("Upload");
            aliquotUploadButtons[i].setBounds(LEFT_MARGIN + 800, TOP_MARGIN + 30 * (i + 1), 75, 25);
            aliquotUploadButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            aliquotUploadButtons[i].setVisible(false);
            add(aliquotUploadButtons[i]);
            aliquotUploadButtons[i].addActionListener((ActionEvent e) -> {
                aliquot.setSampleIGSN("SSR." + sampleIGSN.trim());
                GeochronUploaderUtility.uploadAliquotToGeochron(//
                        (Sample) sample, aliquot, //
                        userName, //
                        password, //
                        true, true);
            });

            aliquotIGSN_TextFields[i].getInputVerifier().verify(aliquotIGSN_TextFields[i]);

        }

    }

    private void saveSample() {
        sample.setSampleIGSN(sampleIGSN.trim().toUpperCase());
        // rename supersample aliquots with new sample name
        Vector<Aliquot> aliquots = project.getCompiledSuperSample().getAliquots();
        aliquots.stream().forEach((aliquot) -> {
            String aliquotName = aliquot.getAliquotName();
            String sName = sample.getSampleName().trim();
            if (aliquotName.startsWith(sName + "::")) {
                aliquotName = aliquotName.replace(sName + "::", sampleNameText.getText().trim() + "::");
                aliquot.setAliquotName(aliquotName);
            }
        });
        sample.setSampleName(sampleNameText.getText().trim());
    }

    private void saveAliquot(Aliquot myAliquot, String aliquotIGSN, JTextField aliquotName_TextField) {
        myAliquot.setAliquotIGSN(aliquotIGSN);
        // rename supersample aliquot also
        Vector<Aliquot> aliquots = project.getCompiledSuperSample().getAliquots();
        aliquots.stream().forEach((aliquot) -> {
            String aliquotName = aliquot.getAliquotName();
            String aName = myAliquot.getAliquotName().trim();
            if (aliquotName.endsWith("::" + aName)) {
                aliquotName = aliquotName.replace("::" + aName, "::" + aliquotName_TextField.getText().trim());
                aliquot.setAliquotName(aliquotName);
            }
        });

        myAliquot.setAliquotName(aliquotName_TextField.getText().trim());
    }

    private JLabel labelFactory(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, ROW_HEIGHT);
        label.setFont(ReduxConstants.sansSerif_12_Bold);

        return label;
    }

    private void updateValidSampleDisplay(boolean valid) {
        checkMarkForValidSampleIGSN.setVisible(valid);
        xMarkForInValidSampleIGSN.setVisible(!valid);

        viewSampleRecordButton.setVisible(valid);
        registerNewSampleButton.setVisible(!valid);

//        saveButton.setVisible(valid);
    }

    private void updateValidAliquotDisplay(int index, boolean valid) {
        checkMarkForValidAliquotIGSNs[index].setVisible(valid);
        xMarkForInValidAliqutIGSNs[index].setVisible(!valid);

        viewAliquotRecordButtons[index].setVisible(valid);
        registerNewAliquotButtons[index].setVisible(!valid);
//
//        saveButton.setVisible(valid);
        //uploadButton.setVisible(valid);
    }

    private class SampleIGSNVerifier extends InputVerifier {

        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String proposedIGSN = textField.getText().toUpperCase();
            textField.setText(proposedIGSN);
            if (SesarSample.validateSampleIGSNatSESAR(proposedIGSN)) {
                sampleIGSN = proposedIGSN.trim().toUpperCase();
                saveSample();
                updateValidSampleDisplay(true);
            } else {
                if (userCode.trim().length() == 0) {
                    xMarkForInValidSampleIGSN.setToolTipText("Please validate credentials above.");
                } else {
                    xMarkForInValidSampleIGSN.setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                }

                sampleIGSN = "IGSN";
                updateValidSampleDisplay(false);
            }
            return true;
        }
    }

    private class AliquotIGSNVerifier extends InputVerifier {

        private int index;

        public AliquotIGSNVerifier(int index) {
            this.index = index;
        }

        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String proposedIGSN = textField.getText().toUpperCase();
            textField.setText(proposedIGSN);
            if (SesarSample.validateAliquotIGSNatSESAR(proposedIGSN, sample.getSampleIGSN())) {
                aliquotIGSNs[index] = proposedIGSN.trim().toUpperCase();
                saveAliquot(activeAliquots.get(index), aliquotIGSN_TextFields[index].getText(), aliquotName_TextFields[index]);
                updateValidAliquotDisplay(index, true);
            } else {
                if (userCode.trim().length() == 0) {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText("Please validate credentials above.");
                } else if (!proposedIGSN.toUpperCase().startsWith(userCode.toUpperCase())) {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText(proposedIGSN + " uses incorrect User Code.  Your User Code is: " + userCode);
                } else {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                }

                aliquotIGSNs[index] = "IGSN";
                updateValidAliquotDisplay(index, false);
            }
            return true;
        }
    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // bottom border
        g2d.setColor(Color.black);
        // g2d.drawRect(0, getBounds().height - 1, getWidth() - 1, getBounds().height - 1);
        g2d.drawRect(1, 1, getBounds().width - 2, getBounds().height - 2);
    }
}

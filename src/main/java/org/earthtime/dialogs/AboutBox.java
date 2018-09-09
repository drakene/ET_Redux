/*
 * AboutBox.java
 *
 * Created on April 8, 2006, 2:19 PM
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
package org.earthtime.dialogs;

import javax.swing.ImageIcon;
import org.earthtime.ETRedux;
import org.earthtime.UPb_Redux.utilities.CustomIcon;

/**
 *
 * @author James F. Bowring
 */
public class AboutBox extends DialogEditor {

    private final ClassLoader cldr = this.getClass().getClassLoader();
    private final java.net.URL imageEarthTimeURL = cldr.getResource("org/earthtime/images/earth_96.jpg");
    private final java.net.URL imageReduxURL = cldr.getResource("org/earthtime/images/uth-pb-redux-logo.png");
    private final ImageIcon myEarthTimeIcon;
    private final ImageIcon myReduxIcon;

    /**
     * Creates new form AboutBox
     *
     * @param parent
     * @param modal
     */
    public AboutBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        myEarthTimeIcon = new ImageIcon(imageEarthTimeURL);
        earthtimeIcon_label.setIcon(myEarthTimeIcon);

        myReduxIcon = new CustomIcon(imageReduxURL);
        ((CustomIcon) myReduxIcon).setSize(redux_Icon_label.getWidth(), redux_Icon_label.getHeight());
        redux_Icon_label.setIcon(myReduxIcon);

        version_text.setText("ET_Redux " + ETRedux.VERSION);

        releaseDate_text.setText(ETRedux.RELEASE_DATE);

        setSizeAndCenter(450, 670);
        // setSize( 450, 670 );

        // removed feb 2014 to support linux
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>
    }

//    /**
//     * 
//     * @param width
//     * @param height
//     */
//    @Override
//    public void setSize ( int width, int height ) {
//        super.setSize( width, height );
//
//        //Get the screen size
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        Dimension screenSize = toolkit.getScreenSize();
//
//        //Calculate the frame location
//        int x = (screenSize.width - getWidth()) / 2;
//        int y = (screenSize.height - getHeight()) / 2;
//
//        //Set the new frame location
//        setLocation( x, y );
//    }
//
//    /**
//     * 
//     * @param size
//     */
//    @Override
//    public void setSize ( Dimension size ) {
//        setSize( size.width, size.height );
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        earthtimeIcon_label = new javax.swing.JLabel();
        redux_Icon_label = new javax.swing.JLabel();
        version_text = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        releaseDate_text = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(earthtimeIcon_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 20, 102, 94));
        jPanel1.add(redux_Icon_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(253, 20, 156, 100));

        version_text.setFont(new java.awt.Font("Helvetica", 3, 14)); // NOI18N
        version_text.setForeground(new java.awt.Color(255, 255, 255));
        version_text.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        version_text.setText("<html>ET_Redux  0.0.0</html>");
        version_text.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(version_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 120, 173, 24));

        jLabel4.setFont(new java.awt.Font("Helvetica", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("<html> Copyright 2006-2017 James F. Bowring and www.Earth-Time.org<br> <br>    Licensed under the Apache License, Version 2.0 (the \"License\");<br>    you may not use this file except in compliance with the License.<br>    You may obtain a copy of the License at<br> <br>        <a href=\"http://www.apache.org/licenses/LICENSE-2.0\" http://www.apache.org/licenses/LICENSE-2.0 </a><br> <br>    Unless required by applicable law or agreed to in writing, software<br>    distributed under the License is distributed on an \"AS IS\" BASIS,<br>    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,<br>    either express or implied.<br> <br>    See the License for the specific language governing permissions and<br>    limitations under the License. </html>");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 208, 415, 225));

        releaseDate_text.setFont(new java.awt.Font("Helvetica", 3, 14)); // NOI18N
        releaseDate_text.setForeground(new java.awt.Color(255, 255, 255));
        releaseDate_text.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        releaseDate_text.setText("<html>release date</html>");
        releaseDate_text.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(releaseDate_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 150, 173, 24));

        jLabel1.setBackground(new java.awt.Color(204, 255, 204));
        jLabel1.setFont(new java.awt.Font("Helvetica", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("<html>Funding: <br><br>Development of ET_Redux is currently supported by NSF Award #0930223: Collaborative Research: Analytical Techniques and Software: Development of Cyber Infrastructure to Support Laser-Ablation ICP Mass Spectrometry  <br><br>Development is also supported by the College of Charleston Department of Computer Science through its Cyber Infrastructure Research and Development Lab for the Earth Sciences (CIRDLES).</html>");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(41, 439, 359, -1));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new AboutBox(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel earthtimeIcon_label;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel redux_Icon_label;
    private javax.swing.JLabel releaseDate_text;
    private javax.swing.JLabel version_text;
    // End of variables declaration//GEN-END:variables
}

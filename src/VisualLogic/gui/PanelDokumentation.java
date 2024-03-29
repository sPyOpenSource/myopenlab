/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package VisualLogic.gui;

import VisualLogic.Element;
import VisualLogic.MyImage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 *
 * @author Homer
 */
public class PanelDokumentation extends javax.swing.JPanel {

    String mainPath;

    public MyImage doc_image = new MyImage();

    JTabbedPane tabPane;
    String nope;
    public String selectedLanguage = "de";
    private FrameMain owner;
    private Element element = null;

    /**
     * Creates new form PanelDokumentation
     */
    public PanelDokumentation() {
        initComponents();

        jPanel3.add(doc_image, BorderLayout.CENTER);

        jEditorPane1.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                // Das aendern des Mauszeigers geht ab 
                // Java 1.3 auch automatisch 
                if (e.getEventType()
                        == HyperlinkEvent.EventType.ENTERED) {
                    ((JEditorPane) e.getSource()).setCursor(
                            Cursor.getPredefinedCursor(
                                    Cursor.HAND_CURSOR));
                } else if (e.getEventType()
                        == HyperlinkEvent.EventType.EXITED) {
                    ((JEditorPane) e.getSource()).setCursor(
                            Cursor.getPredefinedCursor(
                                    Cursor.DEFAULT_CURSOR));
                } else // Hier wird auf ein Klick reagiert
                 if (e.getEventType()
                            == HyperlinkEvent.EventType.ACTIVATED) {
                        JEditorPane pane = (JEditorPane) e.getSource();
                        if (e instanceof HTMLFrameHyperlinkEvent) {
                            HTMLFrameHyperlinkEvent evt
                                    = (HTMLFrameHyperlinkEvent) e;
                            HTMLDocument doc
                                    = (HTMLDocument) pane.getDocument();
                            doc.processHTMLFrameHyperlinkEvent(evt);
                        } else {
                            try {
                                // Normaler Link
                                pane.setPage(e.getURL());
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButtonDE = new javax.swing.JButton();
        jButtonEN = new javax.swing.JButton();
        jButtonES = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setFloatable(false);
        jToolBar1.setOpaque(false);

        jButtonDE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/Bilder/de.png"))); // NOI18N
        jButtonDE.setMargin(new java.awt.Insets(3, 4, 3, 4));
        jButtonDE.setOpaque(false);
        jButtonDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDEActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDE);

        jButtonEN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/Bilder/us.png"))); // NOI18N
        jButtonEN.setMargin(new java.awt.Insets(3, 4, 3, 4));
        jButtonEN.setOpaque(false);
        jButtonEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonENActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEN);

        jButtonES.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/Bilder/es.png"))); // NOI18N
        jButtonES.setMargin(new java.awt.Insets(3, 4, 3, 4));
        jButtonES.setOpaque(false);
        jButtonES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonESActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonES);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("???");
        jLabel1.setAutoscrolls(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(15, 20));
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setEnabled(false);
        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setEnabled(false);

        jEditorPane1.setEditable(false);
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jEditorPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jScrollPane1.setViewportView(jEditorPane1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void setSelectedLanguage(String language) {

        if (language.length() == 0) {

            String strLocale = Locale.getDefault().toString();

            if (strLocale.equalsIgnoreCase("de_DE")) {
                language = "de";
            }
            if (strLocale.equalsIgnoreCase("en_US")) {
                language = "en";
            }
            if (strLocale.equalsIgnoreCase("es_ES")) {
                language = "es";
            }
        }
        selectedLanguage = language;

    }


    private void jButtonESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonESActionPerformed
    {//GEN-HEADEREND:event_jButtonESActionPerformed
        try {
            processButtons();
            selectedLanguage = "es";

            openElementDocFile(this.owner, this.element);
        } catch (Exception ex) {
            Logger.getLogger(PanelDokumentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonESActionPerformed

    private void jButtonENActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonENActionPerformed
    {//GEN-HEADEREND:event_jButtonENActionPerformed
        try {
            processButtons();
            selectedLanguage = "en";

            openElementDocFile(this.owner, this.element);
        } catch (Exception ex) {
            Logger.getLogger(PanelDokumentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonENActionPerformed

    private void processButtons() {

        jButtonDE.setSelected(false);
        jButtonEN.setSelected(false);
        jButtonES.setSelected(false);

        if (jButtonDE.isSelected()) {
            jButtonEN.setSelected(true);
            jButtonES.setSelected(true);
        }
        if (jButtonEN.isSelected()) {
            jButtonDE.setSelected(true);
            jButtonES.setSelected(true);
        }
        if (jButtonES.isSelected()) {
            jButtonDE.setSelected(true);
            jButtonEN.setSelected(true);
        }
    }


    private void jButtonDEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonDEActionPerformed
    {//GEN-HEADEREND:event_jButtonDEActionPerformed

        try {
            processButtons();
            selectedLanguage = "de";

            openElementDocFile(this.owner, this.element);
        } catch (Exception ex) {
            Logger.getLogger(PanelDokumentation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonDEActionPerformed

    JPanel docPanel = new JPanel();

    private void loadDoc(String filename, JEditorPane pane) {
        URL url = null;
        if (!new File(filename).exists()) {
            filename = nope;
        }

        try {
            url = new URL("file:" + filename);
        } catch (Exception ex) {

        }

        try {
            pane.setContentType("text/html");
            pane.setPage(url);
        } catch (Exception e) {

        }

    }

    public void openElementDocFile(FrameMain owner, Element element) throws Exception {

        // Delete Inhalt
        try {
            jEditorPane1.setText("");
            jEditorPane1.setContentType("text");

            Image img = new BufferedImage(10, 10, TYPE_INT_RGB);
            img.getGraphics().setColor(Color.WHITE);
            img.getGraphics().fillRect(0, 0, 100, 100);
            
            doc_image.setImage(img);

            jLabel1.setText("???");

        } catch (Exception e) {

        }

        if (owner == null || element == null) {
            return;
        }

        String docFileName = "";

        this.owner = owner;
        this.element = element;

        nope = element.elementPath + "/nope.html";

        mainPath = element.docPath;

        jButtonDE.setSelected(false);
        jButtonEN.setSelected(false);
        jButtonES.setSelected(false);

        docFileName = mainPath + "doc.html";

        if (selectedLanguage.equalsIgnoreCase("de")) {
            docFileName = mainPath + "doc.html";
            jButtonDE.setSelected(true);
        }
        if (selectedLanguage.equalsIgnoreCase("en")) {
            docFileName = mainPath + "doc_en.html";
            jButtonEN.setSelected(true);
        }
        if (selectedLanguage.equalsIgnoreCase("es")) {
            docFileName = mainPath + "doc_es.html";
            jButtonES.setSelected(true);
        }

        if (new File(docFileName).exists()) {

            loadDoc(docFileName, jEditorPane1);
        } else {

            if (selectedLanguage.equalsIgnoreCase("de")) {
                docFileName = mainPath + "doc_de/index.html";
                jButtonDE.setSelected(true);
            }
            if (selectedLanguage.equalsIgnoreCase("en")) {
                docFileName = mainPath + "doc_en/index.html";
                jButtonEN.setSelected(true);
            }
            if (selectedLanguage.equalsIgnoreCase("es")) {
                docFileName = mainPath + "doc_es/index.html";
                jButtonES.setSelected(true);
            }

            loadDoc(docFileName, jEditorPane1);

        }

        Image img;

        if (element.owner.equals(owner.getActualBasis().getCircuitBasis())) {
            img = element.getImage();
        } else {
            img = owner.createImageOfElement(element);
        }
        
        doc_image.setImage(img);

        String caption = "";
        String strLocale = selectedLanguage;
        if (strLocale.equalsIgnoreCase("de")) {
            caption = element.definition_def.captionDE;
        }
        if (strLocale.equalsIgnoreCase("en")) {
            caption = element.definition_def.captionEN;
        }
        if (strLocale.equalsIgnoreCase("es")) {
            caption = element.definition_def.captionES;
        }

        jLabel1.setText(caption);
        jPanel1.setPreferredSize(new Dimension(10, doc_image.getHeight() + 25));

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDE;
    private javax.swing.JButton jButtonEN;
    private javax.swing.JButton jButtonES;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}

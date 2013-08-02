package net.saucefactory.swing.common;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.StringReader;

public class SFHTMLPreviewFrame extends JDialog implements ActionListener {
  BorderLayout lytMain = new BorderLayout();
  JEditorPane editorPane;
  JButton printButton = new JButton();
  JButton cancelButton = new JButton();
  JPanel commandPanel = new JPanel();
  JScrollPane scrCenter = new JScrollPane();
  FlowLayout commandLayout = new FlowLayout(FlowLayout.RIGHT, 5, 5);
  private boolean print = false;

  public SFHTMLPreviewFrame(Frame parent, JEditorPane editorPane, boolean modal) {
    super(parent, modal);
    this.editorPane = editorPane;
    try {
      jbInit();
      registerHandlers();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean showPreview(Frame parent, JEditorPane editorPane, int x, int y, int width, int height) {
    SFHTMLPreviewFrame dialog = new SFHTMLPreviewFrame(parent, editorPane, true);
    dialog.setSize(new Dimension(width, height));
    dialog.setLocation(x, y);
    dialog.show();
    return dialog.print;
  }

  public static JDialog showHiddenPreview(Frame parent, JEditorPane editorPane, int x, int y, int width, int height) {
    SFHTMLPreviewFrame dialog = new SFHTMLPreviewFrame(parent, editorPane, false);
    dialog.setSize(new Dimension(width, height));
    dialog.setLocation(x, y);
    dialog.show();
    return dialog;
  }

  private void setHTML(String html) {
    try {
      editorPane.getEditorKit().read(new StringReader(html), editorPane.getDocument(), 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void registerHandlers() {
    cancelButton.addActionListener(this);
    printButton.addActionListener(this);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(lytMain);
    setResizable(true);
    setTitle("Print Preview");
    getContentPane().add(scrCenter, BorderLayout.CENTER);
    scrCenter.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrCenter.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrCenter.getViewport().add(editorPane);
    printButton.setText("Print");
    printButton.setMinimumSize(new Dimension(120, 25));
    printButton.setPreferredSize(new Dimension(120, 25));
    printButton.setActionCommand("PRINT");
    cancelButton.setText("Close");
    cancelButton.setMinimumSize(new Dimension(120, 25));
    cancelButton.setPreferredSize(new Dimension(120, 25));
    cancelButton.setActionCommand("CLOSE");
    commandPanel.setLayout(commandLayout);
    commandPanel.setBorder(BorderFactory.createEtchedBorder());
    commandPanel.add(cancelButton);
    commandPanel.add(printButton);
    getContentPane().add(commandPanel, BorderLayout.SOUTH);
  }

  public void actionPerformed(ActionEvent ae) {
    String command = ae.getActionCommand();
    if(command.equals("PRINT"))
      allowPrint();
    else if(command.equals("CLOSE"))
      closeMe();
  }

  public void closeMe() {
    setVisible(false);
    dispose();
  }

  private void allowPrint() {
    print = true;
    closeMe();
  }
}

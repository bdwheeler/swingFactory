package net.saucefactory.swing.diff;

import net.saucefactory.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import net.saucefactory.text.diff.*;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class WordDiffViewer extends JPanel {
  public GridBagLayout gridBagLayout1 = new GridBagLayout();
  public JScrollPane scrDoc1 = new JScrollPane();
  public JScrollPane scrDoc2 = new JScrollPane();
  public JTextPane listDoc1 = new MyTextPane();
  public JTextPane listDoc2 = new MyTextPane();
  public JLabel lblDoc1 = new JLabel();
  public JLabel lblDoc2 = new JLabel();
  public JPopupMenu popupMenu = new JPopupMenu();
  public JMenuItem copyMenuItem = new JMenuItem("Copy");
  public JPanel pnlLegend = new JPanel();
  public JLabel lblUnchanged = new JLabel();
  public JLabel lblAdded = new JLabel();
  public JButton btnClose = new JButton();
  public GridBagLayout gridBagLayout2 = new GridBagLayout();
  public JLabel lblChanged = new JLabel();
  public JLabel lblDeleted = new JLabel();
  public JLabel lblLineLength = new JLabel();
  public JComboBox cboLineSize = new JComboBox();
  public JSplitPane splDocsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

  public WordDiffViewer() {
    super();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    lblChanged.setText("Changed");
    pnlLegend.setLayout(gridBagLayout2);
    btnClose.setText("Close");
    this.setSize(800,600);
    this.setLayout(gridBagLayout1);
    scrDoc2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrDoc2.getViewport().setBackground(Color.white);
    scrDoc2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrDoc1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrDoc1.getViewport().setBackground(Color.white);
    scrDoc1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    lblDoc1.setText("Doc 1 Label");
    lblDoc2.setText("Doc 2 Label");
    lblUnchanged.setText("Unchanged");
    lblAdded.setToolTipText("");
    lblAdded.setText("Added");
    lblDeleted.setToolTipText("");
    lblDeleted.setText("Deleted");
    lblLineLength.setText("Line Size:");
    cboLineSize.setMaximumSize(new Dimension(60, 21));
    cboLineSize.setMinimumSize(new Dimension(60, 21));
    cboLineSize.setPreferredSize(new Dimension(60, 21));
    splDocsPane.setContinuousLayout(true);
    splDocsPane.setOneTouchExpandable(true);
    splDocsPane.setResizeWeight(0.5);
    listDoc1.setEditable(false);
    listDoc2.setEditable(false);
    this.add(lblDoc1,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    this.add(lblDoc2,        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0));
    this.add(pnlLegend,     new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    popupMenu.add(copyMenuItem);
    pnlLegend.add(lblUnchanged,         new GridBagConstraints(2, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
    pnlLegend.add(lblAdded,        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(btnClose,        new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(lblChanged,       new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblDeleted,          new GridBagConstraints(5, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblLineLength,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(cboLineSize,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    this.add(splDocsPane,     new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//    this.getContentPane().add(scrDoc1,         new GridBagConstraints(0, 2, 1, 1, 0.5, 1.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    scrDoc1.getViewport().add(listDoc1, null);
//    this.getContentPane().add(scrDoc2,         new GridBagConstraints(1, 2, 1, 1, 0.5, 1.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
    scrDoc2.getViewport().add(listDoc2, null);
    splDocsPane.add(scrDoc1, JSplitPane.LEFT);
    splDocsPane.add(scrDoc2, JSplitPane.RIGHT);
  }

  // TextPane that does horizontal scrolling
  class MyTextPane extends JTextPane {
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }
  }

}



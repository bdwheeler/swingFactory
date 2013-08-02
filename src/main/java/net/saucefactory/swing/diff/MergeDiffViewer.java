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

public class MergeDiffViewer extends JPanel {
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  protected JScrollPane scrDoc1 = new JScrollPane();
  protected JTextPane listDoc1 = new JTextPane();
  protected JLabel lblDoc1 = new JLabel();
  protected JPopupMenu popupMenu = new JPopupMenu();
  protected JMenuItem copyMenuItem = new JMenuItem("Copy Original Text");
  protected JMenuItem copyMenuItem2 = new JMenuItem("Copy New Text");
  protected JMenuItem copyMenuItem3 = new JMenuItem("Copy All Text");
  protected JPanel pnlLegend = new JPanel();
  protected JLabel lblUnchanged = new JLabel();
  protected JLabel lblAdded = new JLabel();
  protected JButton btnClose = new JButton();
  protected GridBagLayout gridBagLayout2 = new GridBagLayout();
  protected JLabel lblDeleted = new JLabel();

  public MergeDiffViewer() {
    super();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    pnlLegend.setLayout(gridBagLayout2);
    btnClose.setText("Close");
    this.setLayout(gridBagLayout1);
    scrDoc1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrDoc1.getViewport().setBackground(Color.white);
    scrDoc1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    lblUnchanged.setText("Unchanged");
    lblAdded.setText("Added");
    lblDeleted.setToolTipText("");
    lblDeleted.setText("Deleted");
    listDoc1.setEditable(false);
    lblDoc1.setText("Doc 1");
    this.add(lblDoc1,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    this.add(pnlLegend,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    popupMenu.add(copyMenuItem2);
    popupMenu.add(copyMenuItem);
    popupMenu.add(copyMenuItem3);
    pnlLegend.add(lblUnchanged,             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));
    pnlLegend.add(lblAdded,          new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(btnClose,          new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(lblDeleted,            new GridBagConstraints(3, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    this.add(scrDoc1,        new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    scrDoc1.getViewport().add(listDoc1, null);
  }

}



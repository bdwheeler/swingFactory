package net.saucefactory.swing.diff;

import net.saucefactory.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import javax.swing.event.*;
import net.saucefactory.text.diff.*;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class LineDiffViewer extends JPanel {

  public GridBagLayout gridBagLayout1 = new GridBagLayout();
  public JScrollPane scrDoc1 = new JScrollPane();
  public JScrollPane scrDoc2 = new JScrollPane();
  public JList listDoc1 = new DiffItemList(1);
  public JList listDoc2 = new DiffItemList(2);
  public JLabel lblDoc1 = new JLabel();
  public JLabel lblDoc2 = new JLabel();
  public JPopupMenu popupMenu = new JPopupMenu();
  public JMenuItem copyMenuItem = new JMenuItem("Copy");
  public JMenuItem mergedMenuItem = new JMenuItem("Copy Merged");
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

  public LineDiffViewer() {
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
    scrDoc1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    listDoc1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    listDoc2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
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
    popupMenu.add(copyMenuItem);
    popupMenu.add(mergedMenuItem);
    this.add(lblDoc1, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 2, 5), 0, 0));
    this.add(lblDoc2, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 2, 5), 0, 0));
    this.add(pnlLegend, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlLegend.add(lblAdded, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(btnClose, new GridBagConstraints(6, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(lblChanged, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblUnchanged, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
    pnlLegend.add(lblDeleted, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblLineLength, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(cboLineSize, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    this.add(splDocsPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    scrDoc1.getViewport().add(listDoc1, null);
    scrDoc2.getViewport().add(listDoc2, null);
    splDocsPane.add(scrDoc1, JSplitPane.LEFT);
    splDocsPane.add(scrDoc2, JSplitPane.RIGHT);
    splDocsPane.setResizeWeight(.5d);

  }

  public void syncListSelection() {
    listDoc1.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        listDoc2.setSelectionInterval(e.getFirstIndex(), e.getLastIndex());
      }
    });

    listDoc2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        listDoc1.setSelectionInterval(e.getFirstIndex(), e.getLastIndex());
      }
    });
  }
}

// JList subclass that exports DiffItems to the clipboard correctly
class DiffItemList extends JList {
  int mode = 0;
  public DiffItemList(int _mode) {
    super();
    mode = _mode;
    setTransferHandler(new DiffItemTransferHandler());
  }

  public String getSelectedContent() {
    String rtn = null;
    if (!isSelectionEmpty()) {
      Object[] vals = getSelectedValues();
      StringBuffer buf =  new StringBuffer();
      for (int i = 0; i < vals.length; i++) {
        if (vals[i] instanceof DiffItem) {
          DiffItem line = (DiffItem)vals[i];
          if (line.currentLineNo > 0)
            buf.append(line.text + "\n");
        } else
          buf.append(vals[i].toString());
      }
      rtn = buf.toString();
    }
    return rtn;
  }

  public String getMergedSelectedContent() {
    String rtn = null;
    if (!isSelectionEmpty()) {
      Object[] vals = getSelectedValues();
      StringBuffer buf =  new StringBuffer();
      for (int i = 0; i < vals.length; i++) {
        if (vals[i] instanceof DiffItem) {
          DiffItem line = (DiffItem)vals[i];
          if (line.status == DiffItem.NO_CHANGE)
            buf.append(line.text + "\n");
          else if (line.status == DiffItem.ADDED) {
            buf.append((mode == 1 ? line.compText : line.text) + "\n");
          } else if (line.status == DiffItem.DELETED) {
            buf.append((mode == 2 ? line.compText : line.text) + "\n");
          } else
            buf.append(line.text + "\n" + line.compText + "\n");
        } else
          buf.append(vals[i].toString());
      }
      rtn = buf.toString();
    }
    return rtn;
  }

  public void transferSelectedContent() {
    if (!isSelectionEmpty()) {
      Object[] vals = getSelectedValues();
      for (int i = 0; i < vals.length; i++) {
        if (vals[i] instanceof DiffItem) {
          DiffItem line = (DiffItem)vals[i];
          if (line.status == DiffItem.ADDED) {
            if (mode == 1)
              line.text = line.compText;
            else
              line.compText = line.text;
          } else if (line.status == DiffItem.DELETED) {
            if (mode == 2)
              line.text = line.compText;
            else
              line.compText = line.text;
          }
        }
      }
    }
    return;
  }
}

class DiffItemTransferHandler extends TransferHandler {
  public static final int COPY_MERGED = -1;
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    DiffItemList list = (DiffItemList)comp;
    String content = null;
    if (action != COPY_MERGED)
      content = list.getSelectedContent();
    else
      content = list.getMergedSelectedContent();
    if (content != null) {
      StringSelection contents = new StringSelection(content);
      clip.setContents(contents, null);
    }
  }
  public int getSourceActions(JComponent c) {
    return COPY;
  }
}

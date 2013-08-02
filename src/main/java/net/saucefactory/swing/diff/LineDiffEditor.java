package net.saucefactory.swing.diff;

import net.saucefactory.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import net.saucefactory.text.diff.*;
import java.awt.event.ActionListener;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class LineDiffEditor extends JPanel {
  public GridBagLayout gridBagLayout1 = new GridBagLayout();
  public JScrollPane scrDoc1 = new JScrollPane();
  public JScrollPane scrDoc2 = new JScrollPane();
  public DiffItemList2 listDoc1 = new DiffItemList2(1);
  public DiffItemList2 listDoc2 = new DiffItemList2(2);
  public JLabel lblDoc1 = new JLabel();
  public JLabel lblDoc2 = new JLabel();
  public JPopupMenu popupMenu = new JPopupMenu();
  public JMenuItem copyMenuItem = new JMenuItem("Copy Text To Clipboard");
  public JMenuItem mergedMenuItem = new JMenuItem("Copy Merged Text To Clipboard");
  public JMenuItem transferMenuItem = new JMenuItem("Transfer");
  public JPanel pnlLegend = new JPanel();
  public JLabel lblUnchanged = new JLabel();
  public JLabel lblAdded = new JLabel();
  public JButton btnClose = new JButton();
  public JButton btnPrint = new JButton("Print");
  public GridBagLayout gridBagLayout2 = new GridBagLayout();
  public JLabel lblChanged = new JLabel();
  public JLabel lblDeleted = new JLabel();
  public JLabel lblLineLength = new JLabel();
  public JComboBox cboLineSize = new JComboBox();
  public JSplitPane splDocsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  public JButton btnSave = new JButton();

  public LineDiffEditor() {
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
    btnClose.setMinimumSize(new Dimension(100, 25));
    btnClose.setPreferredSize(new Dimension(100, 25));
    btnClose.setMargin(new Insets(2, 2, 2, 2));
    btnClose.setText("Close");
    btnPrint.setMinimumSize(new Dimension(100, 25));
    btnPrint.setPreferredSize(new Dimension(100, 25));
    btnPrint.setMargin(new Insets(2, 2, 2, 2));
    btnPrint.setText("Print");
    //this.setSize(400,200);
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
    btnSave.setEnabled(false);
    btnSave.setMinimumSize(new Dimension(100, 25));
    btnSave.setPreferredSize(new Dimension(100, 25));
    btnSave.setMargin(new Insets(2, 2, 2, 2));
    btnSave.setText("Save");
    popupMenu.add(copyMenuItem);
    popupMenu.add(mergedMenuItem);
    popupMenu.add(transferMenuItem);
    this.add(lblDoc1, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 3, 5), 0, 0));
    this.add(lblDoc2, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 3, 5), 0, 0));
    this.add(pnlLegend, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlLegend.add(lblAdded, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(btnClose, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    pnlLegend.add(lblChanged, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblUnchanged, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
    pnlLegend.add(lblDeleted, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    pnlLegend.add(lblLineLength, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    pnlLegend.add(cboLineSize, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    pnlLegend.add(btnSave, new GridBagConstraints(6, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    this.add(splDocsPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    scrDoc1.getViewport().add(listDoc1, null);
    scrDoc2.getViewport().add(listDoc2, null);
    splDocsPane.add(scrDoc1, JSplitPane.LEFT);
    splDocsPane.add(scrDoc2, JSplitPane.RIGHT);
    splDocsPane.setResizeWeight(.5d);
  }

  public void installPrintButton(ActionListener listener, String buttonLabel) {
    pnlLegend.add(btnPrint, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    btnPrint.setActionCommand("PRINT");
    btnPrint.setText(buttonLabel);
    btnPrint.addActionListener(listener);
  }

  public void addLegendButton(JButton newButton) {
    pnlLegend.add(newButton, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));

  }

  private boolean adjusting = false;

  public void syncListSelection() {
    listDoc1.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(adjusting)
          return;
        adjusting = true;
        listDoc2.setSelectedIndices(listDoc1.getSelectedIndices());
        adjusting = false;
      }
    });

    listDoc2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(adjusting)
          return;
        adjusting = true;
        listDoc1.setSelectedIndices(listDoc2.getSelectedIndices());
        adjusting = false;
      }
    });
  }
}

// JList subclass that exports DiffItems to the clipboard correctly
class DiffItemList2 extends JList {
  int mode = 0;
  public DiffItemList2(int _mode) {
    super();
    mode = _mode;
    setTransferHandler(new DiffItemTransferHandler2());
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

  public String getAllContent() {
    String rtn = null;
    int size = this.getModel().getSize();
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < size; i++) {
      Object item = this.getModel().getElementAt(i);
      if (item instanceof DiffItem) {
        DiffItem line = (DiffItem) item;
        if (mode == 1
            && line.status != DiffItem.ADDED)
          buf.append(line.text + "\n");
        else if (mode == 2
            && line.status != DiffItem.DELETED)
          buf.append(line.text + "\n");

      }
      else
        buf.append(item.toString());
    }
    rtn = buf.toString();
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

  public void transferContent(boolean fromThis, int start, int end) {
    this.setSelectionInterval(start, end);
    Object[] vals = getSelectedValues();
    for (int i = 0; i < vals.length; i++) {
      if (vals[i] instanceof DiffItem) {
        DiffItem line = (DiffItem)vals[i];
        if (fromThis) {
          line.compText = line.text;
          line.compTextNoDelimiter = line.textNoDelimiter;
        } else {
          line.text = line.compText;
          line.textNoDelimiter = line.compTextNoDelimiter;
        }
        line.status = DiffItem.NO_CHANGE;
      }
    }
    return;
  }
}

class DiffItemTransferHandler2 extends TransferHandler {
  public static final int COPY_MERGED = -1;
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    DiffItemList2 list = (DiffItemList2)comp;
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

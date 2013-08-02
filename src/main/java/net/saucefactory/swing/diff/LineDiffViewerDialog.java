package net.saucefactory.swing.diff;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.datatransfer.*;
import java.util.*;

import net.saucefactory.text.diff.*;
import net.saucefactory.swing.*;
import net.saucefactory.swing.event.*;
/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class LineDiffViewerDialog extends SFDialog {
  protected String text1 = null;
  protected String text2 = null;
  protected Diff diff = null;
  protected int lineLength = 0;
  protected LineDiffViewer lineDiffViewer = new LineDiffViewer();
  DiffLineRenderer rend1 = new DiffLineRenderer();
  DiffLineRenderer rend2 = new DiffLineRenderer();

  public LineDiffViewerDialog(Frame owner, String text1_, String text2_, int lineLength_) throws Exception {
    super(owner);
    try {
      owner.setCursor(Cursor.WAIT_CURSOR);
      init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
      setText();
    } finally {
      owner.setCursor(Cursor.DEFAULT_CURSOR);
    }
  }

  public LineDiffViewerDialog(Dialog owner, String text1_, String text2_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
    setText();
  }

  public LineDiffViewerDialog(String text1_, String text2_, int lineLength_) throws Exception {
    super();
    init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
    setText();
  }

  public LineDiffViewerDialog(Frame owner, String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super(owner);
    try {
      owner.setCursor(Cursor.WAIT_CURSOR);
      init(text1_, text1Label_, text2_, text2Label_, lineLength_);
      setText();
    }
    finally {
      owner.setCursor(Cursor.DEFAULT_CURSOR);
    }
  }

  public LineDiffViewerDialog(Dialog owner, String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, text1Label_, text2_, text2Label_, lineLength_);
    setText();
  }

  public LineDiffViewerDialog(String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super();
    init(text1_, text1Label_, text2_, text2Label_, lineLength_);
    setText();
  }

  protected void setComboData() {
    TreeSet set = new TreeSet();
    set.add(new Integer(0));
    Integer def = new Integer(lineLength);
    set.add(def);
    for (int i = 5; i < 55; i += 5) {
      if ((lineLength - i) > 15)
        set.add(new Integer(lineLength-i));
      set.add(new Integer(lineLength+i));
    }
    Integer[] data = new Integer[set.size()];
    data = (Integer[])set.toArray(data);
    lineDiffViewer.cboLineSize.setModel(new DefaultComboBoxModel(data));
    lineDiffViewer.cboLineSize.setSelectedItem(def);
  }

  protected void init(String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) {
    this.setModal(true);
    this.setSize(800,600);
    this.getContentPane().add(lineDiffViewer, null);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    lineLength = lineLength_;
    text1 = text1_;
    text2 = text2_;
    registerHandlers();
    lineDiffViewer.listDoc1.setCellRenderer(rend1);
    lineDiffViewer.listDoc2.setCellRenderer(rend2);
    lineDiffViewer.lblAdded.setForeground(rend1.getAddedForeground());
    lineDiffViewer.lblChanged.setForeground(rend1.getChangedForeground());
    lineDiffViewer.lblDeleted.setForeground(rend1.getDeletedForeground());
    setComboData();
    setLabelText(text1Label_, text2Label_);
  }


  public void setLineSizeSelectionEnabled(boolean enable) {
    this.lineDiffViewer.cboLineSize.setVisible(enable);
    this.lineDiffViewer.lblLineLength.setVisible(enable);
  }

  protected void setText() throws Exception {
    diff = getDiff(text1, text2);
    lineDiffViewer.listDoc1.setListData(diff.getBaseDiffItemsArray());
    lineDiffViewer.listDoc2.setListData(diff.getComparisonDiffItemsArray());
  }

  protected void setLabelText(String doc1Label, String doc2Label) {
    lineDiffViewer.lblDoc1.setText(doc1Label);
    lineDiffViewer.lblDoc2.setText(doc2Label);
  }

  protected void showCopyPopup(MouseEvent e) {
    JList list = (JList)e.getSource();
    if (SwingUtilities.isRightMouseButton(e)
        && !list.isSelectionEmpty()) {
      lineDiffViewer.copyMenuItem.setActionCommand(list.equals(lineDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      lineDiffViewer.mergedMenuItem.setActionCommand(list.equals(lineDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      lineDiffViewer.popupMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
    }
  }

  protected void registerHandlers() {
    lineDiffViewer.listDoc1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showCopyPopup(e);
      }
    });

    lineDiffViewer.listDoc2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showCopyPopup(e);
      }
    });

/*
    lineDiffViewer.listDoc1.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          lineDiffViewer.listDoc2.setSelectionInterval(e.getFirstIndex(),
              e.getLastIndex());
        }
      }
    });

    lineDiffViewer.listDoc2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {

        if (!e.getValueIsAdjusting()) {
          lineDiffViewer.listDoc1.setSelectionInterval(e.getFirstIndex(),
              e.getLastIndex());
        }
      }
    });
*/
    lineDiffViewer.cboLineSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          lineLength = ((Integer)lineDiffViewer.cboLineSize.getSelectedItem()).intValue();
          setText();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    lineDiffViewer.copyMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        JList list = cmd.equals("DOC1") ? lineDiffViewer.listDoc1 : lineDiffViewer.listDoc2;
        TransferHandler handler = list.getTransferHandler();
        handler.exportToClipboard(list, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY);
      }
    });

    lineDiffViewer.mergedMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        JList list = cmd.equals("DOC1") ? lineDiffViewer.listDoc1 : lineDiffViewer.listDoc2;
        DiffItemTransferHandler handler = (DiffItemTransferHandler)list.getTransferHandler();
        handler.exportToClipboard(list, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY_MERGED);
      }
    });

    lineDiffViewer.btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        dispose();
      }
    });

    // only one list at a time can be selected
    lineDiffViewer.listDoc1.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        if (!lineDiffViewer.listDoc2.isSelectionEmpty())
          lineDiffViewer.listDoc2.removeSelectionInterval(lineDiffViewer.listDoc2.getSelectionModel().getMinSelectionIndex(), lineDiffViewer.listDoc2.getSelectionModel().getMaxSelectionIndex());
      }

      public void focusLost(FocusEvent e) {}
    });

    // only one list at a time can be selected
    lineDiffViewer.listDoc2.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        if (!lineDiffViewer.listDoc1.isSelectionEmpty())
          lineDiffViewer.listDoc1.removeSelectionInterval(lineDiffViewer.listDoc1.getSelectionModel().getMinSelectionIndex(), lineDiffViewer.listDoc1.getSelectionModel().getMaxSelectionIndex());
      }

      public void focusLost(FocusEvent e) {}
    });

    // only one scrollbar for both lists
    lineDiffViewer.scrDoc2.getVerticalScrollBar().getModel().addChangeListener(new ScrollSynchronizer(lineDiffViewer.scrDoc1.getVerticalScrollBar()));
    lineDiffViewer.scrDoc2.getHorizontalScrollBar().getModel().addChangeListener(new ScrollSynchronizer(lineDiffViewer.scrDoc1.getHorizontalScrollBar()));
    lineDiffViewer.scrDoc1.getHorizontalScrollBar().getModel().addChangeListener(new ScrollSynchronizer(lineDiffViewer.scrDoc2.getHorizontalScrollBar()));
  }

  protected Diff getDiff(String text1_, String text2_) throws Exception {
    String text = null, text2 = null;
    if (lineLength > 0) {
      text = DiffUtility.addLineBreaks(text1_, lineLength);
      text2 = DiffUtility.addLineBreaks(text2_, lineLength);
    } else {
      text = text1_;
      text2 = text2_;
    }
    return DiffUtility.compareLines(text, text2);
  }

  public static void display(String text1, String text2) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1, text2, 0);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, 0);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, 0);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1_, text1Label, text2_, text2Label, 0);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 0);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 0);
    dialog.show();
  }

  public static void display(String text1, String text2, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1, text2, lineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, lineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, lineLength);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  public static void display(String text1, String text2, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1, text2, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1, text2, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(text1_, text1Label, text2_, text2Label, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label, boolean enableLineLength) throws Exception {
    LineDiffViewerDialog dialog = new LineDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 0);
    dialog.setLineSizeSelectionEnabled(enableLineLength);
    dialog.show();
  }

  public static void main(String[] args) throws HeadlessException {
    try {

      LineDiffViewerDialog.display("01/04/2001\n" +
                                 "deleted this\n" +
                                 "Torsten Welches\n" +
                                 "torsten.welches@netcologne.de\n" +
                                 "before change\n"
                                 , "01/04/2001\n" +
                                 "Torsten Welches\n" +
                                 "added this\n" +
                                 "torsten.welches@netcologne.de\n" +
                                 "after change\n");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
  public java.awt.Color getAddedForegroundColor() {
    return rend1.getAddedForeground();
  }
  public void setAddedForegroundColor(java.awt.Color addedForegroundColor) {
    rend1.setAddedForeground(addedForegroundColor);
    rend2.setAddedForeground(addedForegroundColor);
  }
  public java.awt.Color getChangedForegroundColor() {
    return rend1.getChangedForeground();
  }
  public void setChangedForegroundColor(java.awt.Color changedForegroundColor) {
    rend1.setChangedForeground(changedForegroundColor);
    rend2.setChangedForeground(changedForegroundColor);
  }
  public java.awt.Color getDeletedForegroundColor() {
    return rend1.getDeletedForeground();
  }
  public void setDeletedForegroundColor(java.awt.Color deletedForegroundColor) {
    rend1.setDeletedForeground(deletedForegroundColor);
    rend2.setDeletedForeground(deletedForegroundColor);
  }
  public java.awt.Color getAddedBackgroundColor() {
    return rend1.getAddedBackground();
  }
  public void setAddedBackgroundColor(java.awt.Color addedBackgroundColor) {
    rend1.setAddedBackground(addedBackgroundColor);
    rend2.setAddedBackground(addedBackgroundColor);
  }
  public java.awt.Color getChangedBackgroundColor() {
    return rend1.getChangedBackground();
  }
  public void setChangedBackgroundColor(java.awt.Color changeBackgroundColor) {
    rend1.setChangedBackground(changeBackgroundColor);
    rend2.setChangedBackground(changeBackgroundColor);
  }
  public java.awt.Color getDeletedBackgroundColor() {
    return rend1.getDeletedBackground();
  }
  public void setDeletedBackgroundColor(java.awt.Color deletedBackgroundColor) {
    rend1.setDeletedBackground(deletedBackgroundColor);
    rend2.setDeletedBackground(deletedBackgroundColor);
  }
  public Color getUnchangedForgroundColor() {
    return lineDiffViewer.listDoc1.getForeground();
  }
  public void setUnchangedForgroundColor(Color unchangedForgroundColor) {
    lineDiffViewer.listDoc1.setForeground(unchangedForgroundColor);
    lineDiffViewer.listDoc2.setForeground(unchangedForgroundColor);
  }
  public Color getUnchangedBackgroundColor() {
    return lineDiffViewer.listDoc1.getBackground();
  }
  public void setUnchangedBackgroundColor(Color unchangedBackgroundColor) {
    lineDiffViewer.listDoc1.setBackground(unchangedBackgroundColor);
    lineDiffViewer.listDoc2.setBackground(unchangedBackgroundColor);
  }
  public java.awt.Color getNumberForegroundColor() {
    return rend1.getNumberForeground();
  }
  public void setNumberForegroundColor(java.awt.Color color) {
    rend1.setNumberForeground(color);
    rend2.setNumberForeground(color);
  }
  public LineDiffViewer getLineDiffViewer() {
    return lineDiffViewer;
  }
}


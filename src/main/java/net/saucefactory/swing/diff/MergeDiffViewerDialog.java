package net.saucefactory.swing.diff;

import net.saucefactory.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.datatransfer.*;
import java.util.*;

import net.saucefactory.text.diff.*;
//import net.saucefactory.swing.diff.renderers.*;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class MergeDiffViewerDialog extends SFDialog {
  protected String text1 = null;
  protected String text2 = null;
  protected Diff diff = null;
  public static Color addedForeground = Color.green.darker();
  public static Color deletedForeground = Color.red;
  public static Color unchangedForeground = Color.black;
  public static Color modifiedBackground = new Color(225, 225, 225);
  public static Color unmodifiedBackground = Color.white;
  protected SimpleAttributeSet addedAttSet = new SimpleAttributeSet();
  protected SimpleAttributeSet deletedAttSet = new SimpleAttributeSet();
  protected SimpleAttributeSet unchangedAttSet = new SimpleAttributeSet();
  public static final int NO_CHANGE = 0;
  public static final int ADDED = 1;
  public static final int CHANGED = 2;
  public static final int DELETED = 3;
  protected MergeDiffViewer mergeDiffViewer = new MergeDiffViewer();

  protected MergeDiffViewerDialog(Frame owner, String text1_, String text2_) throws Exception {
    super(owner);
    init(text1_, "Initial Text", text2_, "Comparison Text");
  }

  protected MergeDiffViewerDialog(Dialog owner, String text1_, String text2_) throws Exception {
    super(owner);
    init(text1_, "Initial Text", text2_, "Comparison Text");
  }

  protected MergeDiffViewerDialog(String text1_, String text2_) throws Exception {
    super();
    init(text1_, "Initial Text", text2_, "Comparison Text");
  }

  protected MergeDiffViewerDialog(Frame owner, String text1_, String text1Label_, String text2_, String text2Label_) throws Exception {
    super(owner);
    init(text1_, text1Label_, text2_, text2Label_);
  }

  protected MergeDiffViewerDialog(Dialog owner, String text1_, String text1Label_, String text2_, String text2Label_) throws Exception {
    super(owner);
    init(text1_, text1Label_, text2_, text2Label_);
  }

  protected MergeDiffViewerDialog(String text1_, String text1Label_, String text2_, String text2Label_) throws Exception {
    super();
    init(text1_, text1Label_, text2_, text2Label_);
  }

  protected void init(String text1_, String text1Label_, String text2_, String text2Label_) throws Exception {
    this.setTitle("Merged Difference Viewer");
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setModal(true);
    this.setSize(400,600);
    this.getContentPane().add(mergeDiffViewer, null);
    mergeDiffViewer.lblAdded.setForeground(addedForeground);
    mergeDiffViewer.lblDeleted.setForeground(deletedForeground);
    StyleConstants.setForeground(addedAttSet,addedForeground);
    StyleConstants.setBackground(addedAttSet,modifiedBackground);
    StyleConstants.setForeground(deletedAttSet,deletedForeground);
    StyleConstants.setBackground(deletedAttSet,modifiedBackground);
    StyleConstants.setStrikeThrough(deletedAttSet,true);
    StyleConstants.setForeground(unchangedAttSet,unchangedForeground);
    StyleConstants.setBackground(unchangedAttSet,unmodifiedBackground);
    text1 = text1_;
    text2 = text2_;
    setText();
    registerHandlers();
    setLabelText(text1Label_, text2Label_);
  }


  protected void setText() throws Exception {
    mergeDiffViewer.listDoc1.setFont(this.getFont());
    diff = getDiff(text1, text2);
    DiffItem[] baseDiffItems = diff.getBaseDiffItemsArray();
    StyledDocument baseDoc = new MergeDiffStyledDocument(baseDiffItems);

    mergeDiffViewer.listDoc1.setStyledDocument(baseDoc);
  }


  protected void setLabelText(String doc1Label, String doc2Label) {
    mergeDiffViewer.lblDoc1.setText(doc1Label + " merged with " + doc2Label);
  }

  protected void showCopyPopup(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      mergeDiffViewer.popupMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
    }
  }

  protected void registerHandlers() {
    MergeDiffTransferHandler handler = new MergeDiffTransferHandler();
    mergeDiffViewer.listDoc1.setTransferHandler(handler);

    mergeDiffViewer.listDoc1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (mergeDiffViewer.listDoc1.getSelectedText() == null
            || mergeDiffViewer.listDoc1.getSelectedText().length() == 0)
          return;
        showCopyPopup(e);
      }
    });

    mergeDiffViewer.copyMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          if (mergeDiffViewer.listDoc1.getSelectedText() == null
              || mergeDiffViewer.listDoc1.getSelectedText().length() == 0)
            return;
          MergeDiffTransferHandler handler = (MergeDiffTransferHandler)mergeDiffViewer.listDoc1.getTransferHandler();
          handler.exportBaseToClipboard(mergeDiffViewer.listDoc1, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    mergeDiffViewer.copyMenuItem2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          if (mergeDiffViewer.listDoc1.getSelectedText() == null
              || mergeDiffViewer.listDoc1.getSelectedText().length() == 0)
            return;
          MergeDiffTransferHandler handler = (MergeDiffTransferHandler)mergeDiffViewer.listDoc1.getTransferHandler();
          handler.exportCompToClipboard(mergeDiffViewer.listDoc1, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    mergeDiffViewer.copyMenuItem3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          if (mergeDiffViewer.listDoc1.getSelectedText() == null
              || mergeDiffViewer.listDoc1.getSelectedText().length() == 0)
            return;
          MergeDiffTransferHandler handler = (MergeDiffTransferHandler)mergeDiffViewer.listDoc1.getTransferHandler();
          handler.exportAllToClipboard(mergeDiffViewer.listDoc1, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    mergeDiffViewer.btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        dispose();
      }
    });

  }

  protected Diff getDiff(String text1_, String text2_) throws Exception {
    return DiffUtility.compare(text1_, text2_);
  }

  public static void display(String text1, String text2) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(text1, text2);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(owner, text1, text2);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(owner, text1, text2);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(text1_, text1Label, text2_, text2Label);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    MergeDiffViewerDialog dialog = new MergeDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label);
    dialog.show();
  }



  public static void main(String[] args) throws HeadlessException {
    try {
      MergeDiffViewerDialog.display("Creates and returns a stream tokenizer that has been properly configured to parse sphinx3 data This ExtendedStreamTokenizer has no comment characters"
                                   , "Creates poo and returns a stream tokenizerer that hasn't beeny properly configured to parse sphinx3 data really well This ExtendedStreamTokenizer has comment characters"
                                   );
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  class MergeDiffTransferHandler extends TransferHandler {
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
      exportCompToClipboard(comp, clip, action);
    }

    public void exportAllToClipboard(JComponent comp, Clipboard clip, int action) {
      try {
        String text = ((JTextPane)comp).getSelectedText();
        if (text != null) {
          StringSelection contents = new StringSelection(text);
          clip.setContents(contents, null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void exportBaseToClipboard(JComponent comp, Clipboard clip, int action) {
      try {
        String text = ((MergeDiffStyledDocument)((JTextPane)comp).getStyledDocument()).getBaseText(((JTextPane)comp).getSelectionStart(), ((JTextPane)comp).getSelectionEnd());
        if (text != null) {
          StringSelection contents = new StringSelection(text);
          clip.setContents(contents, null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void exportCompToClipboard(JComponent comp, Clipboard clip, int action) {
      try {
        String text = ((MergeDiffStyledDocument)((JTextPane)comp).getStyledDocument()).getCompText(((JTextPane)comp).getSelectionStart(), ((JTextPane)comp).getSelectionEnd());
        if (text != null) {
          StringSelection contents = new StringSelection(text);
          clip.setContents(contents, null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    public int getSourceActions(JComponent c) {
      return COPY;
    }
  }


  class MergeDiffStyledDocument extends DefaultStyledDocument {
    DiffItem[] words = null;
    int currentIndex = 0;
    HashMap baseAdditions = new HashMap();
    HashMap compAdditions = new HashMap();

    protected String getText(DiffItem word) {
      if (word.textNoDelimiter == null) {
        return word.text;
      } else {
        return word.textNoDelimiter;
      }
    }

    protected String getCompText(DiffItem word) {
      if (word.textNoDelimiter == null) {
        return word.compText;
      } else {
        return word.compTextNoDelimiter;
      }
    }

    public String getBaseText(int start, int end) throws BadLocationException {
      StringBuffer buf = new StringBuffer();
      for (int i = start; i <= end; i++) {
        Position pos = (Position)baseAdditions.get(new Integer(i));
        if (pos == null) {
          buf.append(super.getText(i, 1));
        }
      }
      return buf.toString();
    }

    public String getCompText(int start, int end) throws BadLocationException {
      StringBuffer buf = new StringBuffer();
      for (int i = start; i <= end; i++) {
        Position pos = (Position)compAdditions.get(new Integer(i));
        if (pos == null) {
          buf.append(super.getText(i, 1));
        }
      }
      return buf.toString();
    }

    protected void baseAddition(int currentIndex, String text) {
      for (int i = currentIndex; i < currentIndex + text.length(); i++) {
        try {
          baseAdditions.put(new Integer(i), super.createPosition(i));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    protected void compAddition(int currentIndex, String text) {
      for (int i = currentIndex; i < currentIndex + text.length(); i++) {
        try {
          compAdditions.put(new Integer(i), super.createPosition(i));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public MergeDiffStyledDocument(DiffItem[] baseDiffItems) {
      this.words = words;
      for (int i = 0; i < baseDiffItems.length; i++) {
        DiffItem word = baseDiffItems[i];
        try {
          String text = null, compText = null;
          text = getText(word);
          compText = getCompText(word);
          if (word.status == DELETED) {
            super.insertString(currentIndex, text, deletedAttSet);
            compAddition(currentIndex, text);
            currentIndex += text.length();
          } else  if (word.status == ADDED) {
            super.insertString(currentIndex, compText, addedAttSet);
            baseAddition(currentIndex, text);
            currentIndex += compText.length();
          } else if (word.status == CHANGED) {
            StringBuffer textBuf = new StringBuffer(text);
            StringBuffer compTextBuf = new StringBuffer(compText);
            int q = i + 1;
            while ((q < baseDiffItems.length
                   && baseDiffItems[q].status == CHANGED)
                   ||
                   (
                   q < baseDiffItems.length
                   && baseDiffItems[q].textNoDelimiter.equals(" ")
                   && q + 1 < baseDiffItems.length
                   && baseDiffItems[q + 1].status == CHANGED
                   )) {
              text = getText(baseDiffItems[q]);
              compText = getCompText(baseDiffItems[q]);
              textBuf.append(text);
              compTextBuf.append(compText);
              i = q;
              q++;
              if (q < baseDiffItems.length
                   && baseDiffItems[q].status == CHANGED) {
                text = getText(baseDiffItems[q]);
                compText = getCompText(baseDiffItems[q]);
                textBuf.append(text);
                compTextBuf.append(compText);
                i = q;
                q++;
              }
            }
            text = textBuf.toString();
            compText = compTextBuf.toString();
            super.insertString(currentIndex, text, deletedAttSet);
            compAddition(currentIndex, text);
            currentIndex += text.length();
            super.insertString(currentIndex, " ", unchangedAttSet);
            currentIndex++;
            super.insertString(currentIndex, compText, addedAttSet);
            baseAddition(currentIndex, compText);
            currentIndex += compText.length();
          } else {
            super.insertString(currentIndex, text, unchangedAttSet);
            currentIndex += text.length();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}


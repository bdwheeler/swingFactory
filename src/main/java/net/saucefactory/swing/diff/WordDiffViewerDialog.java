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

public class WordDiffViewerDialog extends SFDialog {
  protected String text1 = null;
  protected String text2 = null;
  protected Diff diff = null;
  protected int lineLength = 65;
  public static Color addedForeground = Color.green.darker();
  public static Color changedForeground = Color.orange.darker();
  public static Color deletedForeground = Color.red;
  public static Color unchangedForeground = Color.black;
  public static Color modifiedBackground = new Color(225, 225, 225);
  public static Color unmodifiedBackground = Color.white;
  protected SimpleAttributeSet addedAttSet = new SimpleAttributeSet();
  protected SimpleAttributeSet changedAttSet = new SimpleAttributeSet();
  protected SimpleAttributeSet deletedAttSet = new SimpleAttributeSet();
  protected SimpleAttributeSet unchangedAttSet = new SimpleAttributeSet();
  public static final int NO_CHANGE = 0;
  public static final int ADDED = 1;
  public static final int CHANGED = 2;
  public static final int DELETED = 3;
  protected WordDiffViewer wordDiffViewer = new WordDiffViewer();

  protected WordDiffViewerDialog(Frame owner, String text1_, String text2_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
    registerHandlers();
  }

  protected WordDiffViewerDialog(Dialog owner, String text1_, String text2_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
    registerHandlers();
  }

  protected WordDiffViewerDialog(String text1_, String text2_, int lineLength_) throws Exception {
    super();
    init(text1_, "Initial Text", text2_, "Comparison Text", lineLength_);
    registerHandlers();
  }

  protected WordDiffViewerDialog(Frame owner, String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, text1Label_, text2_, text2Label_, lineLength_);
    registerHandlers();
  }

  protected WordDiffViewerDialog(Dialog owner, String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super(owner);
    init(text1_, text1Label_, text2_, text2Label_, lineLength_);
    registerHandlers();
  }

  protected WordDiffViewerDialog(String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    super();
    init(text1_, text1Label_, text2_, text2Label_, lineLength_);
    registerHandlers();
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
    wordDiffViewer.cboLineSize.setModel(new DefaultComboBoxModel(data));
    wordDiffViewer.cboLineSize.setSelectedItem(def);
  }

  protected void init(String text1_, String text1Label_, String text2_, String text2Label_, int lineLength_) throws Exception {
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setModal(true);
    this.setSize(800,600);
    this.setTitle("Word Difference Viewer");
    this.getContentPane().add(wordDiffViewer, null);
    wordDiffViewer.lblAdded.setForeground(addedForeground);
    wordDiffViewer.lblChanged.setForeground(changedForeground);
    wordDiffViewer.lblDeleted.setForeground(deletedForeground);
    StyleConstants.setForeground(addedAttSet,addedForeground);
    StyleConstants.setBackground(addedAttSet,modifiedBackground);
    StyleConstants.setForeground(changedAttSet,changedForeground);
    StyleConstants.setBackground(changedAttSet,modifiedBackground);
    StyleConstants.setForeground(deletedAttSet,deletedForeground);
    StyleConstants.setBackground(deletedAttSet,modifiedBackground);
    StyleConstants.setForeground(unchangedAttSet,unchangedForeground);
    StyleConstants.setBackground(unchangedAttSet,unmodifiedBackground);
    lineLength = lineLength_;
    text1 = text1_;
    text2 = text2_;
    setText();
    setComboData();
    setLabelText(text1Label_, text2Label_);
    wordDiffViewer.splDocsPane.setDividerLocation(.5d);
  }


  protected void setText() throws Exception {
    wordDiffViewer.listDoc1.setFont(this.getFont());
    wordDiffViewer.listDoc2.setFont(this.getFont());
    diff = getDiff(text1, text2);
    DiffItem[] baseDiffItems = diff.getBaseDiffItemsArray();
    DiffItem[] compDiffItems = diff.getComparisonDiffItemsArray();
    StyledDocument baseDoc = new WordDiffStyledDocument(text1, baseDiffItems);
    StyledDocument compDoc = new WordDiffStyledDocument(text2, compDiffItems);

    wordDiffViewer.listDoc1.setStyledDocument(baseDoc);
    wordDiffViewer.listDoc2.setStyledDocument(compDoc);
  }


  protected void setLabelText(String doc1Label, String doc2Label) {
    wordDiffViewer.lblDoc1.setText(doc1Label);
    wordDiffViewer.lblDoc2.setText(doc2Label);
  }

  protected void showCopyPopup(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      wordDiffViewer.copyMenuItem.setActionCommand(e.getSource().equals(wordDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      wordDiffViewer.popupMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
    }
  }

  protected void registerHandlers() {
    DiffTransferHandler handler = new DiffTransferHandler();
    wordDiffViewer.listDoc1.setTransferHandler(handler);
    wordDiffViewer.listDoc2.setTransferHandler(handler);


    wordDiffViewer.listDoc1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showCopyPopup(e);
      }
    });

    wordDiffViewer.listDoc2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showCopyPopup(e);
      }
    });

    wordDiffViewer.cboLineSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          lineLength = ((Integer)wordDiffViewer.cboLineSize.getSelectedItem()).intValue();
          setText();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    wordDiffViewer.copyMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          String cmd = ae.getActionCommand();
          JTextPane list = cmd.equals("DOC1") ? wordDiffViewer.listDoc1 : wordDiffViewer.listDoc2;
          String text = ((WordDiffStyledDocument)list.getStyledDocument()).getOriginalText(list.getSelectionStart(), list.getSelectionEnd());
          TransferHandler handler = list.getTransferHandler();
          handler.exportToClipboard(list, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    wordDiffViewer.btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        dispose();
      }
    });

    // only one scrollbar for both lists
    wordDiffViewer.scrDoc2.getVerticalScrollBar().getModel().addChangeListener(new ChangeListener() {
       public void stateChanged(ChangeEvent e) {
         BoundedRangeModel sourceScroll = (BoundedRangeModel)e.getSource();
         JScrollBar sb1 = wordDiffViewer.scrDoc1.getVerticalScrollBar();
         int val = sourceScroll.getValue();
         sb1.setValue(val);
       }
   });
  }

  protected Diff getDiff(String text1_, String text2_) throws Exception {
    return DiffUtility.compare(text1_, text2_);
  }

  public static void display(String text1, String text2) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(text1, text2, 65);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1, text2, 65);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1, text2, 65);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(text1_, text1Label, text2_, text2Label, 65);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 65);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, 65);
    dialog.show();
  }

  public static void display(String text1, String text2, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(text1, text2, lineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1, String text2, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1, text2, lineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1, String text2, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1, text2, lineLength);
    dialog.show();
  }

  public static void display(String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  public static void display(Dialog owner, String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  public static void display(Frame owner, String text1_, String text1Label, String text2_, String text2Label, int lineLength) throws Exception {
    WordDiffViewerDialog dialog = new WordDiffViewerDialog(owner, text1_, text1Label, text2_, text2Label, lineLength);
    dialog.show();
  }

  class WordDiffStyledDocument extends DefaultStyledDocument {
    DiffItem[] words = null;
    Map addedBreaks = new HashMap();
    int currentIndex = 0;
    int currentLineLength = 0;
    int compLineLength = 0;
    public WordDiffStyledDocument(String text, DiffItem[] baseDiffItems) {
      this.words = words;
      for (int i = 0; i < baseDiffItems.length; i++) {
        DiffItem baseWord = baseDiffItems[i];
        int wordLength = baseWord.textNoDelimiter.length() > baseWord.compTextNoDelimiter.length() ? baseWord.textNoDelimiter.length() : baseWord.compTextNoDelimiter.length();
        int wordLineLength = currentLineLength + baseWord.textNoDelimiter.length() > compLineLength + baseWord.compTextNoDelimiter.length() ?
                             currentLineLength + baseWord.textNoDelimiter.length() : compLineLength + baseWord.compTextNoDelimiter.length();
        if (wordLength + wordLineLength > lineLength
            && wordLineLength > 0) {
          addLineBreak();
        }
        addWord(baseWord);
      }

    }

    public String getOriginalText(int start, int end) throws BadLocationException {
      StringBuffer buf = new StringBuffer();
      for (int i = start; i <= end; i++) {
        Position pos = (Position)addedBreaks.get(new Integer(i));
        if (pos == null) {
          buf.append(super.getText(i, 1));
        }
      }
      return buf.toString();
    }

    protected void addLineBreak() {
      try {
        super.insertString(currentIndex, "\n", unchangedAttSet);
        addedBreaks.put(new Integer(currentIndex), super.createPosition(currentIndex));
        currentIndex++;
        currentLineLength = 0;
        compLineLength = 0;
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    protected void addWord(DiffItem word) {
      SimpleAttributeSet set = null;
      switch (word.status) {
        case ADDED:
          set = addedAttSet;
          break;
        case CHANGED:
          set = changedAttSet;
          break;
        case DELETED:
          set = deletedAttSet;
          break;
        default:
          set = unchangedAttSet;
      }
      try {
        String text = word.textNoDelimiter;
        String compText = word.compTextNoDelimiter;
        if (text == null) {
          text = word.text;
          compText = word.compText;
        }

        super.insertString(currentIndex, text, set);
        currentIndex += text.length();
        currentLineLength += text.length();
        compLineLength += compText.length();
        if (text.endsWith("\n"))
          currentLineLength = 0;
        if (compText.endsWith("\n"))
          compLineLength = 0;
        if ((text.indexOf('\n') == -1)
            && compText.indexOf('\n') > -1) {
          int idx2 = compText.indexOf('\n');
          while (idx2  > -1) {
            addLineBreak();
            idx2 = compText.indexOf('\n', idx2 + 1);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

  }

  class DiffTransferHandler extends TransferHandler {
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
      try {
        String text = ((WordDiffStyledDocument)((JTextPane)comp).getStyledDocument()).getOriginalText(((JTextPane)comp).getSelectionStart(), ((JTextPane)comp).getSelectionEnd());
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

  public static void main(String[] args) throws HeadlessException {
    try {
      WordDiffViewerDialog.display("Creates and returns a stream tokenizer that has been properly configured to parse sphinx3 data This ExtendedStreamTokenizer has no comment characters"
                                   , "Creates poo and returns a stream tokenizerer that has been properly configured to parse sphinx3 data really well This ExtendedStreamTokenizer has comment characters"
                                   , 50);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

}


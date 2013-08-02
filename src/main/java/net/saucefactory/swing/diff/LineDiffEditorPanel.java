package net.saucefactory.swing.diff;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import net.saucefactory.text.diff.*;
import net.saucefactory.swing.event.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import com.ice.tar.TarInputStream;
import com.ice.tar.TarEntry;
import java.util.zip.GZIPInputStream;
import net.saucefactory.io.diff.DiffFile;
import net.saucefactory.swing.utils.FileUtility;

public class LineDiffEditorPanel extends JPanel {
  private BorderLayout lytMain = new BorderLayout();
  protected Stack baseEdits = new Stack();
  protected Stack compEdits = new Stack();
  protected String text1 = null;
  protected String text2 = null;
  protected File file1 = null;
  protected File file2 = null;
  protected String entry1 = null;
  protected String entry2 = null;
  protected boolean file1Dirty = false;
  protected boolean file2Dirty = false;
  protected Diff diff = null;
  protected DiffFile diffFile = null;
  protected int lineLength = 0;
  public LineDiffEditor lineDiffViewer = new LineDiffEditor();
  protected DiffLineRenderer rend1 = new DiffLineRenderer();
  protected DiffLineRenderer rend2 = new DiffLineRenderer();
  public boolean filesChanged = false;
  public boolean isArchive = false;

  public LineDiffEditorPanel() {
    init();
  }

  public LineDiffEditorPanel(File text1_, String text1Label_, File text2_, String text2Label_, int lineLength_) throws Exception {
    try {
      init();
      doCompare(text1_, text1Label_, text2_, text2Label_, lineLength_);
      setText();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public LineDiffEditorPanel(File text1, String entry1, String text1Label, File text2, String entry2, String text2Label, int lineLength) throws Exception {
    try {
      this.entry1 = entry1;
      this.entry2 = entry2;
      init();
      isArchive = true;
      doCompare(text1, text1Label, text2, text2Label, lineLength);
      setText();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
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

  public void init() {
    setLayout(lytMain);
    add(lineDiffViewer, BorderLayout.CENTER);
    registerHandlers();
    setComboData();
    lineDiffViewer.listDoc1.setCellRenderer(rend1);
    lineDiffViewer.listDoc2.setCellRenderer(rend2);
    lineDiffViewer.lblAdded.setForeground(rend1.getAddedForeground());
    lineDiffViewer.lblChanged.setForeground(rend1.getChangedForeground());
    lineDiffViewer.lblDeleted.setForeground(rend1.getDeletedForeground());
    lineDiffViewer.btnClose.setVisible(false);
    lineDiffViewer.btnSave.setVisible(false);
    lineDiffViewer.cboLineSize.setVisible(false);
    lineDiffViewer.lblAdded.setVisible(false);
    lineDiffViewer.lblChanged.setVisible(false);
    lineDiffViewer.lblDeleted.setVisible(false);
    lineDiffViewer.lblLineLength.setVisible(false);
    lineDiffViewer.lblUnchanged.setVisible(false);
    lineDiffViewer.syncListSelection();
  }

  public void clearCompare() {
    diff = null;
    lineDiffViewer.listDoc1.setListData(new Vector());
    lineDiffViewer.listDoc2.setListData(new Vector());
    setLabelText("", "");
  }

  public void doCompare(File text1_, String srcEntry, String text1Label_, File text2_, String tgtEntry, String text2Label_, int lineLength_) throws Exception {
    entry1 = srcEntry;
    entry2 = tgtEntry;
    isArchive = true;
    lineLength = lineLength_;
    file1 = text1_;
    file2 = text2_;
    text1 = getArchiveContents(file1, entry1);
    text2 = getArchiveContents(file2, entry2);
    setLabelText(text1Label_, text2Label_);
    setText();
  }


  public void doCompare(File text1_, String text1Label_, File text2_, String text2Label_, int lineLength_) throws Exception {
    lineLength = lineLength_;
    isArchive = false;
    file1 = text1_;
    file2 = text2_;
    text1 = getFileContents(file1);
    text2 = getFileContents(file2);
    setLabelText(text1Label_, text2Label_);
    setText();
  }

  public void doCompare(DiffFile diffFile, String lblSrc, String lblTgt) throws Exception {
    lineLength = 0;
    this.diffFile = diffFile;
    text1 = getDiffFileContents(diffFile, true);
    text2 = getDiffFileContents(diffFile, false);
    setLabelText(lblSrc, lblTgt);
    setText();
  }

  protected void rereadFiles() throws Exception {
    if(isArchive) {
      text1 = getZipFileContents(file1, entry1);
      text2 = getZipFileContents(file2, entry2);
    }
    else {
      text1 = getFileContents(file1);
      text2 = getFileContents(file2);
    }
    setText();
  }

  public void setLineSizeSelectionEnabled(boolean enable) {
    this.lineDiffViewer.cboLineSize.setVisible(enable);
    this.lineDiffViewer.lblLineLength.setVisible(enable);
  }

  protected String getFileContents(File f) throws Exception {
    String rtn = null;
    if (f != null && f.exists()) {
      java.io.FileReader reader = new FileReader(f);
      StringBuffer buf = new StringBuffer();
      int ch;
      while ((ch = reader.read()) != -1) {
       buf.append((char)ch);
      }
      rtn = buf.toString();
      reader.close();
    }
    return rtn;
  }

  protected String getArchiveContents(File f, String entryName) throws Exception {
    String name = f == null ? null : f.getName();
    if(name == null)
      return "";
    if(FileUtility.isZipOrJar(name))
      return getZipFileContents(f, entryName);
    else if(FileUtility.isTarFile(name))
      return getTarFileContents(f, entryName);
    return "";
  }

  protected String getZipFileContents(File f, String entryName) throws Exception {
    String rtn = "";
    try {
      if(f != null && f.exists() && entryName != null) {
        //System.out.println("Loading entry: " + f.getPath() + ": " + entryName);
        FileInputStream fileIn = new FileInputStream(f);
        ZipInputStream zipIn = new ZipInputStream(fileIn);
        boolean found = false;
        ZipEntry tmpEntry = null;
        while(!found && (tmpEntry = zipIn.getNextEntry()) != null) {
          if(tmpEntry.getName().equals(entryName))
            found = true;
        }
        if(tmpEntry != null) {
          StringBuffer buf = new StringBuffer();
          int ch;
          while((ch = zipIn.read()) != -1) {
            buf.append((char)ch);
          }
          //System.out.println("loaded entry: " + buf.length());
          rtn = buf.toString();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return rtn;
  }

  protected String getTarFileContents(File f, String entryName) throws Exception {
    String rtn = "";
    try {
      if(f != null && f.exists() && entryName != null) {
        //System.out.println("Loading entry: " + f.getPath() + ": " + entryName);
        FileInputStream fileIn = new FileInputStream(f);
        TarInputStream tarIn;
        if(FileUtility.isGTar(f.getName()))
          tarIn = new TarInputStream(new GZIPInputStream(fileIn));
        else
          tarIn = new TarInputStream(fileIn);
        boolean found = false;
        TarEntry tmpEntry = null;
        while(!found && (tmpEntry = tarIn.getNextEntry()) != null) {
          if(tmpEntry.getName().equals(entryName))
            found = true;
        }
        if(tmpEntry != null) {
          StringBuffer buf = new StringBuffer();
          int ch;
          while((ch = tarIn.read()) != -1) {
            buf.append((char)ch);
          }
          //System.out.println("loaded entry: " + buf.length());
          rtn = buf.toString();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return rtn;
  }

  public String getDiffFileContents(DiffFile diff, boolean src) throws Exception {
    if(diff.isArchive || diff.isArchiveEntry) {
      String entryName = diff.entryName;
      ArrayList archives = new ArrayList();
      while(true) {
        DiffFile parent = diff.getParent();
        if(parent.isArchive) {
          archives.add(0, parent);
          if(!parent.isArchiveEntry)
              break;
          diff = parent;
        }
      }
      return getArchiveContents(archives, entryName, src);
    }
    else {
      if(src)
        return diff.getBasePath() == null ? null : getFileContents(new File(diff.getBasePath()));
      else
        return diff.getCompPath() == null ? null : getFileContents(new File(diff.getCompPath()));
    }
  }

  protected String getArchiveContents(ArrayList archives, String entryName, boolean src) {
    String rtn = null;
    try {
      InputStream in = null;
      boolean zip = false;
      for(int i = 0; i < archives.size(); i++) {
        boolean last = i == archives.size() - 1;
        DiffFile tmpDiff = (DiffFile)archives.get(i);
        String path;
        if(i == 0) {
          path = src ? tmpDiff.getBasePath() : tmpDiff.getCompPath();
          if(path == null)
            return null;
          if(FileUtility.isZipOrJar(tmpDiff.getName())) {
            in = new ZipInputStream(new FileInputStream(new File(path)));
            zip = true;
          }
          else {
            boolean gZip = FileUtility.isGTar(tmpDiff.getName());
            in = gZip ? new TarInputStream(new GZIPInputStream(new FileInputStream(new File(path)))) :
                new TarInputStream(new FileInputStream(new File(path)));
            zip = false;
          }
        }
        else {
          path = tmpDiff.entryName;
          if(zip) {
            ZipEntry tmpEntry = null;
            boolean found = false;
            while(!found && (tmpEntry = ((ZipInputStream)in).getNextEntry()) != null) {
              if(tmpEntry.getName().equals(path))
                found = true;
            }
            if(tmpEntry != null) {
              if(FileUtility.isZipOrJar(path)) {
                in = new ZipInputStream(in);
                zip = true;
              }
              else {
                if(FileUtility.isGTar(path))
                  in = new GZIPInputStream(in);
                in = new TarInputStream(in);
                zip = false;
              }
            }
          }
          else {
            TarEntry tmpEntry = null;
            boolean found = false;
            while(!found && (tmpEntry = ((TarInputStream)in).getNextEntry()) != null) {
              if(tmpEntry.getName().equals(path))
                found = true;
            }
            if(tmpEntry != null) {
              if(FileUtility.isZipOrJar(path)) {
                in = new ZipInputStream(in);
                zip = true;
              }
              else {
                if(FileUtility.isGTar(path))
                  in = new GZIPInputStream(in);
                in = new TarInputStream(in);
                zip = false;
              }
            }
          }
        }
        if(last) {
          if(zip) {
            ZipEntry tmpEntry = null;
            boolean found = false;
            while(!found && (tmpEntry = ((ZipInputStream)in).getNextEntry()) != null) {
              if(tmpEntry.getName().equals(entryName))
                found = true;
            }
            if(tmpEntry != null) {
              /*if(FileUtility.isZipOrJar(entryName)) {
                in = new ZipInputStream(in);
                zip = true;
              }
              else {
                if(FileUtility.isGTar(entryName))
                  in = new GZIPInputStream(in);
                in = new TarInputStream(in);
                zip = false;
              }*/
            }
          }
          else {
            TarEntry tmpEntry = null;
            boolean found = false;
            while(!found && (tmpEntry = ((TarInputStream)in).getNextEntry()) != null) {
              if(tmpEntry.getName().equals(entryName))
                found = true;
            }
            if(tmpEntry != null) {
              /*if(FileUtility.isZipOrJar(entryName)) {
                in = new ZipInputStream(in);
                zip = true;
              }
              else {
                if(FileUtility.isGTar(entryName))
                  in = new GZIPInputStream(in);
                in = new TarInputStream(in);
                zip = false;
              }*/
            }
          }
        }
      }
      StringBuffer buf = new StringBuffer();
      int ch;
      while((ch = in.read()) != -1) {
        buf.append((char)ch);
      }
      //System.out.println("loaded entry: " + buf.length());
      rtn = buf.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return rtn;
  }

  protected void setText() throws Exception {
    diff = getDiff(text1 == null ? "" : text1, text2 == null ? "" : text2);
    setListData();
  }

  protected void setListData() throws Exception {
    if(text1 == null)
      lineDiffViewer.listDoc1.setListData(new Vector());
    else
      lineDiffViewer.listDoc1.setListData(diff.getBaseDiffItemsArray());
    if(text2 == null)
      lineDiffViewer.listDoc2.setListData(new Vector());
    else
      lineDiffViewer.listDoc2.setListData(diff.getComparisonDiffItemsArray());
  }

  protected void setLabelText(String doc1Label, String doc2Label) {
    lineDiffViewer.lblDoc1.setText(doc1Label);
    lineDiffViewer.lblDoc2.setText(doc2Label);
  }

  protected void showPopup(MouseEvent e) {
    JList list = (JList)e.getSource();
    if (SwingUtilities.isRightMouseButton(e)) {
      int row = list.locationToIndex(e.getPoint());
      if(row > -1)
        list.setSelectedIndex(row);
      boolean disableTransfer = diffFile == null || diffFile.isArchive || diffFile.isArchiveEntry;
      lineDiffViewer.copyMenuItem.setActionCommand(list.equals(lineDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      lineDiffViewer.mergedMenuItem.setActionCommand(list.equals(lineDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      lineDiffViewer.transferMenuItem.setActionCommand(list.equals(lineDiffViewer.listDoc1) ? "DOC1" : "DOC2");
      lineDiffViewer.transferMenuItem.setText(list.equals(lineDiffViewer.listDoc1) ? "Transfer Text to Target File" : "Transfer Text to Source File");
      lineDiffViewer.transferMenuItem.setEnabled(!disableTransfer);
      lineDiffViewer.popupMenu.show((JComponent)e.getSource(), e.getX(), e.getY());
    }
  }

  public void syncListSelection() {
    lineDiffViewer.syncListSelection();
  }

  protected void registerHandlers() {
    lineDiffViewer.listDoc1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showPopup(e);
      }
    });

    lineDiffViewer.listDoc2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        showPopup(e);
      }
    });
    lineDiffViewer.cboLineSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        //try {
          //lineLength = ((Integer)lineDiffViewer.cboLineSize.getSelectedItem()).intValue();
          //setText();
        //} catch (Exception e) {
          //e.printStackTrace();
        //}
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
        DiffItemTransferHandler2 handler = (DiffItemTransferHandler2)list.getTransferHandler();
        handler.exportToClipboard(list, Toolkit.getDefaultToolkit().getSystemClipboard(), handler.COPY_MERGED);
      }
    });


    lineDiffViewer.transferMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          String cmd = ae.getActionCommand();
          boolean fromDoc1 = cmd.equals("DOC1");
          DiffItemList2 list = fromDoc1 ? lineDiffViewer.listDoc1 :
              lineDiffViewer.listDoc2;
          int[] i = list.getSelectedIndices();
          lineDiffViewer.listDoc1.transferContent(fromDoc1, i[0],
                                                  i[i.length - 1]);
          lineDiffViewer.listDoc2.transferContent(!fromDoc1, i[0],
                                                  i[i.length - 1]);
          setListData();
          lineDiffViewer.btnSave.setEnabled(true);
          if (fromDoc1) file2Dirty = true;
          else file1Dirty = true;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    lineDiffViewer.btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          boolean filesChangedThisTime = false;
          if (file1Dirty) {
            int rtn = JOptionPane.showConfirmDialog(LineDiffEditorPanel.this,
                "Save changes to Source File?", "Confirm",
                JOptionPane.OK_CANCEL_OPTION);
            if (rtn == JOptionPane.CANCEL_OPTION)
              return;
            String newContent = lineDiffViewer.listDoc1.getAllContent();
            java.io.FileWriter writer = new FileWriter(file1, false);
            writer.write(newContent);
            writer.flush();
            writer.close();
            filesChanged = true;
            filesChangedThisTime = true;
            file1Dirty = false;
          }

          if (file2Dirty) {
            int rtn = JOptionPane.showConfirmDialog(LineDiffEditorPanel.this,
                "Save changes to Target File?", "Confirm",
                JOptionPane.OK_CANCEL_OPTION);
            if (rtn == JOptionPane.CANCEL_OPTION)
              return;
            String newContent = lineDiffViewer.listDoc2.getAllContent();
            java.io.FileWriter writer = new FileWriter(file2, false);
            writer.write(newContent);
            writer.flush();
            writer.close();
            filesChanged = true;
            filesChangedThisTime = true;
            file2Dirty = false;
          }
          if (filesChangedThisTime) {
            int rtn = JOptionPane.showConfirmDialog(LineDiffEditorPanel.this,
                "Rescan files for differences?", "Files Changed",
                JOptionPane.OK_CANCEL_OPTION);
            if (rtn == JOptionPane.CANCEL_OPTION)
              return;
            rereadFiles();
          }

        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(LineDiffEditorPanel.this, "Error saving files: " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    lineDiffViewer.btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if (file1Dirty || file2Dirty) {
          int rtn = JOptionPane.showConfirmDialog(LineDiffEditorPanel.this, "Discard Changes?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          if (rtn == JOptionPane.NO_OPTION) return;
        }
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
  public LineDiffEditor getLineDiffEditor() {
    return lineDiffViewer;
  }

  public Diff getDiff() {
    return diff;
  }
}


package net.saucefactory.swing.diff;

import net.saucefactory.io.diff.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class FileDiffTree extends JTree {
  public static final int MODE_BASE = 0;
  public static final int MODE_COMPARISON = 1;
  public static final int MODE_MERGED = 2;

  public static final ImageIcon DEFAULT_OPENUNCHANGEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_unchanged_open.gif"));
  public static final ImageIcon DEFAULT_CLOSEDUNCHANGEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_unchanged_closed.gif"));
  public static final ImageIcon DEFAULT_OPENCHANGEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_changed_open.gif"));
  public static final ImageIcon DEFAULT_CLOSEDCHANGEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_changed_closed.gif"));
  public static final ImageIcon DEFAULT_OPENADDEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_added_open.gif"));
  public static final ImageIcon DEFAULT_CLOSEDADDEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_added_closed.gif"));
  public static final ImageIcon DEFAULT_OPENDELETEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_deleted_open.gif"));
  public static final ImageIcon DEFAULT_CLOSEDDELETEDFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_deleted_closed.gif"));
  public static final ImageIcon DEFAULT_OPENBOTHFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_changed_open.gif"));
  public static final ImageIcon DEFAULT_CLOSEDBOTHFOLDERIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/dir_changed_closed.gif"));

  public static final ImageIcon DEFAULT_UNCHANGEDFILEIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_unchanged.gif"));
  public static final ImageIcon DEFAULT_CHANGEDFILEIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_changed.gif"));
  public static final ImageIcon DEFAULT_ADDEDFILEIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_added.gif"));
  public static final ImageIcon DEFAULT_DELETEDFILEIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_deleted.gif"));

  public static final ImageIcon LEGEND_UNCHANGEDIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/unchanged_legend.gif"));
  public static final ImageIcon LEGEND_CHANGEDIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/changed_legend.gif"));
  public static final ImageIcon LEGEND_ADDEDIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/added_legend.gif"));
  public static final ImageIcon LEGEND_DELETEDIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/deleted_legend.gif"));

  public static final ImageIcon BLANKIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/blank.gif"));
  public static final ImageIcon GHOST_FILEIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/ghost_file.gif"));
  public static final ImageIcon GHOST_OPEN_DIRIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/ghost_dir.gif"));
  public static final ImageIcon GHOST_CLOSED_DIRIMAGEICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/ghost_dir.gif"));

  protected ImageIcon openUnchangedFolderImageIcon = DEFAULT_OPENUNCHANGEDFOLDERIMAGEICON;
  protected ImageIcon closedUnchangedFolderImageIcon = DEFAULT_CLOSEDUNCHANGEDFOLDERIMAGEICON;
  protected ImageIcon openChangedFolderImageIcon = DEFAULT_OPENCHANGEDFOLDERIMAGEICON;
  protected ImageIcon closedChangedFolderImageIcon = DEFAULT_CLOSEDCHANGEDFOLDERIMAGEICON;
  protected ImageIcon openAddedFolderImageIcon = DEFAULT_OPENADDEDFOLDERIMAGEICON;
  protected ImageIcon closedAddedFolderImageIcon = DEFAULT_CLOSEDADDEDFOLDERIMAGEICON;
  protected ImageIcon openDeletedFolderImageIcon = DEFAULT_OPENDELETEDFOLDERIMAGEICON;
  protected ImageIcon closedDeletedFolderImageIcon = DEFAULT_CLOSEDDELETEDFOLDERIMAGEICON;
  protected ImageIcon openBothFolderImageIcon = DEFAULT_OPENBOTHFOLDERIMAGEICON;
  protected ImageIcon closedBothFolderImageIcon = DEFAULT_CLOSEDBOTHFOLDERIMAGEICON;

  protected ImageIcon unchangedFileImageIcon = DEFAULT_UNCHANGEDFILEIMAGEICON;
  protected ImageIcon changedFileImageIcon = DEFAULT_CHANGEDFILEIMAGEICON;
  protected ImageIcon addedFileImageIcon = DEFAULT_ADDEDFILEIMAGEICON;
  protected ImageIcon deletedFileImageIcon = DEFAULT_DELETEDFILEIMAGEICON;

  protected ImageIcon ghostFileImageIcon = GHOST_FILEIMAGEICON;
  protected ImageIcon openGhostDirImageIcon = GHOST_OPEN_DIRIMAGEICON;
  protected ImageIcon closedGhostDirImageIcon = GHOST_CLOSED_DIRIMAGEICON;

  // Colors
  /** Color to use for the foreground for selected nodes. */
  protected Color textSelectionColor;

  /** Color to use for the foreground for non-selected nodes. */
  protected Color textNonSelectionColor;

  /** Color to use for the background when a node is selected. */
  protected Color backgroundSelectionColor;

  /** Color to use for the background when the node isn't selected. */
  protected Color backgroundNonSelectionColor;

  /** Color to use for the focus indicator when the node has focus. */
  protected Color borderSelectionColor;


  protected int mode = MODE_MERGED;
  protected DiffFile diffFile = null;
  protected FileDiffTreeRenderer cellRenderer = new FileDiffTreeRenderer(this);
  private java.awt.Color addedForeground = Color.white;
  private java.awt.Color changedForeground = Color.white;
  private java.awt.Color deletedForeground = Color.white;
  private java.awt.Color unchangedForeground = Color.black;
  private java.awt.Color addedBackground = new Color(121, 196, 121);
  private java.awt.Color deletedBackground = new Color(251, 115, 122);
  private java.awt.Color changedBackground = new Color(254, 180, 38);
  private java.awt.Color unchangedBackground = Color.white;

  private javax.swing.ImageIcon addedLegendIcon = LEGEND_ADDEDIMAGEICON;
  private javax.swing.ImageIcon deletedLegendIcon = LEGEND_DELETEDIMAGEICON;
  private javax.swing.ImageIcon changedLegendIcon = LEGEND_CHANGEDIMAGEICON;
  private javax.swing.ImageIcon unchangedLegendIcon = LEGEND_UNCHANGEDIMAGEICON;
  public boolean paintGhostText = true;

  public FileDiffTree() {
    init();
  }

  public FileDiffTree(int _mode) {
    mode = _mode;
    init();
  }

  public FileDiffTree(int _mode, DiffFile _diffFile) {
    mode = _mode;
    diffFile = _diffFile;
    init();
  }



  private void init() {
    FileDiffTreeModel model = new FileDiffTreeModel(diffFile);
    setModel(model);
    setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
    setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
    setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
    setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
    setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
    setEditable(false);
    this.setScrollsOnExpand(false);
    DefaultTreeSelectionModel mod = new DefaultTreeSelectionModel();
    mod.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(mod);
    this.setCellRenderer(cellRenderer);
  }

   public DiffFile getDiffFileForLocation(int x, int y) throws Exception {
     TreePath path = this.getPathForLocation(x, y);
     if (path == null)
       return null;
     this.setSelectionPath(path);
     DiffFile tmpDiffFile = (DiffFile) path.getLastPathComponent();
     return tmpDiffFile;
   }

  public DiffFile getSelectedDiffFile() throws Exception {
    TreePath path = this.getSelectionPath();
    if (path == null) return null;
    DiffFile tmpDiffFile = (DiffFile)path.getLastPathComponent();
    return tmpDiffFile;
  }

  public boolean isSelectedArchive() throws Exception {
    TreePath path = this.getSelectionPath();
    if (path == null) return false;
    DiffFile tmpDiffFile = (DiffFile)path.getLastPathComponent();
    return tmpDiffFile.isArchive || tmpDiffFile.isArchiveEntry;// isCompressed;
  }

  public String getSelectedArchiveEntry() throws Exception {
    TreePath path = this.getSelectionPath();
    if (path == null) return null;
    DiffFile tmpDiffFile = (DiffFile)path.getLastPathComponent();
    return tmpDiffFile.entryName;
  }

  public File getSelectedFile() throws Exception {
    TreePath path = this.getSelectionPath();
    if (path == null) return null;
    DiffFile tmpDiffFile = (DiffFile)path.getLastPathComponent();
    boolean getBase = mode == MODE_BASE;
    File f = null;
    if (getBase) {
      if(tmpDiffFile.isArchive || tmpDiffFile.isArchiveEntry)//isCompressed)
        f = new File(tmpDiffFile.baseArchivePath);
      else
        f = new File(tmpDiffFile.getBasePath());
    }
    else {
      if(tmpDiffFile.isArchive || tmpDiffFile.isArchiveEntry)
        f = new File(tmpDiffFile.compArchivePath);
      else
        f = new File(tmpDiffFile.getCompPath());
    }
    return f;
  }

  public String getSelectedFileContents() throws Exception {
    TreePath path = this.getSelectionPath();
    if (path == null) return null;
    DiffFile tmpDiffFile = (DiffFile)path.getLastPathComponent();
    boolean getBase = mode == MODE_BASE;
    return getFileContents(tmpDiffFile, getBase);
  }

  protected String getFileContents(DiffFile tmpDiffFile, boolean getBase) throws Exception {
    String rtn = "";
    File f = null;
    if (getBase)
      f = new File(tmpDiffFile.getBasePath());
    else
      f = new File(tmpDiffFile.getCompPath());
    if (f.exists()) {
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



  class FileDiffTreeRenderer extends JLabel implements TreeCellRenderer {
    protected FileDiffTree tree = null;
    protected boolean hasFocus = false;
    protected boolean selected = false;
    public FileDiffTreeRenderer(FileDiffTree _tree) {
      tree = _tree;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                   boolean selected, boolean expanded,
                                   boolean leaf, int row, boolean hasFocus) {
      DiffFile tmpDiff = (DiffFile)value;
      setText("");
      this.hasFocus = hasFocus;
      this.selected = selected;
      setEnabled(true);
      if (tmpDiff.getParent() != null)
        setText(tmpDiff.getName());
      else {
        if (mode == MODE_BASE)
          setText(tmpDiff.getBasePath());
        else if (mode == MODE_COMPARISON)
          setText(tmpDiff.getCompPath());
        else
          setText(tmpDiff.getName());
      }

      if (selected)
        setForeground(getTextSelectionColor());
      else
        setForeground(getTextNonSelectionColor());
      if (leaf) {
        switch (tmpDiff.getStatus()) {
          case DiffFile.ADDED:
            if (mode != MODE_BASE) {
              setIcon(getAddedFileImageIcon());
              setForeground(getAddedForeground());
            }
            else {
              setIcon(getGhostFileImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.DELETED:
            if (mode != MODE_COMPARISON) {
              setIcon(getDeletedFileImageIcon());
              setForeground(getDeletedForeground());
            }
            else {
              setIcon(getGhostFileImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.CHANGED:
            setIcon(getChangedFileImageIcon());
            setForeground(getChangedForeground());
            break;
          default:
            setIcon(getUnchangedFileImageIcon());
            break;
        }
      }
      else if (expanded) {
        switch (tmpDiff.getStatus()) {
          case DiffFile.ADDED:
            if (mode != MODE_BASE) {
              setIcon(getOpenAddedFolderImageIcon());
              setForeground(getAddedForeground());
            }
            else {
              setIcon(getOpenGhostDirImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.DELETED:
            if (mode != MODE_COMPARISON) {
              setIcon(getOpenDeletedFolderImageIcon());
              setForeground(getDeletedForeground());
            }
            else {
              setIcon(getOpenGhostDirImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.CHANGED:
            if(tmpDiff.hasMixed())
              setIcon(getOpenBothFolderImageIcon());
            else
              setIcon(getOpenChangedFolderImageIcon());
            setForeground(getChangedForeground());;
            break;
          default:
            setIcon(getOpenUnchangedFolderImageIcon());
            break;
        }
      }
      else {
        switch (tmpDiff.getStatus()) {
          case DiffFile.ADDED:
            if (mode != MODE_BASE) {
              setIcon(getClosedAddedFolderImageIcon());
              setForeground(getAddedForeground());
            }
            else {
              setIcon(getClosedGhostDirImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.DELETED:
            if (mode != MODE_COMPARISON) {
              setIcon(getClosedDeletedFolderImageIcon());
              setForeground(getDeletedForeground());
            }
            else {
              setIcon(getClosedGhostDirImageIcon());
              setForeground(Color.lightGray);
              if(!paintGhostText)
                setText(" ");
            }
            break;
          case DiffFile.CHANGED:
            if(tmpDiff.hasMixed())
              setIcon(getClosedBothFolderImageIcon());
            else
              setIcon(getClosedChangedFolderImageIcon());
            setForeground(getChangedForeground());
            break;
          default:
            setIcon(getClosedUnchangedFolderImageIcon());
            break;
        }
      }
      setComponentOrientation(getComponentOrientation());
      return this;
    }

    public void paint(Graphics g) {
      Color bColor;
      if (selected) {
        bColor = getBackgroundSelectionColor();
      }
      else {
        bColor = getBackgroundNonSelectionColor();
        if (bColor == null)
          bColor = getBackground();
      }
      int imageOffset = -1;
      if (bColor != null) {
        Icon currentI = getIcon();
        imageOffset = getLabelStart();
        g.setColor(bColor);
        if (getComponentOrientation().isLeftToRight()) {
          g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());
        }
        else {
          g.fillRect(0, 0, getWidth() - 1 - imageOffset, getHeight());
        }
      }
      if (hasFocus) {
        imageOffset = getLabelStart(); // no border around icon
        Color bsColor = getBorderSelectionColor();
        if (bsColor != null) {
          g.setColor(bsColor);
          if (getComponentOrientation().isLeftToRight()) {
            g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
          }
          else {
            g.drawRect(0, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
          }
        }
      }
      super.paint(g);
    }

    private int getLabelStart() {
        Icon currentI = getIcon();
        if(currentI != null && getText() != null) {
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        }
        return 0;
    }

  }
  public DiffFile getDiffFile() {
    return diffFile;
  }
  public void setDiffFile(DiffFile diffFile) {
    this.diffFile = diffFile;
    FileDiffTreeModel model = new FileDiffTreeModel(diffFile);
    setModel(model);
  }
  public void setMode(int mode) {
    this.mode = mode;
  }
  public int getMode() {
    return mode;
  }
  public ImageIcon getOpenGhostDirImageIcon() {
    return openGhostDirImageIcon;
  }
  public ImageIcon getClosedGhostDirImageIcon() {
    return closedGhostDirImageIcon;
  }
  public ImageIcon getGhostFileImageIcon() {
    return ghostFileImageIcon;
  }
  public ImageIcon getClosedAddedFolderImageIcon() {
    return closedAddedFolderImageIcon;
  }
  public void setClosedAddedFolderImageIcon(ImageIcon closedAddedFolderImageIcon) {
    this.closedAddedFolderImageIcon = closedAddedFolderImageIcon;
  }
  public void setClosedChangedFolderImageIcon(ImageIcon closedChangedFolderImageIcon) {
    this.closedChangedFolderImageIcon = closedChangedFolderImageIcon;
  }
  public void setClosedDeletedFolderImageIcon(ImageIcon closedDeletedFolderImageIcon) {
    this.closedDeletedFolderImageIcon = closedDeletedFolderImageIcon;
  }
  public void setClosedUnchangedFolderImageIcon(ImageIcon closedUnchangedFolderImageIcon) {
    this.closedUnchangedFolderImageIcon = closedUnchangedFolderImageIcon;
  }
  public ImageIcon getClosedUnchangedFolderImageIcon() {
    return closedUnchangedFolderImageIcon;
  }
  public ImageIcon getClosedDeletedFolderImageIcon() {
    return closedDeletedFolderImageIcon;
  }
  public ImageIcon getClosedChangedFolderImageIcon() {
    return closedChangedFolderImageIcon;
  }
  public ImageIcon getClosedBothFolderImageIcon() {
    return closedBothFolderImageIcon;
  }
  public ImageIcon getOpenBothFolderImageIcon() {
    return openBothFolderImageIcon;
  }
  public ImageIcon getOpenAddedFolderImageIcon() {
    return openAddedFolderImageIcon;
  }
  public ImageIcon getOpenChangedFolderImageIcon() {
    return openChangedFolderImageIcon;
  }
  public ImageIcon getOpenDeletedFolderImageIcon() {
    return openDeletedFolderImageIcon;
  }
  public ImageIcon getOpenUnchangedFolderImageIcon() {
    return openUnchangedFolderImageIcon;
  }
  public void setOpenUnchangedFolderImageIcon(ImageIcon openUnchangedFolderImageIcon) {
    this.openUnchangedFolderImageIcon = openUnchangedFolderImageIcon;
  }
  public void setOpenDeletedFolderImageIcon(ImageIcon openDeletedFolderImageIcon) {
    this.openDeletedFolderImageIcon = openDeletedFolderImageIcon;
  }
  public void setOpenChangedFolderImageIcon(ImageIcon openChangedFolderImageIcon) {
    this.openChangedFolderImageIcon = openChangedFolderImageIcon;
  }
  public void setOpenBothFolderImageIcon(ImageIcon openBothFolderImageIcon) {
    this.openBothFolderImageIcon = openBothFolderImageIcon;
  }
  public void setClosedBothFolderImageIcon(ImageIcon closedBothFolderImageIcon) {
    this.closedBothFolderImageIcon = closedBothFolderImageIcon;
  }
  public void setOpenAddedFolderImageIcon(ImageIcon openAddedFolderImageIcon) {
    this.openAddedFolderImageIcon = openAddedFolderImageIcon;
  }
  public ImageIcon getAddedFileImageIcon() {
    return addedFileImageIcon;
  }
  public void setAddedFileImageIcon(ImageIcon addedFileImageIcon) {
    this.addedFileImageIcon = addedFileImageIcon;
  }
  public void setChangedFileImageIcon(ImageIcon changedFileImageIcon) {
    this.changedFileImageIcon = changedFileImageIcon;
  }
  public ImageIcon getChangedFileImageIcon() {
    return changedFileImageIcon;
  }
  public ImageIcon getDeletedFileImageIcon() {
    return deletedFileImageIcon;
  }
  public void setDeletedFileImageIcon(ImageIcon deletedFileImageIcon) {
    this.deletedFileImageIcon = deletedFileImageIcon;
  }
  public void setUnchangedFileImageIcon(ImageIcon unchangedFileImageIcon) {
    this.unchangedFileImageIcon = unchangedFileImageIcon;
  }
  public ImageIcon getUnchangedFileImageIcon() {
    return unchangedFileImageIcon;
  }
  public Color getTextNonSelectionColor() {
    return textNonSelectionColor;
  }
  public Color getTextSelectionColor() {
    return textSelectionColor;
  }
  public void setTextNonSelectionColor(Color textNonSelectionColor) {
    this.textNonSelectionColor = textNonSelectionColor;
  }
  public void setTextSelectionColor(Color textSelectionColor) {
    this.textSelectionColor = textSelectionColor;
  }
  public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
    this.backgroundNonSelectionColor = backgroundNonSelectionColor;
  }
  public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
    this.backgroundSelectionColor = backgroundSelectionColor;
  }
  public void setBorderSelectionColor(Color borderSelectionColor) {
    this.borderSelectionColor = borderSelectionColor;
  }
  public Color getBorderSelectionColor() {
    return borderSelectionColor;
  }
  public Color getBackgroundSelectionColor() {
    return backgroundSelectionColor;
  }
  public Color getBackgroundNonSelectionColor() {
    return backgroundNonSelectionColor;
  }
  public java.awt.Color getAddedForeground() {
    return addedForeground;
  }
  public void setAddedForeground(java.awt.Color addedForeground) {
    this.addedForeground = addedForeground;
  }
  public java.awt.Color getChangedForeground() {
    return changedForeground;
  }
  public void setChangedForeground(java.awt.Color changedForeground) {
    this.changedForeground = changedForeground;
  }
  public java.awt.Color getDeletedForeground() {
    return deletedForeground;
  }
  public void setDeletedForeground(java.awt.Color deletedForeground) {
    this.deletedForeground = deletedForeground;
  }
  public java.awt.Color getUnchangedForeground() {
    return unchangedForeground;
  }
  public void setUnchangedForeground(java.awt.Color unchangedForeground) {
    this.unchangedForeground = unchangedForeground;
  }
  public java.awt.Color getAddedBackground() {
    return addedBackground;
  }
  public void setAddedBackground(java.awt.Color addedBackground) {
    this.addedBackground = addedBackground;
  }
  public java.awt.Color getDeletedBackground() {
    return deletedBackground;
  }
  public void setDeletedBackground(java.awt.Color deletedBackground) {
    this.deletedBackground = deletedBackground;
  }
  public java.awt.Color getChangedBackground() {
    return changedBackground;
  }
  public void setChangedBackground(java.awt.Color changedBackground) {
    this.changedBackground = changedBackground;
  }
  public java.awt.Color getUnchangedBackground() {
    return unchangedBackground;
  }
  public void setUnchangedBackground(java.awt.Color unchangedBackground) {
    this.unchangedBackground = unchangedBackground;
  }
  public void setGhostFileImageIcon(ImageIcon ghostFileImageIcon) {
    this.ghostFileImageIcon = ghostFileImageIcon;
  }
  public void setOpenGhostDirImageIcon(ImageIcon ghostDirImageIcon) {
    this.openGhostDirImageIcon = ghostDirImageIcon;
  }
  public void setClosedGhostDirImageIcon(ImageIcon ghostDirImageIcon) {
    this.closedGhostDirImageIcon = ghostDirImageIcon;
  }
  public javax.swing.ImageIcon getAddedLegendIcon() {
    return addedLegendIcon;
  }
  public void setAddedLegendIcon(javax.swing.ImageIcon addedLegendIcon) {
    this.addedLegendIcon = addedLegendIcon;
  }
  public javax.swing.ImageIcon getDeletedLegendIcon() {
    return deletedLegendIcon;
  }
  public void setDeletedLegendIcon(javax.swing.ImageIcon deletedLegendIcon) {
    this.deletedLegendIcon = deletedLegendIcon;
  }
  public javax.swing.ImageIcon getChangedLegendIcon() {
    return changedLegendIcon;
  }
  public void setChangedLegendIcon(javax.swing.ImageIcon changedLegendIcon) {
    this.changedLegendIcon = changedLegendIcon;
  }
  public javax.swing.ImageIcon getUnchangedLegendIcon() {
    return unchangedLegendIcon;
  }
  public void setUnchangedLegendIcon(javax.swing.ImageIcon unchangedLegendIcon) {
    this.unchangedLegendIcon = unchangedLegendIcon;
  }

}

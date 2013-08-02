package net.saucefactory.swing.diff;

import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import net.saucefactory.io.diff.DiffFile;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class FileDiffTreeModel implements TreeModel {
  protected DiffFile diffFile = null;

  public FileDiffTreeModel() {
  }

  public FileDiffTreeModel(DiffFile diffFile) {
    this.diffFile = diffFile;
  }

  public Object getRoot() {
    return diffFile;
  }
  public Object getChild(Object parent, int index) {
    DiffFile tempDiff = (DiffFile)parent;
    DiffFile[] directories = tempDiff.getDirectories();
    DiffFile[] files = tempDiff.getFiles();
    int l = directories == null ? 0 : directories.length;
    if (directories != null
        && index < l) {
      return directories[index];
    } else if  (files != null
        && index < (l + files.length)) {
        return files[index - l];
    } else
      return null;
  }
  public int getChildCount(Object parent) {
    DiffFile tempDiff = (DiffFile)parent;
    int count = 0;
    DiffFile[] files = tempDiff.getFiles();
    if (files != null)
      count += files.length;
    DiffFile[] directories = tempDiff.getDirectories();
    if (directories != null)
      count += directories.length;
    return count;
  }
  public boolean isLeaf(Object node) {
    DiffFile tempDiff = (DiffFile)node;
    return !tempDiff.isDirectory();
  }
  public void valueForPathChanged(TreePath path, Object newValue) {
    /**@todo Implement this javax.swing.tree.TreeModel method*/
    throw new java.lang.UnsupportedOperationException("Method valueForPathChanged() not yet implemented.");
  }
  public int getIndexOfChild(Object parent, Object child) {
    DiffFile tempDiff = (DiffFile)parent;
    DiffFile[] directories = tempDiff.getDirectories();
    DiffFile[] files = tempDiff.getFiles();
    int l = directories.length;
    if (directories != null) {
      for (int i = 0; i < l; i++) {
        if (directories[i].equals(child))
          return i;
      }
    }
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        if (files[i].equals(child))
          return i + l;
      }
    }
    return -1;

  }
  public void addTreeModelListener(TreeModelListener l) {
    /**@todo Implement this javax.swing.tree.TreeModel method*/
    //throw new java.lang.UnsupportedOperationException("Method addTreeModelListener() not yet implemented.");
  }
  public void removeTreeModelListener(TreeModelListener l) {
    /**@todo Implement this javax.swing.tree.TreeModel method*/
    //throw new java.lang.UnsupportedOperationException("Method removeTreeModelListener() not yet implemented.");
  }
  public DiffFile getDiffFile() {
    return diffFile;
  }
  public void setDiffFile(DiffFile diffFile) {
    this.diffFile = diffFile;
  }

}
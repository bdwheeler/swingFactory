package net.saucefactory.io.diff;

import java.io.*;
import java.util.*;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class DiffFile implements Serializable {
  public static final int UNCHANGED = 0;
  public static final int ADDED = 1;
  public static final int DELETED = 2;
  public static final int CHANGED = 3;

  public static DiffFileComparator compares = new DiffFileComparator();
  private String relativePath = null;
  private long baseLength;
  private String basePath = null;
  private String compPath = null;
  private int status;
  private boolean isDirectory;
  private Collection directories = null;
  private Collection files = null;
  private String name = null;
  private long compLength;
  private long baseLastModified;
  private long compLastModified;
  private DiffFile parent = null;
  public boolean hasChanged = false;
  public boolean hasMissing = false;
  //public boolean isCompressed = false;
  public boolean isArchiveEntry = false;
  public boolean isArchive = false;
  public String archiveName = null;
  public String baseArchivePath = null;
  public String compArchivePath = null;
  public String entryName = null;

  public DiffFile() {
  }

  public boolean hasMixed() {
    return(hasChanged && hasMissing);
  }

  public String getRelativePath() {
    DiffFile p = getParent();
    StringBuffer buf = new StringBuffer(getName());
    while(p != null) {
      if(p.getParent() != null)
        buf.insert(0, p.name + java.io.File.separator);
      p = p.getParent();
    }
    return buf.toString();
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public long getBaseLastModified() {
    return baseLastModified;
  }

  public void setBaseLastModified(long baseLastModified) {
    this.baseLastModified = baseLastModified;
  }

  public long getBaseLength() {
    return baseLength;
  }

  public void setBaseLength(long baseLength) {
    this.baseLength = baseLength;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getCompPath() {
    return compPath;
  }

  public void setCompPath(String compPath) {
    this.compPath = compPath;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public void setIsDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }

  public List getDirectoryList() {
    if(directories == null)
      return null;
    return (ArrayList)directories;
  }

  public DiffFile[] getDirectories() {
    DiffFile[] _directories = null;
    if(directories != null && directories.size() > 0) {
      _directories = new DiffFile[directories.size()];
      _directories = (DiffFile[])directories.toArray(_directories);
    }
    return _directories;
  }

  public void addDirectory(DiffFile item) {
    if(directories == null)directories = new ArrayList();
    directories.add(item);
  }

  public void removeDirectory(DiffFile item) {
    if(directories != null && item != null)
      directories.remove(item);
  }

  public List getFileList() {
    if(files == null)
      return null;
    return (ArrayList)files;
  }

  public void sortItems() {
    if(directories != null && directories.size() > 0) {
      DiffFile[] items = new DiffFile[directories.size()];
      directories.toArray(items);
      Arrays.sort(items, compares);
      directories = new ArrayList();
      for(int i = 0; i < items.length; i++) {
        ((ArrayList)directories).add(i, items[i]);
        items[i].sortItems();
      }
    }
    if(files != null && files.size() > 0) {
      DiffFile[] items = new DiffFile[files.size()];
      files.toArray(items);
      Arrays.sort(items, compares);
      files = new ArrayList();
      for(int i = 0; i < items.length; i++)
        ((ArrayList)files).add(i, items[i]);
    }
  }

  public DiffFile[] getFiles() {
    DiffFile[] _files = null;
    if(files != null && files.size() > 0) {
      _files = new DiffFile[files.size()];
      _files = (DiffFile[])files.toArray(_files);
    }
    return _files;
  }

  public void addFile(DiffFile item) {
    if(files == null)files = new ArrayList();
    files.add(item);
  }

  public void removeFile(DiffFile item) {
    if(files != null && item != null)
      files.remove(item);
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCompLastModified() {
    return compLastModified;
  }

  public void setCompLastModified(long compLastModified) {
    this.compLastModified = compLastModified;
  }

  public long getCompLength() {
    return compLength;
  }

  public void setCompLength(long compLength) {
    this.compLength = compLength;
  }

  public DiffFile getParent() {
    return parent;
  }

  public void setParent(DiffFile parent) {
    this.parent = parent;
  }

  public String toString() {
    if(name == null)
      return super.toString();
    return name;
  }
}

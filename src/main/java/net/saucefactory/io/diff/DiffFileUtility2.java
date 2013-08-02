package net.saucefactory.io.diff;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */

import java.io.*;
import java.util.zip.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import net.saucefactory.swing.tree.SFStatusTree;
import net.saucefactory.swing.tree.SFStatusTree.StatusNode;
import com.ice.tar.TarArchive;
import com.ice.tar.TarInputStream;
import com.ice.tar.TarEntry;
import java.util.zip.GZIPInputStream;
import net.saucefactory.swing.utils.FileUtility;
import net.saucefactory.swing.common.FileNameComparator;

public class DiffFileUtility2 {
  public static final int RULE_CHECKSUM = 0; //calculate checksum everytime
  public static final int RULE_LAST_MODIFIED = 1; //is length is same and last modified is same, file is same
  public static final int RULE_NEVER_CHECKSUM = 2;

  private static int currentRule = RULE_NEVER_CHECKSUM;

  private static DiffFileComparator compares = new DiffFileComparator();
  private static FileNameComparator fileCompare = new FileNameComparator();

  public static void setCurrentComparisonRule(int rule) throws Exception {
    if(rule != RULE_CHECKSUM
        && rule != RULE_LAST_MODIFIED
        && rule != RULE_NEVER_CHECKSUM)
      throw new Exception("Invalid DiffFileUtility comparison rule.");
    currentRule = rule;
  }

  public DiffFileUtility2() {
  }

  public static DiffFile compare(String basePath, String compPath,
      IDiffListener listener) throws Exception {
    return compare(new File(basePath), new File(compPath), listener);
  }

  public static DiffFile compare(File baseDir, File compDir,
      IDiffListener listener) throws Exception {
    if(!baseDir.exists())
      throw new Exception("Base directory not found.");
    if(!compDir.exists())
      throw new Exception("Comparison directory not found.");
    DiffFile rtn = getUnchangedDiffFile(null, "", baseDir, compDir);
    rtn.setName("Root");
    boolean cntDirs = listener == null ? false : true;
    compare(rtn, baseDir, compDir, listener, cntDirs);
    return rtn;
  }

  private static boolean isZipOrJar(String testFile) {
    if(testFile != null)
      return FileUtility.isZipOrJar(testFile);
    return false;
  }

  private static boolean isTarFile(String testFile) {
    if(testFile != null)
      return FileUtility.isTarFile(testFile);
    return false;
  }

  private static boolean isGTar(String testFile) {
    if(testFile != null)
      return FileUtility.isGTar(testFile);
    return false;
  }

  protected static void compare(DiffFile rtn, File baseDir, File compDir, IDiffListener listener,
      boolean countDirs) throws Exception {
    //String basePath = baseDir == null ? null : baseDir.getParent();
    //String compPath = compDir == null ? null : compDir.getParent();
    //if(basePath == null)basePath = baseDir.getAbsolutePath();
    //if(compPath == null)compPath = compDir.getAbsolutePath();
    File[] baseFiles = baseDir == null ? null : baseDir.listFiles();
    File[] compFiles = compDir == null ? null : compDir.listFiles();
    if(listener != null && countDirs) {
      listener.postStart();
      if(countDirs) {
        File[] counted = null;
        if(baseFiles == null)
          counted = compFiles;
        else if(compFiles == null)
          counted = baseFiles;
        else if(baseFiles.length < compFiles.length)
          counted = compFiles;
        else
          counted = baseFiles;
        int dirCnt = getDirectoryCount(counted);
        //dirCnt += getAddedDirectoryCount(compFiles, countPath);
        listener.postTableCount(dirCnt);
      }
    }
    //sort them
    if(baseFiles != null)
      Arrays.sort(baseFiles, fileCompare);
    if(compFiles != null)
      Arrays.sort(compFiles, fileCompare);
    boolean advanceSrc = baseFiles != null;
    boolean advanceTgt = compFiles != null;
    int srcCnt = 0;
    int tgtCnt = 0;
    boolean moreSrc = baseFiles != null;
    boolean moreTgt = compFiles != null;
    File srcEntry = null;
    File tgtEntry = null;
    while(moreSrc || moreTgt) {
      if(moreSrc && advanceSrc) {
        if(srcCnt < baseFiles.length) {
          srcEntry = baseFiles[srcCnt];
          srcCnt++;
        }
        else {
          srcEntry = null;
          moreSrc = false;
        }
      }
      if(moreTgt && advanceTgt) {
        if(tgtCnt < compFiles.length) {
          tgtEntry = compFiles[tgtCnt];
          tgtCnt++;
        }
        else {
          tgtEntry = null;
          moreTgt = false;
        }
      }
      DiffFile newDiff = null;
      boolean dir = false;
      String tmpName = null;
      if(srcEntry == null && tgtEntry == null)
        break;
      else if(srcEntry == null) {
        newDiff = getAddedDiffFile(rtn, rtn.getCompPath(), tgtEntry);
        advanceTgt = true;
        dir = tgtEntry.isDirectory();
        tmpName = tgtEntry.getName();
      }
      else if(tgtEntry == null) {
        newDiff = getDeletedDiffFile(rtn, rtn.getBasePath(), srcEntry);
        advanceSrc = true;
        dir = srcEntry.isDirectory();
        tmpName = srcEntry.getName();
      }
      else {
        int tmpCompare = srcEntry.getName().compareToIgnoreCase(tgtEntry.getName());
        if(tmpCompare == 0) {
          if(isFileChanged(srcEntry, tgtEntry))
            newDiff = getChangedDiffFile(rtn, rtn.getBasePath(), srcEntry, tgtEntry);
          else
            newDiff = getUnchangedDiffFile(rtn, rtn.getBasePath(), srcEntry, tgtEntry);
          advanceSrc = true;
          advanceTgt = true;
          dir = srcEntry.isDirectory();
          tmpName = srcEntry.getName();
        }
        else if(tmpCompare > 0) {
          newDiff = getAddedDiffFile(rtn, rtn.getCompPath(), tgtEntry);
          advanceTgt = true;
          advanceSrc = false;
          dir = tgtEntry.isDirectory();
          tmpName = tgtEntry.getName();
        }
        else { //if(tmpCompare < 0) {
          newDiff = getDeletedDiffFile(rtn, rtn.getCompPath(), srcEntry);
          advanceTgt = false;
          advanceSrc = true;
          dir = srcEntry.isDirectory();
          tmpName = srcEntry.getName();
        }
      }
      //add it and check for dir etc.
      if(isZipOrJar(tmpName)) {
        rtn.addDirectory(newDiff);
        setDiffToZip(newDiff);
        addZipFile(newDiff, (srcEntry == null || !advanceSrc) ? null : new FileInputStream(srcEntry), (tgtEntry == null || !advanceTgt) ? null : new FileInputStream(tgtEntry));
      }
      else if(isTarFile(tmpName)) {
        rtn.addDirectory(newDiff);
        setDiffToZip(newDiff);
        addTarFile(newDiff, (srcEntry == null || !advanceSrc) ? null : new FileInputStream(srcEntry), (tgtEntry == null || !advanceTgt) ? null : new FileInputStream(tgtEntry), isGTar(newDiff.getName()));
      }
      else if(dir) {
        if(listener != null)
          listener.postFolderProcessing(newDiff.getName());
        rtn.addDirectory(newDiff);
        compare(newDiff, srcEntry, tgtEntry, listener, false);
      }
      else {
        rtn.addFile(newDiff);
      }
      if(newDiff.hasChanged)
        recursiveSetChanged(rtn);
    }
    if(rtn != null) {
      if(rtn.getDirectoryList() != null)
        Collections.sort(rtn.getDirectoryList(), compares);
      if(rtn.getFileList() != null)
        Collections.sort(rtn.getFileList(), compares);
    }
    if(listener != null && countDirs)
      listener.postEnd();
  }

  public static int getDirectoryCount(File[] files) {
    int cnt = 0;
    if(files != null) {
      for(int i = 0; i < files.length; i++) {
        if(files[i].isDirectory()) {
          cnt++;
          cnt += getDirectoryCount(files[i].listFiles());
        }
      }
    }
    return cnt;
  }

  public static int getAddedDirectoryCount(File[] files, String basePath) {
    int cnt = 0;
    if(files != null) {
      for(int i = 0; i < files.length; i++) {
        String path = basePath + files[i].getName();
        File baseFile = new File(path);
        if(!baseFile.exists()) {
          cnt++;
          cnt += getDirectoryCount(files[i].listFiles());
        }
      }
    }
    return cnt;
  }

  protected static boolean isFileChanged(File baseFile, File compFile) throws Exception {
    //dir
    if(baseFile.isDirectory()) {
      if(!compFile.isDirectory())
        return true;
      return baseFile.lastModified() != compFile.lastModified();
    }
    if(compFile.isDirectory())
      return true;
    //file
    if(baseFile.length() != compFile.length())
      return true;
    // bold assumption, time same, file same
    if(currentRule == RULE_LAST_MODIFIED || currentRule == RULE_NEVER_CHECKSUM)
      if(baseFile.lastModified() == compFile.lastModified())
        return false;
      else if(currentRule == RULE_NEVER_CHECKSUM)
        return true;
    // harder way
    FileInputStream fileInput = new FileInputStream(baseFile);
    Adler32 adler32 = new Adler32(); //checksum util
    CheckedInputStream checkInput = new CheckedInputStream(fileInput, adler32); // input that calculates checksum while reading
    while(checkInput.read() != -1) {} // read entire file
    long baseChecksum = checkInput.getChecksum().getValue();
    checkInput.close();
    fileInput.close();
    fileInput = new FileInputStream(compFile);
    adler32.reset();
    checkInput = new CheckedInputStream(fileInput, adler32);
    while(checkInput.read() != -1) {} // read entire file
    long compChecksum = checkInput.getChecksum().getValue();
    checkInput.close();
    fileInput.close();
    return baseChecksum != compChecksum;
  }

  protected static boolean isZipFileChanged(ZipEntry baseFile, ZipEntry compFile) throws Exception {
    if(baseFile.getSize() != compFile.getSize())
      return true;
    // bold assumption, time same, file same
    if(baseFile.getTime() == compFile.getTime())
      return false;
    return true;
  }

  protected static boolean isTarFileChanged(TarEntry baseFile, TarEntry compFile) throws Exception {
    if(baseFile.getSize() != compFile.getSize())
      return true;
    // bold assumption, time same, file same
    if(baseFile.getModTime().getTime() == compFile.getModTime().getTime())
      return false;
    return true;
  }

  protected static void recursiveSetChanged(DiffFile diffFile) {
    if(diffFile == null)return;
    if(diffFile.getStatus() == DiffFile.UNCHANGED)
      diffFile.setStatus(DiffFile.CHANGED);
    recursiveSetChanged(diffFile.getParent());
  }

  protected static void addZipFile(DiffFile parentDiff, InputStream src, InputStream tgt) {
    try {
      ZipInputStream srcZip = src == null ? null : new ZipInputStream(src);
      ZipInputStream tgtZip = tgt == null ? null : new ZipInputStream(tgt);
      boolean advanceSrc = src != null;
      boolean advanceTgt = tgt != null;
      boolean moreSrc = src != null;
      boolean moreTgt = tgt != null;
      ZipEntry srcEntry = null;
      ZipEntry tgtEntry = null;
      Hashtable dirHash = new Hashtable();
      //boolean first = true;
      //String archiveName = null;
      while(moreSrc || moreTgt) {
        if(moreSrc && advanceSrc) {
          try {
            srcEntry = srcZip.getNextEntry();
          }
          catch(Exception e) {
            srcEntry = null;
            moreSrc = false;
          }
        }
        if(moreTgt && advanceTgt) {
          try {
            tgtEntry = tgtZip.getNextEntry();
          }
          catch(Exception e) {
            tgtEntry = null;
            moreTgt = false;
          }
        }
        DiffFile newDiff = null;
        boolean dir = false;
        String tmpName = null;
        if(srcEntry == null && tgtEntry == null)
          break;
        //if(first) {
          //archiveName = srcEntry == null ? tgtEntry.getName() : srcEntry.getName();
          //first = false;
        //}
        //else {
          if(srcEntry == null) {
            newDiff = getAddedZipDiffFile(parentDiff, parentDiff.getCompPath(), tgtEntry);
            advanceTgt = true;
            dir = tgtEntry.isDirectory();
            tmpName = tgtEntry.getName();
          }
          else if(tgtEntry == null) {
            newDiff = getDeletedZipDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry);
            advanceSrc = true;
            dir = srcEntry.isDirectory();
            tmpName = srcEntry.getName();
          }
          else {
            int tmpCompare = srcEntry.getName().compareToIgnoreCase(tgtEntry.getName());
            if(tmpCompare == 0) {
              if(isZipFileChanged(srcEntry, tgtEntry))
                newDiff = getChangedZipDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry,
                    tgtEntry);
              else
                newDiff = getUnchangedZipDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry,
                    tgtEntry);
              advanceSrc = true;
              advanceTgt = true;
              dir = srcEntry.isDirectory();
              tmpName = srcEntry.getName();
            }
            else if(tmpCompare > 0) {
              newDiff = getAddedZipDiffFile(parentDiff, parentDiff.getCompPath(), tgtEntry);
              advanceTgt = true;
              advanceSrc = false;
              dir = tgtEntry.isDirectory();
              tmpName = tgtEntry.getName();
            }
            else { //if(tmpCompare < 0) {
              newDiff = getDeletedZipDiffFile(parentDiff, parentDiff.getCompPath(), srcEntry);
              advanceTgt = false;
              advanceSrc = true;
              dir = srcEntry.isDirectory();
              tmpName = srcEntry.getName();
            }
          }
          //trim archive name
          //trimArchiveName(newDiff, archiveName);
          //add it and check for dir etc.
          if(isZipOrJar(tmpName)) {
            parentDiff.addDirectory(newDiff);
            setDiffToZip(newDiff);
            addZipFile(newDiff, srcZip, tgtZip);
          }
          else if(isTarFile(tmpName)) {
            parentDiff.addDirectory(newDiff);
            setDiffToZip(newDiff);
            addTarFile(newDiff, srcZip, tgtZip, isGTar(newDiff.getName()));
          }
          else if(dir) {
            parentDiff.addDirectory(newDiff);
          }
          else {
            parentDiff.addFile(newDiff);
          }
          //if(newDiff.hasChanged)
            //recursiveSetChanged(parentDiff);
        //}
      }
      if(parentDiff != null) {
        if(parentDiff.getDirectoryList() != null)
          Collections.sort(parentDiff.getDirectoryList(), compares);
        if(parentDiff.getFileList() != null)
          Collections.sort(parentDiff.getFileList(), compares);
        sortArchiveDirectories(parentDiff);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected static void trimArchiveName(DiffFile diff, String archiveName) {
    if(diff != null && archiveName != null)
      diff.setName(diff.getName().replaceAll(archiveName, ""));
  }

  protected static void sortArchiveDirectories(DiffFile diff) {
    DiffFile[] dirs = diff.getDirectories();
    DiffFile[] files = diff.getFiles();
    Hashtable dirHash = new Hashtable();
    if(dirs != null) {
      for(int i = 0; i < dirs.length; i++) {
        String tmpParent = getParentDir(dirs[i].getName());
        dirHash.put(dirs[i].getName(), dirs[i]);
        dirs[i].entryName = dirs[i].getName();
        dirs[i].setName(trimDirName(dirs[i].getName()));
        if(tmpParent != null && dirHash.containsKey(tmpParent)) {
          DiffFile tmpParentDiff = (DiffFile)dirHash.get(tmpParent);
          tmpParentDiff.addDirectory(dirs[i]);
          diff.removeDirectory(dirs[i]);
        }
      }
    }
    if(files != null) {
      for(int i = 0; i < files.length; i++) {
        String tmpFolder = getDirName(files[i].getName());
        if(dirHash.containsKey(tmpFolder)) {
          DiffFile dirDiff = (DiffFile)dirHash.get(tmpFolder);
          files[i].entryName = files[i].getName();
          files[i].setName(trimFileName(files[i].getName()));
          dirDiff.addFile(files[i]);
          diff.removeFile(files[i]);
        }
      }
    }
  }

  protected static void addTarFile(DiffFile parentDiff, InputStream src, InputStream tgt, boolean isGtar) {
    try {
      TarInputStream srcTar = null;
      try {
        if(src != null) {
          if(isGtar)
            srcTar = new TarInputStream(new GZIPInputStream(src));
          else
            srcTar = new TarInputStream(src);
        }
      } catch(Exception e) {
        srcTar = null;
      }
      TarInputStream tgtTar = null;
      try {
        if(tgt != null) {
          if(isGtar)
            tgtTar = new TarInputStream(new GZIPInputStream(tgt));
          else
            tgtTar = new TarInputStream(tgt);
        }
      } catch(Exception e) {
        tgtTar = null;
      }
      boolean advanceSrc = src != null;
      boolean advanceTgt = tgt != null;
      boolean moreSrc = src != null;
      boolean moreTgt = tgt != null;
      TarEntry srcEntry = null;
      TarEntry tgtEntry = null;
      //String archiveName = null;
      //boolean first = true;
      while(moreSrc || moreTgt) {
        if(moreSrc && advanceSrc) {
          try {
            srcEntry = srcTar.getNextEntry();
          }
          catch(Exception e) {
            srcEntry = null;
            moreSrc = false;
          }
        }
        if(moreTgt && advanceTgt) {
          try {
            tgtEntry = tgtTar.getNextEntry();
          }
          catch(Exception e) {
            tgtEntry = null;
            moreTgt = false;
          }
        }
        DiffFile newDiff = null;
        boolean dir = false;
        String tmpName = null;
        if(srcEntry == null && tgtEntry == null)
          break;
        //if(first) {
          //archiveName = srcEntry == null ? tgtEntry.getName() : srcEntry.getName();
          //first = false;
        //}
        //else {
          if(srcEntry == null) {
            newDiff = getAddedTarDiffFile(parentDiff, parentDiff.getCompPath(), tgtEntry);
            advanceTgt = true;
            dir = tgtEntry.isDirectory();
            tmpName = tgtEntry.getName();
          }
          else if(tgtEntry == null) {
            newDiff = getDeletedTarDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry);
            advanceSrc = true;
            dir = srcEntry.isDirectory();
            tmpName = srcEntry.getName();
          }
          else {
            int tmpCompare = srcEntry.getName().compareToIgnoreCase(tgtEntry.getName());
            if(tmpCompare == 0) {
              if(isTarFileChanged(srcEntry, tgtEntry))
                newDiff = getChangedTarDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry,
                    tgtEntry);
              else
                newDiff = getUnchangedTarDiffFile(parentDiff, parentDiff.getBasePath(), srcEntry,
                    tgtEntry);
              advanceSrc = true;
              advanceTgt = true;
              dir = srcEntry.isDirectory();
              tmpName = srcEntry.getName();
            }
            else if(tmpCompare > 0) {
              newDiff = getAddedTarDiffFile(parentDiff, parentDiff.getCompPath(), tgtEntry);
              advanceTgt = true;
              advanceSrc = false;
              dir = tgtEntry.isDirectory();
              tmpName = tgtEntry.getName();
            }
            else { //if(tmpCompare < 0) {
              newDiff = getDeletedTarDiffFile(parentDiff, parentDiff.getCompPath(), srcEntry);
              advanceTgt = false;
              advanceSrc = true;
              dir = srcEntry.isDirectory();
              tmpName = srcEntry.getName();
            }
          }
          //trim archive name
          //trimArchiveName(newDiff, archiveName);
          //add it and check for dir etc.
          if(isZipOrJar(tmpName)) {
            parentDiff.addDirectory(newDiff);
            setDiffToZip(newDiff);
            addZipFile(newDiff, srcTar, tgtTar);
          }
          else if(isTarFile(tmpName)) {
            parentDiff.addDirectory(newDiff);
            setDiffToZip(newDiff);
            addTarFile(newDiff, srcTar, tgtTar, isGTar(newDiff.getName()));
          }
          else if(dir) {
            parentDiff.addDirectory(newDiff);
          }
          else {
            parentDiff.addFile(newDiff);
          }
          if(newDiff.hasChanged)
            recursiveSetChanged(parentDiff);
        }
        if(parentDiff != null) {
          if(parentDiff.getDirectoryList() != null)
            Collections.sort(parentDiff.getDirectoryList(), compares);
          if(parentDiff.getFileList() != null)
            Collections.sort(parentDiff.getFileList(), compares);
          sortArchiveDirectories(parentDiff);
        }
      //}
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected static String getDirName(String name) {
    if(name != null)
      return name.substring(0, name.lastIndexOf("/") + 1);
    return name;
  }

  protected static String trimDirName(String name) {
    if(name != null && name.length() > 0) {
      if(name.charAt(name.length() - 1) == '/')
        name = name.substring(0, name.length() - 1);
      if(name.indexOf("/") > -1)
        name = name.substring(name.lastIndexOf("/") + 1);
    }
    return name;
  }

  protected static String trimFileName(String name) {
    if(name != null) {
      if(name.indexOf("/") > -1)
        name = name.substring(name.lastIndexOf("/") + 1);
    }
    return name;
  }

  protected static String getParentDir(String name) {
    if(name != null) {
      name = name.substring(0, name.length() -1);
      if(name.indexOf("/") > -1) {
        String tmpStr = name.substring(0, name.lastIndexOf("/") + 1);
        return tmpStr;
      }
    }
    return null;
  }

  protected static void setDiffToZip(DiffFile file) {
    //file.isCompressed = true;
    file.isArchive = true;
    file.setIsDirectory(true);
    file.archiveName = file.getName();
    file.baseArchivePath = file.getBasePath();
    file.compArchivePath = file.getCompPath();
  }

  protected static DiffFile getUnchangedDiffFile(DiffFile parent, String relPath, File base,
      File comp) {
    DiffFile rtn = getDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.UNCHANGED);
    return rtn;
  }

  protected static DiffFile getUnchangedZipDiffFile(DiffFile parent, String relPath, ZipEntry base,
      ZipEntry comp) {
    DiffFile rtn = getZipDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.UNCHANGED);
    return rtn;
  }

  protected static DiffFile getUnchangedTarDiffFile(DiffFile parent, String relPath, TarEntry base,
      TarEntry comp) {
    DiffFile rtn = getTarDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.UNCHANGED);
    return rtn;
  }

  protected static DiffFile getDiffFile(DiffFile parent, String relPath, File base, File comp) {
    DiffFile rtn = new DiffFile();
    rtn.setParent(parent);
    rtn.setRelativePath(relPath);
    if(base != null)
      rtn.setBasePath(base.getAbsolutePath());
    if(comp != null)
      rtn.setCompPath(comp.getAbsolutePath());
    if(base != null)
      rtn.setBaseLastModified(base.lastModified());
    if(comp != null)
      rtn.setCompLastModified(comp.lastModified());
    if(base != null)
      rtn.setBaseLength(base.length());
    if(comp != null)
      rtn.setCompLength(comp.length());
    if(base != null)
      rtn.setIsDirectory(base.isDirectory());
    else
      rtn.setIsDirectory(comp.isDirectory());
    if(base != null)
      rtn.setName(base.getName());
    else if(comp != null)
      rtn.setName(comp.getName());

    return rtn;
  }

  protected static DiffFile getZipDiffFile(DiffFile parent, String relPath, ZipEntry base,
      ZipEntry comp) {
    DiffFile rtn = new DiffFile();
    rtn.setParent(parent);
    rtn.setRelativePath(relPath);
    //rtn.isCompressed = true;
    rtn.isArchiveEntry = true;
    rtn.archiveName = parent.archiveName;
    rtn.baseArchivePath = parent.baseArchivePath;
    rtn.compArchivePath = parent.compArchivePath;
    if(base != null)
      rtn.setBasePath(base.getName());
    if(comp != null)
      rtn.setCompPath(comp.getName());
    if(base != null)
      rtn.setBaseLastModified(base.getTime());
    if(comp != null)
      rtn.setCompLastModified(comp.getTime());
    if(base != null)
      rtn.setBaseLength(base.getSize());
    if(comp != null)
      rtn.setCompLength(comp.getSize());
    if(base != null)
      rtn.setIsDirectory(base.isDirectory());
    else
      rtn.setIsDirectory(comp.isDirectory());
    if(base != null)
      rtn.setName(base.getName());
    else if(comp != null)
      rtn.setName(comp.getName());
    rtn.entryName = rtn.getName();
    return rtn;
  }

  protected static DiffFile getTarDiffFile(DiffFile parent, String relPath, TarEntry base,
      TarEntry comp) {
    DiffFile rtn = new DiffFile();
    rtn.setParent(parent);
    rtn.setRelativePath(relPath);
    //rtn.isCompressed = true;
    rtn.isArchiveEntry = true;
    rtn.archiveName = parent.archiveName;
    rtn.baseArchivePath = parent.baseArchivePath;
    rtn.compArchivePath = parent.compArchivePath;
    if(base != null)
      rtn.setBasePath(base.getName());
    if(comp != null)
      rtn.setCompPath(comp.getName());
    if(base != null)
      rtn.setBaseLastModified(base.getModTime().getTime());
    if(comp != null)
      rtn.setCompLastModified(comp.getModTime().getTime());
    if(base != null)
      rtn.setBaseLength(base.getSize());
    if(comp != null)
      rtn.setCompLength(comp.getSize());
    if(base != null)
      rtn.setIsDirectory(base.isDirectory());
    else
      rtn.setIsDirectory(comp.isDirectory());
    if(base != null)
      rtn.setName(base.getName());
    else if(comp != null)
      rtn.setName(comp.getName());
    rtn.entryName = rtn.getName();
    return rtn;
  }

  protected static DiffFile getChangedDiffFile(DiffFile parent, String relPath, File base,
      File comp) {
    DiffFile rtn = getDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.CHANGED);
    parent.hasChanged = true;
    return rtn;
  }

  protected static DiffFile getChangedZipDiffFile(DiffFile parent, String relPath, ZipEntry base,
      ZipEntry comp) {
    DiffFile rtn = getZipDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.CHANGED);
    parent.hasChanged = true;
    return rtn;
  }

  protected static DiffFile getChangedTarDiffFile(DiffFile parent, String relPath, TarEntry base,
      TarEntry comp) {
    DiffFile rtn = getTarDiffFile(parent, relPath, base, comp);
    rtn.setStatus(DiffFile.CHANGED);
    parent.hasChanged = true;
    return rtn;
  }

  protected static DiffFile getAddedDiffFile(DiffFile parent, String relPath, File comp) {
    DiffFile rtn = getDiffFile(parent, relPath, null, comp);
    rtn.setStatus(DiffFile.ADDED);
    parent.hasMissing = true;
    return rtn;
  }

  protected static DiffFile getAddedZipDiffFile(DiffFile parent, String relPath, ZipEntry comp) {
    DiffFile rtn = getZipDiffFile(parent, relPath, null, comp);
    rtn.setStatus(DiffFile.ADDED);
    parent.hasMissing = true;
    return rtn;
  }

  protected static DiffFile getAddedTarDiffFile(DiffFile parent, String relPath, TarEntry comp) {
    DiffFile rtn = getTarDiffFile(parent, relPath, null, comp);
    rtn.setStatus(DiffFile.ADDED);
    parent.hasMissing = true;
    return rtn;
  }

  protected static DiffFile getDeletedDiffFile(DiffFile parent, String relPath, File base) {
    DiffFile rtn = getDiffFile(parent, relPath, base, null);
    rtn.setStatus(DiffFile.DELETED);
    parent.hasMissing = true;
    return rtn;
  }

  protected static DiffFile getDeletedZipDiffFile(DiffFile parent, String relPath, ZipEntry base) {
    DiffFile rtn = getZipDiffFile(parent, relPath, base, null);
    rtn.setStatus(DiffFile.DELETED);
    parent.hasMissing = true;
    return rtn;
  }

  protected static DiffFile getDeletedTarDiffFile(DiffFile parent, String relPath, TarEntry base) {
    DiffFile rtn = getTarDiffFile(parent, relPath, base, null);
    rtn.setStatus(DiffFile.DELETED);
    parent.hasMissing = true;
    return rtn;
  }

  public static void fillDiffTree(DiffFile diff, SFStatusTree tree) {
    tree.resetTree();
    int oldFilter = tree.currentFilter;
    tree.currentFilter = SFStatusTree.STATUS_ALL;
    tree.refreshRoot();
    if(diff != null) {
      StatusNode parent = tree.addBlankNode(tree.rootNode, diff.getName(), diff);
      DiffFile[] dirs = diff.getDirectories();
      appendDirectories(tree, parent, dirs);
      DiffFile[] files = diff.getFiles();
      appendFiles(tree, parent, files);
    }
    tree.setCurrentFilter(oldFilter);
    tree.refreshRoot();
  }

  public static void appendDirectories(SFStatusTree tree, StatusNode parent, DiffFile[] dirs) {
    if(dirs != null) {
      for(int i = 0; i < dirs.length; i++) {
        StatusNode node = tree.addBlankNode(parent, dirs[i].getName(), dirs[i]);
        setStatus(node, dirs[i]);
        node.forceDirectory = true;
        DiffFile[] subDirs = dirs[i].getDirectories();
        appendDirectories(tree, node, subDirs);
        DiffFile[] subFiles = dirs[i].getFiles();
        appendFiles(tree, node, subFiles);
        appendStatus(parent, node.status);
      }
    }
  }

  public static void appendFiles(SFStatusTree tree, StatusNode parent, DiffFile[] files) {
    if(files != null) {
      for(int i = 0; i < files.length; i++) {
        StatusNode node = tree.addBlankNode(parent, files[i].getName(), files[i]);
        setStatus(node, files[i]);
        appendStatus(parent, node.status);
      }
    }
  }

  public static void setStatus(StatusNode node, DiffFile file) {
    int status = file.getStatus();
    if(status == DiffFile.UNCHANGED)
      node.setStatus(SFStatusTree.STATUS_UNCHANGED);
    else if(status == DiffFile.CHANGED)
      node.setStatus(SFStatusTree.STATUS_CHANGED);
    else if(status == DiffFile.DELETED)
      node.setStatus(SFStatusTree.STATUS_DELETED);
    else if(status == DiffFile.ADDED)
      node.setStatus(SFStatusTree.STATUS_ADDED);
  }

  public static void appendStatus(StatusNode node, int status) {
    if(status != SFStatusTree.STATUS_UNCHANGED)
      node.appendStatus(status);
  }
}

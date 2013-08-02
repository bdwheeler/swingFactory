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

public class DiffFileUtility {
  public static final int RULE_CHECKSUM = 0; //calculate checksum everytime
  public static final int RULE_LAST_MODIFIED = 1; //is length is same and last modified is same, file is same
  public static final int RULE_NEVER_CHECKSUM = 2;

  private static int currentRule = RULE_NEVER_CHECKSUM;

  private static DiffFileComparator compares = new DiffFileComparator();

  public static void setCurrentComparisonRule(int rule) throws Exception {
    if(rule != RULE_CHECKSUM
        && rule != RULE_LAST_MODIFIED
        && rule != RULE_NEVER_CHECKSUM)
      throw new Exception("Invalid DiffFileUtility comparison rule.");
    currentRule = rule;
  }

  public DiffFileUtility() {
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

  private static boolean isZipOrJar(File testFile) {
    if(testFile != null)
      return FileUtility.isZipOrJar(testFile.getName());
    return false;
  }

  private static boolean isTarFile(File testFile) {
    if(testFile != null)
      return FileUtility.isTarFile(testFile.getName());
    return false;
  }

  private static boolean isGTar(File testFile) {
    if(testFile != null)
      return FileUtility.isGTar(testFile.getName());
    return false;
  }

  protected static void compare(DiffFile rtn, File baseDir, File compDir, IDiffListener listener,
      boolean countDirs) throws Exception {
    StringBuffer currRelPath = new StringBuffer(baseDir.getName());
    String basePath = baseDir.getParent();
    String compPath = compDir.getParent();
    if(basePath == null)basePath = baseDir.getAbsolutePath();
    if(compPath == null)compPath = compDir.getAbsolutePath();
    File[] baseFiles = baseDir.listFiles();
    File[] compFiles = compDir.listFiles();
    if(listener != null) {
      listener.postStart();
      if(countDirs) {
        String countPath = basePath + (baseDir.getName() == null ? "" :
            File.separator + baseDir.getName() + File.separator);
        int dirCnt = getDirectoryCount(baseFiles);
        dirCnt += getAddedDirectoryCount(compFiles, countPath);
        listener.postTableCount(dirCnt);
      }
    }
    for(int i = 0; i < baseFiles.length; i++) {
      String path = compPath + (compDir.getName() == null ? "" :
          File.separator + compDir.getName() + File.separator) + baseFiles[i].getName();
      File compFile = new File(path);
      if(!compFile.exists()) {
        String pre = rtn.getParent() == null ? "" : baseDir.getName();
        String path2 = pre + File.separator + baseFiles[i].getName();
        DiffFile newDiff = getDeletedDiffFile(rtn, path2, baseFiles[i]);
        if(isZipOrJar(baseFiles[i])) {
          setDiffToZip(newDiff);
          addZipFile(path2, newDiff, baseFiles[i], null, true, false);
          rtn.addDirectory(newDiff);
        }
        else if(isTarFile(baseFiles[i])) {
          setDiffToZip(newDiff);
          addTarFile(path2, newDiff, baseFiles[i], null, true, false);
          rtn.addDirectory(newDiff);
        }
        else if(!baseFiles[i].isDirectory()) {
          rtn.addFile(newDiff);
        }
        else {
          if(listener != null)
            listener.postFolderProcessing(baseFiles[i].getName());
          addChildren(path2, newDiff, baseFiles[i], true, listener);
          rtn.addDirectory(newDiff);
        }
        recursiveSetChanged(newDiff);
      }
      else {
        String pre = rtn.getParent() == null ? "" : baseDir.getName();
        String path2 = pre + File.separator + baseFiles[i].getName();
        if(isZipOrJar(baseFiles[i])) {
          boolean changed = isFileChanged(baseFiles[i], compFile);
          DiffFile newDiff = null;
          if(changed) {
            newDiff = getChangedDiffFile(rtn, path2, baseFiles[i], compFile);
            recursiveSetChanged(newDiff);
          }
          else {
            newDiff = getUnchangedDiffFile(rtn, path2, baseFiles[i], compFile);
          }
          setDiffToZip(newDiff);
          addZipFile(path2, newDiff, baseFiles[i], compFile, false, false);
          rtn.addDirectory(newDiff);
        }
        else if(isTarFile(baseFiles[i])) {
          boolean changed = isFileChanged(baseFiles[i], compFile);
          DiffFile newDiff = null;
          if(changed) {
            newDiff = getChangedDiffFile(rtn, path2, baseFiles[i], compFile);
            recursiveSetChanged(newDiff);
          }
          else {
            newDiff = getUnchangedDiffFile(rtn, path2, baseFiles[i], compFile);
          }
          setDiffToZip(newDiff);
          addTarFile(path2, newDiff, baseFiles[i], compFile, false, false);
          rtn.addDirectory(newDiff);
        }
        else if(baseFiles[i].isFile()) {
          boolean changed = isFileChanged(baseFiles[i], compFile);
          DiffFile newDiff = null;
          if(changed) {
            newDiff = getChangedDiffFile(rtn, path2, baseFiles[i], compFile);
            recursiveSetChanged(newDiff);
          }
          else {
            newDiff = getUnchangedDiffFile(rtn, path2, baseFiles[i], compFile);
          }
          rtn.addFile(newDiff);
        }
        else {
          if(listener != null)
            listener.postFolderProcessing(baseFiles[i].getName());
          DiffFile newDiff = getUnchangedDiffFile(rtn, path2, baseFiles[i], compFile);
          compare(newDiff, baseFiles[i], compFile, listener, false);
          rtn.addDirectory(newDiff);
        }
      }
    }
    for(int i = 0; i < compFiles.length; i++) {
      String path = basePath + (baseDir.getName() == null ? "" :
          File.separator + baseDir.getName() + File.separator) + compFiles[i].getName();
      File baseFile = new File(path);
      if(!baseFile.exists()) {
        String pre = rtn.getParent() == null ? "" : compDir.getName();
        String path2 = pre + File.separator + compFiles[i].getName();
        DiffFile newDiff = getAddedDiffFile(rtn, path2, compFiles[i]);
        if(isZipOrJar(compFiles[i])) {
          setDiffToZip(newDiff);
          addZipFile(path2, newDiff, compFiles[i], null, false, true);
          rtn.addDirectory(newDiff);
        }
        else if(isTarFile(compFiles[i])) {
          setDiffToZip(newDiff);
          addTarFile(path2, newDiff, compFiles[i], null, false, true);
          rtn.addDirectory(newDiff);
        }
        else if(compFiles[i].isFile())
          rtn.addFile(newDiff);
        else {
          if(listener != null)
            listener.postFolderProcessing(compFiles[i].getName());
          addChildren(path, newDiff, compFiles[i], false, listener);
          rtn.addDirectory(newDiff);
        }
        recursiveSetChanged(newDiff);
      }
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
    if(compFile.isDirectory())return true;
    // easier way
    if(baseFile.length() != compFile.length())return true;
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

  protected static void addZipFile(String relPath, DiffFile diffFile, File file, File comp, boolean deleted, boolean added) {
    try {
      Hashtable entryHash = new Hashtable();
      Hashtable compHash = new Hashtable();
      ZipInputStream zip = file == null ? null : new ZipInputStream(new FileInputStream(file));
      ZipInputStream compZip = comp == null ? null : new ZipInputStream(new FileInputStream(comp));
      //Enumeration zipEnum = zip == null ? null : zip.entries();
      //Enumeration compZipEnum = compZip == null ? null : compZip.entries();
      boolean haveComp = compZip != null;
      if(haveComp) {
        ZipEntry compEntry = null;
        while(true) {
          try {
            compEntry = compZip.getNextEntry(); //Enum.nextElement(); //.getNextEntry();
          } catch(Exception e) {
            break;
          }
          if(compEntry == null)
            break;
          compHash.put(compEntry.getName(), compEntry);
        }
      }
      ZipEntry tmpEntry = null;
      while(true) { //zipEnum != null && zipEnum.hasMoreElements()) { //.available() > 0) {
        try {
          tmpEntry = zip.getNextEntry();//ZipEntry)zipEnum.nextElement(); //zip.getNextEntry();
        } catch(Exception e) {
          break;
        }
        if(tmpEntry == null)
          break;
        ZipEntry tmpCompEntry = null;
        if(haveComp && compHash.containsKey(tmpEntry.getName()))
          tmpCompEntry = (ZipEntry)compHash.remove(tmpEntry.getName());
        DiffFile newDiff = null;
        if(deleted) {
          newDiff = getDeletedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        }
        else if(added) {
          newDiff = getAddedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        }
        else if(haveComp && tmpCompEntry == null)
          newDiff = getDeletedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        else {
          if(isZipFileChanged(tmpEntry, tmpCompEntry)) {
            newDiff = getChangedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry, tmpCompEntry);
            recursiveSetChanged(newDiff);
          }
          else {
            newDiff = getUnchangedZipDiffFile(diffFile,
                relPath + File.separator + tmpEntry.getName(),
                tmpEntry, tmpCompEntry);
          }
        }
        if(isZipOrJar(tmpEntry.getName())) {
          setDiffToZip(newDiff);
          addZipFile(relPath + File.separator + tmpEntry.getName(), newDiff, zip, compZip, added, deleted);
              //zip == null ? null : new ZipInpuzip.getInputStream(tmpEntry), tmpCompEntry == null ? null : compZip.getInputStream(tmpCompEntry), deleted, added);
          diffFile.addDirectory(newDiff);
        }
        else if(isTarFile(tmpEntry.getName())) {
          setDiffToZip(newDiff);
          addTarFile(relPath + File.separator + tmpEntry.getName(), newDiff, zip, compZip, added, deleted);
              //zip == null ? null : zip.getInputStream(tmpEntry), tmpCompEntry == null ? null : compZip.getInputStream(tmpCompEntry), deleted, added);
          diffFile.addDirectory(newDiff);
        }
        else if(tmpEntry.isDirectory()) {
          //get parent dir...
          String tmpParent = getParentDir(tmpEntry.getName());
          newDiff.setIsDirectory(true);
          entryHash.put(tmpEntry.getName(), newDiff);
          newDiff.entryName = newDiff.getName();
          newDiff.setName(trimDirName(newDiff.getName()));
          if(tmpParent == null || !entryHash.containsKey(tmpParent))
            diffFile.addDirectory(newDiff);
          else {
            DiffFile tmpParentDiff = (DiffFile)entryHash.get(tmpParent);
            tmpParentDiff.addDirectory(newDiff);
          }
        }
        else {
          newDiff.setIsDirectory(false);
          String tmpFolder = getDirName(tmpEntry.getName());
          if(entryHash.containsKey(tmpFolder)) {
            DiffFile addDiff = (DiffFile)entryHash.get(tmpFolder);
            newDiff.entryName = newDiff.getName();
            newDiff.setName(trimFileName(newDiff.getName()));
            addDiff.addFile(newDiff);
          }
          else
            diffFile.addFile(newDiff);
        }
      }
      if(compHash.size() > 0) {
        Enumeration addedEnum = compHash.elements();
        while(addedEnum.hasMoreElements()) {
          ZipEntry tmpAddEntry = (ZipEntry)addedEnum.nextElement();
          DiffFile newDiff = getAddedZipDiffFile(diffFile, relPath + File.separator + tmpAddEntry.getName(),
                tmpAddEntry);
          if(tmpAddEntry.isDirectory())
            diffFile.addDirectory(newDiff);
          else
            diffFile.addFile(newDiff);
        }
        //diffFile.sortItems();
      }
      try {
        if(zip != null)
          zip.close();
        if(compZip != null)
          compZip.close();
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
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
      while(true) {
        if(moreSrc && advanceSrc) {
          try {
            srcEntry = srcZip.getNextEntry();
          } catch(Exception e) {
            srcEntry = null;
            moreSrc = false;
          }
        }
        if(moreTgt && advanceTgt) {
          try {
            tgtEntry = tgtZip.getNextEntry();
          } catch(Exception e) {
            tgtEntry = null;
            moreTgt = false;
          }
        }
        DiffFile newDiff = null;
        boolean dir = false;
        String tmpName = null;
        if(srcEntry == null && tgtEntry == null)
          return;
        else if(srcEntry == null) {
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
          int tmpCompare = srcEntry.getName().compareTo(tgtEntry.getName());
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
        //add it and check for dir etc.
        if(isZipOrJar(tmpName)) {
          parentDiff.addDirectory(newDiff);
          setDiffToZip(newDiff);
          addZipFile(newDiff, srcZip, tgtZip);
        }
        else if(isTarFile(tmpName)) {
          parentDiff.addDirectory(newDiff);
          setDiffToZip(newDiff);
          //addTarFile(newDiff, srcZip, tgtZip);
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
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected static void addZipFile(String relPath, DiffFile diffFile, InputStream file, InputStream comp, boolean deleted, boolean added) {
    try {
      Hashtable entryHash = new Hashtable();
      Hashtable compHash = new Hashtable();
      ZipInputStream zip = file == null ? null : new ZipInputStream(file);
      ZipInputStream compZip = comp == null ? null : new ZipInputStream(comp);
      boolean haveComp = compZip != null;
      boolean haveBase = zip != null;
      if(haveComp) {
        ZipEntry compEntry = null;
        while(true) {
          try {
            compEntry = zip.getNextEntry();
            if(compEntry != null)
              compHash.put(compEntry.getName(), compEntry);
            else
              break;
          }
          catch(Exception e) {
            break;
          }
        }
      }
      ZipEntry tmpEntry = null;
      while(true) {
        try {
          tmpEntry = zip.getNextEntry();
        } catch(Exception e) {
          break;
        }
        if(tmpEntry == null)
          break;
        ZipEntry tmpCompEntry = null;
        if(haveComp && compHash.containsKey(tmpEntry.getName()))
          tmpCompEntry = (ZipEntry)compHash.remove(tmpEntry.getName());
        DiffFile newDiff = null;
        if(deleted) {
          newDiff = getDeletedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        }
        else if(added) {
          newDiff = getAddedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        }
        else if(haveComp && tmpCompEntry == null)
          newDiff = getDeletedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry);
        else {
          if(isZipFileChanged(tmpEntry, tmpCompEntry)) {
            newDiff = getChangedZipDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
              tmpEntry, tmpCompEntry);
            recursiveSetChanged(newDiff);
          }
          else {
            newDiff = getUnchangedZipDiffFile(diffFile,
                relPath + File.separator + tmpEntry.getName(),
                tmpEntry, tmpCompEntry);
          }
        }
        if(isZipOrJar(tmpEntry.getName())) {
          setDiffToZip(newDiff);
          addZipFile(relPath + File.separator + tmpEntry.getName(), newDiff, zip, compZip, added, deleted);
          newDiff.addDirectory(newDiff);
        }
        else if(isTarFile(tmpEntry.getName())) {
          setDiffToZip(newDiff);
          addTarFile(relPath + File.separator + tmpEntry.getName(), newDiff, zip, compZip, added, deleted);
          newDiff.addDirectory(newDiff);
        }
        else if(tmpEntry.isDirectory()) {
          //get parent dir...
          String tmpParent = getParentDir(tmpEntry.getName());
          newDiff.setIsDirectory(true);
          entryHash.put(tmpEntry.getName(), newDiff);
          newDiff.entryName = newDiff.getName();
          newDiff.setName(trimDirName(newDiff.getName()));
          if(tmpParent == null || !entryHash.containsKey(tmpParent))
            diffFile.addDirectory(newDiff);
          else {
            DiffFile tmpParentDiff = (DiffFile)entryHash.get(tmpParent);
            tmpParentDiff.addDirectory(newDiff);
          }
        }
        else {
          newDiff.setIsDirectory(false);
          String tmpFolder = getDirName(tmpEntry.getName());
          if(entryHash.containsKey(tmpFolder)) {
            DiffFile addDiff = (DiffFile)entryHash.get(tmpFolder);
            newDiff.entryName = newDiff.getName();
            newDiff.setName(trimFileName(newDiff.getName()));
            addDiff.addFile(newDiff);
          }
          else
            diffFile.addFile(newDiff);
        }
        if(FileUtility.isZipOrJar(newDiff.getName())) {
          //setDiffToZip(newDiff, );
          //addZipFile(path2, newDiff, compFiles[i], null, false, true);
          //rtn.addDirectory(newDiff);
        }
        else if(FileUtility.isTarFile(newDiff.getName())) {
          //setDiffToZip(newDiff, compFiles[i]);
          //addTarFile(path2, newDiff, compFiles[i], null, false, true);
          //rtn.addDirectory(newDiff);
        }
      }
      if(compHash.size() > 0) {
        Enumeration addedEnum = compHash.elements();
        while(addedEnum.hasMoreElements()) {
          ZipEntry tmpAddEntry = (ZipEntry)addedEnum.nextElement();
          DiffFile newDiff = getAddedZipDiffFile(diffFile, relPath + File.separator + tmpAddEntry.getName(),
                tmpAddEntry);
          if(tmpAddEntry.isDirectory())
            diffFile.addDirectory(newDiff);
          else
            diffFile.addFile(newDiff);
        }
        //diffFile.sortItems();
      }
      try {
        if(zip != null)
          zip.close();
        if(compZip != null)
          compZip.close();
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected static void addTarFile(String relPath, DiffFile diffFile, File file, File comp, boolean deleted, boolean added) {
    try {
      Hashtable entryHash = new Hashtable();
      Hashtable compHash = new Hashtable();
      TarInputStream srcTar = null;
      boolean isGtar = false;
      try {
        if(file != null) {
          if(isGTar(file)) {
            isGtar = true;
            srcTar = new TarInputStream(new GZIPInputStream(new FileInputStream(file)));
          }
          else
            srcTar = new TarInputStream(new FileInputStream(file));
        }
      } catch(Exception e) {
        srcTar = null;
      }
      TarInputStream tgtTar = null;
      try {
        if(comp != null) {
          if(isGtar)
            tgtTar = new TarInputStream(new GZIPInputStream(new FileInputStream(comp)));
          else
            tgtTar = new TarInputStream(new FileInputStream(comp));
        }
      } catch(Exception e) {
        tgtTar = null;
      }
      boolean haveComp = tgtTar != null;
      if(haveComp) {
        boolean more = true;
        while(more) {
          try {
            TarEntry compEntry = (TarEntry)tgtTar.getNextEntry();
            if(compEntry != null)
              compHash.put(compEntry.getName(), compEntry);
            else
              more = false;
          }
          catch(Exception e) {
            more = false;
          }
        }
      }
      boolean more = srcTar != null;
      while(more) {
        TarEntry tmpEntry;
        try {
          tmpEntry = (TarEntry)srcTar.getNextEntry();
          if(tmpEntry == null)
            more = false;
        }
        catch(Exception e) {
          more = false;
          tmpEntry = null;
        }
        if(tmpEntry != null) {
          TarEntry tmpCompEntry = null;
          if(haveComp && compHash.containsKey(tmpEntry.getName()))
            tmpCompEntry = (TarEntry)compHash.remove(tmpEntry.getName());
          DiffFile newDiff = null;
          if(deleted) {
            newDiff = getDeletedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          }
          else if(added) {
            newDiff = getAddedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          }
          else if(haveComp && tmpCompEntry == null)
            newDiff = getDeletedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          else {
            if(isTarFileChanged(tmpEntry, tmpCompEntry)) {
              newDiff = getChangedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                  tmpEntry, tmpCompEntry);
              recursiveSetChanged(newDiff);
            }
            else {
              newDiff = getUnchangedTarDiffFile(diffFile,
                  relPath + File.separator + tmpEntry.getName(),
                  tmpEntry, tmpCompEntry);
            }
          }
          if(isZipOrJar(tmpEntry.getName())) {
            setDiffToZip(newDiff);
            addZipFile(relPath + File.separator + tmpEntry.getName(), newDiff,
                srcTar == null ? null : new TarInputStream(srcTar),
                tgtTar == null ? null : new TarInputStream(tgtTar), deleted, added);
            diffFile.addDirectory(newDiff);
          }
          else if(isTarFile(tmpEntry.getName())) {
            setDiffToZip(newDiff);
            addTarFile(relPath + File.separator + tmpEntry.getName(), newDiff,
                srcTar == null ? null : new TarInputStream(srcTar),
                tgtTar == null ? null : new TarInputStream(tgtTar), deleted, added);
            diffFile.addDirectory(newDiff);
          }
          else if(tmpEntry.isDirectory()) {
            //get parent dir...
            String tmpParent = getParentDir(tmpEntry.getName());
            newDiff.setIsDirectory(true);
            entryHash.put(tmpEntry.getName(), newDiff);
            newDiff.entryName = newDiff.getName();
            newDiff.setName(trimDirName(newDiff.getName()));
            if(tmpParent == null || !entryHash.containsKey(tmpParent))
              diffFile.addDirectory(newDiff);
            else {
              DiffFile tmpParentDiff = (DiffFile)entryHash.get(tmpParent);
              tmpParentDiff.addDirectory(newDiff);
            }
          }
          else {
            newDiff.setIsDirectory(false);
            String tmpFolder = getDirName(tmpEntry.getName());
            if(entryHash.containsKey(tmpFolder)) {
              DiffFile addDiff = (DiffFile)entryHash.get(tmpFolder);
              newDiff.entryName = newDiff.getName();
              newDiff.setName(trimFileName(newDiff.getName()));
              addDiff.addFile(newDiff);
            }
            else
              diffFile.addFile(newDiff);
          }
        }
        if(compHash.size() > 0) {
          Enumeration addedEnum = compHash.elements();
          while(addedEnum.hasMoreElements()) {
            TarEntry tmpAddEntry = (TarEntry)addedEnum.nextElement();
            DiffFile newDiff = getAddedTarDiffFile(diffFile,
                relPath + File.separator + tmpAddEntry.getName(),
                tmpAddEntry);

          }
        }
        try {
          if(srcTar != null)
            srcTar.close();
          if(tgtTar != null)
            tgtTar.close();
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected static void addTarFile(String relPath, DiffFile diffFile, InputStream tar, InputStream compTar, boolean deleted, boolean added) {
    try {
      Hashtable entryHash = new Hashtable();
      Hashtable compHash = new Hashtable();
      TarInputStream srcTar = null;
      boolean isGtar = false;
      try {
        if(tar != null) {
          if(isGTar(diffFile.getName())) {
            isGtar = true;
            srcTar = new TarInputStream(new GZIPInputStream(tar));
          }
          else
            srcTar = new TarInputStream(tar);
        }
      } catch(Exception e) {
        srcTar = null;
      }
      TarInputStream tgtTar = null;
      try {
        if(compTar != null) {
          if(isGtar)
            tgtTar = new TarInputStream(new GZIPInputStream(compTar));
          else
            tgtTar = new TarInputStream(compTar);
        }
      } catch(Exception e) {
        tgtTar = null;
      }
      boolean haveComp = tgtTar != null;
      if(haveComp) {
        boolean more = true;
        while(more) {
          try {
            TarEntry compEntry = (TarEntry)tgtTar.getNextEntry();
            if(compEntry != null)
              compHash.put(compEntry.getName(), compEntry);
            else
              more = false;
          }
          catch(Exception e) {
            more = false;
          }
        }
      }
      boolean more = srcTar != null;
      while(more) {
        TarEntry tmpEntry;
        try {
          tmpEntry = (TarEntry)srcTar.getNextEntry();
          if(tmpEntry == null)
            more = false;
        }
        catch(Exception e) {
          more = false;
          tmpEntry = null;
        }
        if(tmpEntry != null) {
          TarEntry tmpCompEntry = null;
          if(haveComp && compHash.containsKey(tmpEntry.getName()))
            tmpCompEntry = (TarEntry)compHash.remove(tmpEntry.getName());
          DiffFile newDiff = null;
          if(deleted) {
            newDiff = getDeletedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          }
          else if(added) {
            newDiff = getAddedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          }
          else if(haveComp && tmpCompEntry == null)
            newDiff = getDeletedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                tmpEntry);
          else {
            if(isTarFileChanged(tmpEntry, tmpCompEntry)) {
              newDiff = getChangedTarDiffFile(diffFile, relPath + File.separator + tmpEntry.getName(),
                  tmpEntry, tmpCompEntry);
              recursiveSetChanged(newDiff);
            }
            else {
              newDiff = getUnchangedTarDiffFile(diffFile,
                  relPath + File.separator + tmpEntry.getName(),
                  tmpEntry, tmpCompEntry);
            }
          }
          if(tmpEntry.isDirectory()) {
            //get parent dir...
            String tmpParent = getParentDir(tmpEntry.getName());
            newDiff.setIsDirectory(true);
            entryHash.put(tmpEntry.getName(), newDiff);
            newDiff.entryName = newDiff.getName();
            newDiff.setName(trimDirName(newDiff.getName()));
            if(tmpParent == null || !entryHash.containsKey(tmpParent))
              diffFile.addDirectory(newDiff);
            else {
              DiffFile tmpParentDiff = (DiffFile)entryHash.get(tmpParent);
              tmpParentDiff.addDirectory(newDiff);
            }
          }
          else {
            newDiff.setIsDirectory(false);
            String tmpFolder = getDirName(tmpEntry.getName());
            if(entryHash.containsKey(tmpFolder)) {
              DiffFile addDiff = (DiffFile)entryHash.get(tmpFolder);
              newDiff.entryName = newDiff.getName();
              newDiff.setName(trimFileName(newDiff.getName()));
              addDiff.addFile(newDiff);
            }
            else
              diffFile.addFile(newDiff);
          }
        }
        if(compHash.size() > 0) {
          Enumeration addedEnum = compHash.elements();
          while(addedEnum.hasMoreElements()) {
            TarEntry tmpAddEntry = (TarEntry)addedEnum.nextElement();
            DiffFile newDiff = getAddedTarDiffFile(diffFile,
                relPath + File.separator + tmpAddEntry.getName(),
                tmpAddEntry);

          }
        }
        try {
          if(srcTar != null)
            srcTar.close();
          if(tgtTar != null)
            tgtTar.close();
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }
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
    if(name != null) {
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

  protected static void addChildren(String relPath, DiffFile diffFile, File dir, boolean deleted,
      IDiffListener listener) {
    File[] children = dir.listFiles();
    for(int i = 0; i < children.length; i++) {
      DiffFile newDiff = null;
      if(deleted)
        newDiff = getDeletedDiffFile(diffFile, relPath + File.separator + children[i].getName(),
            children[i]);
      else
        newDiff = getAddedDiffFile(diffFile, relPath + File.separator + children[i].getName(),
            children[i]);
      if(isZipOrJar(children[i])) {
        setDiffToZip(newDiff);
        addZipFile(relPath + File.separator + dir.getName(), newDiff, (deleted ? children[i] : null), (deleted ? null : children[i]), deleted, !deleted);
        diffFile.addDirectory(newDiff);
      }
      else if(isTarFile(children[i])) {
        setDiffToZip(newDiff);
        addTarFile(relPath + File.separator + dir.getName(), newDiff, (deleted ? children[i] : null), (deleted ? null : children[i]), deleted, !deleted);
        diffFile.addDirectory(newDiff);
      }
      else if(children[i].isFile()) {
        newDiff.setIsDirectory(false);
        diffFile.addFile(newDiff);
      }
      else {
        newDiff.setIsDirectory(true);
        if(listener != null)
          listener.postFolderProcessing(children[i].getName());
        addChildren(relPath + File.separator + children[i].getName(), newDiff, children[i], deleted,
            listener);
        diffFile.addDirectory(newDiff);
      }
    }
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

  public static void main(String[] args) {
    try {
      DiffFile diffFile = DiffFileUtility.compare(new File("F:\\FileTest\\A"),
          new File("F:\\FileTest\\B"), null);
      System.out.println("!");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      System.exit(0);
    }
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

package net.saucefactory.swing.utils;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.nio.channels.FileChannel;
import javax.swing.JFileChooser;
import java.awt.Component;

public class FileUtility
{
  public static String fileSep = System.getProperty("file.separator");
  public static String appPath = System.getProperty("user.dir");
  public static java.net.URL docBase = null;

  public static String readFile(String fileName) throws Exception {
    return readXMLFileFromDisk(fileName);
  }

  public static boolean isZipOrJar(String testFile) {
    if(testFile != null) {
      testFile = testFile.toLowerCase();
      if(testFile.endsWith(".zip") ||
          testFile.endsWith(".jar") ||
          testFile.endsWith(".ear") ||
          testFile.endsWith(".war"))
        return true;
    }
    return false;
  }

  public static boolean isTarFile(String testFile) {
    if(testFile != null) {
      testFile = testFile.toLowerCase();
      if(testFile.endsWith(".tar") ||
          testFile.endsWith(".tgz"))
        return true;
    }
    return false;
  }

  public static boolean isGTar(String testFile) {
    if(testFile != null) {
      testFile = testFile.toLowerCase();
      if(testFile.endsWith(".tgz") ||
          testFile.endsWith(".gz"))
        return true;
    }
    return false;
  }

/*
  public static String readWebFile(String fileName) throws Exception {
    InputStream istream = null;
    try {
      checkDocBase();
      docBase = com.caiso.slic.client.services.WebSystemSettingsService.getDocumentBase();
      java.net.URL url = new java.net.URL(docBase, fileName);
      java.net.URLConnection conn = (java.net.URLConnection)url.openConnection();
      // ** Configure the connection
      conn.setUseCaches( false );
      istream = conn.getInputStream();
      StringBuffer finalString = new StringBuffer();
      int ch;
      while ((ch = istream.read()) != -1) {
       finalString.append((char)ch);
      }
      return finalString.toString();
    } catch(Exception e) {
      throw e;
    } finally {
      try { istream.close();} catch (Exception ee) {}
    }
  }

  protected static void checkDocBase() {
    if (docBase == null)
      docBase = com.caiso.slic.client.services.WebSystemSettingsService.getDocumentBase();
  }

  public static void setDocumentBase(java.net.URL documentBase) {
    docBase = documentBase;
  }

  public static java.net.URL getDocumentBase() {
    return docBase;
  }

  public static void copyWebFile(String fileName, String localFileSpec) throws Exception {
    InputStream istream = null;
    FileOutputStream f = new FileOutputStream(getFullPath(localFileSpec));
    try {
      checkDocBase();
      java.net.URL url = new java.net.URL(docBase, fileName);
      java.net.URLConnection conn = (java.net.URLConnection)url.openConnection();
      // ** Configure the connection
      conn.setUseCaches( false );
      istream = conn.getInputStream();
      int ch;
      while ((ch = istream.read()) != -1) {
       f.write(ch);
      }
      f.flush();
    } catch(Exception e) {
      throw e;
    } finally {
      try { istream.close();} catch (Exception ee) {}
      try { f.close();} catch (Exception ee) {}
    }
  }

  public static Properties readPropertiesFileFromWeb(String fileName) throws Exception
  {
    InputStream istream = null;
    try {
      checkDocBase();
      java.net.URL url = new java.net.URL(docBase, fileName);
      java.net.URLConnection conn = (java.net.URLConnection)url.openConnection();
      // ** Configure the connection
      conn.setUseCaches( false );
      istream = conn.getInputStream();
      Properties p = new Properties();
      p.load(istream);
      return p;
    } finally {
      try { istream.close();} catch (Exception ee) {}
    }
  }

  */

  public static boolean isFullPath(String path) {
    File f = new File(path);
    return f.getAbsolutePath().equalsIgnoreCase(path);
  }

  public static String getFullPath(String path) {
    if (!isFullPath(path)) path = appPath + fileSep + path;
    return path;
  }

  public static boolean confirmDirectory(String path) {
    path = getFullPath(path);
    return new File(path).mkdir();
  }

  public static File getTempFile(String name) throws Exception
  {
    return new File(appPath + fileSep + "temp" + fileSep + name);
  }

  public static boolean exists(String path) {
    return new File(getFullPath(path)).exists();
  }

  public static void copyFile(String srcFile, String tgtFile) {
    try {
      FileChannel srcChannel = new FileInputStream(getFullPath(srcFile)).getChannel();
      FileChannel tgtChannel = new FileOutputStream(getFullPath(tgtFile)).getChannel();
      tgtChannel.transferFrom(srcChannel, 0, srcChannel.size());
      srcChannel.close();
      tgtChannel.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void copyFile(File srcFile, File tgtFile) {
    try {
      FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
      FileChannel tgtChannel = new FileOutputStream(tgtFile).getChannel();
      tgtChannel.transferFrom(srcChannel, 0, srcChannel.size());
      srcChannel.close();
      tgtChannel.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static String readXMLFileFromDisk(String fileName) throws Exception {
    //FileReader reader = null;
    try {
      StringBuffer buf = new StringBuffer();
      FileInputStream fileIn = new FileInputStream(getFullPath(fileName));
      //reader = new FileReader(getFullPath(fileName));
      //char[] charsIn = new char[5120];
      byte[] bytes = new byte[1024];
      int offset = 0;
      int readCount = 0;
      while(readCount > -1) {
        readCount = fileIn.read(bytes, 0, 1024);
        if(readCount > 0)
          buf.append(new String(bytes, 0, readCount));
        offset += readCount;
      }
      //int readCount = 0;
      //while(readCount > -1) {
      //fileIn.read(bytesIn)
      //readCount = reader.read(charsIn);
      //if(readCount > 0)
      //buf.append(charsIn, 0, readCount);
      //}
      //reader.close();
      fileIn.close();
      return buf.toString();
    }
    catch(Exception e) {
      //e.printStackTrace();
      throw e;
    }
    //finally {
      //try {
       // reader.close();
      //}
      //catch(Exception ee) {}
    //}
  }

  public static Properties readPropertiesFileFromDisk(String fileName) throws Exception
  {
    FileInputStream fileIn = null;
    try {
      Properties p = new Properties();
      fileIn = new FileInputStream(getFullPath(fileName));
      p.load(fileIn);
      return p;
    } catch(Exception e) {
      //e.printStackTrace();
      throw e;
    } finally {
      try { fileIn.close();} catch (Exception ee) {}
    }
  }

  public static void appendToFile(String fileName, String data) {
    FileOutputStream fileOut = null;
    try {
      fileOut = new FileOutputStream(getFullPath(fileName), true);
      byte[] bytes = data.getBytes();
      fileOut.write(bytes);
      fileOut.flush();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try { fileOut.close();} catch (Exception ee) {}
    }
  }


  public static void writeFile(String fileName, String data)
  {
    FileOutputStream fileOut = null;
    try {
      fileOut = new FileOutputStream(getFullPath(fileName));
      byte[] bytes = data.getBytes();
      fileOut.write(bytes);
      fileOut.flush();
      fileOut.close();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try { fileOut.close();} catch (Exception ee) {}
    }
  }

  public static void writePropertiesFileToDisk(String fileName, Properties p)
  {
    FileOutputStream fileOut = null;
    try {
      fileOut = new FileOutputStream(getFullPath(fileName));
      p.store(fileOut, "#Updated - " + System.currentTimeMillis());
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try { fileOut.close();} catch (Exception ee) {}
    }
  }

  public static void serializeObject(Object obj, String fileName)
  {
    FileOutputStream fileOut = null;
    ObjectOutputStream objOut = null;
    try {
      deleteFile(fileName);
      fileOut = new FileOutputStream(getFullPath(fileName));
      objOut = new ObjectOutputStream(fileOut);
      objOut.writeObject(obj);
      objOut.flush();
      objOut.close();
      fileOut.close();
    } catch(Exception e) {
      e.printStackTrace();
      try { fileOut.close(); } catch (Exception ee) {}
      try { objOut.close(); } catch (Exception ee) {}
    }
  }

  public static String serializeObject(Object obj)
  {
    ByteArrayOutputStream byteOut = null;
    ObjectOutputStream objOut = null;
    try {
      byteOut = new ByteArrayOutputStream();
      objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(obj);
      objOut.flush();
      objOut.close();
      byte[] bytes = byteOut.toByteArray();
      StringBuffer buf = new StringBuffer();
      if(bytes != null) {
        char[] chars = new char[bytes.length];
        for(int i = 0; i < bytes.length; i++)
          chars[i] = (char)bytes[i];
        buf.append(chars);
        return buf.toString();
      }
      return null;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void deleteFile(String fileName) {
    File f = null;
    try {
      f = new File(getFullPath(fileName));
      if (f.exists()) {
	boolean rtn = f.delete();
	if (!rtn) {
	  try {
	    Process p = Runtime.getRuntime().exec ("delete \"" + fileName + "\"");
	    p.waitFor ();
	  } catch (Exception ee) {
	    ee.printStackTrace();
	  }
	}
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Object inflateObject(String fileName) throws Exception
  {
    FileInputStream fileIn = null;
    ObjectInputStream objIn = null;
    try {
      fileIn = new FileInputStream(getFullPath(fileName));
      objIn = new ObjectInputStream(fileIn);
      Object rtnObj = objIn.readObject();
      return rtnObj;
    } catch (Exception e) {
      throw e;
    } finally {
      try { fileIn.close(); } catch (Exception e) {}
      try { objIn.close(); } catch (Exception e) {}
    }
  }

  public static Object inflateStringObject(String object) throws Exception
  {
    ObjectInputStream objIn = null;
    try {
      ByteArrayInputStream bytesIn = null;
      char[] chars = object.toCharArray();
      if(chars != null) {
        byte[] bytes = new byte[chars.length];
        for(int i = 0; i < bytes.length; i++)
          bytes[i] = (byte)chars[i];
         bytesIn = new ByteArrayInputStream(bytes);
         objIn = new ObjectInputStream(bytesIn);
         Object rtnObj = objIn.readObject();
         return rtnObj;
      }
      return null;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  public static File chooseDirectory(Component parent, String title, String startPath, javax.swing.filechooser.FileFilter filter) throws Exception {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDragEnabled(false);
    fileChooser.setMultiSelectionEnabled(false);
    if(startPath != null) {
      try {
        File f = new File(startPath);
        if(f.exists() && f.isDirectory())
          fileChooser.setCurrentDirectory(f);
      }
      catch(Exception ex) {}
    }
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if(filter != null) {
      fileChooser.setFileFilter(filter);
      fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    }
    if(title != null)
      fileChooser.setDialogTitle(title);
    else
      fileChooser.setDialogTitle("Select Directory");
    fileChooser.setApproveButtonText("Select");
    int result = fileChooser.showOpenDialog(parent);
    if(result != JFileChooser.CANCEL_OPTION) {
      try {
        File f = fileChooser.getSelectedFile();
        if(!f.exists())
          throw new Exception("Directory could not be found.");
        return f;
      }
      catch(Exception ex) {
        throw ex;
      }
    }
    return null;
  }

  public static File chooseFile(Component parent, String title, String startPath, javax.swing.filechooser.FileFilter filter) throws Exception {
    return chooseFile(parent, title, startPath, filter, true);
  }

  public static File chooseNewFile(Component parent, String title, String startPath, javax.swing.filechooser.FileFilter filter) throws Exception {
    return chooseFile(parent, title, startPath, filter, false);
  }

  public static File chooseFile(Component parent, String title, String startPath, javax.swing.filechooser.FileFilter filter, boolean confirmExists) throws Exception {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDragEnabled(false);
    fileChooser.setMultiSelectionEnabled(false);
    if(startPath != null) {
      try {
        File f = new File(startPath);
        if(f.isDirectory())
          fileChooser.setCurrentDirectory(f);
        else
          fileChooser.setSelectedFile(f);
      }
      catch(Exception ex) {}
    }
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if(filter != null) {
      fileChooser.setFileFilter(filter);
      fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    }
    if(title != null)
      fileChooser.setDialogTitle(title);
    else
      fileChooser.setDialogTitle("Select File");
    fileChooser.setApproveButtonText("Select");
    int result = fileChooser.showOpenDialog(parent);
    if(result != JFileChooser.CANCEL_OPTION) {
      try {
        File f = fileChooser.getSelectedFile();
        if(confirmExists && !f.exists())
          throw new Exception("File does not exist");
        return f;
      }
      catch(Exception ex) {
        throw ex;
      }
    }
    return null;
  }

  public static final String backslashToFowrwardSlash(String str) {
    if(str == null)
      return null;
    while(str.indexOf("\\") > -1)
      str = str.replace('\\', '/');
    return str;
  }
}

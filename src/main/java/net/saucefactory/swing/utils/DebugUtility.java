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
import java.text.*;
import java.util.*;

public abstract class DebugUtility {
  public static String TRACE_FILE = "slic_client_trace_file.log";
  public static boolean doDebug = false;
  public static boolean doTimestamp = true;
  public static boolean doDumps = false;
  private static String dumpDir = "";
  public static String fileSep = System.getProperty("file.separator");

  static {
    init();
  }

  private static void init() {
    try {
      //String debug = SystemSettingsService.getHandle().getSetting(CAISOConstants.CFG_DEBUG, "false");
      //String trace = SystemSettingsService.getHandle().getSetting(CAISOConstants.CFG_DO_TRACE_FILE, "false");
      //doDumps = new Boolean(SystemSettingsService.getHandle().getSetting(CAISOConstants.CFG_DO_DUMPS, "false")).booleanValue();
      //String console = SystemSettingsService.getHandle().getSetting(CAISOConstants.CFG_DO_TRACE_CONSOLE, "false");
      String debug = "false";
      String trace = "false";
      String console = "false";
      doDebug = Boolean.valueOf(debug).booleanValue();
      //dumpDir = SystemSettingsService.getHandle().getSetting(CAISOConstants.DUMPDIR, "");
      boolean doTrace = Boolean.valueOf(trace).booleanValue();
      boolean doConsole = Boolean.valueOf(console).booleanValue();
      FileUtility.confirmDirectory(dumpDir);
      if (doTrace) {
	OutLogger newOut = new OutLogger(dumpDir + fileSep + TRACE_FILE, doConsole);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'yyyy HH':'mm':'ss'.'SSS ':' ");
  private static String formatDate(Date in) {
    return sdf.format(in);
  }

  public static void installSystemOutLogger(String pathNoEndFileSep) {
    try {
      OutLogger newOut = new OutLogger(pathNoEndFileSep + fileSep + TRACE_FILE, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getTimestamp() {
    return formatDate(new Date());
  }

  public static void setTraceFile(String newFileName) {
    TRACE_FILE = newFileName;
    init();
  }

  public static boolean isDebug() {
    return doDebug;
  }

  public static void debugPrintln(String msg) {
    if (doDebug) {
      if (doTimestamp)
	System.out.println(getTimestamp() + msg);
      else
	System.out.println(msg);
    }
  }

  public static void debugDump(String fileName, String fileContent) {
    if (doDumps) {
      try {
	FileUtility.writeFile(dumpDir + fileSep + fileName, fileContent);
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  public static void debugEncodeAndDump(String fileName, String docName, String[] fileContent) {
    if (doDumps) {
      StringBuffer buf = new StringBuffer("<");
      buf.append(docName);
      buf.append('>');
      for (int i = 0; i < fileContent.length; i++) {
	buf.append("<item");
	buf.append(i);
	buf.append(">");
	buf.append(fileContent[i]);
	buf.append("</item");
	buf.append(i);
	buf.append(">");
      }
      buf.append("</");
      buf.append(docName);
      buf.append('>');
      try {
	FileUtility.writeFile(dumpDir + fileSep + fileName, buf.toString());
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  public static void debugSerializeAndDump(String fileName, Object fileContent) {
    if (doDumps) {
      try {
	FileUtility.writeFile(dumpDir + fileSep + fileName, fileContent.toString());
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  public static void printStackTrace(Exception e) {
    if (doDebug) {
      if (doTimestamp) System.out.print(getTimestamp());
      e.printStackTrace(System.out);
    }
  }

  public static void printMemoryStamp(String prefix) {
    if (!doDebug) return;
    System.out.println(prefix);
    System.out.println("TotalMem: " + Runtime.getRuntime().totalMemory());
    System.out.println("FreeMem: " + Runtime.getRuntime().freeMemory());
  }

  public static void printTimeStamp() {
    if (!doDebug) return;
    System.out.println("Time: " + System.currentTimeMillis());
  }

}

class OutLogger extends FilterOutputStream {
  private String filePath = null;
  private boolean doConsole = true;
  public OutLogger(String filePath, boolean doConsole) {
    super(System.out);
    this.filePath = filePath;
    this.doConsole = doConsole;
    PrintStream ps = new PrintStream(this);
    System.setOut(ps);
    System.setErr(ps);
  }

  public void write(byte[] b) throws IOException  {
    FileUtility.appendToFile(filePath, new String(b));
    if (doConsole) out.write(b);
  }

  public void write(byte[] b,int off,int len) throws IOException {
    FileUtility.appendToFile(filePath, new String(b,off,len));
    if (doConsole) out.write(b,off,len);
  }

  public void write(int b) throws IOException {
    byte[] barr={(byte)b};
    FileUtility.appendToFile(filePath, new String(barr));
    if (doConsole) out.write(b);
  }
}
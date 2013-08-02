package net.saucefactory.swing.utils;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public class WinBrowserControl {
  /* Windows security masks */
  private final static int KEY_QUERY_VALUE = 1;

  /* Constants used to interpret returns of native functions    */
  private final static int NATIVE_HANDLE = 0;
  private final static int ERROR_CODE = 1;

  /*  Windows error codes. */
  private final static int ERROR_SUCCESS = 0;

  /**
   * Split a command line into component parts (path to program,
   * parameters, etc.) supporting quoted strings (for parameters with
   * spaces in them), and one extra parameter (%1), which will be
   * quoted if it's not already.
   *
   * @param cmd - The command to split out and build an array from.
   *
   * @return - An array of String values which are directly passed to Runtime.exec(...)
   */
  public static String[] splitCommandLine(String cmd, String replace) {
    StreamTokenizer str = new StreamTokenizer(new StringReader(cmd));
    Vector result = new Vector();

    str.resetSyntax();
    str.wordChars(0,255);
    str.whitespaceChars(32,32);
    str.quoteChar('\"');

    try {
      while(str.nextToken() != StreamTokenizer.TT_EOF) {
        result.add(str.sval);
      }
    } catch(IOException ioe) {
      System.err.println("Caught an IO exception from a STRING?!?!");
    }

    String[] output = new String[result.size()];
    for(int i=0; i<result.size(); i++) {
      output[i] = (String)result.get(i);
      if(replace != null) {
        if(output[i].equals("\"%1\"") ||
           output[i].equals("%1")) {
          output[i] = "\"" + replace + "\"";
        }
      }
    }

    return output;
  }

  public static String replaceCommandParams(String cmd, String replace) {
    if(cmd != null && replace != null) {
      if(cmd.indexOf("\"%1\"") > -1)
        cmd = cmd.replaceAll("\"%1\"", replace);
      else if(cmd.indexOf("%1") > -1)
        cmd = cmd.replaceAll("%1", replace);
      //else if(cmd.indexOf("-nohome") > -1)
        //cmd = cmd.replaceAll("-nohome", replace);
      else
        cmd = cmd + " " + replace;
    }
    return cmd;
  }

  public static String[] splitCommandLine(String cmd) { return splitCommandLine(cmd, null); }

  public static String getIndirect(String primary) {
    String clientName, command;

    clientName = getKeyDefault("HKEY_CLASSES_ROOT\\\\" + primary);
    if(clientName == null) return null;

    command = getKeyDefault("HKEY_CLASSES_ROOT\\\\" + clientName + "\\shell\\open\\command");
    return command;
  }

  /**
   * Under the Windows registry, each key (path) has a default value.
   * Given a key, return the default string value.
   *
   * @param key - The key (HKEY_CLASSES_ROOT\\http\shell\open\command)
   * to get the default value of.
   *
   * @return - A string representation of the default value for the
   * given key.  Will return 'null' if the JVM doesn't support
   * registry operations.
   */
  public static String getKeyDefault(String key) {
    try {
      int hkey = getHKEY(key);
      byte[] rootpath = stripHKEY(key);
      return getValue(hkey, rootpath, "");
    } catch(Exception e) {
      //  For debugging, if necessary.
      if(false) {
        e.printStackTrace();
      }
      return null;
    }
  }

  /**
   * Given a protocol, return the launch command needed to execute it.
   *
   * @param protocol - A protocol (http, https, mailto, rtsp, ftp, etc.)
   *
   * @return - A string given the pathspec and parameters to launch
   * the program that handles the given protocol.  Will return 'null' if
   * it can't find an adequate program, or if the JVM doesn't support
   * registry operations.
   */
  public static String getBrowser(String protocol) {
    String clientName=null;

    clientName = getKeyDefault("HKEY_CLASSES_ROOT\\\\" + protocol + "\\shell\\open\\command");
    if(clientName == null) {
      clientName = getIndirect(protocol);
      if(clientName == null) return null;
    }
    return clientName;
  }

  /**
   * Returns the control value associated with each of the toplevel
   * registry elements in a given registry path.
   *
   * @param path - A registry key that starts with a known root.
   *
   * @return - An int representing the value to pass into registry
   * functions to retrieve the registry values along the given path.
   */
  public static int getHKEY(String path) throws Exception {
    if (path.startsWith("HKEY_CURRENT_USER")) {
      return 0x80000001;
    }
    else if (path.startsWith("HKEY_LOCAL_MACHINE")) {
      return 0x80000002;
    }
    else if (path.startsWith("HKEY_CLASSES_ROOT")) {
      return 0x80000000;
    }
    else {
      throw new Exception("Path should start with HKEY_CURRENT_USER " +
                          "or HKEY_LOCAL_MACHINE");
    }
  }

  /**
   * Remove the registry root from a registry path, and turn the rest
   * of the path into a format compatible with the registry functions.
   *
   * @param path - The path to strip the root off of.
   *
   * @return - The registry path turned into a registry-compatible byte array.
   */
  public static byte[] stripHKEY(String path) {
    int beginIndex = path.indexOf("\\\\");
    return stringToByteArray(path.substring(beginIndex+2));
  }

  /**
   * Get a string return from a registry key and value search.  Uses
   * reflection to keep from bollixing up older JVMs.
   *
   * @param hkey - The integer identifier of the branch of the registry to search.
   * @param WINDOWS_ROOT_PATH - The path (in bytearray form) as returned from stripHKEY.
   * @param key - The value at that point in the registry to return.
   *
   * @return - The data associated with the given key at the W_R_P
   * point in the registry, under the root node hkey.
   */
  public static String getValue(int hkey, byte[] WINDOWS_ROOT_PATH, String key) throws Exception {
    Class theClass = Class.forName("java.util.prefs.WindowsPreferences");
    byte[] windowsName;
    int nativeHandle;
    Object value;
    int[] result;
    Method m;

    result = openKey1(hkey, windowsAbsolutePath(WINDOWS_ROOT_PATH),
                      KEY_QUERY_VALUE);
    if (result[ERROR_CODE] != ERROR_SUCCESS) {
      throw new Exception("Path not found!");
    }
    nativeHandle = result[NATIVE_HANDLE];

    m = theClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[]{int.class, byte[].class});
    m.setAccessible(true);

    windowsName = toWindowsName(key);
    value = m.invoke(null, new Object[]{new Integer(nativeHandle), windowsName});
    WindowsRegCloseKey(nativeHandle);

    if (value == null) {
      throw new Exception("Path found.  Key not found.");
    }

    byte[] origBuffer = (byte[]) value;
    byte[] destBuffer = new byte[origBuffer.length - 1];
    System.arraycopy(origBuffer, 0, destBuffer, 0, origBuffer.length - 1);

    return new String(destBuffer);
  }

  /**
   * Using reflection, call the windows registry close key function to
   * close the current key.
   *
   * @param nativeHandle - The system native value used to refer to the current key.
   *
   * @return - The return value from the WindowsRegCloseKey reflected call.
   */
  public static int WindowsRegCloseKey(int nativeHandle) throws Exception {
    Class theClass = Class.forName("java.util.prefs.WindowsPreferences");
    Method m = theClass.getDeclaredMethod("WindowsRegCloseKey", new Class[]{int.class});
    Object ret;

    m.setAccessible(true);

    ret = m.invoke(null, new Object[]{new Integer(nativeHandle)});
    return ((Integer) ret).intValue();
  }

  /**
   * Using reflection, call the windows registry open key function to
   * open a new key to get data from.
   *
   * @param hkey - The int value of the registry root tree to look in.
   * @param windowsAbsolutePath - The path of the key to look up
   * @param securityMask - What is being done, so the security system can interrupt
   *
   * @return - An array where [0] is a handle to the key, and [1] is
   * an error code, if any errors were found, or ERROR_SUCCESS if no
   * errors.
   */
  public static int[] openKey1(int hkey, byte[] windowsAbsolutePath, int securityMask) throws Exception {
    Class theClass = Class.forName("java.util.prefs.WindowsPreferences");
    Method m = theClass.getDeclaredMethod("WindowsRegOpenKey", new Class[]{int.class,
                                                                           byte[].class,
                                                                           int.class});
    m.setAccessible(true);
    Object ret = m.invoke(null, new Object[]{new Integer(hkey),
                                             windowsAbsolutePath,
                                             new Integer(securityMask)});
    return (int[]) ret;
  }

  /**
   * Convert a string into a byte[] for use with the actual registry functions.
   *
   * @param str - The string to convert.
   *
   * @return - An array (byte[]) containing the characters from the string.
   */
  private static byte[] stringToByteArray(String str) {
    byte[] result = new byte[str.length() + 1];
    for (int i = 0; i < str.length(); i++) {
      result[i] = (byte) str.charAt(i);
    }
    result[str.length()] = 0;
    return result;
  }

  /**
   * Returns a byte array with the absolute path stored into it,
   * potentially with a terminating '\' added.
   *
   * @param WINDOWS_ROOT_PATH - The root path to translate.
   *
   * @return - The absolute version of the translated path.
   */
  private static byte[] windowsAbsolutePath(byte[] WINDOWS_ROOT_PATH) {
    ByteArrayOutputStream bstream = new ByteArrayOutputStream();
    bstream.write(WINDOWS_ROOT_PATH, 0, WINDOWS_ROOT_PATH.length - 1);
    StringTokenizer tokenizer = new StringTokenizer("/", "/");
    while (tokenizer.hasMoreTokens()) {
      bstream.write((byte) '\\');
      String nextName = tokenizer.nextToken();
      byte[] windowsNextName = toWindowsName(nextName);
      bstream.write(windowsNextName, 0, windowsNextName.length - 1);
    }
    bstream.write(0);
    return bstream.toByteArray();
  }

  /**
   * Process a name into a Windows name from a Java name (path
   * conversion?)
   *
   * @param javaName - The name to translate
   *
   * @return - A byte array containing the Windows translated name.
   */
  private static byte[] toWindowsName(String javaName) {
    StringBuffer windowsName = new StringBuffer();
    for (int i = 0; i < javaName.length(); i++) {
      char ch = javaName.charAt(i);
      if ((ch < 0x0020) || (ch > 0x007f)) {
        throw new RuntimeException("Unable to convert to Windows name");
      }
      if (ch == '\\') {
        windowsName.append("//");
      } else if (ch == '/') {
        windowsName.append('\\');
      } else if ((ch >= 'A') && (ch <= 'Z')) {
        windowsName.append("/" + ch);
      } else {
        windowsName.append(ch);
      }
    }
    return stringToByteArray(windowsName.toString());
  }

  /**
   * Convert a value string that comes out of the registry into a Java
   * String.
   *
   * @param windowsNameArray - The byte[] that gets converted into a String.
   *
   * @return - A string containing the text of a Windows value.
   */
  private static String toJavaValueString(byte[] windowsNameArray) {
    // Use modified native2ascii algorithm
    String windowsName = byteArrayToString(windowsNameArray);
    StringBuffer javaName = new StringBuffer();
    char ch;
    for (int i = 0; i < windowsName.length(); i++) {
      if ((ch = windowsName.charAt(i)) == '/') {
        char next = ' ';

        if (windowsName.length() > i + 1 &&
            (next = windowsName.charAt(i + 1)) == 'u') {
          if (windowsName.length() < i + 6) {
            break;
          } else {
            ch = (char) Integer.parseInt
              (windowsName.substring(i + 2, i + 6), 16);
            i += 5;
          }
        } else
          if ((windowsName.length() > i + 1) &&
              ((windowsName.charAt(i + 1)) >= 'A') && (next <= 'Z')) {
            ch = next;
            i++;
          } else if ((windowsName.length() > i + 1) &&
                     (next == '/')) {
            ch = '\\';
            i++;
          }
      } else if (ch == '\\') {
        ch = '/';
      }
      javaName.append(ch);
    }
    return javaName.toString();
  }

  /**
   * Converts a byte array to a string, i.e. from a Windows format
   * into a Java format.
   *
   * @param array - The data to convert into string format.
   *
   * @return - A string containing the text extracted from the input array.
   */
  private static String byteArrayToString(byte[] array) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < array.length - 1; i++) {
      result.append((char) array[i]);
    }
    return result.toString();
  }

  /**
   * Pass in a parameter of the protocol type, and it will print out a
   * string containing the value extracted from the registry for what
   * program is run to handle that protocol.
   *
   * @param args - Any number of command line arguments.
   */
  public static void main(String[] args) {
    String protocolLauncher;

    if(args.length >= 1) {
      protocolLauncher = getBrowser(args[0]);

      //  If we couldn't get a 'decent' protocol, fall back to HTTP,
      //  and let the browser handle it.
      if(protocolLauncher == null) protocolLauncher = getBrowser("http");
      System.out.println("Protocol launcher is: " + protocolLauncher);
      if(protocolLauncher != null) {
        if(args.length == 2) {
          String[] cmdline = splitCommandLine(protocolLauncher, args[1]);
          try {
            Runtime.getRuntime().exec(cmdline);
          } catch(IOException ioe) {
            System.err.println("Caught exception, darn it!");
          }
        }
      }
    }
  }
}

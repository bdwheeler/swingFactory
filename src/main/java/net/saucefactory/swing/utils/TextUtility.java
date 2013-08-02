package net.saucefactory.swing.utils;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.StringTokenizer;
import net.saucefactory.swing.common.SFMultiLineLabel;
import javax.swing.text.*;
import java.util.*;
import java.io.File;
import java.text.*;

public class TextUtility {
  public static final String TYPE_STRING = "String";
  public static final String TYPE_DOUBLE = "Double";
  public static final String TYPE_INTEGER = "Integer";

  public TextUtility(){
  }

  public static int getTextAreaLineCount(JTextArea textArea) {
    try {
      int width = textArea.getWidth();
      if(width == 0)
        width = textArea.getMinimumSize().width;
      if(width == 0)
        width = 50;
      FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
      String tmpStr = textArea.getText();
      if(tmpStr.length() > 0) {
        int lineCount = 0;
        StringTokenizer st = new StringTokenizer(tmpStr, "\n", true);
        while(st.hasMoreTokens())
          lineCount += fm.stringWidth(st.nextToken()) / width + 1;
        return lineCount;
      }
      else
        return 0;
    }
    catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /*
     public static int getRequiredTextAreaHeight(JTextArea textArea)
     {
    int width = textArea.getWidth();
    if(width == 0)
      width = textArea.getPreferredSize().width;
    if(width == 0)
      width = textArea.getMinimumSize().width;
    if(width == 0)
      width = 50;
    FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
    String tmpStr = textArea.getText();
    if(tmpStr.length() > 0) {
      int lineCount = 0;
      int tmpWidth;
      int tmpMod;
      String tmpToken;
      StringTokenizer st = new StringTokenizer(tmpStr, "\n", true);
      while(st.hasMoreTokens()) {
    tmpToken = st.nextToken();
    tmpWidth = fm.stringWidth(tmpToken);
    if(tmpWidth <= width)
   lineCount += 1;
    else {
   tmpMod = tmpWidth % width;
   lineCount += tmpWidth / width + 1;
      }

    }
     }
   */

  public static int getStringWidth(String string, Font font) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    return metrics.stringWidth(string);
  }

  public static int getStringWidth(String string, Font font, int maxWidth) {
    int tmpWidth = getStringWidth(string, font);
    return tmpWidth < maxWidth ? tmpWidth : maxWidth;
  }

  public static int getMaxStringWidth(String[] values, Font font) {
    if(values == null)
      return 0;
    int width = 0;
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    for(int i = 0; i < values.length; i++) {
      int tmpWidth = metrics.stringWidth(values[i]);
      if(tmpWidth > width)
        width = tmpWidth;
    }
    return width;
  }

  public static int getMaxStringWidth(String[] values, Font font, int maxWidth) {
    int tmpWidth = getMaxStringWidth(values, font);
    return tmpWidth < maxWidth ? tmpWidth : maxWidth;
  }

  public SFMultiLineLabel splitTextToLinesLabel(String text, int maxWidth, Font font) {
    try {
      SFMultiLineLabel rtnLabel = new SFMultiLineLabel();
      rtnLabel.setText(text);
      return rtnLabel;
    }
    catch(Exception e) {
      return null;
    }
  }

  public static String stripHTMLBreaks(String str) {
    if(str == null)
      return str;
    str = str.replaceAll("<br>", " ");
    return str.replaceAll("<BR>", " ");
  }

  public static String stripLineBreaks(String str) {
    if(str == null)
      return str;
    str = str.replaceAll("\n", " ");
    return str;
  }

  public static String convertBreaksToHtmlBreaks(String str) {
    if(str == null)
      return str;
    str = str.replaceAll("\n", "<br>");
    return str;
  }

  public static String splitStringToLines(String str, int maxWidth) {
    StringBuffer displayText = new StringBuffer();
    String currentLine = "";
    int tmpWidth = 0;
    StringTokenizer tok = new StringTokenizer(str, " \n", true);
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth += tmpToken.length();
      if(!tmpToken.equals(" ")) {
        if(tmpToken.equals("\n")) {
          displayText.append(tmpToken);
          currentLine = "";
          tmpWidth = 0;
        }
        else if(tmpWidth < maxWidth) {
          displayText.append(tmpToken + " ");
          currentLine = currentLine + tmpToken + " ";
        }
        else {
          displayText.append("\n" + tmpToken + " ");
          currentLine = tmpToken + " ";
          tmpWidth = tmpToken.length();
        }
      }
    }
    return displayText.toString();
  }

  public static String trim(String s) {
    return rtrim(ltrim(s));
  }

  public static String ltrim(String s) {
    try {
      while(s.charAt(0) == ' ') {
        s = s.substring(1);
      }
    }
    catch(StringIndexOutOfBoundsException e) {
      return "";
    }
    return(s);
  }

  public static String rtrim(String s) {
    try {
      int len = s.length();
      while(s.charAt(len - 1) == ' ') {
        s = s.substring(0, len - 1);
        len--;
      }
    }
    catch(StringIndexOutOfBoundsException e) {
      return "";
    }
    return(s);
  }

  public static String insertStringValueIntoXML(String XML, String name, String value) {
    return insertPrimitiveValueIntoXML(XML, name, value, TYPE_STRING);
  }

  public static String insertIntValueIntoXML(String XML, String name, int value) {
    return insertPrimitiveValueIntoXML(XML, name, String.valueOf(value), TYPE_INTEGER);
  }

  public static String insertPrimitiveValueIntoXML(String XML, String name, String value,
      String type) {
    StringBuffer buf = new StringBuffer("<");
    buf.append(name);
    buf.append(" Type=\"");
    buf.append(type);
    buf.append("\">");
    buf.append(value);
    buf.append("</");
    buf.append(name);
    buf.append(">");
    return insertXMLIntoXML(XML, buf.toString());
  }

  public static String insertDoubleValueIntoXML(String XML, String name, double value) {
    return insertPrimitiveValueIntoXML(XML, name, String.valueOf(value), TYPE_DOUBLE);
  }

  public static String insertXMLIntoXML(String XML, String insertXML) {
    StringBuffer buf = new StringBuffer(XML);
    buf.delete(XML.length() - 14, XML.length() - 1);
    buf.append(insertXML);
    buf.append(getDataObjectFooter());
    return buf.toString();
  }

  public static String getDataObjectHeader(String _id_, String attName) {
    StringBuffer rowXML = new StringBuffer("<DATAOBJECT");
    if(attName != null)rowXML.append(" Name=\"" + attName + "\"");
    rowXML.append(">");
    rowXML.append("<_id_ Type=\"String\">");
    rowXML.append(_id_);
    rowXML.append("</_id_>");
    return rowXML.toString();
  }

  public static String getDataObjectFooter() {
    return "</DATAOBJECT>";
  }

  public static String trimMaxString(String src, int maxLength) {
    if(src == null)
      return "";
    if(src.length() <= maxLength)
      return src;
    return src.substring(0, maxLength) + "..";
  }

  public static String trimFileString(String src, int maxLength) {
    if(src == null)
      return "";
    if(src.length() <= maxLength)
      return src;
    maxLength -= 3;
    String sep = "\\";
    int firstFolder = src.indexOf(sep);
    if(firstFolder < 0) {
      sep = "/";
      firstFolder = src.indexOf(sep);
    }
    if(firstFolder < 0)
      return (src.substring(0, maxLength - 3) + "...");
    int lastFolder = src.lastIndexOf(sep);
    int length = src.length();
    int newLength = firstFolder + (length - lastFolder);
    String newStringStart = src.substring(0, firstFolder);
    String newStringEnd = src.substring(lastFolder);
    if(newLength >= maxLength)
      return newStringStart + "..." + newStringEnd;
    boolean good = false;
    int tmpIndex;
    while(!good) {
      tmpIndex = src.indexOf(sep, firstFolder + 1);
      if((newStringStart.length() + newStringEnd.length() + (tmpIndex - firstFolder) >= maxLength) ||
          tmpIndex >= lastFolder) {
        good = true;
        break;
      }
      else {
        newStringStart = newStringStart + src.substring(firstFolder, tmpIndex);
        firstFolder = tmpIndex;
      }
      tmpIndex = src.lastIndexOf(sep, lastFolder - 1);
      if((newStringStart.length() + newStringEnd.length() + (lastFolder - tmpIndex) >= maxLength) ||
          tmpIndex <= firstFolder) {
        good = true;
        break;
      }
      else {
        newStringEnd = src.substring(tmpIndex, lastFolder) + newStringEnd;
        lastFolder = tmpIndex;
      }
    }
    int spaceCnt = maxLength - (newStringStart.length() + newStringEnd.length());
    if(spaceCnt < 3)
      spaceCnt = 3;
    String ellipsesStr = "...";
    //for(int i = 3; i < spaceCnt; i++)
      //ellipsesStr = ellipsesStr + ".";
    return newStringStart + ellipsesStr + newStringEnd;
  }

  public static int getGapTabStopCount(int startPos, int endPos, int tabWidth) {
    return(int)Math.ceil((double)((double)endPos - (double)startPos) / (double)tabWidth);
  }

  public static String getRTFTabString(int startPos, int endPos, int tabWidth) {
    String rtnStr = "";
    int tabCnt = getGapTabStopCount(startPos, endPos, tabWidth);
    for(int i = 0; i < tabCnt; i++)
      rtnStr = rtnStr + "\\tab ";
    return rtnStr;
  }

  public static String getRTFMultiLineString(String str) {
    return str.replaceAll("\n", "\n\\\\par ");
  }

  public static int[] parseCommaSepIntString(String str) {
    int[] rtnArray = null;
    try {
      StringTokenizer tok = new StringTokenizer(str, ",", false);
      String tmpStr;
      int tmpInt;
      Vector rtnItems = new Vector();
      while(tok.hasMoreTokens()) {
        tmpStr = tok.nextToken();
        tmpStr = tmpStr.trim();
        try {
          tmpInt = Integer.parseInt(tmpStr);
        }
        catch(Exception e) {
          tmpInt = -1;
        }
        if(tmpInt > -1)
          rtnItems.add(new Integer(tmpInt));
      }
      if(rtnItems.size() > 0) {
        rtnArray = new int[rtnItems.size()];
        for(int i = 0; i < rtnArray.length; i++)
          rtnArray[i] = ((Integer)rtnItems.get(i)).intValue();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return rtnArray;
  }
}

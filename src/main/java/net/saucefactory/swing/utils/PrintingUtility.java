package net.saucefactory.swing.utils;

/**
 * Title:        SLIC Client
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      California Independent Systems Operator
 * @author Jeremy Leng
 * @version 1.0
 */

import java.awt.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.awt.image.*;
import java.awt.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class PrintingUtility
{
  public static Font plainFont = new Font("Monospaced", Font.PLAIN, 9);//Dialog
  public static Font headerFont = new Font("Monospaced", Font.BOLD, 12);
  public static Font boldFont = new Font("Monospaced", Font.BOLD, 9);
  public static Font italicsFont = new Font("Monospaced", Font.ITALIC, 9);
  public static Font italicBoldFont = new Font("Monospaced", Font.ITALIC + Font.BOLD, 9);
  public static Font tablePlainFont = new Font("Monospaced", Font.PLAIN, 9);
  public static Font tableBoldFont = new Font("Monospaced", Font.BOLD, 9);

  public static final int rowSpacing = 3; //spacing for title strips
  public static final int itemSpacing = 1; //spacing for list items
  public static final int lineSpacing = 2; //spacing for line breaks
  public static final int multiStrSpacing = 0;//1; //spacing for multi line string breaks
  public static final int colSpacing = 2; //spacing for columns.
  public static final int gridWidth = 1; //thinkness of table lines.
  public static final int lineBreakSpacing = 12;
  public static final String elipseStr = "... ";
  public static int elipseStrWidth;

  public static Color darkGrey = new Color(204, 204, 204);
  public static Color lightGrey = new Color(238, 238, 238);
  public static Color redColor = Color.red;
  public static Color blackColor = Color.black;
  public static Graphics2D fontGraphics = null;
  public static Map renderingHints = new HashMap();

  public static SimpleDateFormat sdf;

  static {
    renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    renderingHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
    renderingHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
    BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_INDEXED);
    fontGraphics = image.createGraphics();
    fontGraphics.addRenderingHints(renderingHints);
    sdf = new SimpleDateFormat("mm/dd/yyyy hh:MM");
    /*
    try {
      //plain font
      String tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingPlainFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingPlainFontName", tmpFontName);
      String tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingPlainFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingPlainFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  plainFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //bold
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingBoldFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingBoldFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingBoldFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingBoldFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  boldFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //italics
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingItalicsFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingItalicsFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingItalicsFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingItalicsFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  italicsFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //italicBold
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingItalicBoldFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingItalicBoldFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingItalicBoldFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingItalicBoldFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  italicBoldFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //header
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingHeaderFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingHeaderFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingHeaderFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingHeaderFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  headerFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //tablePlain
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingTablePlainFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingTablePlainFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingTablePlainFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingTablePlainFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  tablePlainFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
      //tableBold
      tmpFontName = SystemSettingsService.getHandle().getSetting("PrintingTableBoldFontName", "");
      tmpFontName = UserSettingsService.getHandle().getSetting("PrintingTableBoldFontName", tmpFontName);
      tmpFontSize = SystemSettingsService.getHandle().getSetting("PrintingTableBoldFontSize", "");
      tmpFontSize = UserSettingsService.getHandle().getSetting("PrintingTableBoldFontSize", tmpFontName);
      if(!tmpFontName.equals("") && !tmpFontSize.equals(""))
  tableBoldFont = new Font(tmpFontName, Font.PLAIN, Integer.parseInt(tmpFontSize));
    }
    catch(Exception e) {
      System.out.println("Error loading default print font settings.");
    }
    */
    elipseStrWidth = fontGraphics.getFontMetrics(plainFont).stringWidth(elipseStr);
  }

  public static Paper getLegalPaper() {
    Paper p = new Paper();
    p.setSize(612d, 1008d);
    p.setImageableArea(36, 36, p.getWidth() - 72, p.getHeight() - 72);
    return p;
  }

  public static MediaPrintableArea getLegalMediaPrintArea() {
    return new MediaPrintableArea(.5f, .5f, 7.5f, 13f, MediaPrintableArea.INCH);
  }

  public static MediaPrintableArea getLetterMediaPrintArea() {
    return new MediaPrintableArea(.5f, .5f, 7.5f, 10f, MediaPrintableArea.INCH);
  }

  public static Paper getLetterPaper() {
    Paper p = new Paper();
    p.setSize(612d, 792d);
    p.setImageableArea(36, 36, p.getWidth() - 72, p.getHeight() - 72);
    return p;
  }

  public static boolean isLegalPaper(Paper p) {
    return (p.getHeight() / p.getWidth() > 1.6);
  }

  public static void clearFontGraphics() {
    fontGraphics.clearRect(0, 0, 10, 10);
  }

  public static boolean checkLineForPage(int currentHeight, int printHeight, int page) {
    return ((currentHeight >= (printHeight * page)) && (currentHeight < printHeight * (page + 1)));
  }

  public static boolean checkLineForPage(int currentHeight, int pageHeight, int rowPage, int currentWidth, int printWidth, int pageWidth, int colPage) {
    return ((currentHeight >= (pageHeight * rowPage)) && (currentHeight < pageHeight * (rowPage + 1)) &&
      (((currentWidth >= (pageWidth * colPage)) && (currentWidth < pageWidth * (colPage + 1))) || ( (currentWidth + printWidth >= (pageWidth * colPage)) && (currentWidth + pageWidth < pageWidth * (colPage + 1)))));
  }

  public static int getPageWidthClip(int clipWidth, int pageWidth, int colPage) {
    return clipWidth <= (pageWidth * (colPage + 1)) ? clipWidth : (pageWidth * (colPage + 1));
  }

  public static int getMaxPageWidthClip(int usedWidth, int clipWidth, int pageWidth, int colPage) {
    return usedWidth + clipWidth <= (pageWidth * (colPage + 1)) ? clipWidth : pageWidth * (colPage + 1) - usedWidth;
  }

  public static int getMinPageWidthClip(int usedWidth, int pageWidth, int colPage) {
    return usedWidth >= (pageWidth * colPage) ? usedWidth : (pageWidth * colPage);
  }

  public static int getMinPageHeightClip(int usedHeight, int pageHeight, int rowPage) {
    return usedHeight >= (pageHeight * rowPage) ? usedHeight : (pageHeight * rowPage);
  }

  public static int getMaxPageHeightClip(int usedHeight, int clipHeight, int pageHeight, int rowPage) {
    return usedHeight + clipHeight <= (pageHeight * (rowPage + 1)) ? clipHeight : (pageHeight * (rowPage + 1) - usedHeight);
  }

  public static int getMultiLineStrHeight(int lineCount, int fontHeight) {
    if(lineCount == 0)
      return 0;
    else if(lineCount == 1)
      return fontHeight;
    else
      return ((lineCount * fontHeight) + ((lineCount - 1) * multiStrSpacing));
  }

  public static int[] getMultiLineStrPageBreaks(int fontHeight, int strHeight, int usedHeight, int pageHeight) {
    int[] rtnArray;
    if(usedHeight + strHeight < pageHeight)
      rtnArray = new int[]{strHeight};
    else {
      int gapHeight = pageHeight - usedHeight;
      int lineCnt = (strHeight + multiStrSpacing) / (fontHeight + multiStrSpacing);
      int gapLineCnt = (gapHeight + multiStrSpacing) / (fontHeight + multiStrSpacing);
      int leftOvers = lineCnt - gapLineCnt;
      int pageLineCnt = (pageHeight + multiStrSpacing) / (fontHeight + multiStrSpacing);
      if(leftOvers < pageLineCnt)
  rtnArray = new int[]{gapHeight, leftOvers * (fontHeight + multiStrSpacing)};
      else {
  int pageCnt = leftOvers / pageLineCnt;
  int mod = leftOvers % pageLineCnt;
  rtnArray = new int[pageCnt + 2];
  rtnArray[0] = gapHeight;
  rtnArray[pageCnt + 1] = mod * (fontHeight + multiStrSpacing);
      }
    }
    return rtnArray;
  }

  public static void paintElipseStr(Graphics2D g, String str, int x, int y, int width, int clipStartHeight, int clipHeight) {
    FontMetrics fm = g.getFontMetrics();
    int strWidth = fm.stringWidth(str);
    if(strWidth > width) {
      g.setClip(x, clipStartHeight, width - elipseStrWidth, clipHeight);
      g.drawString(str, x, y);
      g.setClip(x + width - elipseStrWidth, clipStartHeight, elipseStrWidth, clipHeight);
      g.drawString(elipseStr, x + width - elipseStrWidth, y);
    }
    else
      g.drawString(str, x, y);
  }

  public static void paintRightJustifiedStr(Graphics2D g, String str, int x, int y, int width) {
    FontMetrics fm = g.getFontMetrics();
    int strWidth = fm.stringWidth(str);
    g.drawString(str, x + width - strWidth, y);
  }

  public static void paintCenterJustifiedStr(Graphics2D g, String str, int x, int y, int width) {
    FontMetrics fm = g.getFontMetrics();
    int strWidth = fm.stringWidth(str);
    g.drawString(str, x + (int)(((double)width - (double)strWidth) / 2d), y);
  }

  public static int paintMultiLineStr(Graphics2D g, String str, int x, int y, int width, int pageHeight, int page) {
    Vector disclaimerVec = new Vector();
    StringTokenizer tok = new StringTokenizer(str, " \n\r", true);
    int tmpWidth;
    String currentLine = "";
    FontMetrics metrics = g.getFontMetrics();
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth = metrics.stringWidth(currentLine + tmpToken);
      if(tmpToken.equals("\n")) {
  disclaimerVec.add(currentLine);
  currentLine = "";
      }
      else if(tmpToken.equals("\r")) {}
      else if(tmpWidth < width || currentLine.equals(""))
  currentLine = currentLine + tmpToken;
      else {
  disclaimerVec.add(currentLine);
  currentLine = tmpToken.equals(" ") ? "" : tmpToken;
      }
    }
    if(!currentLine.equals(""))
      disclaimerVec.add(currentLine);
    if(disclaimerVec.size() > 0) {
      g.setClip(x, y, width, (disclaimerVec.size() * (metrics.getHeight() + multiStrSpacing) - metrics.getDescent()) + itemSpacing);
      for(int j = 0; j < disclaimerVec.size(); j++) {
  String tmpStr = (String)disclaimerVec.get(j);
  if(checkLineForPage(y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent(), pageHeight, page))
    g.drawString(tmpStr, x, y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent());
      }
    }
    return disclaimerVec.size();
  }

  public static int paintPageBreakMultiLineStr(Graphics2D g, String str, int x, int y, int width, int pageHeight, int page, int minClip) {
    Vector disclaimerVec = new Vector();
    StringTokenizer tok = new StringTokenizer(str, " \n\r", true);
    int tmpWidth;
    String currentLine = "";
    FontMetrics metrics = g.getFontMetrics();
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth = metrics.stringWidth(currentLine + tmpToken);
      if(tmpToken.equals("\n")) {
  disclaimerVec.add(currentLine);
  currentLine = "";
      }
      else if(tmpToken.equals("\r")) {}
      else if(tmpWidth < width || currentLine.equals(""))
  currentLine = currentLine + tmpToken;
      else {
  disclaimerVec.add(currentLine);
  currentLine = tmpToken.equals(" ") ? "" : tmpToken;
      }
    }
    if(!currentLine.equals(""))
      disclaimerVec.add(currentLine);
    if(disclaimerVec.size() > 0) {
      int tmpY;
      g.setClip(x, minClip, width, (disclaimerVec.size() * (metrics.getHeight() + multiStrSpacing)) - metrics.getDescent() + itemSpacing);
      for(int j = 0; j < disclaimerVec.size(); j++) {
  String tmpStr = (String)disclaimerVec.get(j);
  tmpY = y + j * (metrics.getHeight() + multiStrSpacing);
  if(tmpY > minClip && checkLineForPage(y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent(), pageHeight, page))
    g.drawString(tmpStr, x, y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent());
      }
    }
    return disclaimerVec.size();
  }

  public static void paintCheckBox(Graphics g, int x, int y, int width, int height, boolean selected) {
    int boxWidth = 7;
    //paint 4 sides.
    int xAdjust = (width - boxWidth) / 2;
    g.setColor(darkGrey);
    g.setClip(x, y, width, height);
    g.drawLine(x + xAdjust, y + 2, x + boxWidth + xAdjust, y + 2);
    g.drawLine(x + xAdjust, y + 2, x + xAdjust, y + 2 + boxWidth);
    g.setColor(lightGrey);
    g.drawLine(x + xAdjust, y + 2 + boxWidth, x + boxWidth + xAdjust, y + 2 + boxWidth);
    g.drawLine(x + xAdjust + boxWidth, y + 2, x + boxWidth + xAdjust, y + 2 + boxWidth);
    g.setColor(blackColor);
    if(selected) {
      g.drawLine(x + xAdjust + 1, y + 2 + boxWidth / 2, x + xAdjust + boxWidth / 3, y + 1 + boxWidth);
      g.drawLine(x + xAdjust + boxWidth / 3, y + 1 + boxWidth, x + xAdjust + boxWidth - 1, y + 3);
    }
  }

  public static int paintMultiLineStr(Graphics2D g, String str, int x, int y, int width, int pageHeight, int pageWidth, int rowPage, int colPage) {
    Vector disclaimerVec = new Vector();
    StringTokenizer tok = new StringTokenizer(str, " \n\r", true);
    int tmpWidth;
    String currentLine = "";
    FontMetrics metrics = g.getFontMetrics();
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth = metrics.stringWidth(currentLine + tmpToken);
      if(tmpToken.equals("\n")) {
  disclaimerVec.add(currentLine);
  currentLine = "";
      }
      else if(tmpToken.equals("\r")) {}
      else if(tmpWidth < width || currentLine.equals(""))
  currentLine = currentLine + tmpToken;
      else {
  disclaimerVec.add(currentLine);
  currentLine = tmpToken.equals(" ") ? "" : tmpToken;
      }
    }
    if(!currentLine.equals(""))
      disclaimerVec.add(currentLine);
    if(disclaimerVec.size() > 0) {
      g.setClip(getMinPageWidthClip(x, pageWidth, colPage), getMinPageHeightClip(y, pageHeight, rowPage), getMaxPageWidthClip(x, width, pageWidth, colPage), getMaxPageHeightClip(y, ((disclaimerVec.size() * (metrics.getHeight() + multiStrSpacing)) - metrics.getDescent() + itemSpacing), pageHeight, rowPage));
      for(int j = 0; j < disclaimerVec.size(); j++) {
  String tmpStr = (String)disclaimerVec.get(j);
  if(checkLineForPage(y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent(), pageHeight, rowPage))
    g.drawString(tmpStr, x, y + ((j + 1) * metrics.getHeight())  + (j * multiStrSpacing) - metrics.getDescent());
      }
    }
    return disclaimerVec.size();
  }

  public static int getMultiLineStrHeight(String str, int fontHeight, int width, FontMetrics metrics) {
    StringTokenizer tok = new StringTokenizer(str, " \n", true);
    int tmpWidth;
    String currentLine = "";
    int lineCount = 0;
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth = metrics.stringWidth(currentLine + tmpToken);
      if(tmpToken.equals("\n")) {
  lineCount++;
  currentLine = "";
      }
      else if(tmpToken.equals("\r")) {}
      else if(tmpWidth < width || currentLine.equals(""))
  currentLine = currentLine + tmpToken;
      else {
  lineCount++;
  currentLine = tmpToken.equals(" ") ? "" : tmpToken;
      }
    }
    if(!currentLine.equals(""))
      lineCount++;
    return getMultiLineStrHeight(lineCount, fontHeight);
  }

  public static String _getAtt(Object o) {
    try {
      if(o instanceof Date) {
  return sdf.format((Date)o);
      }
      if (o instanceof String) {
  String s = (String)o;
  if (s == null) return "";
      }
      return o.toString();
    } catch (NullPointerException n) {
      return "";
    }
  }
}

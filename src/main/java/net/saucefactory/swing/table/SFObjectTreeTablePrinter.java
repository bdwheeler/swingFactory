package net.saucefactory.swing.table;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CAISO</p>
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.Dimension;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import net.saucefactory.swing.common.*;
import net.saucefactory.swing.utils.*;
import net.saucefactory.swing.common.SFPrintPreviewFrame.PreviewResults;

public class SFObjectTreeTablePrinter implements Printable, Pageable, ISFPrintPreviewable {
  protected SFObjectTableTree table = null;
  protected PrintHeader header = null;
  protected PageFormat pageFormat = null;
  protected Date currentDateTime = new Date();
  protected int[] rowHeights = null;
  protected int[] colWidths = null;
  protected PageBreak[] pageBreakData = null;
  protected int[] columnBreakData = null;
  protected int colHeaderHeight = 0;
  protected int totalPageCount = 0;
  protected int totalColPageSpan = 0;
  protected int lastPageIndex = 0;
  protected int lastRowIndex = 0;
  protected double columnScale = .83d;
  protected boolean initComplete = false;

  public SFObjectTreeTablePrinter(SFObjectTableTree _table, PageFormat _pageFormat) {
    table = _table;
    pageFormat = _pageFormat;
    initTableMeasurements();
  }

  public SFObjectTreeTablePrinter(SFObjectTableTree _table, PageFormat _pageFormat,
      String _headerTitle, String _headerDateRange, String _headerCreateDTS) {
    table = _table;
    pageFormat = _pageFormat;
    header = new PrintHeader(_headerTitle, _headerDateRange, _headerCreateDTS);
    initTableMeasurements();
  }

  public int print(Graphics g, PageFormat pageFormat,
      int pageIndex) throws PrinterException {
    return paintTable(g
        , pageFormat.getImageableWidth()
        , pageFormat.getImageableHeight()
        , pageFormat.getImageableX()
        , pageFormat.getImageableY()
        , pageIndex);
  }

  public static void printTable(SFObjectTableTree _table, boolean _forceLandscape) {
    printTable(_table, _forceLandscape, false);
  }

  public static void printTable(SFObjectTableTree _table, boolean _forceLandscape,
      boolean _showPreview) {
    printTable(_table, _forceLandscape, false);
  }

  public static void printTable(final SFObjectTableTree _table, final boolean _forceLandscape,
      final boolean _showPreview,
      final String _headerTitle, final String _headerDateRange, final String _headerCreateDTS) {
    Runnable runner = new Runnable() {
      public void run() {
        try {
          PrinterJob pj = PrinterJob.getPrinterJob();
          PageFormat pageFormat = pj.defaultPage();
          Paper paper = pageFormat.getPaper();
          paper.setImageableArea(paper.getImageableX() - 36
              , paper.getImageableY() - 36
              , paper.getImageableWidth() + 72
              , paper.getImageableHeight() + 72);
          pageFormat.setPaper(paper);
          if(_forceLandscape)
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
          SFObjectTreeTablePrinter tmpPrinter;
          if(_headerTitle != null && _headerDateRange != null && _headerCreateDTS != null)
            tmpPrinter = new SFObjectTreeTablePrinter(_table, pageFormat, _headerTitle,
                _headerDateRange, _headerCreateDTS);
          else
            tmpPrinter = new SFObjectTreeTablePrinter(_table, pageFormat);
          PreviewResults proceed = new PreviewResults();
          proceed.proceed = true;
          proceed.cnt = -1;
          if(_showPreview)
            proceed = SFPrintPreviewFrame.showPreview(tmpPrinter, pageFormat);
            //pj.setPrintable(tmpPrinter, tmpPrinter.pageFormat);
          pj.setPageable(tmpPrinter);
          if(tmpPrinter.header != null)
            pj.setJobName(tmpPrinter.header.getJobName());
          OrientationRequested oldOrientation;
          MediaSizeName mediaName;
          boolean isLegal = PrintingUtility.isLegalPaper(pageFormat.getPaper());
          PrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
          if(pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
            set.add(OrientationRequested.LANDSCAPE);
            oldOrientation = OrientationRequested.LANDSCAPE;
          }
          else {
            set.add(OrientationRequested.PORTRAIT);
            oldOrientation = OrientationRequested.PORTRAIT;
          }
          if(isLegal) {
            set.add(MediaSizeName.NA_LEGAL);
            set.add(PrintingUtility.getLegalMediaPrintArea());
            mediaName = MediaSizeName.NA_LEGAL;
          }
          else {
            set.add(MediaSizeName.NA_LETTER);
            set.add(PrintingUtility.getLetterMediaPrintArea());
            mediaName = MediaSizeName.NA_LETTER;
          }
          //set.add(new PageRanges(1, tmpPrinter.totalPageCount));
          set.add(new JobName("Report", Locale.ENGLISH));
          if(proceed.proceed && pj.printDialog(set)) {
            boolean jobChanged = false;
            if(oldOrientation != (OrientationRequested)set.get(oldOrientation.getCategory())) {
              jobChanged = true;
              if(pageFormat.getOrientation() == PageFormat.LANDSCAPE)
                pageFormat.setOrientation(PageFormat.PORTRAIT);
              else
                pageFormat.setOrientation(PageFormat.LANDSCAPE);
            }
            if(mediaName != (MediaSizeName)set.get(mediaName.getCategory())) {
              jobChanged = true;
              if(isLegal)
                pageFormat.setPaper(PrintingUtility.getLetterPaper());
              else
                pageFormat.setPaper(PrintingUtility.getLegalPaper());
            }
            if(jobChanged)
              tmpPrinter.formatChanged(pageFormat);
            pj.print(set);
          }
          else
            pj.cancel();
        }
        catch(Exception PrintException) {
          PrintException.printStackTrace();
          JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(_table)
              , "Unable to print!\n\n" + PrintException.getMessage()
              , "Printing Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    };
    Thread t = new Thread(runner);
    t.start();
  }

  public void formatChanged(PageFormat format) {
    clearInit();
    pageFormat = format;
    initTableMeasurements();
  }

  public void clearInit() {
    initComplete = false;
  }

  public void initTableMeasurements() {
    //calculate total page count.
    Graphics2D g = PrintingUtility.fontGraphics;
    initComplete = true;
    if(table == null || table.getRowCount() < 1 || table.getColumnCount() < 1)
      return;
    int baseRowHeight = table.getRowHeight();
    int rowCount = table.getRowCount();
    int colCount = table.getColumnCount();
    int totalColumnWidth = (int)(columnScale * (double)table.getColumnModel().getTotalColumnWidth());
    int paperWidth = (int)pageFormat.getImageableWidth();
    rowHeights = new int[rowCount];
    colWidths = new int[colCount];
    totalColPageSpan = (int)Math.ceil((double)totalColumnWidth / (double)paperWidth);
    columnBreakData = new int[totalColPageSpan];
    FontMetrics fm = g.getFontMetrics(PrintingUtility.headerFont);
    SFFontPrintData fontData = new SFFontPrintData();
    fontData.headerHeight = fm.getHeight();
    fontData.headerDesent = fm.getDescent();
    fm = g.getFontMetrics(PrintingUtility.tableBoldFont);
    fontData.boldHeight = fm.getHeight();
    fontData.boldDesent = fm.getDescent();
    fm = g.getFontMetrics(PrintingUtility.tablePlainFont);
    fontData.plainHeight = fm.getHeight();
    fontData.plainDesent = fm.getDescent();
    int simpleRowHeight = 2 * PrintingUtility.itemSpacing + fontData.plainHeight +
        PrintingUtility.gridWidth;
    int usedHeight = 0;
    int tmpPageCount = 0;
    int tmpMaxHeight = 0;
    int tmpInt = 0;
    int intPageHeight = (int)pageFormat.getImageableHeight();
    //take out columnHeaders
    int tmpColWidth = 0;
    int colSpanCnt = 0;
    int usedColCnt = 0;
    int runningSum = 0;
    for(int i = 0; i < colCount; i++) {
      if(i != (colCount - 1))
        tmpInt = (int)(columnScale * (double)table.getColumnModel().getColumn(i).getWidth());
      else
        tmpInt = totalColumnWidth - runningSum;
      runningSum += tmpInt;
      colWidths[i] = tmpInt;
      if(tmpColWidth + tmpInt > paperWidth) {
        tmpColWidth = tmpInt;
        columnBreakData[colSpanCnt] = i - usedColCnt;
        colSpanCnt++;
        usedColCnt = i;
      }
      else
        tmpColWidth += tmpInt;
      tmpInt = PrintingUtility.getMultiLineStrHeight(table.getColumnName(i), fontData.plainHeight,
          tmpInt, fm);
      if(tmpInt > tmpMaxHeight)
        tmpMaxHeight = tmpInt;
    }
    if(usedColCnt < colCount)
      columnBreakData[columnBreakData.length - 1] = colCount - usedColCnt;
    tmpMaxHeight += (2 * PrintingUtility.gridWidth + 2 * PrintingUtility.itemSpacing);
    colHeaderHeight = tmpMaxHeight;
    intPageHeight -= tmpMaxHeight;
    //adjust for page #
    intPageHeight -= (fontData.plainHeight + PrintingUtility.itemSpacing);
    //adjust displaimer
    //take out header.
    if(header != null)
      tmpMaxHeight = fontData.headerHeight + 3 * PrintingUtility.itemSpacing +
          2 * fontData.boldHeight;
    intPageHeight -= tmpMaxHeight;
    //table data.
    Vector tmpVec = new Vector();
    tmpVec.add(0, new PageBreak(0, 0));
    for(int i = 0; i < rowCount; i++) {
      if(table.getRowHeight(i) == baseRowHeight) { //normal row.
        rowHeights[i] = simpleRowHeight;
        if((usedHeight + simpleRowHeight) < intPageHeight)
          usedHeight += simpleRowHeight;
        else {
          tmpPageCount++;
          usedHeight = simpleRowHeight;
          tmpVec.add(tmpPageCount, new PageBreak(i, 0));
        }
      }
      else { //calc max height.
        tmpMaxHeight = 0;
        for(int j = 0; j < colCount; j++) {
          tmpInt = PrintingUtility.getMultiLineStrHeight(table.getStringValueAt(i, j, false),
              fontData.plainHeight,
              colWidths[j] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing, fm);
          if(tmpInt > tmpMaxHeight)
            tmpMaxHeight = tmpInt;
        }
        rowHeights[i] = tmpMaxHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth;
        tmpMaxHeight += 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth;
        int[] pageBreaks = PrintingUtility.getMultiLineStrPageBreaks(fontData.plainHeight,
            tmpMaxHeight, usedHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth,
            intPageHeight);
        if(pageBreaks.length < 2)
          usedHeight += tmpMaxHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth;
        else {
          int tmpRunningSum = pageBreaks[0];
          for(int k = 1; k < pageBreaks.length; k++) {
            tmpPageCount++;
            tmpVec.add(tmpPageCount, new PageBreak(i, tmpRunningSum));
            tmpRunningSum += pageBreaks[k];
          }
          usedHeight = pageBreaks[pageBreaks.length - 1] + PrintingUtility.itemSpacing +
              PrintingUtility.gridWidth;
        }
      }
    } //end for
    if(usedHeight > 0)
      tmpPageCount++;
    tmpPageCount = tmpPageCount * totalColPageSpan;
    totalPageCount = tmpPageCount;
    pageBreakData = new PageBreak[tmpVec.size()];
    tmpVec.copyInto(pageBreakData);
  }

  public int getNumberOfPages() {
    return totalPageCount;
  }

  public PageFormat getPageFormat(int pageIndex) {
    return pageFormat;
  }

  public Printable getPrintable(int pageIndex) {
    return this;
  }

  public int paintTable(Graphics g
      , PageFormat pageFormat
      , int pageIndex
      , boolean forceLandscape) throws PrinterException {
    if(forceLandscape)pageFormat.setOrientation(PageFormat.LANDSCAPE);
    return paintTable(g
        , pageFormat.getImageableWidth()
        , pageFormat.getImageableHeight()
        , pageFormat.getImageableX()
        , pageFormat.getImageableY()
        , pageIndex);
  }

  public int paintTable(Graphics g
      , double width
      , double height
      , double tx
      , double ty
      , int pageIndex) throws PrinterException {
    // debug
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHints(PrintingUtility.renderingHints);
    if(!initComplete)
      initTableMeasurements();
    if(pageIndex >= totalPageCount)
      return Printable.NO_SUCH_PAGE;
    // set font
    //set font metrics.
    SFFontPrintData fontData = new SFFontPrintData();
    g2.setFont(PrintingUtility.tablePlainFont);
    fontData.plainHeight = g2.getFontMetrics().getHeight();
    fontData.plainDesent = g2.getFontMetrics().getDescent();
    g2.setFont(PrintingUtility.tableBoldFont);
    fontData.boldHeight = g2.getFontMetrics().getHeight();
    fontData.boldDesent = g2.getFontMetrics().getDescent();
    g2.setFont(PrintingUtility.italicsFont);
    fontData.italicsHeight = g2.getFontMetrics().getHeight();
    fontData.italicsDesent = g2.getFontMetrics().getDescent();
    g2.setFont(PrintingUtility.italicBoldFont);
    fontData.italicBoldHeight = g2.getFontMetrics().getHeight();
    fontData.italicBoldDesent = g2.getFontMetrics().getDescent();
    g2.setFont(PrintingUtility.headerFont);
    fontData.headerHeight = g2.getFontMetrics().getHeight();
    fontData.headerDesent = g2.getFontMetrics().getDescent();
    //leave room for page number & grow the page a bit --
    double pageHeight = height - (fontData.plainHeight + PrintingUtility.itemSpacing);
    double pageWidth = width;
    int intPageWidth = (int)pageWidth;
    int intPageHeight = (int)pageHeight;
    int tmpStrWidth;
    String tmpStr;
    int usedHeight = 0;
    int usedWidth = 0;
    int tmpMaxLine = 0;
    int maxHeight = 0;
    int tmpUsedHeight = 0;
    int maxColHeight = 0;
    int columnPageCount;
    //paint table
    int rowCount = table.getRowCount();
    int colCount = table.getColumnCount();
    int baseRowHeight = table.getRowHeight();
    int simpleRowHeight = fontData.plainHeight + 2 * PrintingUtility.itemSpacing +
        PrintingUtility.gridWidth;
    int totalColumnWidth = (int)(columnScale * (double)table.getColumnModel().getTotalColumnWidth());
    //translate
    g2.translate(tx, ty);
    //adjust for current page index
    if(rowCount == 0 || colCount == 0)
      return Printable.NO_SUCH_PAGE;
    int tablePageHeight = totalPageCount / totalColPageSpan;
    int currentColPage = pageIndex / tablePageHeight;
    int currentRowPage = pageIndex - (currentColPage * tablePageHeight);
    int startRow = getStartRow(currentRowPage);
    int endRow = getEndRow(currentRowPage);
    int startCol = getStartColumn(currentColPage);
    int endCol = getEndColumn(currentColPage);
    //paint header
    if(header != null) {
      usedWidth = 1;
      g.setFont(PrintingUtility.headerFont);
      g.setColor(PrintingUtility.blackColor);
      //logo
      //if(pageIndex == 0) {
      //ImageIcon tmpImg = SFIconManager.getImageFromDefaultPackage("reportLogo.gif");
      //g.setClip(intPageWidth - 262, usedHeight, 262, 40);
      //g.drawImage(tmpImg.getImage(), intPageWidth - 262, usedHeight, 262, 40, null);
      //}
      //title
      g.setClip(usedWidth, usedHeight, intPageWidth - usedWidth,
          fontData.headerHeight + PrintingUtility.itemSpacing);
      if(PrintingUtility.checkLineForPage(usedHeight, intPageHeight, 0))
        g.drawString(header.titleString, usedWidth,
            usedHeight + fontData.headerHeight - fontData.headerDesent);
      usedHeight += fontData.headerHeight + PrintingUtility.itemSpacing;
      //created
      g.setFont(PrintingUtility.tableBoldFont);
      g.setClip(usedWidth, usedHeight, intPageWidth - usedWidth,
          fontData.boldHeight + PrintingUtility.itemSpacing);
      if(PrintingUtility.checkLineForPage(usedHeight, intPageHeight, 0))
        g.drawString(header.createdDTSStr, usedWidth,
            usedHeight + fontData.boldHeight - fontData.boldDesent);
      usedHeight += fontData.boldHeight + PrintingUtility.itemSpacing;
      //date range.
      g.setClip(usedWidth, usedHeight, intPageWidth - usedWidth,
          fontData.boldHeight + PrintingUtility.itemSpacing);
      if(PrintingUtility.checkLineForPage(usedHeight, intPageHeight, 0))
        g.drawString(header.dateRangeStr, usedWidth,
            usedHeight + fontData.boldHeight - fontData.boldDesent);
      usedHeight += fontData.boldHeight + PrintingUtility.itemSpacing;
    }
    //paint columns.
    g2.setFont(PrintingUtility.tablePlainFont);
    int[] colWidths = new int[colCount];
    int tmpCnt = 0;
    int tmpInt = 0;
    for(int i = 0; i < colCount; i++) {
      colWidths[i] = (int)(columnScale * (double)table.getColumnModel().getColumn(i).getWidth());
      tmpInt = PrintingUtility.getMultiLineStrHeight(table.getColumnName(i), fontData.plainHeight,
          table.getColumnModel().getColumn(i).getWidth(), g2.getFontMetrics());
      if(tmpInt > tmpUsedHeight)
        tmpUsedHeight = tmpInt;
    }
    usedWidth = 0;
    g2.setFont(PrintingUtility.tablePlainFont);
    for(int i = startCol; i < endCol; i++) { //colCount; i++) {
      //if(PrintingUtility.checkLineForPage(colStartHeight, intPageHeight, currentRowPage, usedWidth, colWidths[i], intPageWidth, currentColPage)) {
      g2.setColor(PrintingUtility.lightGrey);
      g2.setClip(PrintingUtility.getMinPageWidthClip(usedWidth, intPageWidth, 0), usedHeight,
          PrintingUtility.getMaxPageWidthClip(usedWidth, colWidths[i], intPageWidth, 0),
          colHeaderHeight); // tmpMaxHeight + 2 * PrintingUtility.itemSpacing + 2 * PrintingUtility.gridWidth);
      g2.fillRect(usedWidth, usedHeight, colWidths[i], colHeaderHeight); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + 2 * PrintingUtility.gridWidth);
      g2.setColor(PrintingUtility.blackColor);
      if(i == startCol)
        g2.drawLine(usedWidth, usedHeight, usedWidth, usedHeight + colHeaderHeight); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + 2 * PrintingUtility.gridWidth);
      g2.drawLine(usedWidth, usedHeight, usedWidth + colWidths[i], usedHeight);
      g2.drawLine(usedWidth + colWidths[i], usedHeight, usedWidth + colWidths[i],
          usedHeight + colHeaderHeight); // + tmpUsedHeight + 2 * PrintingUtility.itemSpacing + 2 * PrintingUtility.gridWidth);
      g2.drawLine(usedWidth, usedHeight + colHeaderHeight, usedWidth + colWidths[i],
          usedHeight + colHeaderHeight);
      PrintingUtility.paintMultiLineStr(g2, table.getColumnName(i),
          usedWidth + PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
          usedHeight + PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
          colWidths[i] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing,
          intPageHeight + colHeaderHeight, intPageWidth, 0, 0);
      //}
      usedWidth += colWidths[i];
    }
    usedHeight += colHeaderHeight;
    //paint table data.
    int tmpAdjustment = getStartAdjustment(currentRowPage);
    g2.setColor(PrintingUtility.blackColor);
    for(int j = startRow; j < endRow; j++) {
      usedWidth = 0;
      tmpUsedHeight = fontData.plainHeight;
      for(int i = startCol; i < endCol; i++) {
        if(PrintingUtility.checkLineForPage(usedHeight + simpleRowHeight, intPageHeight,
            currentRowPage, usedWidth, colWidths[i], intPageWidth, currentColPage)) {
          tmpStr = table.getPrintValueAt(j, i);
          g2.setClip(PrintingUtility.getMinPageWidthClip(usedWidth, intPageWidth, 0),
              PrintingUtility.getMinPageHeightClip(usedHeight, intPageHeight, 0),
              PrintingUtility.getMaxPageWidthClip(usedWidth, colWidths[i], intPageWidth, 0),
              PrintingUtility.getMaxPageHeightClip(usedHeight, rowHeights[j], intPageHeight, 0)); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth);
          if(tmpStr != null) {
            if(rowHeights[j] > simpleRowHeight) {
              tmpInt = PrintingUtility.paintPageBreakMultiLineStr(g2, tmpStr,
                  usedWidth + PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
                  usedHeight + PrintingUtility.gridWidth + PrintingUtility.itemSpacing -
                  tmpAdjustment,
                  colWidths[i] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing,
                  intPageHeight, 0, usedHeight);
              tmpInt = PrintingUtility.paintMultiLineStr(g2, tmpStr,
                  usedWidth + PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
                  usedHeight + PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
                  colWidths[i] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing,
                  intPageHeight, intPageWidth, 0, 0); //currentRowPage, currentColPage);
              tmpInt = PrintingUtility.getMultiLineStrHeight(tmpInt, fontData.plainHeight);
              if(tmpInt > tmpUsedHeight)
                tmpUsedHeight = tmpInt;
            }
            else {
              PrintingUtility.paintElipseStr(g2, tmpStr,
                  usedWidth + 2 * PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
                  usedHeight + fontData.plainHeight - fontData.plainDesent +
                  PrintingUtility.itemSpacing,
                  colWidths[i] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing,
                  usedHeight, simpleRowHeight);
              //g2.drawString(tmpStr, usedWidth + 2 * PrintingUtility.gridWidth + PrintingUtility.itemSpacing, usedHeight + fontData.plainHeight - fontData.plainDesent + PrintingUtility.itemSpacing);
            }
          }
          else {
            table.printValueAt(g2, j, i,
                usedWidth + 2 * PrintingUtility.gridWidth + PrintingUtility.itemSpacing,
                usedHeight + PrintingUtility.itemSpacing,
                colWidths[i] - 2 * PrintingUtility.gridWidth - 2 * PrintingUtility.itemSpacing,
                rowHeights[j] - 2 * PrintingUtility.gridWidth);
          }
          g2.setClip(PrintingUtility.getMinPageWidthClip(usedWidth, intPageWidth, 0),
              PrintingUtility.getMinPageHeightClip(usedHeight, intPageHeight, 0),
              PrintingUtility.getMaxPageWidthClip(usedWidth, colWidths[i], intPageWidth, 0),
              PrintingUtility.getMaxPageHeightClip(usedHeight, rowHeights[j], intPageHeight, 0)); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth);
          if(i == startCol)
            g2.drawLine(usedWidth, usedHeight, usedWidth,
                usedHeight + rowHeights[j] - tmpAdjustment); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth);
          g2.drawLine(usedWidth + colWidths[i], usedHeight, usedWidth + colWidths[i],
              usedHeight + rowHeights[j] - tmpAdjustment); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + 2 * PrintingUtility.gridWidth);
          g2.drawLine(usedWidth, usedHeight + rowHeights[j] - tmpAdjustment,
              usedWidth + colWidths[i],
              usedHeight + rowHeights[j] - tmpAdjustment); //tmpUsedHeight + 2 * PrintingUtility.itemSpacing + PrintingUtility.gridWidth);
        }
        usedWidth += colWidths[i];
      }
      usedHeight += rowHeights[j] - tmpAdjustment; //tmpUsedHeight + PrintingUtility.gridWidth + 2 * PrintingUtility.itemSpacing;
      tmpAdjustment = 0;
    }
    //disclaimer
    //g2.setFont(PrintingUtility.tablePlainFont);
    //g2.setColor(PrintingUtility.redColor);
    //tmpMaxLine = PrintingUtility.paintMultiLineStr(g2, PrintingUtility.EMAIL_DISCLAIMER, 1, intPageHeight + PrintingUtility.itemSpacing,
    //intPageWidth - 1, intPageHeight + disclaimerHeight + PrintingUtility.itemSpacing, intPageWidth, 0, 0);//currentRowPage, currentColPage);
    //paint page
    g2.setFont(PrintingUtility.tablePlainFont);
    g2.setColor(PrintingUtility.blackColor);
    g2.setClip(0, intPageHeight + 2 * PrintingUtility.itemSpacing, intPageWidth,
        fontData.plainHeight + PrintingUtility.lineSpacing);
    g2.drawString("Page " + (pageIndex + 1) + " of " + totalPageCount + "  - printed on " +
        DataUtility.dateToDisplayString(currentDateTime), (intPageWidth / 2 - 70), intPageHeight + 2 * PrintingUtility.itemSpacing + fontData.plainHeight - fontData.plainDesent);
    //end
    return Printable.PAGE_EXISTS;
  }

  protected int getStartRow(int rowPage) {
    if(pageBreakData == null)
      return -1;
    return pageBreakData[rowPage].startIndex;
  }

  protected int getStartAdjustment(int rowPage) {
    if(pageBreakData == null)
      return -1;
    return pageBreakData[rowPage].multiLineAdjustment;
  }

  protected int getEndRow(int rowPage) {
    if(pageBreakData == null)
      return -1;
    int totalLines = rowHeights.length;
    if(rowPage + 1 < pageBreakData.length) {
      totalLines = pageBreakData[rowPage + 1].startIndex;
      if(pageBreakData[rowPage + 1].multiLineAdjustment > 0)
        totalLines += 1;
    }
    return totalLines;
  }

  protected int getStartColumn(int colPage) {
    if(columnBreakData == null)
      return -1;
    int tmpCnt = 0;
    for(int i = 0; i < colPage; i++) {
      tmpCnt += columnBreakData[i];
    }
    return tmpCnt;
  }

  protected int getEndColumn(int colPage) {
    if(columnBreakData == null)
      return -1;
    int tmpCnt = 0;
    for(int i = 0; i < colPage + 1; i++)
      tmpCnt += columnBreakData[i];
    return tmpCnt;
  }

  class PageBreak {
    int startIndex = -1;
    int multiLineAdjustment = -1;

    public PageBreak() {}

    public PageBreak(int _startIndex, int _multiLineAdjustment) {
      startIndex = _startIndex;
      multiLineAdjustment = _multiLineAdjustment;
    }
  }

  class PrintHeader {
    String titleString;
    String dateRangeStr;
    String createdDTSStr;

    public PrintHeader(String _titleString, String _dateRangeStr, String _createdDTSStr) {
      titleString = _titleString;
      dateRangeStr = _dateRangeStr;
      createdDTSStr = "Created On: " + _createdDTSStr;
    }

    public String getJobName() {
      return titleString + " - " + dateRangeStr;
    }
  }
}

package net.saucefactory.swing.table;

import net.saucefactory.swing.utils.TextUtility;
import java.awt.Color;

public class SFTableHtmlExportAdapter extends SFAbstractTableExportAdapter {

  private int pageWidth = 900;
  private double colWidthSum = 0;
  private int runningPercent = 0;
  private int colCount = 0;

  public String headerFont = "<font size=2 face=bold type=arial>";
  public String cellFont = "<font size=2 type=arial>";

  public SFTableHtmlExportAdapter() {
  }

  public void initExportAdapter(SFTable table) {
    super.initExportAdapter(table);
    setHeaderBackgroundColor(new Color(153, 153, 153));
    SFTableColumn[] htmlCols = table.getTableModel().getColumnHeaders();
    if(htmlCols == null)
      ready = false;
    else {
      ready = true;
      colCount = htmlCols.length;
      for(int i = 0; i < colCount; i++)
        colWidthSum += (double)htmlCols[i].getColumnWidth();
    }
  }

  public Object getTableValueAt(int row, int column) {
    String rtn = table.getStringValueAt(row, column, false);
    if(rtn == null || rtn.equals(""))
      rtn = "&nbsp;";
    return rtn;
  }

  public void appendBodyStart(StringBuffer buf) {
    buf.append("<table cellpadding=2 cellspacing=0 border=1 width=" + pageWidth + ">\n");
  }

  public void appendBodyEnd(StringBuffer buf) {
    buf.append("</table>");
  }

  public void appendHeaderStart(StringBuffer buf) {
    buf.append("<tr>\n");
  }

  public void appendHeaderColumnStart(StringBuffer buf, SFTableColumn header, int column) {
    double tmpColWidth = (double)header.getColumnWidth();
    double tmpPercent = tmpColWidth / colWidthSum * 100;
    int tmpWidth = (int)tmpPercent;
    if(column == (colCount - 1))
      tmpWidth = 100 - runningPercent;
    else
      runningPercent += tmpWidth;
    buf.append("<td bgcolor=#" + getHeaderBackgroundHex() + " align=left valign=top width=" + tmpWidth +
        "%>" + headerFont);
  }

  public void appendHeaderColumnValue(StringBuffer buf, SFTableColumn header, int column) {
    String tmpTitle = header.getColumnName();
    tmpTitle = TextUtility.convertBreaksToHtmlBreaks(tmpTitle);
    buf.append(tmpTitle);
  }

  public void appendHeaderColumnEnd(StringBuffer buf, SFTableColumn header, int column) {
    buf.append("</font></td>\n");
  }

  public void appendHeaderEnd(StringBuffer buf) {
    buf.append("</tr>\n");
  }

  public void appendDataStart(StringBuffer buf) {
    //nothing
  }

  public void appendDataRowStart(StringBuffer buf, int row) {
    buf.append("<tr>\n");
  }

  public void appendDataColumnStart(StringBuffer buf, Object columnValue, int row, int column) {
    buf.append("<td align=left valign=top>" + cellFont);
  }

  public void appendDataColumnValue(StringBuffer buf, Object columnValue, int row, int column) {
    buf.append((String)columnValue);
  }

  public void appendDataColumnEnd(StringBuffer buf, Object columnValue, int row, int column) {
    buf.append("</font></td>\n");
  }

  public void appendDataRowEnd(StringBuffer buf, int row) {
    buf.append("</tr>\n");
  }

  public void appendDataEnd(StringBuffer buf) {
    //nothing
  }
}

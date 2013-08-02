package net.saucefactory.text.diff;

import java.util.*;
/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class Diff implements DiffStatus {
  private int addedLines = 0;
  private int deletedLines = 0;
  private int changedLines = 0;
  private int unchangedLines = 0;
  private CloneableDiffItemList compDiffItems = new CloneableDiffItemList(); // contains DiffItems
  private CloneableDiffItemList baseDiffItems = new CloneableDiffItemList(); // contains DiffItems
  private String addedFontColor = "green";
  private String deletedFontColor = "red";
  private String changedFontColor = "orange";
  private String unchangedFontColor = "black";
  private String font = "sans-serif";
  private int fontSize = 10;
  private boolean showAbsoluteLineNumbers = false;
  private boolean showCurrentLineNumbers = false;
  private boolean showStatusIndicator = false;

  public Object clone()
      throws CloneNotSupportedException {
    Diff clone = new Diff();
    clone.addedLines = addedLines;
    clone.deletedLines = deletedLines;
    clone.changedLines = changedLines;
    clone.unchangedLines = unchangedLines;
    clone.compDiffItems = (CloneableDiffItemList)compDiffItems.clone(); // contains DiffItems
    clone.baseDiffItems = (CloneableDiffItemList)baseDiffItems.clone(); // contains DiffItems
    clone.addedFontColor = new String(addedFontColor);
    clone.deletedFontColor = new String(deletedFontColor);
    clone.changedFontColor = new String(changedFontColor);
    clone.unchangedFontColor = new String(unchangedFontColor);
    clone.font = new String(unchangedFontColor);
    clone.fontSize = fontSize;
    clone.showAbsoluteLineNumbers = showAbsoluteLineNumbers;
    clone.showCurrentLineNumbers = showCurrentLineNumbers;
    clone.showStatusIndicator = showStatusIndicator;
    return clone;
  }

  void incAddedLines() {
    addedLines++;
  }

  void incChangedLines() {
    changedLines++;
  }

  void incUnchangedLines() {
    changedLines++;
  }

  void incDeletedLines() {
    deletedLines++;
  }

  public void setFontName(String fontName_) {
    font = fontName_;
  }

  public void setFontSize(int fontSize_) {
    fontSize = fontSize_;
  }

  public void setAddedFontColor(String addedFontColor_) {
    addedFontColor = addedFontColor_;
  }

  public void setChangedFontColor(String changedFontColor_) {
    changedFontColor = changedFontColor_;
  }

  public void setDeletedFontColor(String deletedFontColor_) {
    deletedFontColor = deletedFontColor_;
  }

  public void setUnchangedFontColor(String unchangedFontColor_) {
    unchangedFontColor = unchangedFontColor_;
  }

  public void showAbsoluteLineNumbers(boolean b) {
    showAbsoluteLineNumbers = b;
  }

  public void showCurrentLineNumbers(boolean b) {
    showCurrentLineNumbers = b;
  }

  public void showStatusIndicator(boolean b) {
    showStatusIndicator = b;
  }

  public int getAddedLines() {
    return addedLines;
  }

  public int getChangedLines() {
    return changedLines;
  }

  public int getUnchangedLines() {
    return unchangedLines;
  }

  public int getDeletedLines() {
    return deletedLines;
  }

  void addBaseDiffItem(DiffItem DiffItem) {
    baseDiffItems.add(DiffItem);
  }

  void addComparisonDiffItem(DiffItem DiffItem) {
    compDiffItems.add(DiffItem);
  }

  public DiffItem[] getBaseDiffItemsArray() {
    DiffItem[] rtn = new DiffItem[baseDiffItems.size()];
    rtn = (DiffItem[])baseDiffItems.toArray(rtn);
    return rtn;
  }

  public Collection getBaseDiffItemsCollection() {
    return baseDiffItems;
  }

  public DiffItem[] getComparisonDiffItemsArray() {
    DiffItem[] rtn = new DiffItem[compDiffItems.size()];
    rtn = (DiffItem[])compDiffItems.toArray(rtn);
    return rtn;
  }

  public Collection getComparisonDiffItemsCollection() {
    return compDiffItems;
  }

  protected String getHTML(DiffItem[] DiffItems) {
    StringBuffer buf = new StringBuffer();
    buf.append("<HTML>\n<BODY>\n<TABLE cellspacing=0 cellpadding=1 width=1000>\n");
    for (int i = 0; i < DiffItems.length; i++) {
      buf.append("<TR>\n");
      buf.append(getLineHTML(DiffItems[i]));
      buf.append("</TR>\n");
    }
    buf.append("</TABLE>\n</BODY>\n</HTML>");
    return buf.toString();
  }

  protected String getStatusIndicator(DiffItem DiffItem) {
    switch (DiffItem.status) {
      case ADDED:
        return "+";
      case DELETED:
        return "-";
      case CHANGED:
        return "*";
      default:
        return " ";
    }
  }

  protected String getFontTag(DiffItem DiffItem) {
    StringBuffer buf = new StringBuffer();
    buf.append("<FONT style=\"");
    buf.append("color: ");
    buf.append(getFontColor(DiffItem));
    buf.append(";");
    buf.append("font-size: ");
    buf.append(fontSize);
    buf.append("pt;");
    buf.append("font-family: ");
    buf.append(font);
    buf.append(";");
    if (DiffItem.status != NO_CHANGE) {
      buf.append("font-weight: bold;");
    }
    buf.append("\">\n");
    return buf.toString();
  }

  protected String getFontTag() {
    StringBuffer buf = new StringBuffer();
    buf.append("<FONT style=\"");
    buf.append("font-size: ");
    buf.append(fontSize);
    buf.append("pt;");
    buf.append("font-family: ");
    buf.append(font);
    buf.append(";");
    buf.append("\">\n");
    return buf.toString();
  }

  protected String getFontTagClose() {
    return "</FONT>";
  }

  protected String getFontColor(DiffItem DiffItem) {
    switch (DiffItem.status) {
      case ADDED:
        return addedFontColor;
      case DELETED:
        return deletedFontColor;
      case CHANGED:
        return changedFontColor;
      default:
        return unchangedFontColor;
    }
  }

  protected String getLineHTML(DiffItem DiffItem) {
    StringBuffer buf = new StringBuffer();
    if (showAbsoluteLineNumbers) {
      buf.append("\n<TD width=20>");
      buf.append(getFontTag());
      buf.append(DiffItem.lineNo);
      buf.append(getFontTagClose());
      buf.append("\n</TD>");
    }
    if (showCurrentLineNumbers) {
      buf.append("\n<TD width=20>");
      buf.append(getFontTag(DiffItem));
      buf.append(DiffItem.currentLineNo > 0 ? String.valueOf(DiffItem.currentLineNo) : " ");
      buf.append(getFontTagClose());
      buf.append("\n</TD>");
    }
    if (showStatusIndicator) {
      buf.append("\n<TD width=20>");
      buf.append(getFontTag(DiffItem));
      buf.append(getStatusIndicator(DiffItem));
      buf.append(getFontTagClose());
      buf.append("\n</TD>");
    }
    buf.append("\n<TD>");
    buf.append(getFontTag(DiffItem));
    buf.append(DiffItem.textNoDelimiter.equals("") ? "&nbsp;" : DiffItem.text);
    buf.append(getFontTagClose());
    buf.append("\n</TD>");
    return buf.toString();
  }

  public String getCompHTML() {
    DiffItem[] DiffItems = getComparisonDiffItemsArray();
    return getHTML(DiffItems);
  }

  public String getBaseHTML() {
    DiffItem[] DiffItems = getBaseDiffItemsArray();
    return getHTML(DiffItems);
  }

  public String getCompText() {
    DiffItem[] DiffItems = getComparisonDiffItemsArray();
    return null;
  }

  public String getBaseText() {
    DiffItem[] DiffItems = getBaseDiffItemsArray();
    return null;
  }

  class CloneableDiffItemList extends ArrayList {
    /**
     * Returns a deep copy of this <tt>ArrayList</tt> instance.  (The
     * elements themselves are copied.)
     *
     * @return  a clone of this <tt>CloneableDiffItemList</tt> instance.
     */
    public Object clone() {
        CloneableDiffItemList v = new CloneableDiffItemList();
        Iterator iter = v.iterator();
        while (iter.hasNext()){
          DiffItem item = (DiffItem)iter.next();
          v.add(item.clone());
        }
        return v;
    }
  }
}

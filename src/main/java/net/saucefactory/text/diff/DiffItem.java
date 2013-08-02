package net.saucefactory.text.diff;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class DiffItem implements DiffStatus, Cloneable {
  public int lineNo = 0;
  public int currentLineNo = -1;
  public String textNoDelimiter = null;
  public String delimiter = null;
  public String text = null;
  public String compTextNoDelimiter = null;
  public String compText = null;
  public int status = 0;

  public DiffItem() {
  }

  public DiffItem(int lineNo_, String line_, String compLine_, int status_) {
    lineNo = lineNo_;
    text = line_;
    compText = compLine_;
    status = status_;
    textNoDelimiter = line_;
    compTextNoDelimiter = compLine_;
  }

  public DiffItem(int lineNo_, String line_, String compLine_, int status_, String delim) {
    lineNo = lineNo_;
    text = line_ + delim;
    compText = compLine_ + delim;
    delimiter = delim;
    textNoDelimiter = line_;
    compTextNoDelimiter = compLine_;
    status = status_;
  }

  public DiffItem(int lineNo_, int status_, String compLine_, String delim) {
    lineNo = lineNo_;
    text = delim;
    delimiter = delim;
    compText = compLine_ + delim;
    compTextNoDelimiter = compLine_;
    textNoDelimiter = text;
    status = status_;
  }

  public DiffItem(int lineNo_, int currentLineNo_, String line_, String compLine_, int status_) {
    lineNo = lineNo_;
    currentLineNo = currentLineNo_;
    text = line_;
    textNoDelimiter = line_;
    compText = compLine_;
    compTextNoDelimiter = compLine_;
    status = status_;
  }

  public DiffItem(int lineNo_, int currentLineNo_, String line_, String compLine_, int status_, String delim) {
    lineNo = lineNo_;
    currentLineNo = currentLineNo_;
    textNoDelimiter = line_;
    text = line_ + delim;
    compText = compLine_ + delim;
    compTextNoDelimiter = compLine_;
    delimiter = delim;
    status = status_;
  }

  public String toString() {
    return text;
  }

  public Object clone() {
    DiffItem clone = new DiffItem();
    clone.lineNo = lineNo;
    clone.currentLineNo = currentLineNo;
    clone.textNoDelimiter = textNoDelimiter;
    clone.text = text;
    clone.compText = compText;
    clone.compTextNoDelimiter = compTextNoDelimiter;
    clone.delimiter = delimiter;
    clone.status = status;
    return clone;
  }
}

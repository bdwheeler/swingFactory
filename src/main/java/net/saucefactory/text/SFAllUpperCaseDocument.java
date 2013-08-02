package net.saucefactory.text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.awt.event.*;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.text.*;

public class SFAllUpperCaseDocument extends PlainDocument {
  public SFAllUpperCaseDocument() {
    super();
  }

  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    if (str.length() > 1)
      super.insertString(offset, str, a);
    else
      super.insertString(offset, str.toUpperCase(), a);
  }
}

package net.saucefactory.swing.datepicker;

import java.awt.event.*;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.text.*;

public class SFShortDateFilter
  extends PlainDocument {
  protected int maxLength = 10;
  protected static SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'yyyy");
  protected SFDateTextFieldGroup group = null;

  public void insertString(int offset, String str, AttributeSet a)
    throws BadLocationException {
    int length = super.getLength();
    if (length >= maxLength)  {
        beep();
        return;
    }
    if (str.length() > 1) {
      super.insertString(offset, str, a);
      return;
    }
    int newoffset = offset;
    switch (offset) {
      case 0: {
        if (!isNumeric(str)) {
          beep();
          return;
        }
        if ((!str.equalsIgnoreCase("1")) && !str.equalsIgnoreCase("0")) {
          beep();
          return;
        }
        break;
      } case 1: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        int pre = Integer.parseInt(this.getText(0, 1));
        if (pre == 1 && Integer.parseInt(str) > 2) return;
        if (newoffset == this.getLength()) str += "/";
        break;
      } case 2: {
        if (!str.equalsIgnoreCase("/"))  {
          beep();
          return;
        }
        break;
      } case 3: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        if (Integer.parseInt(str) > 3)  {
          beep();
          return;
        }
        break;
      } case 4: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        int pre = Integer.parseInt(this.getText(3, 1));
        if (pre == 3 && Integer.parseInt(str) > 1)  {
          beep();
          return;
        }
        if (newoffset == this.getLength()) str += "/";
        break;
      } case 5: {
        if (!str.equalsIgnoreCase("/"))  {
          beep();
          return;
        }
        break;
      } case 6: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        break;
      } case 7: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        if (this.getLength() != 9) {
        String pre = this.getText(6, 1);
        str = this.getText(0, 6) + "20" + pre + str;
        try {
          Date dt = parseDate(str);
          str = formatDate(dt);
        } catch (Exception ee) {}
        this.remove(0, getLength());
        newoffset = 0;
        }
        break;
      } default: {
        if (!isNumeric(str))  {
          beep();
          return;
        }
        break;
      }
    }

    super.insertString(newoffset, str, a);
  }

  public void addDocumentListener(DocumentListener listener) {
    super.addDocumentListener(listener);
    if (listener instanceof SFDateTextFieldGroup)
      group = (SFDateTextFieldGroup)listener;
  }

  public void removeDocumentListener(DocumentListener listener) {
    super.removeDocumentListener(listener);
    if (listener.equals(group))
      group = null;
  }

  public SFDateTextFieldGroup getDateTextFieldGroup() {
    return group;
  }

  protected boolean isNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if ((!Character.isDigit(str.charAt(i))))
        return false;
    }
    return true;
  }

  protected void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public static Date getTextFieldDate(JTextField txt) throws Exception {
    try {
      Date rtn = sdf.parse(txt.getText());
      return rtn;
    } catch (Exception e) {
      return null;
    }
  }

  public static Date parseDate(String txt) throws Exception {
    try {
      Date rtn = sdf.parse(txt);
      return rtn;
    } catch (Exception e) {
      return null;
    }
  }

  public static String formatDate(Date date) throws Exception {
    try {
      String rtn = sdf.format(date);
      return rtn;
    } catch (Exception e) {
      return null;
    }
  }

  public static SFDateTextFieldGroup getDateTextFieldGroup(SFDateTextField textField) {
    try {
      return ((SFShortDateFilter)textField.getDocument()).getDateTextFieldGroup();
    } catch (Exception e) {
      return null;
    }
  }

  public static void formatTextField(JTextField txt) throws Exception {
    String str = new String();
    StringTokenizer tokenizer = new StringTokenizer(txt.getText(), "/");
    if (tokenizer.countTokens() < 3) throw new Exception("invalid date");
    int month = Integer.parseInt(tokenizer.nextToken());
    int date = Integer.parseInt(tokenizer.nextToken());
    int year = Integer.parseInt(tokenizer.nextToken());
    if ((year > 0) & (year < 100)) year = year + 2000;
    if (year < 2000 || year > 2099) throw new Exception("invalid date");
    String apd = "", apd2 = "";
    if (month < 10) apd = "0";
    if (date < 10) apd2 = "0";
    txt.setText(apd + month + "/" + apd2 + date + "/" + year);
  }

  public static void install(JTextField textField) {
    SFShortDateFilter filter = new SFShortDateFilter();
    textField.setDocument(filter);
  }
}
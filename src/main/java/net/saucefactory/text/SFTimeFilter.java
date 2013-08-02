package net.saucefactory.text;

import java.awt.event.*;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.text.*;

public class SFTimeFilter extends PlainDocument implements FocusListener {
  private int maxLength = 5;
  boolean requestFocus = false;
  public static SimpleDateFormat sdf = new SimpleDateFormat("HH':'mm");
  public static SimpleDateFormat sdfLocal = new SimpleDateFormat("HH':'mm");
  private boolean doLocal = false;

  static {
    TimeZone t = TimeZone.getTimeZone("America/Los_Angeles");
    SimpleTimeZone tz = new SimpleTimeZone(t.getRawOffset(), t.getID());
    tz.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    tz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    sdf.setTimeZone(tz);
    TimeZone t2 = TimeZone.getDefault();
    SimpleTimeZone tz2 = new SimpleTimeZone(t2.getRawOffset(), t2.getID());
    tz2.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    tz2.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    sdfLocal.setTimeZone(tz2);

  }

  public SFTimeFilter() {

  }

  public SFTimeFilter(boolean doLocal_) {
    doLocal = doLocal_;
  }

  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    int length = super.getLength();
    if(str.length() < 1) {
      super.insertString(offset, str, a);
      return;
    }
    if(length >= maxLength) {
      beep();
      return;
    }
    if(str.length() > 1) {
      super.insertString(offset, str, a);
      return;
    }
    int newoffset = offset;
    switch(offset) {
      case 0: {
        if(!isNumeric(str)) {
          beep();
          return;
        }
        if(!str.equalsIgnoreCase("1") && !str.equalsIgnoreCase("0") && !str.equalsIgnoreCase("2")) {
          beep();
          return;
        }
        break;
      }
      case 1: {
        if(!isNumeric(str)) {
          beep();
          return;
        }
        int pre = Integer.parseInt(this.getText(0, 1));
        if(pre == 2 && Integer.parseInt(str) > 3) {
          beep();
          return;
        }
        if(this.getLength() == offset)str += ":";
        break;
      }
      case 2: {
        if(!str.equalsIgnoreCase(":")) {
          beep();
          return;
        }
        break;
      }
      case 3: {
        if(!isNumeric(str))return;
        if(Integer.parseInt(str) > 5) {
          beep();
          return;
        }
        break;
      }
      case 4: {
        if(!isNumeric(str)) {
          beep();
          return;
        }
        break;
      }
      default: {
        if(!isNumeric(str)) {
          beep();
          return;
        }
        break;
      }
    }
    super.insertString(newoffset, str, a);
  }

  private boolean isNumeric(String str) {
    for(int i = 0; i < str.length(); i++) {
      if((!Character.isDigit(str.charAt(i))))
        return false;
    }
    return true;
  }

  private void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public void focusGained(FocusEvent e) {}

  public void focusLost(FocusEvent e) {
    JTextField txt1 = (JTextField)e.getSource();
    if(txt1.getText().length() == 0)return;
    try {
      checkFormat(txt1);
    }
    catch(Exception ee) {
      //System.out.println("Failed time format.");
      if(!requestFocus)return;
      final JTextField txt = (JTextField)e.getSource();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          txt.requestFocus();
          beep();
        }
      });
    }
  }

  private static void checkFormat(JTextField txt) throws Exception {
    sdf.parse(txt.getText());
    if(txt.getText().length() < 5)throw new Exception();
  }

  private static void checkLocalFormat(JTextField txt) throws Exception {
    sdfLocal.parse(txt.getText());
    if(txt.getText().length() < 5)throw new Exception();
  }

  public static long getTextFieldTime(JTextField txt) throws Exception {
    checkFormat(txt);
    int hours = Integer.parseInt(txt.getText(0, 2));
    int mins = Integer.parseInt(txt.getText(3, 2));
    long ms = (hours * 60 * 60 * 1000) + (mins * 60 * 1000);
    return ms;
  }

  public static long getTextFieldLocalTime(JTextField txt) throws Exception {
    checkLocalFormat(txt);
    int hours = Integer.parseInt(txt.getText(0, 2));
    int mins = Integer.parseInt(txt.getText(3, 2));
    long ms = (hours * 60 * 60 * 1000) + (mins * 60 * 1000);
    return ms;
  }

  public static int getTextFieldHours(JTextField txt) throws Exception {
    checkFormat(txt);
    return Integer.parseInt(txt.getText(0, 2));
  }

  public static int getTextFieldMinutes(JTextField txt) throws Exception {
    checkFormat(txt);
    return Integer.parseInt(txt.getText(3, 2));
  }

  public static void setTextFieldTime(JTextField txt, Date date) {
    txt.setText(sdf.format(date));
  }

  public static void setTextFieldLocalTime(JTextField txt, Date date) {
    txt.setText(sdfLocal.format(date));
  }

  public static void install(JTextField textField, boolean keepFocus) {
    SFTimeFilter filter = new SFTimeFilter();
    textField.setDocument(filter);
    filter.requestFocus = keepFocus;
    textField.addFocusListener(filter);
  }

  public static void install(JTextField textField, boolean keepFocus, boolean doLocal) {
    SFTimeFilter filter = new SFTimeFilter(doLocal);
    textField.setDocument(filter);
    filter.requestFocus = keepFocus;
    textField.addFocusListener(filter);
  }

}

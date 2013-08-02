package net.saucefactory.swing.datepicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SFDateTextField
  extends JTextField {
  public static final int FORMAT_MM_dd_yyyy = 0;
  public static final int FORMAT_dd_MM_yyyy = 1;
  public int format = 0;
  public boolean keepFocus = false;
  protected FocusHandler focusHandler = new FocusHandler();

  public SFDateTextField() {
    init(null, format);
  }

  public SFDateTextField(boolean keepFocus) {
    this.keepFocus = keepFocus;
    init(null, format);
  }

  public SFDateTextField(int format, boolean keepFocus) {
    init(null, format);
  }

  public SFDateTextField(Date date, boolean keepFocus) {
    this.keepFocus = keepFocus;
    init(date, format);
  }

  public SFDateTextField(int format, Date date, boolean keepFocus) {
    this.keepFocus = keepFocus;
    init(date, format);
  }

  protected void init(Date date, int newFormat) {
    setMinimumSize(new Dimension(67, 21));
    setPreferredSize(new Dimension(67, 21));
    SFDateTextFieldGroup group = null;
    if (format == 0) {
      group = SFUSShortDateFilter.getDateTextFieldGroup(this);
    } else {
      group = SFEuroShortDateFilter.getDateTextFieldGroup(this);
    }
    format = newFormat;
    if (format == 0) {
      SFUSShortDateFilter.install(this);
    } else {
      SFEuroShortDateFilter.install(this);
    }
    if (group != null) group.reinstall(this);
    if (date != null)
      setDate(date);
    if (keepFocus)
      addFocusListener(focusHandler);
  }

  public void setKeepFocus(boolean b) {
    try {
      removeFocusListener(focusHandler);
    } catch (Exception e) {}
    if (b) addFocusListener(focusHandler);
  }

  public void setFormat(int format) {
    Date date = getDate();
    init(date, format);
  }

  public Date getDate() {
    return parseDate(getText());
  }

  public void setDate(Date date) {
    setText(formatDate(date));
  }

  public boolean equalsDate(Date date) {
    return getDate().getTime() == date.getTime();
  }

  public String formatDate(Date date) {
    try {
      if (format == FORMAT_MM_dd_yyyy)
        return SFUSShortDateFilter.formatDate(date);
      else
        return SFEuroShortDateFilter.formatDate(date);
    } catch (Exception e) {
      return null;
    }
  }

  public Date parseDate(String stringDate) {
    try {
      if (format == FORMAT_MM_dd_yyyy)
        return SFUSShortDateFilter.parseDate(stringDate);
      else
        return SFEuroShortDateFilter.parseDate(stringDate);
    } catch (Exception e) {
      return null;
    }
  }

  protected void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  class FocusHandler
    implements FocusListener {

    public void focusGained(FocusEvent e) {}

    public void focusLost(FocusEvent e) {
      Date date = getDate();
      if (date == null)
        requestFocus();
    }
  }
}

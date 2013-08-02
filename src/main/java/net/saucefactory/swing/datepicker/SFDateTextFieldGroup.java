package net.saucefactory.swing.datepicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.event.*;

public class SFDateTextFieldGroup
  implements DocumentListener {
  public static final int DAY = Calendar.DATE;
  public static final int MONTH = Calendar.MONTH;
  public static final int YEAR = Calendar.YEAR;

  public SFDateTextField afterDT = null;
  public SFDateTextField beforeDT = null;

  protected int unit = DAY;
  protected int amount = 0;

  public SFDateTextFieldGroup(SFDateTextField firstDateField, SFDateTextField secondDateField) {
    init(firstDateField, secondDateField);
  }

  public SFDateTextFieldGroup(SFDateTextField firstDateField, SFDateTextField secondDateField,
                              int timeUnit, int amount) {
    unit = timeUnit;
    this.amount = amount;
    init(firstDateField, secondDateField);
  }

  public SFDateTextFieldGroup(SFDateChooser firstDateChooser, SFDateChooser secondDateChooser) {
    init(firstDateChooser.getTextField(), secondDateChooser.getTextField());
  }

  public SFDateTextFieldGroup(SFDateChooser firstDateChooser, SFDateChooser secondDateChooser,
                              int timeUnit, int amount) {
    unit = timeUnit;
    this.amount = amount;
    init(firstDateChooser.getTextField(), secondDateChooser.getTextField());
  }

  protected void init(SFDateTextField afterDT, SFDateTextField beforeDT) {
    this.afterDT = afterDT;
    this.beforeDT = beforeDT;
    afterDT.getDocument().addDocumentListener(this);

    beforeDT.getDocument().addDocumentListener(this);
  }

  public void reinstall(SFDateTextField textField) {
    textField.getDocument().addDocumentListener(this);
  }

  public void setMinimumIntervalUnit(int timeUnit) {
    unit = timeUnit;
    handleAfterChange();
    handleBeforeChange();
  }

  public void setMinimumInterval(int interval) {
    amount = interval;
    handleAfterChange();
    handleBeforeChange();
  }

  public void insertUpdate(DocumentEvent e) {
    if (e.getDocument().equals(afterDT.getDocument()))
      handleAfterChange();
    else if (e.getDocument().equals(beforeDT.getDocument()))
      handleBeforeChange();
  }
  public void removeUpdate(DocumentEvent e) {}
  public void changedUpdate(DocumentEvent e) {}

  public void handleBeforeChange() {
    try {
      if (beforeDT.getText().length() < 10) return;
      Calendar cal = Calendar.getInstance();
      cal.setTime(beforeDT.getDate());
      cal.add(unit, -(amount));
      Date date = cal.getTime();
      if ((afterDT.getText() == null || afterDT.getText().length() < 10) ||
           date.getTime() < afterDT.getDate().getTime())
        afterDT.setDate(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void handleAfterChange() {
    try {
      if (afterDT.getText().length() < 10) return;
      Calendar cal = Calendar.getInstance();
      cal.setTime(afterDT.getDate());
      cal.add(unit, amount);
      Date date = cal.getTime();
      if ((beforeDT.getText() == null || beforeDT.getText().length() < 10) ||
           date.getTime() > beforeDT.getDate().getTime())
        beforeDT.setDate(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
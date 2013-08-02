package net.saucefactory.swing.combo;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SFPredictiveComboBoxEditor implements ComboBoxEditor
{
  SFPredictiveTextField textField;

  public SFPredictiveComboBoxEditor()
  {
    textField = new SFPredictiveTextField();
  }

  public SFPredictiveComboBoxEditor(boolean makeAllCaps)
  {
    textField = new SFPredictiveTextField();
    if (makeAllCaps) textField.forceUpperCase();
  }


  public void setPredictiveSearch(ISFPredictiveSearch searchImpl)
  {
    textField.setPredictiveSearch(searchImpl);
  }

  //ComboBoxEditor
  public Component getEditorComponent()
  {
    return textField;
  }

  public void setItem(Object anObject)
  {
    textField.setText((String)anObject);
    textField.setCaretPosition(0);
  }

  public Object getItem()
  {
    return textField.getText();
  }

  public void selectAll()
  {
    textField.selectAll();
  }

  public void addActionListener(ActionListener l)
  {
    textField.addActionListener(l);
  }

  public void removeActionListener(ActionListener l)
  {
    textField.removeActionListener(l);
  }
}
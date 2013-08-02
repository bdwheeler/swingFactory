package net.saucefactory.swing.editors;

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
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import net.saucefactory.swing.buttons.*;
import net.saucefactory.swing.*;
import net.saucefactory.swing.table.*;

public class SFMultilinePopupEditor extends JPanel implements TableCellEditor, TableCellRenderer
{
  private BorderLayout mainLayout = new BorderLayout();
  private JTextArea textField = new JTextArea();
  private SFArrowButton popupButton = new SFArrowButton(SFArrowButton.SOUTH);
  public String getText(){return textField.getText();}
  public void setText(String text){textField.setText(text);}
  protected transient Vector listeners = new Vector();
  protected transient String originalValue;

  public SFMultilinePopupEditor()
  {
    super();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    this.setLayout(mainLayout);
    textField.setLineWrap(true);
    textField.setWrapStyleWord(true);
    textField.setEditable(false);
    popupButton.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent ae) {
       SFTextViewInternalFrame viewFrame = new SFTextViewInternalFrame("Text Detail", getText(), false);
       SwingUtilities.getWindowAncestor(textField).add(viewFrame);//DefaultMainFrame.getHandle().addCenteredInternalFrame(viewFrame, "EditPopup", 600, 500, 2, true);
       viewFrame.startModal();
     }
    });
    add(textField, BorderLayout.CENTER);
    add(popupButton, BorderLayout.EAST);
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    textField.setFont(table.getFont());
    popupButton.setVisible(true);
    if(value == null) {
      setText("");
      return this;
    }
    try {
      String tmpStr = (String)value;
      setText(tmpStr);
      table.setRowSelectionInterval(row, row);
      table.setColumnSelectionInterval(column, column);
      originalValue = getText();
    }
    catch(Exception e){
      setText("");
    }
    try {
     SFObjectTable oTable = (SFObjectTable)table;
     ISFTableColorAdapter adapter = oTable.getColorAdapter();
     textField.setForeground(adapter.getForeground(table, isSelected, true, row, column));
     textField.setBackground(adapter.getBackground(table, isSelected, true, row, column));
   } catch (Exception e) {
       textField.setForeground(table.getSelectionForeground());
       textField.setBackground(table.getSelectionBackground());
   }

    return this;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    textField.setFont(table.getFont());
    popupButton.setVisible(false);
    setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    try {
      SFObjectTable oTable = (SFObjectTable)table;
      ISFTableColorAdapter adapter = oTable.getColorAdapter();
      textField.setForeground(adapter.getForeground(table, isSelected, hasFocus, row, column));
      textField.setBackground(adapter.getBackground(table, isSelected, hasFocus, row, column));
    } catch (Exception e) {
      if(isSelected && (!hasFocus || (hasFocus && !table.isCellEditable(row, column))))
      {
	textField.setForeground(table.getSelectionForeground());
	textField.setBackground(table.getSelectionBackground());
      }
      else
      {
	textField.setForeground(table.getForeground());
	textField.setBackground(table.getBackground());
      }
    }
    if(value != null) {
      setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getWidth(), getHeight()));
      setText((String)value);
    }
    else
      setText("");
/*
    if(textField.getText() != null && textField.getText().length() > 0) {
      table.setRowHeight(row, 50);
    }
*/
    return this;
  }

  public void addCellEditorListener(CellEditorListener cel){listeners.addElement(cel);}
  public void removeCellEditorListener(CellEditorListener cel){listeners.removeElement(cel);}
  public boolean shouldSelectCell(java.util.EventObject eo){return true;}
  public boolean isCellEditable(java.util.EventObject eo){return true;}
  public void cancelCellEditing(){fireEditingCanceled();}

  public boolean stopCellEditing()
  {
    fireEditingStopped();
    return true;
  }

  public Object getCellEditorValue()
  {
    return getText();
  }

  protected void fireEditingCanceled()
  {
    setText(originalValue);
    ChangeEvent ce = new ChangeEvent(this);
    for(int i = 0; i < listeners.size(); i++)
      ((CellEditorListener)listeners.elementAt(i)).editingCanceled(ce);
  }

  protected void fireEditingStopped()
  {
    ChangeEvent ce = new ChangeEvent(this);
    for(int i = 0; i < listeners.size(); i++)
      ((CellEditorListener)listeners.elementAt(i)).editingStopped(ce);
  }
}
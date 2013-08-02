package net.saucefactory.swing.editors;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.TableCellEditor;
import net.saucefactory.swing.table.ISFTable;
import net.saucefactory.swing.table.ISFTableColorAdapter;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class SFBooleanEditor extends JCheckBox implements TableCellEditor, ItemListener {
  protected transient Vector listeners;
  protected transient Boolean originalValue = Boolean.FALSE;

  public SFBooleanEditor() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFBooleanEditor(int hAlign, int vAlign) {
    setOpaque(true);
    setVerticalAlignment(vAlign);
    setHorizontalAlignment(hAlign);
    setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    listeners = new Vector();
    addItemListener(this);
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    if(((ISFTable)table).hasColorAdapter()) {
        ISFTableColorAdapter adapter = ((ISFTable)table).getColorAdapter();
        setForeground(adapter.getForeground(table, isSelected, isSelected, row, column));
        setBackground(adapter.getBackground(table, isSelected, isSelected, row, column));
    }
    else {
      if(isSelected || table.getSelectedRow() == row || table.getEditingRow() == row) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      }
      else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
    }
    if(value != null) {
      Boolean tmpBool = (Boolean)value;
      setSelected(tmpBool.booleanValue());
      table.setRowSelectionInterval(row, row);
      table.setColumnSelectionInterval(column, column);
      originalValue = new Boolean(isSelected());
    }
    else
      setSelected(false);
    return this;
  }

  public void itemStateChanged(ItemEvent e) {
    stopCellEditing();
  }

  public void addCellEditorListener(CellEditorListener cel) {
    listeners.addElement(cel);
  }

  public void removeCellEditorListener(CellEditorListener cel) {
    listeners.removeElement(cel);
  }

  public boolean shouldSelectCell(java.util.EventObject eo) {
    return true;
  }

  public boolean isCellEditable(java.util.EventObject eo) {
    return true;
  }

  public void cancelCellEditing() {
    fireEditingCanceled();
  }

  public boolean stopCellEditing() {
    fireEditingStopped();
    return true;
  }

  public Object getCellEditorValue() {
    return new Boolean(isSelected());
  }

  protected void fireEditingCanceled() {
    setSelected(originalValue.booleanValue());
    ChangeEvent ce = new ChangeEvent(this);
    for(int i = 0; i < listeners.size(); i++)
      ((CellEditorListener)listeners.elementAt(i)).editingCanceled(ce);
  }

  protected void fireEditingStopped() {
    ChangeEvent ce = new ChangeEvent(this);
    for(int i = 0; i < listeners.size(); i++)
      ((CellEditorListener)listeners.elementAt(i)).editingStopped(ce);
  }
}

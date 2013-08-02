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
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.TableCellEditor;

public class SFTextFieldEditor extends JTextField implements TableCellEditor {

	protected transient Vector listeners;
	protected transient String originalValue;

	public SFTextFieldEditor() {
		super();
		listeners = new Vector();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
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
		} catch (Exception e) {
			setText("");
		}
		return this;
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
		return getText();
	}

	protected void fireEditingCanceled() {
		setText(originalValue);
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
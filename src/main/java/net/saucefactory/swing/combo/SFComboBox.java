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
import javax.swing.plaf.ComboBoxUI;
import java.util.*;
import java.lang.reflect.*;
import net.saucefactory.swing.data.SFFieldDataAdapter;
import net.saucefactory.swing.data.ISFDataAdapter;
import net.saucefactory.swing.data.SFStringDataAdapter;
import javax.swing.plaf.basic.BasicComboPopup;

public class SFComboBox extends JComboBox implements ISFPredictiveIndexListener {

  protected SFComboBoxModel model;
  protected int selectedIndex = -1;
  protected int popupWidth = -1;
  protected boolean autoPopupSize = false;
  protected boolean showTooltips = false;

  public SFComboBox() {
    super();
    model = new SFComboBoxModel();
    setModel(model);
    addItemListener(new ItemHandler());
  }

  public void setComboData(Object[] internalData, String displayField) {
    model.setComboData(internalData, new SFFieldDataAdapter(displayField));
    checkSelectedIndex();
  }

  public void setComboData(Object[] internalData, String displayField, boolean firstCellEmpty,
      String emptyText) {
    model.setComboData(internalData, new SFFieldDataAdapter(displayField),
        firstCellEmpty,
        emptyText);
    checkSelectedIndex();
  }

  public void setComboData(Object[] internalData, ISFDataAdapter dataAdapter) {
    model.setComboData(internalData, dataAdapter);
    checkSelectedIndex();
  }

  public void setComboData(String[] internalData, boolean firstCellEmpty, String emptyText) {
    model.setComboData(internalData, new SFStringDataAdapter(), firstCellEmpty, emptyText);
    checkSelectedIndex();
  }

  public void setComboData(Object[] internalData, ISFDataAdapter dataAdapter,
      boolean firstCellEmpty, String emptyText) {
    model.setComboData(internalData, dataAdapter, firstCellEmpty, emptyText);
    checkSelectedIndex();
  }

  public Object getObjectAt(int index) {
    return model.getObjectAt(index);
  }

  public Object getSelectedObject() {
    int index = getSelectedIndex();
    return model.getSelectedObject(index);
  }

  public Object getSelectedField(String fieldName) {
    int index = getSelectedIndex();
    return model.getSelectedField(index, fieldName);
  }

  public String getSelectedStringField(String fieldName) {
    int index = getSelectedIndex();
    return model.getSelectedStringField(index, fieldName);
  }

  public void setSelectedElementByField(String field, Object value) {
    model.setSelectionByField(field, value, this);
    selectedItemChanged();
  }

  public void setSelectedElementByMethod(String method, Object value) {
    model.setSelectionByMethod(method, value, this);
    selectedItemChanged();
  }

  public void clearComboData() {
    try {
      model.clearComboData();
    }
    catch(Exception e) {
      this.removeAllItems();
    }
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public Object getSelectedItem() {
    return model.getSelectedItem();
  }

  public Object getItemAt(int index) {
    return model.getElementAt(index);
  }

  public void findSelectedIndex() {
    if(model == null)
      return;
    Object tmpObj = model.getSelectedItem();
    if(tmpObj == null)
      return;
    int size = model.getSize();
    int tmpIndex = -1;
    Object obj;
    for(int i = 0; i < size; i++) {
      obj = model.getElementAt(i);
      if(obj != null && obj.equals(tmpObj)) {
        tmpIndex = i;
      }
    }
    if(tmpIndex == -1 && model.isFirstCellEmpty())
      tmpIndex = 0;
    swapSelectedIndex(tmpIndex);
  }

  public void checkSelectedIndex() {
    if(model == null)
      return;
    if(selectedIndex == -1) {
      if(model.isFirstCellEmpty())
        setSelectedIndex(0);
      return;
    }
    Object tmpObj = model.getSelectedItem();
    if(tmpObj == null)
      return;
    int size = model.getSize();
    int tmpIndex = -1;
    Object obj;
    for(int i = 0; i < size; i++) {
      obj = model.getElementAt(i);
      if(obj != null && obj.equals(tmpObj)) {
        tmpIndex = i;
      }
    }
    if(tmpIndex == -1 && model.isFirstCellEmpty())
      tmpIndex = 0;
    swapSelectedIndex(tmpIndex);
  }

  public void setSelectedIndex(int index) {
    setSelectedIndex(index, true);
  }

  public void setSelectedIndex(int index, boolean forceChange) {
    //if(selectedIndex == index)
      //return;
    selectedIndex = index;
    boolean postEvent = model.selectionChanged(index, forceChange);
    if(postEvent)
      selectedItemChanged();
    else
      fireActionEvent();
  }

  public void swapSelectedIndex(int index) {
    if(selectedIndex == index)
      return;
    selectedIndex = index;
    selectedItemChanged();
  }

  public void setSelectedItem(Object item) {
    model.setSelectedItem(item);
    findSelectedIndex();
    selectedItemChanged();
    if(showTooltips && item != null)
      setToolTipText(item.toString());
  }

  public boolean isItemSelected() {
    int selectedIndex = getSelectedIndex();
    //System.out.println("index: " + selectedIndex);
    if(model.isFirstCellEmpty() && selectedIndex < 1)
      return false;
    return(selectedIndex > -1);
  }

  public void setShowTooltip(boolean showTooltips) {
    this.showTooltips = showTooltips;
  }

  public void setListCellRenderer(ListCellRenderer renderer) {
    this.setRenderer(renderer);
  }

  public void installPopupUI(ComboBoxUI ui) {
    setUI(ui);
  }

  public int getPopupWidth() {
    return popupWidth;
  }

  public void setPopupWidth(int popupWidth) {
    this.popupWidth = popupWidth;
    resizePopup();
  }

  public void setPopupAutoSizeEnabled(boolean autoPopupSize) {
    this.autoPopupSize = autoPopupSize;
  }

  private void resizePopup() {
    BasicComboPopup popup = (BasicComboPopup)getUI().getAccessibleChild(this,0);
    JScrollPane popupScroll = findScrollPane(popup);//(JScrollPane)popup.getComponent(0);
    if(popup == null)
      return;
    int size = popupWidth;
    int offset = 0;
    if(autoPopupSize) {
      FontMetrics fm = getFontMetrics(getFont());
      for(int i = 0; i < getItemCount(); i++) {
        String tmpStr = (String)getItemAt(i);
        int tmpSize = fm.stringWidth(tmpStr);
        if(size < tmpSize)
          size = tmpSize;
      }
      if(popupScroll.getVerticalScrollBar().isVisible())
        offset += popupScroll.getVerticalScrollBar().getWidth();
    }
    popup.setPreferredSize(new Dimension(size + offset, popup.getPreferredSize().height));
    popup.invalidate();
    popup.setLayout(new BorderLayout());
    popup.add(popupScroll, BorderLayout.CENTER);
  }
  
  private JScrollPane findScrollPane(BasicComboPopup popup) {
  	for(int i = 0; i < popup.getComponentCount(); i++) {
  		Component tmpComp = popup.getComponent(i);
  		if(tmpComp instanceof JScrollPane)
  			return (JScrollPane)tmpComp;
  	}
  	return null;
  }

  public Dimension getPopupSize() {
    Dimension size = getSize();
    if(popupWidth < 1)popupWidth = size.width;
    return new Dimension(popupWidth, size.height);
  }

  public Dimension getSize() {
    Dimension d = super.getSize();
    //if(popupWidth > d.width)
      //d.width = popupWidth;
    return d;
  }

  public void enablePredictiveEditor() {
    enablePredictiveEditor(true, false);
  }

  public void enablePredictiveEditor(boolean autoReplace) {
    enablePredictiveEditor(autoReplace, false);
  }

  public void enablePredictiveEditor(boolean autoReplace, boolean forceUpperCase) {
    SFPredictiveComboBoxEditor editor = new SFPredictiveComboBoxEditor(forceUpperCase);
    model.setAutoReplaceMode(autoReplace);
    editor.setPredictiveSearch(model);
    model.setComparator(new SFComboBoxItemToStringComparator(model.getDataAdapter()));
    model.setPredictiveIndexListener(this);
    setEditable(true);
    setEditor(editor);
    //setSelectedIndex(selectedIndex);
    selectedItemChanged();
  }

  public void enableLinearPredictiveEditor() {
    enableLinearPredictiveEditor(true, false);
  }

  public void enableLinearPredictiveEditor(boolean autoReplace, boolean forceUpperCase) {
    SFPredictiveComboBoxEditor editor = new SFPredictiveComboBoxEditor(forceUpperCase);
    model.setAutoReplaceMode(autoReplace);
    editor.setPredictiveSearch(model.getLinearPredictionSearch());
    //model.setComparator(new CComboBoxItemToStringComparator(model.getDisplayItems()));
    model.setPredictiveIndexListener(this);
    setEditable(true);
    setEditor(editor);
    selectedItemChanged();
  }

  public void enableNarrowingPredictiveEditor(boolean autoReplace) {
    enableNarrowingPredictiveEditor(autoReplace, false);
  }

  public void enableNarrowingPredictiveEditor(boolean autoReplace, boolean forceUpperCase) {
    SFPredictiveComboBoxEditor editor = new SFPredictiveComboBoxEditor(forceUpperCase);
    model.setAutoReplaceMode(autoReplace);
    editor.setPredictiveSearch(model.getNarrowingPredictionSearch());
    model.setComparator(new SFComboBoxItemToStringComparator(model.getDataAdapter()));
    model.setPredictiveIndexListener(this);
    setEditable(true);
    setEditor(editor);
    selectedItemChanged();
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if(isEditable()) {
      Component tmpComp = getEditor().getEditorComponent();
      if(tmpComp != null && tmpComp instanceof JTextField)
        ((JTextField)tmpComp).setEditable(enabled);
    }
  }

  public void setComboOnlyEnabled(boolean enabled) {
    super.setEnabled(enabled);
  }

  public void setEditorEnabled(boolean enabled) {
    if(isEditable()) {
      Component tmpComp = getEditor().getEditorComponent();
      if(tmpComp != null && tmpComp instanceof JTextField)
        ((JTextField)tmpComp).setEnabled(enabled);
    }
  }

  public void setEditorEditable(boolean editable) {
    if(isEditable()) {
      Component tmpComp = getEditor().getEditorComponent();
      if(tmpComp != null && tmpComp instanceof JTextField)
        ((JTextField)tmpComp).setEditable(editable);
    }
  }

  protected void selectedItemChanged() {
    super.selectedItemChanged();
    fireActionEvent();
  }

  //IPredictor
  public void indexChanged(int index) {
    if(index > -1)
      setSelectedIndex(index, false);
    else {
      int addOn = model.firstCellEmpty ? 1 : 0;
      if( -index >= (getItemCount() + addOn))
        setSelectedIndex(getItemCount() - 1, false);
      else
        setSelectedIndex( -index, false);
    }
  }

  public void addFocusListener2(FocusListener listener) {
    super.addFocusListener(listener);
    try {
      ComboBoxEditor ed = getEditor();
      if(ed != null) {
        Component c = ed.getEditorComponent();
        c.addFocusListener(listener);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void removeFocusListener2(FocusListener listener) {
    super.removeFocusListener(listener);
    try {
      ComboBoxEditor ed = getEditor();
      if(ed != null) {
        Component c = ed.getEditorComponent();
        c.removeFocusListener(listener);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void focusGained() {
    setPopupVisible(true);
  }

  public void focusLost() {
    setPopupVisible(false);
  }

  public void updateUI() {
    if(isEditable()) {
      setEditable(false);
      super.updateUI();
      setEditable(true);
    }
    else
      super.updateUI();
    if(autoPopupSize)
      resizePopup();
  }

  public class ItemHandler implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      ComboBoxModel model = getModel();
      Object v = model.getSelectedItem();
      if(editor != null) {
        configureEditor(getEditor(), v);
      }
      repaint();
    }
  }
}

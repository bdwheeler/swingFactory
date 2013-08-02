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
import javax.swing.*;
import java.util.Vector;

public class SFSimpleComboBox extends JComboBox {
  private int popupWidth = -1;
  private boolean showTooltip = false;

  public SFSimpleComboBox() {
    super();
  }

  public SFSimpleComboBox(Vector items) {
    super(items);
  }

  public SFSimpleComboBox(Object[] items) {
    super(items);
  }

  public SFSimpleComboBox(ComboBoxModel aModel) {
    super(aModel);
  }

  public void setComboData(Vector items) {
    removeComboData();
    if(items == null)
      return;
    for(int i = 0; i < items.size(); i++)
      super.addItem(items.get(i));
  }

  public void setComboData(Object[] items) {
    removeComboData();
    if(items == null)
      return;
    for(int i = 0; i < items.length; i++)
      super.addItem(items[i]);
  }

  public void removeComboData() {
    removeAllItems();
  }

  public int getPopupWidth() {
    return popupWidth;
  }

  public void setPopupWidth(int popupWidth) {
    this.popupWidth = popupWidth;
  }

  public Dimension getPopupSize() {
    Dimension size = getSize();
    if(popupWidth < size.width)popupWidth = size.width;
    return new Dimension(popupWidth, size.height);
  }

  public void setShowTooltip(boolean showTooltip) {
    this.showTooltip = showTooltip;
  }

  public void setSelectedItem(Object item) {
    super.setSelectedItem(item);
    try {
      if(showTooltip && item != null)
        setToolTipText(item.toString());
    }
    catch(Exception e) {}
  }
}

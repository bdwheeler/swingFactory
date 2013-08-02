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
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import net.saucefactory.swing.utils.ScreenUtility;

public class SFSimpleComboListUI extends MetalComboBoxUI
{
  public SFSimpleComboListUI()
  {
  }

  protected ComboPopup createPopup()
  {
    BasicComboPopup popup = new BasicComboPopup(comboBox)
    {
      public void show()
      {
	Dimension popupSize = ((SFSimpleComboBox)comboBox).getPopupSize();
	popupSize.setSize(popupSize.width, getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
	Rectangle popupBounds = new Rectangle(0, comboBox.getBounds().height, popupSize.width, popupSize.height);// bunk swing call = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
	ScreenUtility.calculateComboPopupPosition(popupBounds, comboBox);
	scroller.setMaximumSize(popupBounds.getSize());
	scroller.setPreferredSize(popupBounds.getSize());
	scroller.setMinimumSize(popupBounds.getSize());
	list.invalidate();
	int selectedIndex = comboBox.getSelectedIndex();
	if(selectedIndex == -1)
	  list.clearSelection();
	else
	  list.setSelectedIndex(selectedIndex);
	list.ensureIndexIsVisible(list.getSelectedIndex());
	setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
	try
	{
	  if(comboBox.isShowing());
	    show(comboBox, popupBounds.x, popupBounds.y);
	}
	catch(Exception e){}
      }
    };
    popup.getAccessibleContext().setAccessibleParent(comboBox);
    return popup;
  }
}
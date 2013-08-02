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

public class SFComboListUI extends BasicComboBoxUI
{
  public SFComboListUI()
  {
  }

  protected ComboPopup createPopup()
  {
    BasicComboPopup popup = new BasicComboPopup(comboBox)
    {
      public void show()
      {
	Dimension popupSize = ((SFComboBox)comboBox).getPopupSize();
	popupSize.setSize(popupSize.width, getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
	Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
	scroller.setMaximumSize(popupBounds.getSize());
	scroller.setPreferredSize(popupBounds.getSize());
	scroller.setMinimumSize(popupBounds.getSize());
	list.invalidate();
	int selectedIndex = comboBox.getSelectedIndex();
	//System.out.println("SelectedIndex: " + selectedIndex);
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

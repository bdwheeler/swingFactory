package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SFMultipleToolbarPanel extends JPanel
{
  Vector toolBars = new Vector();
  private static Color bgColor = null;

  static {
    try {
      bgColor = UIManager.getColor("SFMultipleToolbarPanel.background");
    }
    catch(Exception e) {
      bgColor = null;
    }
  }


  public SFMultipleToolbarPanel()
  {
    super();
    FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);//this, BoxLayout.X_AXIS);
    setLayout(layout);
  }

  public void addToolbar(JToolBar toolbar)
  {
    toolBars.add(toolbar);
    add(toolbar);
  }

  public Color getBackground() {
    return bgColor == null ? super.getBackground() : bgColor;
  }

/*
  public void updateUI()
  {
    if(toolBars != null) {
      for(int i = 0; i < toolBars.size(); i++) {
	((JToolBar)toolBars.elementAt(i)).updateUI();
      }
    }
  }
  */
}

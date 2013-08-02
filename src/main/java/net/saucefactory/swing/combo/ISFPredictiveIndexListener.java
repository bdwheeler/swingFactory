package net.saucefactory.swing.combo;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

public interface ISFPredictiveIndexListener
{
  public void indexChanged(int index);
  public void focusGained();
  public void focusLost();
}
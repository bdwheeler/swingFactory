package net.saucefactory.swing.combo;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

public interface ISFPredictiveSearch
{
  public String getPrediction(String key) throws Exception;
  public void selectionCleared();
  public boolean isAutoReplace();
}
package net.saucefactory.swing;

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

public class SFSpeedLabel extends JLabel
{

  public SFSpeedLabel()
  {
  }

  public void validate() {}
  public void revalidate() {}
  public void repaint(long tm, int x, int y, int width, int height) {}
  public void repaint(Rectangle r) {}
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if(propertyName=="text")
      super.firePropertyChange(propertyName, oldValue, newValue);
  }
  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}
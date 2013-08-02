package net.saucefactory.swing.common;

import java.awt.*;
import javax.swing.*;

public class SFMultiStateCheckbox extends JCheckBox {

  public boolean betweenOn = false;

  public static final int STATE_ON = 1;
  public static final int STATE_OFF = 2;
  public static final int STATE_BETWEEN = 4;

  //public static final Color betweenColor = new Color(170, 170, 170);

  public SFMultiStateCheckbox() {
    setModel(new InternalModel());
  }

  public SFMultiStateCheckbox(String text) {
    super(text);
    setModel(new InternalModel());
  }

  public void paint(Graphics g) {
    if(betweenOn) {
      InternalModel m = (InternalModel)getModel();
      m.tempDisableOn();
      //final Color oldBg = getBackground();
      //setBackground(betweenColor);
      super.paint(g);
      //setBackground(oldBg);
      m.tempDisableOff();
    }
    else
      super.paint(g);
  }

  public int getState() {
    if(betweenOn)
      return STATE_BETWEEN;
    if(isSelected())
      return STATE_ON;
    return STATE_OFF;
  }

  public void setBetween(boolean between) {
    setSelected(between);
    betweenOn = between;
  }

  public void setSelected(boolean selected) {
    super.setSelected(selected);
    betweenOn = false;
  }
}

class InternalModel extends JToggleButton.ToggleButtonModel
{
  public void tempDisableOff() {
    stateMask |= ENABLED;
  }

  public void tempDisableOn() {
    stateMask &= ~ENABLED;
  }
}

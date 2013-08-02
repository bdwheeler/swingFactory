package net.saucefactory.swing.common;

import java.awt.*;
import javax.swing.*;

public class SFNoEditCheckBox extends JCheckBox
{
  public SFNoEditCheckBox() {
    setEnabled(false);
    setModel(new InternalTM());
  }

  public SFNoEditCheckBox(String text) {
    super(text);
    setEnabled(false);
    setModel(new InternalTM());
  }

  public void paint(Graphics g) {
    InternalTM m = (InternalTM)getModel();
    m.tempPressedOn();
    super.paint(g);
    m.tempPressedOff();
  }
}

class InternalTM extends JToggleButton.ToggleButtonModel
{
  public void tempPressedOn() {
    stateMask |= ENABLED;
  }

  public void tempPressedOff() {
    stateMask &= ~ENABLED;
  }
}

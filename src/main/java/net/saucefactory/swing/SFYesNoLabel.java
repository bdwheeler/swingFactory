package net.saucefactory.swing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

public class SFYesNoLabel extends JLabel
{
  boolean selected = false;

  public SFYesNoLabel() {
  }

  public void setSelected(boolean _selected) {
    selected = _selected;
    if(selected)
      setText("Yes");
    else
      setText("No");
  }

  public boolean isSelected() {
    return selected;
  }
}
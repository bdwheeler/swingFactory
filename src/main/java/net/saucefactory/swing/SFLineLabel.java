package net.saucefactory.swing;

import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.BorderFactory;

public class SFLineLabel extends JLabel {

  public SFLineLabel() {
    setPreferredSize(new Dimension(1, 2));
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
    setMinimumSize(new Dimension(0, 2));
    setBorder(BorderFactory.createEtchedBorder());
  }
}

package net.saucefactory.swing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

public class SFButtonLabel extends JLabel {
  public SFButtonLabel() {
    setOpaque(true);
    setBackground(Color.gray);
    setForeground(Color.white);
    setBorder(BorderFactory.createRaisedBevelBorder());
  }

  public SFButtonLabel(String text) {
    this();
    setText(text);
  }

}
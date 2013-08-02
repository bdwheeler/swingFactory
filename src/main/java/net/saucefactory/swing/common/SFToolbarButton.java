package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SauceFactory
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.plaf.metal.*;

public class SFToolbarButton extends JButton {

  private static Color bgColor = null;

  static {
    try {
      bgColor = UIManager.getColor("SFToolbarButton.background");
    }
    catch(Exception e) {
      bgColor = null;
    }
  }

  public SFToolbarButton() {
    this(null, null);
  }

  public SFToolbarButton(ImageIcon img) {
    this(img, null);
  }

  public SFToolbarButton(ImageIcon img, String tooltip) {
    super();
    if(tooltip != null)
      setToolTipText(tooltip);
    if(img != null)
      setIcon(img);
    Dimension d = new Dimension(22, 22);
    setPreferredSize(d);
    setMinimumSize(d);
    setMaximumSize(d);
    setMargin(new Insets(4, 4, 4, 4));
    this.setFocusPainted(false);
    setText("");
    setRolloverEnabled(true);
  }

  public Color getBackground() {
    return bgColor == null ? super.getBackground() : bgColor;
  }

  protected void configurePropertiesFromAction(Action a) {
    setEnabled((a != null ? a.isEnabled() : true));
    if(a != null) {
      Integer i = (Integer)a.getValue(Action.MNEMONIC_KEY);
      if(i != null)
        setMnemonic(i.intValue());
    }
  }
}

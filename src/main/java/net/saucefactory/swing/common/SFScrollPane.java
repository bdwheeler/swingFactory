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
import net.saucefactory.swing.table.*;

public class SFScrollPane extends JScrollPane
{
  public static final Color DEFAULT_COLOR = Color.white;

  private Component cornerLabel = null;
  Color backColor;

  public SFScrollPane() {
    this(null, true);
  }

  public SFScrollPane(Color backColor) {
    this(backColor, true);
  }

  public SFScrollPane(Color backColor, boolean addCorner) {
    super();
    if(backColor != null) {
      this.backColor = backColor;
      getViewport().setBackground(backColor);
    }
    else
      getViewport().setBackground(DEFAULT_COLOR);
    if(addCorner) {
      cornerLabel = createCornerLabel();
      /*cornerLabel = new JLabel("") {
        public Color getBackground() {
          return UIManager.getColor("TableHeader.background");
        }
      };
      //cornerLabel.setBorder(BorderFactory.createRaisedBevelBorder());
      //cornerLabel.setOpaque(true);*/
      add(JScrollPane.UPPER_RIGHT_CORNER, cornerLabel);
    }
  }

  public Component createCornerLabel() {
    return SFTableHeaderRendererFactory.getHeaderCornerRenderer();
  }

  public Component getCornerLabel() {
    return cornerLabel;
  }

  public Color getBackground() {
    if(backColor != null)
      return backColor;
    else {
      Color c = UIManager.getColor("Table.background");
      if(c != null) {
        getViewport().setBackground(c);
        return c;
      }
      else
        return DEFAULT_COLOR;
    }
  }

  public void paint(Graphics g)
  {
    //Uncomment for java <= 1.22
    //g.setColor(backColor);
    //g.fillRect(1, 19, getSize().width - 2, getSize().height - 20);
    super.paint(g);
  }
}

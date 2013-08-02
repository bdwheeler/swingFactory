package net.saucefactory.swing.buttons;

import java.awt.*;
import javax.swing.*;

public class SFArrowButton
	extends JButton
	implements SwingConstants {

  protected int direction;

  public SFArrowButton(int direction) {
    super();
    setRequestFocusEnabled(false);
    setDirection(direction);
    setBackground(UIManager.getColor("control"));
    setPreferredSize(new Dimension(16, 16));
    setMinimumSize(new Dimension(5, 5));
  }

  public int getDirection() { return direction; }

  public void setDirection(int dir) { direction = dir; }

  public void paint(Graphics g) {
    super.paint(g);
    boolean isPressed, isEnabled;
    int w, h, size;

    w = getSize().width;
    h = getSize().height;
    isPressed = getModel().isPressed();
    isEnabled = isEnabled();

    if(h < 5 || w < 5) return;

    if (isPressed) g.translate(1, 1);

    // Draw the arrow
    size = Math.min((h - 4) / 3, (w - 4) / 3);
    size = Math.max(size, 2);
    paintTriangle(g, (w - size) / 2, (h - size) / 2,
    size, direction, isEnabled);

    // Reset the Graphics back to it's original settings
    if (isPressed) {
      g.translate(-1, -1);
    }
  }

  public boolean isFocusTraversable() {
    return false;
  }

  public void paintTriangle(Graphics g, int x, int y, int size,
    int direction, boolean isEnabled) {
    Color oldColor = g.getColor();
    int mid, i, j;

    j = 0;
    size = Math.max(size, 2);
    mid = size / 2;

    g.translate(x, y);
    if(isEnabled)
      g.setColor(Color.black);
    else
      g.setColor(UIManager.getColor("controlShadow"));

    switch(direction) {
      case NORTH: {
        for(i = 0; i < size; i++) {
          g.drawLine(mid-i, i, mid+i, i);
        }

        if(!isEnabled) {
          g.setColor(UIManager.getColor("controlLtHighlight"));
          g.drawLine(mid-i+2, i, mid+i, i);
        }
        break;
      } case SOUTH: {
        if(!isEnabled) {
          g.translate(1, 1);
          g.setColor(UIManager.getColor("controlLtHighlight"));
          for(i = size-1; i >= 0; i--) {
            g.drawLine(mid-i, j, mid+i, j);
            j++;
          }
          g.translate(-1, -1);
          g.setColor(UIManager.getColor("controlShadow"));
        }

        j = 0;
        for(i = size-1; i >= 0; i--) {
          g.drawLine(mid-i, j, mid+i, j);
          j++;
        }
        break;
      } case WEST: {
        for(i = 0; i < size; i++)      {
          g.drawLine(i, mid-i, i, mid+i);
        }
        if(!isEnabled)  {
          g.setColor(UIManager.getColor("controlLtHighlight"));
          g.drawLine(i, mid-i+2, i, mid+i);
        }
        break;
      } case EAST: {
        if(!isEnabled)  {
          g.translate(1, 1);
          g.setColor(UIManager.getColor("controlLtHighlight"));
          for(i = size-1; i >= 0; i--)   {
            g.drawLine(j, mid-i, j, mid+i);
            j++;
          }
          g.translate(-1, -1);
          g.setColor(UIManager.getColor("controlShadow"));
        }

        j = 0;
        for(i = size-1; i >= 0; i--)   {
          g.drawLine(j, mid-i, j, mid+i);
          j++;
        }
        break;
      }
    }
    g.translate(-x, -y);
    g.setColor(oldColor);
  }
}
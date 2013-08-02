package net.saucefactory.swing.utils;

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
import net.saucefactory.swing.common.DialogSizeListener;

public class ScreenUtility {
  public static final Point CENTER_OF_SCREEN;
  public static final Dimension SCREEN_SIZE;

  public ScreenUtility() {
  }

  static {
    SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    CENTER_OF_SCREEN = new Point(SCREEN_SIZE.width / 2, SCREEN_SIZE.height / 2);
  }

  public static void centerOnScreen(Component component) {
    Dimension size = component.getSize();
    component.setLocation(CENTER_OF_SCREEN.x - (size.width / 2),
        CENTER_OF_SCREEN.y - (size.height / 2));
  }

  public static void centerOverComponent(Component parent, Component child) {
    Dimension childSize = child.getSize();
    Dimension parentSize = parent.getSize();
    Point parentCenter = new Point(parentSize.width / 2, parentSize.height / 2);
    child.setLocation(parentCenter.x - (childSize.width / 2),
        parentCenter.y - (childSize.height / 2));
  }

  public static Point getCenterLocation(int width, int height) {
    return new Point(CENTER_OF_SCREEN.x - (width / 2),
        CENTER_OF_SCREEN.y - (height / 2));
  }

  public static void setDialogMinSize(JDialog dialog, int minWidth, int minHeight) {
    dialog.addComponentListener(new DialogSizeListener(dialog, minWidth, minHeight));
  }

  public static Point calculatePopupPosition(int popupWidth, int popupHeight, Component parent) {
    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    Point parentLocation = parent.getLocationOnScreen();
    Point rtnPoint = new Point();
    rtnPoint.x = parentLocation.x - popupWidth + parent.getWidth();
    rtnPoint.y = parentLocation.y + parent.getHeight();
    if(rtnPoint.x < 0)
      rtnPoint.x = parentLocation.x;
    if((rtnPoint.y + popupHeight) > screenHeight)
      rtnPoint.y = parentLocation.y - popupHeight;
    return rtnPoint;
  }

  public static void showPopupMenu(Component invoker, JPopupMenu menu, int x, int y) {
    try {
      Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension size = menu.getSize();
      if(size.width == 0) {
        menu.show(invoker, x, y);
        menu.setVisible(false);
        size = menu.getSize();
      }
      Point screenLoc = invoker.getLocationOnScreen();
      if((x + screenLoc.x + size.width) > screenDim.width)
        x -= size.width;
      if((y + screenLoc.y + size.height) > screenDim.height)
        y -= size.height;
      menu.show(invoker, x, y);
    }
    catch(Exception e) {
      System.out.println("popuperror");
      menu.show(invoker, x, y);
    }
  }

  public static void calculateComboPopupPosition(Rectangle popupBounds, JComboBox combo) {
    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    Point parentLocation = combo.getLocationOnScreen();
    if(parentLocation.x + popupBounds.width > screenWidth)
      popupBounds.x -= (popupBounds.width - combo.getSize().width);
  }

  public static Rectangle calculatePopupOverPosition(Rectangle popupBounds, JFrame parent) {
    Rectangle mainFrameBounds = parent.getBounds();
    popupBounds.x = mainFrameBounds.x + (mainFrameBounds.width - popupBounds.width) / 2;
    popupBounds.y = mainFrameBounds.y + (mainFrameBounds.height - popupBounds.height) / 2;
    return popupBounds;
  }

  public static void recursiveRepaint(Component component) {
    try {
      if(component == null)
        return;
      if(component instanceof JTable) {
        ((JTable)component).getTableHeader().resizeAndRepaint();
        //((JTable)component).getTableHeader().repaint();
        ((JTable)component).revalidate();
      }
      else if(component instanceof JTree) {
        ((JTree)component).treeDidChange();
        ((JTree)component).revalidate();
      }
      else if(component instanceof Container) {
        Component[] children = ((Container)component).getComponents();
        if(children != null) {
          for(int i = 0; i < children.length; i++) {
            recursiveRepaint(children[i]);
          }
        }
        if(component instanceof JComponent)
          ((JComponent)component).revalidate();
      }
      else if(component instanceof JComponent)
        ((JComponent)component).revalidate();
      component.repaint();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static Color hexStringToColor(String color) {
    if(color != null)
      return new Color(Integer.decode("#" + color).intValue());
    return Color.white;
  }

  public static String colorToHexString(Color color) {
    if(color != null) {
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      return Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
      //System.out.println("color rgb" + red + " " + green + " " + blue);
      //System.out.println("color hex" + rtn);
      //return rtn;
      //return Integer.toHexString(color.getRGB());
    }
    return "000000";
  }
}

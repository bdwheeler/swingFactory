package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SFCursorManager extends Thread
{
  public static Cursor pausedCursor;
  public static Cursor defaultCursor;
  private static SFCursorManager instance = new SFCursorManager();

  private SFCursorManager()
  {
    pausedCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    defaultCursor = Cursor.getDefaultCursor();
  }

  public static SFCursorManager getHandle()
  {
    return instance;
  }

  public static void pauseCursor(JInternalFrame component)
  {
    if(component != null) {
      component.getGlassPane().setCursor(pausedCursor);
      component.getGlassPane().setVisible(true);
    }
  }

  public static boolean isCursorPaused(JInternalFrame component)
  {
    boolean paused = false;
    if(component != null)
      paused = component.getGlassPane().getCursor().equals(pausedCursor);
    return paused;
  }

  public static void pauseDialogCursor(JDialog component)
  {
    if(component != null) {
      component.getGlassPane().setCursor(pausedCursor);
      component.getGlassPane().setVisible(true);
      component.getContentPane().setCursor(pausedCursor);
    }
  }

  public static void pauseParentFrame(Component comp) {
    if(comp != null) {
      Window parent = SwingUtilities.windowForComponent(comp);
      if(parent != null) {
	parent.setCursor(pausedCursor);
      }
    }
  }

  public static void resumeParentFrame(Component comp) {
    if(comp != null) {
      Window parent = SwingUtilities.windowForComponent(comp);
      if(parent != null) {
	parent.setCursor(defaultCursor);
      }
    }
  }

  public static void pauseFrame(JFrame frame) {
    if(frame != null) {
      frame.getGlassPane().setCursor(pausedCursor);
      frame.getGlassPane().setVisible(true);
      frame.setCursor(pausedCursor);
    }
  }

  public static void resumeFrame(JFrame frame) {
    if(frame != null) {
      frame.getGlassPane().setCursor(defaultCursor);
      frame.getGlassPane().setVisible(false);
      frame.setCursor(defaultCursor);
    }
  }

  public static void pauseTable(JTable table) {
    if(table != null) {
      table.setCursor(pausedCursor);
    }
  }

  public static void resumeTable(JTable table) {
    if(table != null) {
      table.setCursor(defaultCursor);
    }
  }

  public static void resumeCursor(JInternalFrame component)
  {
    if(component != null) {
      component.getGlassPane().setCursor(defaultCursor);
      component.getGlassPane().setVisible(false);
      //component.getContentPane().setCursor(defaultCursor);
    }
  }

  public static void resumeDialogCursor(JDialog component)
  {
    if(component != null) {
      component.getGlassPane().setCursor(defaultCursor);
      component.getGlassPane().setVisible(false);
      component.getContentPane().setCursor(defaultCursor);
    }
  }

}

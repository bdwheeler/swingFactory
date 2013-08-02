package net.saucefactory.swing.common;

/**
 * Title:        CDesktopPane
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

public class SFDesktopPane extends JDesktopPane
{
  SFDesktopManager desktopManager;
  private static int FRAME_OFFSET = 20;
  private static boolean doAutoOffset = false;
  private static boolean doAutoFrameSize = false;
  private static boolean defaultMaximumFrames = false;
  //layers
  public static final int LAYER_LOWEST = 0;
  public static final int LAYER_NORMAL = 1;
  public static final int LAYER_MODAL = 2;
  public static final int LAYER_OVER_MODAL = 3;
  public static final int LAYER_TOP = 10;

  public SFDesktopPane()
  {
    super();
    desktopManager=new SFDesktopManager(this);
    setDesktopManager(desktopManager);
  }

  public void setBounds(int x, int y, int w, int h)
  {
    super.setBounds(x,y,w,h);
    checkDesktopSize();
  }

  public void setDoAutoOffset(boolean b){this.doAutoOffset = b;}
  public void setDoAutoFrameSize(boolean b){this.doAutoFrameSize = b;}
  public void setDefaultMaximumFrames(boolean b){this.defaultMaximumFrames = b;}

  public void add(JInternalFrame frame, Integer layer)
  {
      frame.setLayer(layer);
      super.add(frame);
      if(doAutoOffset) {
	JInternalFrame[] array = getAllFrames();
	Point p;
	int w;
	int h;
	checkDesktopSize();
	if (array.length > 0) {
	    p = array[0].getLocation();
	    p.x = p.x + FRAME_OFFSET;
	    p.y = p.y + FRAME_OFFSET;
	}
	else {
	    p = new Point(0, 0);
	}
	frame.setLocation(p.x, p.y);
      }
      if(doAutoFrameSize) {
	if (frame.isResizable()) {
	    int w2 = getWidth() - (getWidth()/3);
	    int h2 = getHeight() - (getHeight()/3);
	    if (w2 < frame.getMinimumSize().getWidth()) w2 = (int)frame.getMinimumSize().getWidth();
	    if (h2 < frame.getMinimumSize().getHeight()) h2 = (int)frame.getMinimumSize().getHeight();
	    frame.setSize(w2, h2);
	}
      }
      moveToFront(frame);
      frame.setVisible(true);
      try {
	  frame.setSelected(true);
	  if(defaultMaximumFrames && frame.isMaximizable())
	    frame.setMaximum(true);
      } catch (PropertyVetoException e) {
	  frame.toBack();
      }
  }

  public Component add(JInternalFrame frame) {
      Component retval = super.add(frame);
      if(doAutoOffset) {
	JInternalFrame[] array = getAllFrames();
	Point p;
	int w;
	int h;
	checkDesktopSize();
	if (array.length > 0) {
	    p = array[0].getLocation();
	    p.x = p.x + FRAME_OFFSET;
	    p.y = p.y + FRAME_OFFSET;
	}
	else {
	    p = new Point(0, 0);
	}
	frame.setLocation(p.x, p.y);
      }
      if(doAutoFrameSize) {
	if (frame.isResizable()) {
	    int w2 = getWidth() - (getWidth()/3);
	    int h2 = getHeight() - (getHeight()/3);
	    if (w2 < frame.getMinimumSize().getWidth()) w2 = (int)frame.getMinimumSize().getWidth();
	    if (h2 < frame.getMinimumSize().getHeight()) h2 = (int)frame.getMinimumSize().getHeight();
	    frame.setSize(w2, h2);
	}
      }
      moveToFront(frame);
      frame.setVisible(true);
      try {
	  frame.setSelected(true);
	  if(defaultMaximumFrames && frame.isMaximizable())
	    frame.setMaximum(true);
      } catch (PropertyVetoException e) {
	  frame.toBack();
      }
      return retval;
  }

  public void remove(Component c) {
      super.remove(c);
      checkDesktopSize();
  }


  /**
   * Cascade all internal frames
   */
  public void cascadeFrames() {
      int x = 0;
      int y = 0;
      JInternalFrame allFrames[] = getAllFrames();

      desktopManager.setNormalSize();
      int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
      int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
      JInternalFrame currentFrame = desktopManager.getCurrentFrame();
      if(currentFrame != null && currentFrame.isMaximum())
	try {currentFrame.setMaximum(false);} catch(Exception e){}
      for (int i = allFrames.length - 1; i >= 0; i--) {
	if (allFrames[i].isVisible()) {
	  boolean moveIt = true;
	  if(currentFrame != null && allFrames[i] == currentFrame)
	    moveIt = false; //leave current frame for last
	  if(moveIt) {
	    try {
	      if(allFrames[i].isMaximum()) {
		allFrames[i].setMaximum(false);
	      }
	    } catch (Exception e) {}
	    desktopManager.setBoundsForFrame(allFrames[i], x, y, allFrames[i].getWidth(), allFrames[i].getHeight());
	    x = x + FRAME_OFFSET;
	    y = y + FRAME_OFFSET;
	  }
	}
      }
      if(currentFrame != null)
	desktopManager.setBoundsForFrame(currentFrame, x, y, currentFrame.getWidth(), currentFrame.getHeight());
  }

  /**
   * Tile all internal frames
   */
  public void tileFrames() {
      JInternalFrame allFrames[] = getAllFrames();
      desktopManager.setNormalSize();
      int frameHeight = getBounds().height/allFrames.length;
      int y = 0;
      for (int i = 0; i < allFrames.length; i++) {
	try {
	  allFrames[i].setMaximum(false);
	} catch (Exception e) {}
	allFrames[i].setSize(getBounds().width,frameHeight);
	allFrames[i].setLocation(0,y);
	y = y + frameHeight;
      }
  }

  /**
   * Sets all component size properties ( maximum, minimum, preferred)
   * to the given dimension.
   */
  public void setAllSize(Dimension d){
      setMinimumSize(d);
      setMaximumSize(d);
      setPreferredSize(d);
  }

  /**
   * Sets all component size properties ( maximum, minimum, preferred)
   * to the given width and height.
   */
  public void setAllSize(int width, int height){
      setAllSize(new Dimension(width,height));
  }

  private void checkDesktopSize() {
      if (getParent() != null && isVisible()) desktopManager.resizeDesktop();
  }

  public DesktopManager getDesktopManager()
  {
    return (SFDesktopManager)desktopManager;
  }
}
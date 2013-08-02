package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:  Extending DefaultDesktopManager to override closeFrame
 *               we want the last active frame to appear when closing a frame,
 *               not first frame added, and adding other functionality.
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import com.sun.java.swing.plaf.windows.*;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class SFDesktopManager extends DefaultDesktopManager implements java.io.Serializable
{
  JInternalFrame currentFrame = null;
  JInternalFrame initialFrame = null;
  //JInternalFrame previousFrame = null;
  Vector frameChangeListeners = new Vector(2);

  //Vector childFrames = new Vector(5);
  private SFDesktopPane desktop;

  public SFDesktopManager(SFDesktopPane desktop) {
      this.desktop = desktop;
  }

  public void closeFrame(JInternalFrame f) {
    super.closeFrame(f);
    if(currentFrame == f) {
      currentFrame = null;
      notifyFrameChangeListeners(null);
      desktop.requestFocus();
    }
    //activatePreviousFrame();
    //childFrames.removeElement(f);
  }

  public void closeCurrentFrame() {
    if(currentFrame != null)
      closeFrame(currentFrame);
  }

  public void addFrameChangeListener(ISFActiveFrameListener listener)
  {
    if(!frameChangeListeners.contains(listener))
      frameChangeListeners.add(listener);
  }

  public void activateFrame(JInternalFrame f)
  {
    try {
      boolean max = (currentFrame != null && currentFrame != f && currentFrame.isMaximum());
      //if(max) {
	//try { currentFrame.setMaximum(false); } catch(PropertyVetoException e){}
      //}
      super.activateFrame(f);
      if(currentFrame != null && f != currentFrame) {
	if(max && f.isMaximizable()) {
	  //currentFrame.setMaximum(false);
	  if(currentFrame.isSelected())
	    currentFrame.setSelected(false);
	  f.setMaximum(true);
	}
	else if(currentFrame.isSelected())
	  currentFrame.setSelected(false);
      }
      if(!f.isSelected())
	f.setSelected(true);
      if(f.isMaximum() && f.getFocusOwner() == null) {
	f.getFocusTraversalPolicy().getFirstComponent(f.getLayeredPane()).requestFocusInWindow();
      }
      currentFrame = f;
      notifyFrameChangeListeners(f);
    }
    catch(PropertyVetoException e){}
  }

  private void notifyFrameChangeListeners(JInternalFrame frame) {
    try {
      for(int i = 0; i < frameChangeListeners.size(); i++)
	((ISFActiveFrameListener)frameChangeListeners.get(i)).activeFrameChanged(frame);
    }
    catch(Exception e){}
  }

  public void activateNextFrame() {
    activateNextFrame(desktop);
    //switchFrame(true);
  }

  public void activateNextFrame(JInternalFrame f) {
    initialFrame = f;
    activateNextFrame(desktop);
    //switchFrame(true);
  }

  protected void activateNextFrame(Container c) {
    try {
      boolean max = (currentFrame != null && currentFrame.isMaximum());
      //if(max)
	//try { currentFrame.setMaximum(false); } catch(PropertyVetoException e){}
      int i;
      JInternalFrame nextFrame = null;
      if (c == null) return;
      for (i = 0; i < c.getComponentCount(); i++) {
	if (c.getComponent(i) instanceof JInternalFrame) {
	  nextFrame = (JInternalFrame) c.getComponent(i);
	  break;
	}
      }
      if (nextFrame != null) {
	try { nextFrame.setSelected(true); }
	catch (PropertyVetoException e2) { }
	nextFrame.moveToFront();
	currentFrame = nextFrame;
	if(max && currentFrame.isMaximizable())
	  currentFrame.setMaximum(true);
	if(currentFrame.isMaximum() && currentFrame.getFocusOwner() == null) {
	  currentFrame.getFocusTraversalPolicy().getFirstComponent(currentFrame.getLayeredPane()).requestFocusInWindow();
	}
      }
      notifyFrameChangeListeners(nextFrame);
    }
    catch(PropertyVetoException e) {}
  }

  public void activatePreviousFrame() {
    //switchFrame(false);
  }

  public JInternalFrame getCurrentFrame() {
    return currentFrame;
  }

  public void endResizingFrame(JComponent f) {
      super.endResizingFrame(f);
      resizeDesktop();
  }

  public void endDraggingFrame(JComponent f) {
      super.endDraggingFrame(f);
      resizeDesktop();
  }

  public void setNormalSize() {
      JScrollPane scrollPane=getScrollPane();
      int x = 0;
      int y = 0;
      Insets scrollInsets = getScrollPaneInsets();

      if (scrollPane != null) {
	  Dimension d = scrollPane.getVisibleRect().getSize();
	  if (scrollPane.getBorder() != null) {
	     d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
		       d.getHeight() - scrollInsets.top - scrollInsets.bottom);
	  }

	  d.setSize(d.getWidth() - 20, d.getHeight() - 20);
	  desktop.setAllSize(x,y);
	  scrollPane.invalidate();
	  scrollPane.validate();
      }
  }

  private Insets getScrollPaneInsets() {
      JScrollPane scrollPane=getScrollPane();
      if (scrollPane==null) return new Insets(0,0,0,0);
      else return getScrollPane().getBorder().getBorderInsets(scrollPane);
  }

  private JScrollPane getScrollPane() {
      if (desktop.getParent() instanceof JViewport) {
	  JViewport viewPort = (JViewport)desktop.getParent();
	  if (viewPort.getParent() instanceof JScrollPane)
	      return (JScrollPane)viewPort.getParent();
      }
      return null;
  }

  protected void resizeDesktop() {
      int x = 0;
      int y = 0;
      JScrollPane scrollPane = getScrollPane();
      Insets scrollInsets = getScrollPaneInsets();

      if (scrollPane != null) {
	  JInternalFrame allFrames[] = desktop.getAllFrames();
	  for (int i = 0; i < allFrames.length; i++) {
	      if (allFrames[i].getX()+allFrames[i].getWidth()>x) {
		  x = allFrames[i].getX() + allFrames[i].getWidth();
	      }
	      if (allFrames[i].getY()+allFrames[i].getHeight()>y) {
		  y = allFrames[i].getY() + allFrames[i].getHeight();
	      }
	  }
	  Dimension d=scrollPane.getVisibleRect().getSize();
	  if (scrollPane.getBorder() != null) {
	     d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
		       d.getHeight() - scrollInsets.top - scrollInsets.bottom);
	  }

	  if (x <= d.getWidth()) x = ((int)d.getWidth()) - 20;
	  if (y <= d.getHeight()) y = ((int)d.getHeight()) - 20;
	  desktop.setAllSize(x,y);
	  scrollPane.invalidate();
	  scrollPane.validate();
      }
  }
  /*
  private void switchFrame(boolean next)
  {
    if(currentFrame == null) {
      if(initialFrame != null)
	activateFrame(initialFrame);
      return;
    }
    int currentIndex = childFrames.indexOf(currentFrame);
    if(currentIndex == -1)
      return;
    int nextIndex;
    int count = childFrames.size();
    if(count == 1)
      nextIndex = 0;
    else {
      if(next) {
	nextIndex = currentIndex + 1;
	if(nextIndex == count)
	  nextIndex = 0;
      }
      else {
	nextIndex = currentIndex - 1;
	if(nextIndex == -1)
	  nextIndex = count - 1;
      }
    }
    JInternalFrame f = (JInternalFrame)childFrames.elementAt(nextIndex);
    if(!f.isSelected())
      activateFrame(f);
    currentFrame = f;
  }
  */
}
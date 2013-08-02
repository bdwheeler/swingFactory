package net.saucefactory.swing;

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
import java.awt.event.*;
import java.beans.PropertyVetoException;
import net.saucefactory.swing.common.SFCursorManager;

public class SFInternalFrame extends JInternalFrame
{
  private String frameName = "CAISOInternalFrame";
  public int suggestedHeight = 0;
  public int suggestedWidth = 0;
  public java.util.Date createdDate = new java.util.Date();

  public SFInternalFrame()
  {
    super();
    setVisible(true);
    setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    setGlassPane(new CGlassPane());
  }

  public void setVisible(boolean v) {
    super.setVisible(v);
    if (v) createdDate = new java.util.Date();
  }

  public void setFrameName(String frameName)
  {
    this.frameName = frameName;
  }

  public String getFrameName()
  {
    return frameName;
  }

  public String toString()
  {
    return frameName;
  }

  //convienence to not have to wrap try catch.
  public void closeMe()
  {
    try {
      setClosed(true);
    }
    catch(PropertyVetoException e) {
      dispose();
    }
  }

  public SFInternalFrame getGlassParent() {
    return this;
  }

  public void moveToFront() {
    toFront();
  }

  //Override dispose to prevent memory leak in swings default closing - 1.2.2 - taken out for 1.3.
  /*
  public void dispose()
  {
    try {
      if(!isClosed())
	setClosed(true);
      super.dispose();
    }
    catch(PropertyVetoException e) {
      super.dispose();
    }
  }
  */

  class CGlassPane extends JComponent implements AWTEventListener{

    public CGlassPane() {
      super();
      //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      setOpaque(false);
      addMouseAdapter();
      addKeyListener(new KeyAdapter() {});
      setInputVerifier(new InputVerifier() {
	public boolean verify(JComponent anInput) {
	  return false;
	}
      });
    }

    public void setVisible(boolean aFlag) {
      super.setVisible(aFlag);
      /*
      if(aFlag) {
	super.setVisible(aFlag);
      }
      else {
	super.setVisible(aFlag);
      }
      */
    }

    public void addMouseAdapter() {
      addMouseListener(new MouseAdapter() {
	public void mouseReleased(MouseEvent e) {
	  if(!getGlassParent().isSelected() && !SFCursorManager.isCursorPaused(getGlassParent()))
	    moveToFront();
	}
      });
    }

    public void eventDispatched(AWTEvent anEvent) {
      System.out.println("got event");
      if(anEvent instanceof KeyEvent && anEvent.getSource() instanceof Component) {
	if(SwingUtilities.isDescendingFrom((Component)anEvent.getSource(), this)) {//.windowForComponent((Component)anEvent.getSource()) == parentWindow) {
	  System.out.println("consuming");
	  ((KeyEvent)anEvent).consume();
	}
      }
    }
  }
}
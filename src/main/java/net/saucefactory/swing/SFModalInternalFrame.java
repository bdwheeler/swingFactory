package net.saucefactory.swing;

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

public class SFModalInternalFrame extends SFInternalFrame
{
  public boolean proceed = false;

  public SFModalInternalFrame()
  {
    super();
    setMaximizable(false);
    setResizable(true);
  }

  public synchronized void startModal()
  {
    if(isVisible() && !isShowing()) {
      Container parent = this.getParent();
      while(parent != null) {
	if(parent.isVisible() == false)
	  parent.setVisible(true);
	parent = parent.getParent();
      }
    }

    try {
      if(SwingUtilities.isEventDispatchThread()) {
	EventQueue theQueue = getToolkit().getSystemEventQueue();
	while(isVisible() && !isClosed()) {
	  AWTEvent event = theQueue.getNextEvent();
	  Object src = event.getSource();
	  //System.out.println("source: " + src.toString());
	  if(event instanceof ActiveEvent)
	    ((ActiveEvent) event).dispatch();
	  else if(src instanceof Component)
	    ((Component)src).dispatchEvent(event);
	  else if(src instanceof MenuComponent)
	    ((MenuComponent) src).dispatchEvent(event);
	  else
	    System.err.println("unable to dispatch event: " + event);
	}
      }
      else
	while(isVisible())
	  wait();
    }
    catch(InterruptedException e) {
    }
  }

  public synchronized void stopModal()
  {
    notifyAll();
  }

  public void setProceed(boolean proceed)
  {
    this.proceed = proceed;
  }

  public boolean isProceed() {
    return proceed;
  }
}

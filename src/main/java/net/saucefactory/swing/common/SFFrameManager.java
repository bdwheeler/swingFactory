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
import java.util.Hashtable;

public class SFFrameManager
{
  private static SFDesktopPane targetDesktop;
  private static SFDesktopManager targetManager;

  private static Hashtable frameNames;

  public SFFrameManager(SFDesktopPane targetDesktop)
  {
    this.targetDesktop = targetDesktop;
    targetManager = (SFDesktopManager)targetDesktop.getDesktopManager();
  }

  public static void addFrame(JInternalFrame tempFrame, String frameName, int layer)
  {
    addFrame(tempFrame, frameName, layer, true);
  }

  public static void addFrame(JInternalFrame tempFrame, String frameName, int layer, boolean bringFrameToFront)
  {
    targetDesktop.add(tempFrame, new Integer(layer));
    //if(bringFrameToFront)
    //	targetManager.activateFrame(tempFrame);
  }

  public static void clearOpenFrames()
  {
    JInternalFrame[] tempFrames = targetDesktop.getAllFrames();
    if(tempFrames != null) {
      for(int i = 0; i < tempFrames.length; i++) {
	try {
	  tempFrames[i].setClosed(true);
	}
	catch(Exception e) {
	  tempFrames[i].dispose();
	}
      }
    }
    System.gc();
  }

  public static void iconifyAllFrames()
  {
    JInternalFrame[] tempFrames = targetDesktop.getAllFrames();
    if(tempFrames != null) {
      for(int i = 0; i < tempFrames.length; i++) {
	try {
	  tempFrames[i].setIcon(true);
	}
	catch(Exception e) {}
      }
    }
  }

  public static void bringFrameToFront(JInternalFrame frame)
  {
    bringFrameToFront(frame, true);
  }

  public static void bringFrameToFront(JInternalFrame frame, boolean setSelected)
  {
    if(setSelected)
      targetManager.activateFrame(frame);
    else {
      try {
	frame.toFront();
	frame.setSelected(false);
      }
      catch(Exception e) {
	//Vetoed
      }
    }
  }
  //add specific frame additions.
}
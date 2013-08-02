package net.saucefactory.swing;

/**
 * Title:        Slic Web Client/Server
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      California ISO
 * @author Jeremy Leng
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.saucefactory.swing.common.*;
//import com.caiso.slic.client.utils.DebugUtility;

public class SFMainFrame extends JFrame {
  protected BorderLayout frameLayout = null;
  protected SFDesktopPane desktopPane = null;
  protected SFFrameManager frameManager = null;
  protected JScrollPane vertMenu = null;
  protected JPanel underPanel = new JPanel(); //
  protected SFMultipleToolbarPanel toolPanel = null;
  private static SFMainFrame internalInstance = null;
  private ExitListener exiter = new ExitListener();
  protected JScrollPane scrollPane;

  private SFMainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    frameLayout = new BorderLayout();
    desktopPane = new SFDesktopPane();
    underPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    frameManager = new SFFrameManager(desktopPane);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(exiter);
    try {
      getContentPane().setLayout(frameLayout);
      getContentPane().add(underPanel, BorderLayout.CENTER);
      underPanel.setLayout(new BorderLayout());
      underPanel.add(desktopPane, BorderLayout.CENTER);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static SFMainFrame getHandle() {
    if (internalInstance == null) reset();
    return internalInstance;
  }

  public void setDefaultIcon() {
    Image tmpImg = SFIconManager.getFrameIcon().getImage();
    setIconImage(tmpImg);
  }

  public static void reset() {
    //System.out.println("resetting main frame");
    internalInstance = null;
    internalInstance = new SFMainFrame();
  }

  public SFFrameManager getFrameManager() {
    return frameManager;
  }

  public SFDesktopPane getDesktopPane() {
    return desktopPane;
  }

  public SFMultipleToolbarPanel getToolPanel() {
    return toolPanel;
  }

  public void setDesktopCanScroll(boolean canScroll) {
    if (canScroll) {
      try {
  getContentPane().remove(desktopPane);
      } catch (Exception e) {}
      scrollPane = new JScrollPane();
      scrollPane.getViewport().add(desktopPane);
      getContentPane().add(scrollPane,BorderLayout.CENTER);
      desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    } else {
      try {
  getContentPane().remove(scrollPane);
      } catch (Exception e) {}
      scrollPane = null;
      getContentPane().add(desktopPane, BorderLayout.CENTER);
    }
  }

  public void addInternalFrame(JInternalFrame addFrame, String frameName, int xPos, int yPos, int width, int height, int layer, boolean bringToFront) {
    addFrame.setBounds(xPos, yPos, width, height);
    frameManager.addFrame(addFrame, frameName, layer, bringToFront);
    setMaximized(addFrame);
  }

  public void addInternalFrame(JInternalFrame addFrame, String frameName, int layer, boolean bringToFront) {
    addFrame.setBounds(0, 0, desktopPane.getWidth(), getContentPane().getHeight());
    frameManager.addFrame(addFrame, frameName, layer, bringToFront);
    setMaximized(addFrame);
  }

  public void addCenteredInternalFrame(JInternalFrame addFrame, String frameName, int layer, boolean bringToFront) {
    Dimension d = addFrame.getPreferredSize();
    addCenteredInternalFrame(addFrame, frameName, (int)d.getWidth(), (int)d.getHeight(), layer, bringToFront);
  }

  public void addCenteredInternalFrame(JInternalFrame addFrame, String frameName, int width, int height, int layer, boolean bringToFront) {
    addFrame.setBounds((desktopPane.getWidth() - width) / 2, (getContentPane().getHeight() - height) / 2, width, height);
    frameManager.addFrame(addFrame, frameName, layer, bringToFront);
    setMaximized(addFrame);
  }

  public void addCenteredOverInternalFrame(JInternalFrame parentFrame, JInternalFrame addFrame, String frameName, int width, int height, int layer, boolean bringToFront) {
    addFrame.setBounds((parentFrame.getBounds().width - width) / 2 + parentFrame.getBounds().x, (parentFrame.getBounds().height - height) / 2 + parentFrame.getBounds().y, width, height);
    frameManager.addFrame(addFrame, frameName, layer, bringToFront);
    setMaximized(addFrame);
  }

  public void addSingletonInternalFrame(JInternalFrame addFrame, String frameName, int xPos, int yPos, int width, int height, int layer, boolean bringToFront) {
    if (!checkForInternalFrame(addFrame)) {
      addInternalFrame(addFrame, frameName, xPos, yPos, width, height, layer, bringToFront);
    } else {
      try {
  addFrame.setIcon(false);
  frameManager.bringFrameToFront(addFrame, true);
  //addFrame.setSelected(true);
      } catch (Exception e) {}
    }
  }

  public boolean checkForInternalFrame(JInternalFrame frame) {
    JInternalFrame[] frames = desktopPane.getAllFrames();
    for (int i = 0; i < frames.length; i++) {
      if (frames[i].equals(frame)) return true;
    }
    return false;
  }

  private void setMaximized(JInternalFrame addFrame) {
    JInternalFrame[] frames = desktopPane.getAllFrames();
    for (int i = 0; i < frames.length; i++) {
      if (frames[i].isSelected()) {
  try { addFrame.setMaximum(frames[i].isMaximum()); } catch (Exception e) {}
  break;
      }
    }
  }

  public void setBackground(Color c) {
    try {
      super.setBackground(c);
      desktopPane.setBackground(c);
    } catch (Exception e) {}
  }

  public JScrollPane setVerticalIconMenu(JScrollPane iconMenu) {
    return setVerticalIconMenu(iconMenu, BorderLayout.WEST);
  }

  public JScrollPane setVerticalIconMenu(JScrollPane iconMenu, String position) {
    this.getContentPane().add(iconMenu, position);
    vertMenu = iconMenu;
    return iconMenu;
  }

  public JScrollPane getVerticalIconMenu() {
    return vertMenu;
  }

  public JToolBar addToolBar(JToolBar toolBar) {
    if (toolPanel == null) {
      toolPanel = new SFMultipleToolbarPanel();
      getContentPane().add(toolPanel, BorderLayout.NORTH);
    }
    toolPanel.addToolbar(toolBar);
    return toolBar;
  }

  public void addSouthComponent(JComponent component)
  {
    getContentPane().add(component, BorderLayout.SOUTH);
  }

  public void setExitOnClose(boolean exitOnClose) {
    try {
      this.removeWindowListener(exiter);
    } catch (Exception e) {}
    exiter.doConfirm = false;
    exiter.doSystemExit = exitOnClose;
    this.addWindowListener(exiter);
  }

  public void exit() {
    exiter.windowClosing(null);
  }

  public void setConfirmOnClose(String confirmationMessage, String confirmationTitle) {
    setConfirmOnClose(confirmationMessage, confirmationTitle, true);
  }

  public void setConfirmOnClose(String confirmationMessage, String confirmationTitle, boolean doSystemExit) {
    try {
      this.removeWindowListener(exiter);
    } catch (Exception e) {}
    exiter.title = confirmationTitle;
    exiter.message = confirmationMessage;
    exiter.doConfirm = true;
    exiter.doSystemExit = doSystemExit;
    this.addWindowListener(exiter);
  }

  Vector exitListeners = new Vector();
  public void addExitListener(ISFExitListener listener) {
    exitListeners.add(listener);
  }

  protected void notifyExitListeners() {
    Iterator iter = exitListeners.iterator();
    while (iter.hasNext()) {
      ISFExitListener itm = (ISFExitListener)iter.next();
      itm.exiting();
    }
  }

  class ExitListener extends WindowAdapter
  {
    public String title = "Confirm Exit";
    public String message = "Are you sure you wish to exit?";
    public boolean doConfirm = false;
    public boolean doSystemExit = true;
    public void windowClosing(WindowEvent e) {
      if(doConfirm) {
  SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      if(JOptionPane.showConfirmDialog(SFMainFrame.getHandle(), message, title,
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        notifyExitListeners();
        //DebugUtility.debugPrintln("**************************************");
        //DebugUtility.debugPrintln("SLIC CLIENT APPLICATION SHUTTING DOWN!");
        //DebugUtility.debugPrintln("**************************************");
        if (doSystemExit) System.exit(0);
        else {
    dispose();
    internalInstance = null;
        }
      }
    }
  });
      } else {
  notifyExitListeners();
  //DebugUtility.debugPrintln("**************************************");
  //DebugUtility.debugPrintln("SLIC CLIENT APPLICATION SHUTTING DOWN!");
  //DebugUtility.debugPrintln("**************************************");
  if (doSystemExit) System.exit(0);
  else {
    dispose();
    internalInstance = null;
  }
      }
    }
  }
}

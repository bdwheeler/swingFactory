package net.saucefactory.swing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.saucefactory.swing.common.*;

public class SFMainPanelFrame extends JFrame {
  protected BorderLayout frameLayout = null;
  protected JScrollPane vertMenu = null;
  protected SFMultipleToolbarPanel toolPanel = null;
  private static SFMainPanelFrame internalInstance = null;
  private ExitListener exiter = new ExitListener();
  protected JPanel pnlContextHolder = new JPanel();
  public JPanel contextPanel = null;

  public SFMainPanelFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    frameLayout = new BorderLayout();
    pnlContextHolder.setBorder(BorderFactory.createLoweredBevelBorder());
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(exiter);
    try {
      getContentPane().setLayout(frameLayout);
      getContentPane().add(pnlContextHolder, BorderLayout.CENTER);
      pnlContextHolder.setLayout(new BorderLayout());
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static SFMainPanelFrame getHandle() {
    if (internalInstance == null) reset();
    return internalInstance;
  }

  public void setDefaultIcon() {
    Image tmpImg = SFIconManager.getFrameIcon().getImage();
    setIconImage(tmpImg);
  }

  public static void reset() {
    internalInstance = null;
    internalInstance = new SFMainPanelFrame();
  }

  public void setContextPanel(JPanel _contextPanel) {
    try {
      if(contextPanel != null)
        pnlContextHolder.remove(contextPanel);
      pnlContextHolder.add(_contextPanel, BorderLayout.CENTER);
      contextPanel = _contextPanel;
      pnlContextHolder.revalidate();
      pnlContextHolder.repaint();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public JPanel getContextPanel() {
    return contextPanel;
  }

  public SFMultipleToolbarPanel getToolPanel() {
    return toolPanel;
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

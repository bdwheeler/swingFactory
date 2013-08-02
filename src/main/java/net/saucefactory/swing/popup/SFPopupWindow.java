package net.saucefactory.swing.popup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SFPopupWindow
	extends Window {
    protected final SFPopupListener popupListener = new SFPopupListener(this);
    protected boolean visFlag = false;

    public SFPopupWindow() {
    	this(null);
    }

    public SFPopupWindow(Frame parent) {
        this((Window)parent);
    }

    public SFPopupWindow(Window wdw) {
    	super(wdw);
        addNotify();
        hide();
        setLocation(0,0);
        installListener();
    }

    public boolean isManagingFocus() {
        return false;
    }

    public void installListener() {
        addFocusListener(popupListener);
    }

    public void hide() {
        visFlag = false;
        super.hide();
    }

    public void show() {
        visFlag = true;
        super.show();
        super.toFront();
		requestFocus();
    }

    public boolean isVisible() {
        return visFlag;
    }

    public void setVisible(boolean flag) {
        if(flag)
            show();
        else
            hide();
    }

    public void popupOver(Component c, int xOffset, int yOffset) {
        Point p = c.getLocationOnScreen();
        setLocation(((int)p.getX()) + xOffset, ((int)p.getY()) + yOffset);
        show();
    }

    public void popupAt(int x, int y) {
        setLocation(x,y);
        show();
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

    public class SFPopupListener
		extends MouseAdapter
		implements FocusListener {

		SFPopupWindow popupParent = null;

		public SFPopupListener(SFPopupWindow parent) {
			popupParent = parent;
		}

		public void focusGained(FocusEvent e) {
			dispatchEvent(new WindowEvent(popupParent, WindowEvent.WINDOW_ACTIVATED));
		}

		public void focusLost(FocusEvent e) {
                        String version = System.getProperty("java.version");
                        if ((version.charAt(0) == '1') &&
                            (4 > Integer.parseInt(new String(new char[]{version.charAt(2)})) &&
                            (3 < version.length()) &&
                            (1 > Integer.parseInt(new String(new char[]{version.charAt(4)}))))
                            )
                          hide();
                        else
                          if (e.isTemporary()) hide();
		}

		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				Component c = (Component)e.getSource();
				popupParent.popupOver(c, e.getX(), e.getY());
			} else {
				if(popupParent.isVisible())
					popupParent.hide();
			}
		}
    }
}

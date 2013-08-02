package net.saucefactory.swing.event;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */
import javax.swing.*;
import javax.swing.event.*;

public class ScrollSynchronizer implements ChangeListener {
    JScrollBar partner = null;
    public ScrollSynchronizer(JScrollBar p) {
      partner = p;
    }

    public void stateChanged(ChangeEvent e) {
      BoundedRangeModel sourceScroll = (BoundedRangeModel)e.getSource();
      int val = sourceScroll.getValue();
      partner.setValue(val);
    }
  }

package net.saucefactory.swing;

import java.awt.*;
import javax.swing.JDialog;
import java.awt.HeadlessException;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class SFDialog extends JDialog {

  public SFDialog() throws HeadlessException {
      super();
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Frame owner) throws HeadlessException {
      super(owner);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Frame owner, boolean modal) throws HeadlessException {
      super(owner, modal);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Frame owner, String title) throws HeadlessException {
      super(owner, title);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Frame owner, String title, boolean modal)
      throws HeadlessException {
    super(owner, title, modal);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Frame owner, String title, boolean modal,
                 GraphicsConfiguration gc) {
      super(owner, title, modal, gc);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Dialog owner) throws HeadlessException {
      super(owner);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Dialog owner, boolean modal) throws HeadlessException {
      super(owner, modal);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Dialog owner, String title) throws HeadlessException {
      super(owner, title);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Dialog owner, String title, boolean modal)
      throws HeadlessException {
      super(owner, title, modal);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public SFDialog(Dialog owner, String title, boolean modal,
                 GraphicsConfiguration gc) throws HeadlessException {

      super(owner, title, modal, gc);
      this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  }

  public void show() {
    try {
      Point p = getParent().getLocation();
      if (getParent().getWidth() == 0 || getParent().getHeight() == 0)
        throw new Exception();
      int x = p.x + (getParent().getWidth() / 2) - (this.getWidth() / 2);
      int y = p.y + (getParent().getHeight() / 2) - (this.getHeight() / 2);
      setLocation(x, y);
    } catch (Exception e) {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
    }
    super.show();
  }

  public void showNormal() {
    super.show();
  }

  public static void main(String[] args) throws HeadlessException {
    SFDialog CDialog1 = new SFDialog();
  }
}

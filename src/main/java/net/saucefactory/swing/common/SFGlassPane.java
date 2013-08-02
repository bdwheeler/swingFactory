package net.saucefactory.swing.common;

import javax.swing.JComponent;
import java.awt.event.AWTEventListener;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import javax.swing.InputVerifier;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.AWTEvent;

public class SFGlassPane extends JComponent implements AWTEventListener {

  public SFGlassPane(Cursor busyCursor) {
    super();
    setCursor(busyCursor);
    setOpaque(false);
    addMouseAdapter();
    addKeyListener(new KeyAdapter() {});
    setInputVerifier(new InputVerifier() {
      public boolean verify(JComponent anInput) {
        return false;
      }
    });
  }

  public void setVisible(boolean visible) {
    super.setVisible(visible);
  }

  public void addMouseAdapter() {
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        //if(!getGlassParent().isSelected() && !SFCursorManager.isCursorPaused(getGlassParent()))
          //moveToFront();
      }
    });
  }

  public void eventDispatched(AWTEvent anEvent) {
    if(anEvent instanceof KeyEvent && anEvent.getSource() instanceof Component) {
      if(SwingUtilities.isDescendingFrom((Component)anEvent.getSource(), this)) { //.windowForComponent((Component)anEvent.getSource()) == parentWindow) {
        //System.out.println("consuming");
        ((KeyEvent)anEvent).consume();
      }
    }
  }
}

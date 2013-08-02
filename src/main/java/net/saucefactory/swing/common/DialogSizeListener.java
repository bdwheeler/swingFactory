package net.saucefactory.swing.common;

import java.awt.event.ComponentEvent;
import javax.swing.JDialog;
import java.awt.event.ComponentAdapter;

public class DialogSizeListener extends ComponentAdapter {
    JDialog dialog;
    int minWidth;
    int minHeight;

    public DialogSizeListener(JDialog dialog, int minWidth, int minHeight) {
      this.dialog = dialog;
      this.minWidth = minWidth;
      this.minHeight = minHeight;
    }

    public void componentResized(ComponentEvent e) {
      int width = dialog.getWidth();
      int height = dialog.getHeight();
      boolean change = false;
      if(width < minWidth) {
        change = true;
        width = minWidth;
      }
      if(height < minHeight) {
        change = true;
        height = minHeight;
      }
      if(change)
        dialog.setSize(width, height);
    }
  }

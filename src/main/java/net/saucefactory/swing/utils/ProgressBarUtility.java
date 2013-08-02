package net.saucefactory.swing.utils;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;
import net.saucefactory.swing.common.SwingWorker;

public class ProgressBarUtility extends JProgressBar {

  protected DefaultBoundedRangeModel model;
  protected SwingWorker worker;
  protected static final int DEFAULT_SIZE = 32;

  public ProgressBarUtility() {
    setStringPainted(false);
  }

  public void startAnimation() {
    if(worker != null)
      return;
    setStringPainted(false);
    model = new DefaultBoundedRangeModel(0, 20, 0, DEFAULT_SIZE);
    setModel(model);
    worker = new SwingWorker() {

      int value = model.getValue();

      public Object construct() {
        while(true) {
          try {
            Thread.sleep(30);
            if(value != model.getValue())
              return null; // return if the value was set explicitly
            model.setValue(value = (model.getValue() + 1) % DEFAULT_SIZE);
          }
          catch(Exception e) {
            return null;
          }
        }
      }
    };
    worker.start();
  }

  public void stopAnimation() {
    if(worker != null) {
      worker.interrupt();
      worker = null;
    }
    if(model != null) {
      model.setValue(0);
    }
  }
}

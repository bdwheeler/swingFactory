package net.saucefactory.swing.dnd;

import javax.swing.border.Border;
import java.awt.dnd.DropTargetListener;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;
import java.awt.event.HierarchyListener;
import java.awt.event.HierarchyEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JComponent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

public class SFExternalDropAdapter {

  private Border plainBorder;
  private Border dropBorder;
  private DropTargetListener dropListener;
  private SFDropListener externalListener;
  private Component targetComponent;

  private static Color BORDER_COLOR = new Color(0f, 0f, 1f, 0.25f);
  private static Border DEFAULT_DROP_BORDER = BorderFactory.createLineBorder(BORDER_COLOR);

  public SFExternalDropAdapter(Component c, Border dropBorder, boolean recursive, SFDropListener externalListener) {
    dropListener = new SFInternalDropListener();
    this.externalListener = externalListener;
    this.dropBorder = dropBorder == null ? DEFAULT_DROP_BORDER : dropBorder;
    makeDropTarget(c, recursive);
  }

  private void makeDropTarget(final Component c, boolean recursive) {
    c.addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent evt) {
        Component parent = c.getParent();
        if(parent == null) {
          c.setDropTarget(null);
        }
        else {
          new DropTarget(c, dropListener);
        }
      }
    });
    if(c.getParent() != null)
      new DropTarget(c, dropListener);
    if(recursive && (c instanceof Container)) {
      Container cont = (Container)c;
      Component[] comps = cont.getComponents();
      for(int i = 0; i < comps.length; i++)
        makeDropTarget(comps[i], recursive);
    }
  }

  private boolean isDragOk(DropTargetDragEvent evt) {
    boolean ok = false;
    DataFlavor[] flavors = evt.getCurrentDataFlavors();
    int i = 0;
    while(!ok && i < flavors.length) {
      if(flavors[i].equals(DataFlavor.javaFileListFlavor))
        ok = true;
      i++;
    }
    return ok;
  }

  public static void install(Component c, Border dropBorder, boolean recursive, SFDropListener listener) {
    SFExternalDropAdapter adapter = new SFExternalDropAdapter(c, dropBorder, recursive, listener);
  }

  public static boolean uninstall(Component c, boolean recursive) {
    c.setDropTarget(null);
    if(recursive && (c instanceof Container)) {
      Component[] comps = ((Container)c).getComponents();
      for(int i = 0; i < comps.length; i++)
        uninstall(comps[i], recursive);
      return true;
    }
    else return false;
  }

  class SFInternalDropListener implements DropTargetListener {

    public SFInternalDropListener() {
    }

    public void dragEnter(DropTargetDragEvent evt) {
      //System.out.println("Drag enter");
      if(isDragOk(evt)) {
        Component c = evt.getDropTargetContext().getComponent();
        if(c instanceof JComponent) {
          plainBorder = ((JComponent)c).getBorder();
          ((JComponent)c).setBorder(dropBorder);
        }
        evt.acceptDrag(DnDConstants.ACTION_COPY);
      }
      else {
        evt.rejectDrag();
      }
      if(externalListener != null)
        externalListener.dragEnter(evt);
    }

    public void dragOver(DropTargetDragEvent evt) {
      if(externalListener != null)
        externalListener.dragOver(evt);
    }

    public void drop(DropTargetDropEvent evt) {
      //System.out.println("Drop");
      Component c = evt.getDropTargetContext().getComponent();
      try {
        Transferable tr = evt.getTransferable();
        if(tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          List fileList = (List)tr.getTransferData(DataFlavor.javaFileListFlavor);
          Iterator iterator = fileList.iterator();
          File[] files = new File[fileList.size()];
          fileList.toArray(files);
          if(externalListener != null)
            externalListener.filesDropped(evt, files);
          evt.getDropTargetContext().dropComplete(true);
        }
        else {
          evt.rejectDrop();
        }
      }
      catch(Exception e) {
        e.printStackTrace();
        evt.rejectDrop();
      }
      finally {
        if(c instanceof JComponent) {
          ((JComponent)c).setBorder(plainBorder);
        }
      }
    }

    public void dragExit(DropTargetEvent evt) {
      Component c = evt.getDropTargetContext().getComponent();
      if(c instanceof JComponent)
        ((JComponent)c).setBorder(plainBorder);
      if(externalListener != null)
        externalListener.dragExit(evt);
    }

    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent evt) {
      if(isDragOk(evt)) {
        evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
      }
      else {
        evt.rejectDrag();
      }
    }
  }

  public interface SFDropListener {
    public void filesDropped(DropTargetDropEvent evt, File[] files);
    public void dragOver(DropTargetDragEvent evt);
    public void dragEnter(DropTargetDragEvent evt);
    public void dragExit(DropTargetEvent evt);
  }
}

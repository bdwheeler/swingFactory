package net.saucefactory.swing.common;

import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Component;
import java.util.Vector;
import java.awt.Container;
import java.util.Iterator;
import java.awt.Window;

public class SFFocusPolicy extends ContainerOrderFocusTraversalPolicy {

  private Vector components;
  private int index;

  public SFFocusPolicy() {
    super();
    components = new Vector();
    index = 0;
  }

  public Component getComponentAfter(Container c, Component comp) {
    Iterator iter = components.iterator();
    boolean found = false;
    while(!found && iter.hasNext()) {
      Component test = (Component)iter.next();
      found = (test == comp);
    }
    Component rtn;
    if(iter.hasNext())
      rtn = (Component)iter.next();
    else
      rtn = getFirstComponent();
    if(rtn.isVisible() && rtn.isEnabled() && rtn.isFocusable())
      return rtn;
    else
      return getComponentAfter(c, rtn);
  }

  public Component getComponentBefore(Container c, Component comp) {
    int i = 0;
    boolean found = false;
    while(!found && (i < components.size())) {
      Component test = (Component)components.elementAt(i);
      found = (test == comp);
      if(!found)
        i++;
    }
    Component rtn;
    if(i > 0)
      rtn = (Component)components.elementAt(i - 1);
    else
      rtn = getLastComponent(null);
    if(rtn.isVisible() && rtn.isEnabled() && rtn.isFocusable())
      return rtn;
    else
      return getComponentBefore(c, rtn);
  }

  public Component getFirstComponent() {
    index = 0;
    return (Component)components.elementAt(index);
  }

  public Component getLastComponent(Container c) {
    int i = components.size() - 1;
    if(i >= 0)
      return(Component)components.elementAt(i);
    else
      return null;
  }

  public void addComponent(Component c) {
    components.addElement(c);
  }

  public Component getDefaultComponent() {
    return getFirstComponent();
  }

  public Component getInitialComponent(Window window) {
    return getDefaultComponent();
  }
}

package net.saucefactory.swing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class SFList extends JList {

  DefaultListModel model;
  private Object saveSelection = null;

  public SFList() {
    model = new DefaultListModel();
    setModel(model);
  }

  public Object getSelectedObject() {
    int selection = getSelectedIndex();
    if(selection > -1)
      return model.get(selection);
    return null;
  }

  public Object[] getSelectedObjects() {
    int[] selections = getSelectedIndices();
    if(selections != null && selections.length > 0) {
      Object[] rtnArray = new Object[selections.length];
      for(int i = 0; i < selections.length; i++)
        rtnArray[i] = this.getDListModel().get(selections[i]);
      return rtnArray;
    }
    return null;
  }

  public boolean isItemSelected() {
    return getSelectedIndex() > -1;
  }

  public void setListData(Object[] data) {
    model.removeAllElements();
    if(data != null) {
      for(int i = 0; i < data.length; i++)
	model.add(i, data[i]);
    }
  }

  public void setListData(Vector data) {
    model.removeAllElements();
    if(data != null) {
      for(int i = 0; i < data.size(); i++)
	model.add(i, data.get(i));
    }
  }

  public void setListData(Collection data) {
    model.removeAllElements();
    if(data != null) {
      Iterator it = data.iterator();
      while(it.hasNext())
	model.addElement(it.next());
    }
  }

  public Collection getDataAsCollection() {
    ArrayList rtnLst = new ArrayList();
    if(model.size() > 0) {
      for(int i = 0; i < model.size(); i++)
	rtnLst.add(model.elementAt(i));
    }
    return rtnLst;
  }

  public DefaultListModel getDListModel() {
    return model;
  }

  public void saveSelection() {
    saveSelection = getSelectedObject();
  }

  public void restoreSelection() {
    if(saveSelection == null)
      return;
    int row = model.indexOf(saveSelection);
    if(row > -1)
      setSelectedIndex(row);
    saveSelection = null;
  }
}

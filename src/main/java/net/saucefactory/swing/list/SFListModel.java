package net.saucefactory.swing.list;

import net.saucefactory.swing.data.ISFDataAdapter;
import javax.swing.DefaultListModel;

public class SFListModel extends DefaultListModel {

  private ISFDataAdapter dataAdapter = null;

  public SFListModel() {}

  public SFListModel(ISFDataAdapter dataAdapter) {
    setDataAdapter(dataAdapter);
  }

  public Object getElementAt(int index) {
    return dataAdapter == null ? super.getElementAt(index) : dataAdapter.getValue(super.getElementAt(index));
  }

  public Object getObjectAt(int index) {
    return super.getElementAt(index);
  }

  public void setDataAdapter(ISFDataAdapter dataAdapter) {
    this.dataAdapter = dataAdapter;
  }
}

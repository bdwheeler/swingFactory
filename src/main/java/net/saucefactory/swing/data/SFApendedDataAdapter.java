package net.saucefactory.swing.data;

import java.util.ArrayList;
import java.util.Collection;

public class SFApendedDataAdapter implements ISFDataAdapter {

  ArrayList adapters = new ArrayList();

  public SFApendedDataAdapter(ISFDataAdapter adapter) {
    adapters.add(adapter);
  }

  public SFApendedDataAdapter(Collection dataAdapters) {
    adapters.addAll(dataAdapters);
  }

  public SFApendedDataAdapter append(ISFDataAdapter adapter) {
    adapters.add(adapter);
    return this;
  }

  public Object getValue(Object source) {
    String rtn = "";
    for(int i = 0; i < adapters.size(); i++) {
      rtn = rtn + ((ISFDataAdapter)adapters.get(i)).getValue(source);
    }
    return rtn;
  }

  public void setValue(Object target, Object value) {
  }

  public boolean isValue(Object value, Object target) {
    value = getValue(value);
    if(value == null)
      return target == null;
    if(target == null)
      return false;
    return target.equals(value);
  }
}

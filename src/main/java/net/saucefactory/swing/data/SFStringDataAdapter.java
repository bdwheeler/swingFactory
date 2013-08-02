package net.saucefactory.swing.data;

public class SFStringDataAdapter implements ISFDataAdapter {

  public SFStringDataAdapter() {}

  public Object getValue(Object source) {
    return source;
  }

  public void setValue(Object target, Object value) {
    target = value;
  }

  public boolean isValue(Object value, Object target) {
    if(value == null)
      return target == null;
    if(target == null)
      return false;
    return target.equals(value);
  }
}

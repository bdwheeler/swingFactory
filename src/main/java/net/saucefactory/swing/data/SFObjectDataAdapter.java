package net.saucefactory.swing.data;

public class SFObjectDataAdapter implements ISFDataAdapter {

  public SFObjectDataAdapter() {}

  public Object getValue(Object source) {
    return source;
  }

  public void setValue(Object target, Object value) {
    try {
      target = value;
    } catch(Exception e) {
      return;
    }
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

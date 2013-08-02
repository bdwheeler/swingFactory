package net.saucefactory.swing.data;

public class SFFieldDataAdapter implements ISFDataAdapter {

  String fieldName;

  public SFFieldDataAdapter(String fieldName) {
    this.fieldName = fieldName;
  }

  public Object getValue(Object source) {
    try {
      return source.getClass().getField(fieldName).get(source);
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    try {
      target.getClass().getField(fieldName).set(target, value);
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

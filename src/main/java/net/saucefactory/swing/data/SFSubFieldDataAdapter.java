package net.saucefactory.swing.data;

public class SFSubFieldDataAdapter implements ISFDataAdapter {

  String fieldName;
  String subFieldName;

  public SFSubFieldDataAdapter(String fieldName, String subFieldName) {
    this.fieldName = fieldName;
    this.subFieldName = subFieldName;
  }

  public Object getValue(Object source) {
    try {
      Object tmpObj = source.getClass().getField(fieldName).get(source);
      if(tmpObj != null)
        return tmpObj.getClass().getField(subFieldName).get(tmpObj);
      return null;
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    try {
      Object tmpObj = target.getClass().getField(fieldName).get(target);
      if(tmpObj != null)
        tmpObj.getClass().getField(subFieldName).set(tmpObj, value);
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

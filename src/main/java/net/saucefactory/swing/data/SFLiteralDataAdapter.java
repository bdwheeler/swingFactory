package net.saucefactory.swing.data;

public class SFLiteralDataAdapter implements ISFDataAdapter {

  String literal;

  public SFLiteralDataAdapter(String literal) {
    this.literal = literal;
  }

  public Object getValue(Object source) {
    return literal;
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

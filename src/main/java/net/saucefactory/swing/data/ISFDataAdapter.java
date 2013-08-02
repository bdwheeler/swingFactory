package net.saucefactory.swing.data;

public interface ISFDataAdapter {
  public void setValue(Object target, Object value);
  public Object getValue(Object source);
  public boolean isValue(Object value, Object target);
}

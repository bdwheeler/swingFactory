package net.saucefactory.swing.data;

import java.lang.reflect.Method;

public class SFMethodDataAdapter implements ISFDataAdapter {

  public Method getMethod;
  public Method setMethod;

  public SFMethodDataAdapter(Class className, String getMethodName) {
    try {
      getMethod = className.getMethod(getMethodName, null);
    } catch(Exception e) {
      getMethod = null;
    }
    setMethod = null;
  }

  public SFMethodDataAdapter(Class className, String getMethodName, Class[] getMethodParams,
      String setMethodName, Class[] setMethodParams) {
    try {
      getMethod = className.getMethod(getMethodName, getMethodParams);
    } catch(Exception e) {
      getMethod = null;
    }
    try {
      setMethod = className.getMethod(setMethodName, setMethodParams);
    } catch(Exception e) {
      setMethod = null;
    }
  }

  public Object getValue(Object source) {
    try {
      return getMethod.invoke(source, null);
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    try {
      setMethod.invoke(target, new Object[]{value});
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

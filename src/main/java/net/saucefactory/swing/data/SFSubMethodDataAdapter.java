package net.saucefactory.swing.data;

import java.lang.reflect.Method;

public class SFSubMethodDataAdapter implements ISFDataAdapter {

  public Method getMethod;
  public Method subGetMethod;
  public Method subSetMethod;

  public SFSubMethodDataAdapter(Class className, String getMethodName, Class subClassName, String subGetMethodName) {
    try {
      getMethod = className.getMethod(getMethodName, null);
      subGetMethod = subClassName.getMethod(subGetMethodName, null);
    } catch(Exception e) {
      getMethod = null;
      subGetMethod = null;
    }
    subSetMethod = null;
  }

  public SFSubMethodDataAdapter(Class className, String getMethodName, Class[] getMethodParams,
      Class subClassName, String subGetMethodName, Class[] subGetMethodParams,
      String subSetMethodName, Class[] subSetMethodParams) {
    try {
      getMethod = className.getMethod(getMethodName, getMethodParams);
      subGetMethod = subClassName.getMethod(subGetMethodName, subGetMethodParams);
    } catch(Exception e) {
      getMethod = null;
      subGetMethod = null;
    }
    try {
      subSetMethod = subClassName.getMethod(subSetMethodName, subSetMethodParams);
    } catch(Exception e) {
      subSetMethod = null;
    }
  }

  public Object getValue(Object source) {
    try {
      Object tmpObj = getMethod.invoke(source, null);
      if(tmpObj != null)
        return subGetMethod.invoke(tmpObj, null);
      return null;
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    try {
      Object tmpObj = getMethod.invoke(target, null);
      if(tmpObj != null)
        subSetMethod.invoke(tmpObj, new Object[]{value});
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

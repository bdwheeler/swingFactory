package net.saucefactory.swing.data;

import java.lang.reflect.Method;

public class SFMethodChainDataAdapter implements ISFDataAdapter {

  public Method[] getMethods;
  public Method setMethod;

  public SFMethodChainDataAdapter(Class[] classNames, String[] getMethodNames) {
    try {
      getMethods = new Method[classNames.length];
      for(int i = 0; i < getMethods.length; i++)
        getMethods[i] = classNames[i].getMethod(getMethodNames[i], null);
    } catch(Exception e) {
      getMethods = null;
    }
  }

  public SFMethodChainDataAdapter(Class[] classNames, String[] getMethodNames, Class[] getMethodParams,
      String subSetMethodName, Class[] subSetMethodParams) {
    try {
      getMethods = new Method[classNames.length];
      for(int i = 0; i < getMethods.length; i++)
        getMethods[i] = classNames[i].getMethod(getMethodNames[i], null);
    } catch(Exception e) {
      getMethods = null;
    }
    try {
      setMethod = classNames[classNames.length - 1].getMethod(subSetMethodName, subSetMethodParams);
    } catch(Exception e) {
      setMethod = null;
    }
  }

  public Object getValue(Object source) {
    try {
      Object tmpObj = source;
      for(int i = 0; i < getMethods.length; i++)
        tmpObj = getMethods[i].invoke(tmpObj, null);
      return tmpObj;
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    try {
      Object tmpObj = target;
      for(int i = 0; i < getMethods.length - 1; i++)
        tmpObj = getMethods[i].invoke(tmpObj, null);
      if(tmpObj != null)
        setMethod.invoke(tmpObj, new Object[]{value});
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

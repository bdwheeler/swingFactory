package net.saucefactory.swing.data;

import java.util.StringTokenizer;

public class SFSimpleMethodDataAdapter implements ISFDataAdapter {

  public String[] getMethods;
  public String setMethod;

  public SFSimpleMethodDataAdapter(String get) {
    this(get, null);
  }

  public SFSimpleMethodDataAdapter(String get, String set) {
    try {
      buildMethodArray(get);
      setMethod = set;
    }
    catch(Exception e) {
      getMethods = null;
    }
  }

  public Object getValue(Object source) {
    if(source == null)
      return null;
    try {
      Object tmpObj = source;
      for(int i = 0; i < getMethods.length; i++)
        tmpObj = tmpObj.getClass().getMethod(getMethods[i], null).invoke(tmpObj, null);
      return tmpObj;
    }
    catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    if(target == null)
        return;
    try {
      Object tmpObj = target;
      for(int i = 0; i < getMethods.length - 1; i++)
        tmpObj = tmpObj.getClass().getMethod(getMethods[i], null).invoke(tmpObj, null);
      if(tmpObj != null)
        tmpObj.getClass().getMethod(setMethod, null).invoke(tmpObj, new Object[]{value});
    }
    catch(Exception e) {
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

  private void buildMethodArray(String get) {
    if(get.indexOf(".") < 0)
      getMethods = new String[]{get};
    else {
      StringTokenizer tok = new StringTokenizer(get, ".");
      getMethods = new String[tok.countTokens()];
      int cnt = 0;
      while(tok.hasMoreTokens()) {
        getMethods[cnt] = tok.nextToken();
        cnt++;
      }
    }
  }
}

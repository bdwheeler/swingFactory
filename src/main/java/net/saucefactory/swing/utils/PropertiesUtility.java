package net.saucefactory.swing.utils;

import java.util.Properties;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.Date;

public class PropertiesUtility {
  public PropertiesUtility() {
  }

  public static void saveArrayToProperties(String fileName, Object[] objects) throws Exception {
    try {
      Properties p = new Properties();
      int propCount = 0;
      String className = "";
      if(objects != null && objects.length > 0) {
        propCount = objects.length;
        Field[] fields = objects[0].getClass().getFields();
        className = objects[0].getClass().getName();
        if(fields != null) {
          for(int i = 0; i < objects.length; i++) {
            Object tmpObj = objects[i];
            for(int j = 0; j < fields.length; j++) {
              Object tmpProp = tmpObj.getClass().getField(fields[j].getName()).get(tmpObj);
              if(tmpProp == null)
                tmpProp = "";
              p.put(fields[j].getName() + "_" + i, objectToString(tmpProp));
            }
          }
        }
      }
      p.put("rowCount", String.valueOf(propCount));
      p.put("objectClass", className);
      FileUtility.writePropertiesFileToDisk(fileName, p);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public static Object[] loadPropertiesToArray(String fileName) throws Exception {
    try {
      Properties p = FileUtility.readPropertiesFileFromDisk(fileName);
      if(p == null)
        return null;
      String className = p.getProperty("objectClass");
      int rowCount = Integer.parseInt(p.getProperty("rowCount"));
      Class objectClass = Class.forName(className);
      Object[] rtnArray = (Object[])Array.newInstance(objectClass, rowCount);
      Field[] fields = objectClass.getFields();
      if(fields != null) {
        for(int i = 0; i < rtnArray.length; i++) {
          Object tmpObj = objectClass.newInstance();
          for(int j = 0; j < fields.length; j++) {
            String tmpVal = p.getProperty(fields[j].getName() + "_" + i);
            if(tmpVal != null) {
              Class fieldClass = fields[j].getType();
              Object tmpObjVal = getObjectValue(fieldClass, tmpVal);
              if(fieldClass == boolean.class)
                tmpObj.getClass().getField(fields[j].getName()).setBoolean(tmpObj, ((Boolean)tmpObjVal).booleanValue());
              else if(fieldClass == int.class)
                tmpObj.getClass().getField(fields[j].getName()).setInt(tmpObj, ((Integer)tmpObjVal).intValue());
              else if(fieldClass == float.class)
                tmpObj.getClass().getField(fields[j].getName()).setFloat(tmpObj, ((Float)tmpObjVal).floatValue());
              else if(fieldClass == double.class)
                tmpObj.getClass().getField(fields[j].getName()).setDouble(tmpObj, ((Double)tmpObjVal).doubleValue());
              else if(fieldClass == long.class)
                tmpObj.getClass().getField(fields[j].getName()).setLong(tmpObj, ((Long)tmpObjVal).longValue());
              else
                tmpObj.getClass().getField(fields[j].getName()).set(tmpObj, tmpObjVal);
            }
          }
          rtnArray[i] = tmpObj;
        }
      }
      return rtnArray;
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public static String objectToString(Object val) {
    if(val == null)
      return null;
    if(val instanceof Boolean)
      return encodeBoolean(((Boolean)val).booleanValue());
    return String.valueOf(val);
  }

  public static Object getObjectValue(Class objClass, String value) {
    if(objClass == String.class)
      return value;
    else if(objClass == Integer.class || objClass == int.class)
      return new Integer(value);
    else if(objClass == Double.class || objClass == double.class)
      return new Double(value);
    else if(objClass == Long.class || objClass == long.class)
      return new Long(value);
    else if(objClass == Float.class || objClass == float.class)
      return new Float(value);
    else if(objClass == Date.class)
      return new Date(value);
    else if(objClass == Boolean.class || objClass == boolean.class)
      return new Boolean(decodeBoolean(value));
    else
      return value;
  }

  private static String encodeBoolean(boolean val) {
    return val ? "true" : "false";
  }

  private static boolean decodeBoolean(String val) {
    if(val == null || !val.equals("true"))
      return false;
    return true;
  }
}

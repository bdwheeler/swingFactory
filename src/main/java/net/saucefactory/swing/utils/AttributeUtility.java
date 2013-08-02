package net.saucefactory.swing.utils;

import java.util.Date;

public class AttributeUtility {
  public AttributeUtility() {
  }

  public static String getAttributeValue(Object att) {
    if(att == null)
      return null;
    else if(att instanceof String)
      return (String)att;
    if(att instanceof Date)
      return DataUtility.dateToDisplayString((Date)att);
    if(att instanceof Boolean)
      return ((Boolean)att).booleanValue() ? "Yes" : "No";
    return String.valueOf(att);
  }

  public static String getAttributeValue(Object att, String defaultStr) {
    String rtn = getAttributeValue(att);
    if(rtn == null || rtn.equals(""))
      return defaultStr;
    return rtn;
  }

  public static String getStringAttributeValue(Object att) {
    if(att == null)
      return "";
    else if(att instanceof String)
      return (String)att;
    if(att instanceof Date)
      return DataUtility.dateToDisplayString((Date)att);
    if(att instanceof Boolean)
      return ((Boolean)att).booleanValue() ? "Yes" : "No";
    return String.valueOf(att);
  }

  public static String getStringValue(String att) {
    if(att == null)
      return "";
    return att;
  }
  }

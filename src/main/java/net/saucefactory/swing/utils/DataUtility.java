package net.saucefactory.swing.utils;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.util.*;
import java.text.*;

public class DataUtility
{
  public static final String DATE_PATTERN_DISPLAY = "MM/dd/yyyy HHmm";
  public static final String DATE_PATTERN_DISPLAY_NOTIME = "MM/dd/yyyy";
  public static final String DATE_PATTERN_STORE = "yyyyMMdd'T'HHmmss.SSS'Z'";
  public static final String DATE_PATTERN_STORE_TIMEZONE_GMT = "yyyyMMdd'T'HHmmss.SSS'-0000'";
  public static final String DATE_PATTERN_STORE_TIMEZONE_PST = "yyyyMMdd'T'HHmmss.SSS'-0800'";
  public static final String DATE_PATTERN_REPORTING = "MM'/'dd'/'yyyy HH':'mm";
  public static final String DATE_PATTERN_SAVE = "yyyy'_'MM'_'dd'_'HHmmss";
  public static final String TIME_PATTERN_DISPLAY = "HHmm";
  private static final String DATE_SUBSTITUTION = "01/01/2002 0000";
  private static final String DATE_SUBSTITUTION_DAY_END = "01/01/2002 2359";
  private static final String DATE_SUBSTITUTION_REPORT_DAY_END = "01/01/2002 23:59";
  public static Calendar calendar;
  public static Calendar calendarGMT;
  public static SimpleDateFormat dateFormat;
  public static SimpleDateFormat dateFormatGMT;
  public static DecimalFormat decimalFormat;

  static {
    calendarGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    calendar = Calendar.getInstance();
    dateFormat = new SimpleDateFormat();
    dateFormat.setCalendar(calendar);
    dateFormatGMT = new SimpleDateFormat();
    dateFormatGMT.setCalendar(calendarGMT);
    decimalFormat = new DecimalFormat("#,##0.0#");
  }

  private DataUtility()
  {
  }

  public static String getDoubleString(double dbl)
  {
    return decimalFormat.format(dbl);
  }

  public static Date stringToDate(String dateStr)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      return dateFormat.parse(dateStr);
    }
    catch(Exception e){return null;}
  }

  public static Date stringToDayEndDate(String dateStr)
  {
    try
    {
      int length = dateStr.length();
      if(length < 15)
	dateStr = dateStr + DATE_SUBSTITUTION_DAY_END.substring(length);
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      return dateFormat.parse(dateStr);
    }
    catch(Exception e){return null;}
  }

  public static Date stringNoTimeToDate(String dateStr)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY_NOTIME);
      return dateFormat.parse(dateStr);
    }
    catch(Exception e){return null;}
  }

  public static String dateToDBString(Date date)
  {
    try
    {
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      return dateFormat.format(date);
    }
    catch(Exception e){return null;}
  }

  public static String dateToDisplayString(Date date)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      return dateFormat.format(date);
    }
    catch(Exception e){return null;}
  }

  public static String dateToDiaplayTimeString(Date date)
  {
    try {
      dateFormat.applyPattern(TIME_PATTERN_DISPLAY);
      return dateFormat.format(date);
    }
    catch(Exception e) {
      return "";
    }
  }

  public static String dateToSaveString(Date date)
  {
    try {
      dateFormat.applyPattern(DATE_PATTERN_SAVE);
      return dateFormat.format(date);
    }
    catch(Exception e){return null;}
  }

  public static String dateToNoTimeString(Date date)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY_NOTIME);
      return dateFormat.format(date);
    }
    catch(Exception e){return null;}
  }

  public static Date dbStringToDate(String dateStr)
  {
    try
    {
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      return dateFormatGMT.parse(dateStr);
    }
    catch(Exception e){return null;}
  }

  public static String dbStringToDisplayString(String dateStr)
  {
    try
    {
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      Date tempDate = dateFormatGMT.parse(dateStr);
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      return dateFormat.format(tempDate);
    }
    catch(Exception e){return null;}
  }

  public static String dbStringToDisplayStringNoTime(String dateStr)
  {
    try
    {
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      Date tempDate = dateFormatGMT.parse(dateStr);
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY_NOTIME);
      return dateFormat.format(tempDate);
    }
    catch(Exception e){return null;}
  }


  public static String dbStringTimezoneToDisplayString(String dateStr)
  {
    try
    {
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE_TIMEZONE_GMT);
      Date tempDate = dateFormatGMT.parse(dateStr);
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      return dateFormat.format(tempDate);
    }
    catch(Exception e){return null;}
  }

  public static String displayStringToDBString(String dateStr)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      Date tempDate = dateFormat.parse(dateStr);
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      return dateFormatGMT.format(tempDate);
    }
    catch(Exception e){return null;}
  }

  public static String displayStringNoTimeToDBString(String dateStr)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY_NOTIME);
      Date tempDate = dateFormat.parse(dateStr);
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE);
      return dateFormatGMT.format(tempDate);
    }
    catch(Exception e){return null;}
  }

  public static String displayStringToDBStringTimezone(String dateStr)
  {
    try
    {
      dateFormat.applyPattern(DATE_PATTERN_DISPLAY);
      Date tempDate = dateFormat.parse(dateStr);
      dateFormatGMT.applyPattern(DATE_PATTERN_STORE_TIMEZONE_GMT);
      return dateFormatGMT.format(tempDate);
    }
    catch(Exception e){return null;}
  }

  public static Date addDaysToDate(Date date, int days)
  {
    try
    {
      Calendar tmpCalendar = Calendar.getInstance();
      tmpCalendar.setTime(date);
      tmpCalendar.add(Calendar.DAY_OF_YEAR, days);
      return tmpCalendar.getTime();
    }
    catch(Exception e){return date;}
  }

  public static Date addBizDaysToDate(Date date, int days)
  {
    try {
      Calendar tmpCalendar = Calendar.getInstance();
      tmpCalendar.setTime(date);
      tmpCalendar.add(Calendar.DAY_OF_YEAR, days);
      int tmpDayofWeek = tmpCalendar.get(Calendar.DAY_OF_WEEK);
      int adjust = 0;
      if(tmpDayofWeek == Calendar.SATURDAY)
        adjust = 2;
      else if(tmpDayofWeek == Calendar.SUNDAY)
        adjust = 1;
      if(adjust > 0)
        tmpCalendar.add(Calendar.DAY_OF_YEAR, adjust);
      return tmpCalendar.getTime();
    }
    catch(Exception e){return date;}
  }

  public static Date stripTimeFromDate(Date date)
  {
    try {
      Calendar tmpCalendar = Calendar.getInstance();//Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
      tmpCalendar.setTime(date);
      tmpCalendar.set(Calendar.MINUTE, 0);
      tmpCalendar.set(Calendar.HOUR_OF_DAY, 0);
      return tmpCalendar.getTime();
    }
    catch(Exception e){return date;}
  }

  public static Date addMinutesToDate(Date date, int minutes)
  {
    try {
      Calendar tmpCalendar = Calendar.getInstance();
      tmpCalendar.setTime(date);
      tmpCalendar.add(Calendar.MINUTE, minutes);
      return tmpCalendar.getTime();
    }
    catch(Exception e) {
      return date;
    }
  }

  public static Date addHoursToDate(Date date, int hours)
  {
    try {
      Calendar tmpCalendar = Calendar.getInstance();
      tmpCalendar.setTime(date);
      tmpCalendar.add(Calendar.HOUR_OF_DAY, hours);
      return tmpCalendar.getTime();
    }
    catch(Exception e) {
      return date;
    }
  }

  public static String stringToDBTimeString(String str)
  {
    if(str.length() == 3)
      return "0" + str + "00.000Z";
    else if(str.length() == 4)
      return str + "00.000Z";
    else
      return "000000.000Z";
  }

  public static String dbTimeStringToString(String str)
  {
    try {
      return str.substring(0, 4);
    }
    catch(Exception e) {
      return "";
    }
  }

  public static String dbPreparedTimeStringToString(String str)
  {
    try {
      return str.substring(1, 5);
    }
    catch(Exception e) {
      return "";
    }
  }

  public static String getReportingDateString(Date date)
  {
    try {
      dateFormatGMT.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormatGMT.format(date);
    }
    catch(Exception e) {
      return null;
    }
  }

  public static String getReportingDateDisplayString(Date date)
  {
    try {
      dateFormat.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormat.format(date);
    }
    catch(Exception e) {
      return null;
    }
  }

  public static Date reportingStringToDate(String dateStr)
  {
    try {
      dateFormatGMT.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormatGMT.parse(dateStr);
    } catch(Exception e) {
      return null;
    }
  }

  public static Date reportingStringToDisplayDate(String dateStr)
  {
    try {
      dateFormat.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormat.parse(dateStr);
    } catch(Exception e) {
      return null;
    }
  }

  public static String getReprotingEndOfDayString(Date date)
  {
    try {

      Calendar tmpCal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
      tmpCal.setTime(date);
      tmpCal.add(Calendar.DAY_OF_YEAR, 1);
      tmpCal.add(Calendar.MINUTE, -1);
      date = calendar.getTime();
      dateFormatGMT.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormatGMT.format(date);
    }
    catch(Exception e) {
      return null;
    }
  }

  public static int getCurrentHour()
  {
    try {
      Calendar tmpCal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
      calendar.setTime(new Date());
      return tmpCal.get(Calendar.HOUR_OF_DAY);
    }
    catch(Exception e) {
      return -1;
    }
  }

  public static Date getCurrentHourGMTDate()
  {
    try {
      Calendar tmpCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      tmpCal.setTime(new Date());
      tmpCal.set(Calendar.MINUTE, 0);
      tmpCal.set(Calendar.SECOND, 0);
      return tmpCal.getTime();
    }
    catch(Exception e) {
      return null;
    }
  }

  public static Date getStartOfDayDate()
  {
    try {
      Calendar tmpCal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
      tmpCal.setTime(new Date());
      tmpCal.set(Calendar.HOUR_OF_DAY, 0);
      tmpCal.set(Calendar.MINUTE, 0);
      tmpCal.set(Calendar.SECOND, 0);
      return tmpCal.getTime();
    }
    catch(Exception e) {
      return null;
    }
  }

  public static Date getEndOfYearDate() {
    return getEndOfYearDate(new Date());
  }

  public static Date getEndOfYearDate(Date startDate)
  {
    try {
      Calendar tmpCal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
      tmpCal.setTime(startDate);
      tmpCal.set(Calendar.DAY_OF_YEAR, 1);
      tmpCal.set(Calendar.HOUR_OF_DAY, 0);
      tmpCal.set(Calendar.MINUTE, 0);
      tmpCal.set(Calendar.SECOND, 0);
      tmpCal.add(Calendar.YEAR, 1);
      tmpCal.add(Calendar.MINUTE, -1);
      return tmpCal.getTime();
    }
    catch(Exception e) {
      return null;
    }
  }

  public static Date getDateFromReportingString(String tmpStr)
  {
    try {
      dateFormatGMT.applyPattern(DATE_PATTERN_REPORTING);
      return dateFormatGMT.parse(tmpStr);
    }
    catch(Exception e) {
      return null;
    }
  }

  public static Date getDateFromDateGMT(Date date)
  {
    try {
      calendar.setTime(date);
      return calendar.getTime();
    }
    catch(Exception e) {
      return date;
    }
  }

  private static int changeStringToInt(String val)
  {
    int rtnInt = -1;
    if(val != null && !val.equals("")) {
      if(val.indexOf('.') > -1)
	val = val.substring(0, val.indexOf('.'));
      rtnInt = Integer.parseInt(val);
    }
    return rtnInt;
  }

  public static String getHEString(int heInt) {
    String rtnStr;
    if(heInt < 0)
      rtnStr = "0000";
    else if(heInt < 10)
      rtnStr = "0" + heInt + "00";
    else
      rtnStr = heInt + "00";
    return rtnStr;
  }

  public static void main(String args[]) {
    try {
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

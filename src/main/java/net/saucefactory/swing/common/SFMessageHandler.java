package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class SFMessageHandler
{
  private static boolean sendErrorMail = false;
  private static SFMessageHandler instance = new SFMessageHandler();

  private SFMessageHandler()
  {
  }

  public static SFMessageHandler getHandle()
  {
    return instance;
  }

  public static void setSendErrorMail(boolean sendErrorMail)
  {
    sendErrorMail = sendErrorMail;
  }

  public static void sendInformationMessage(Component parentComponent, String message, String title)
  {
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected event occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void sendErrorMessage(Component parentComponent, String message, String title)
  {
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected error occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public static void sendWarningMessage(Component parentComponent, String message, String title)
  {
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected warning occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
  }

  /*Replace with a error message.
  public static void sendRemoteObjectErrorMessage(Component parentComponent, java.rmi.RemoteException e)
  {
    String errStr = e.getMessage();
    if(errStr != null && errStr.equalsIgnoreCase("no such object in table"))
      JOptionPane.showMessageDialog(parentComponent, "Warning: Your connection with the server has been reset.  Please retry your last action.\nIf this problem persists, please contact system support with message ID: 203.", "Application Warning", JOptionPane.ERROR_MESSAGE);
    else if(errStr != null && (errStr.indexOf("to host") > -1))
      JOptionPane.showMessageDialog(parentComponent, "Warning: The server is temporarily unavailable.  Please retry your last action.\nIf this problem persists, please contact system support with message ID: 202.", "Application Warning", JOptionPane.ERROR_MESSAGE);
    else if(errStr != null && (errStr.indexOf("Connection refused") > -1))
      JOptionPane.showMessageDialog(parentComponent, "Warning: The server is temporarily unavailable.  Please retry your last action.\nIf this problem persists, please contact system support with message ID: 202.", "Application Warning", JOptionPane.ERROR_MESSAGE);
    else
      JOptionPane.showMessageDialog(parentComponent, "Warning: An unexplained error occured while contacting the server.  Please retry your last action.\nIf this problem persists, please contact system support with message ID: 201.", "Application Warning", JOptionPane.ERROR_MESSAGE);
  }
  */

  public static void sendInformationMessage(Component parentComponent, Exception e, String title)
  {
    String message = e.getMessage();
    if(sendErrorMail && (message == null || message.equalsIgnoreCase("")))
    {
      String mailMessage = formatErrorMessage(e, title);
      SFErrorNotificationHandler.sendAllert(mailMessage);
    }
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected event occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void sendErrorMessage(Component parentComponent, Exception e, String title)
  {
    e.printStackTrace();
    String message = e.getMessage();
    if(sendErrorMail && (message == null || message.equalsIgnoreCase("")))
    {
      message = "An unexpected error occured!\nPlease contact support with details";
      String mailMessage = formatErrorMessage(e, title);
      SFErrorNotificationHandler.sendAllert(mailMessage);
    }
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected error occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public static void sendWarningMessage(Component parentComponent, Exception e, String title)
  {
    String message = e.getMessage();
    if(sendErrorMail && (message == null || message.equalsIgnoreCase("")))
    {
      String mailMessage = formatErrorMessage(e, title);
      SFErrorNotificationHandler.sendAllert(mailMessage);
    }
    if(message == null || message.equalsIgnoreCase(""))
      message = "An unexpected warning occured!\nPlease contact support with details";
    JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
  }

  private static String getStackTrace(Exception e)
  {
    try
    {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(out);
      e.printStackTrace(ps);
      return out.toString();
    }
    catch(Exception exc)
    {
      return "";
    }
  }

  private static String formatErrorMessage(Exception e, String errorFrameTitle)
  {
    try
    {
      StringBuffer buf = new StringBuffer();
      if(errorFrameTitle != null)
  buf.append("Error Frame Title: " + errorFrameTitle + "\n");
      if(e.getMessage() != null)
  buf.append("Exception Message: " + e.getMessage() + "\n");
      String stackTrace = getStackTrace(e);
      buf.append("Stack Trace: " + stackTrace);
      return buf.toString();
    }
    catch(Exception exc)
    {
      return "An Error occured building error message";
    }
  }

  public static boolean confirmContinueMessage(Component parent, String message)
  {
    int rtnInt = JOptionPane.showConfirmDialog(parent, message, "Confirmation", JOptionPane.YES_NO_OPTION);
    return (rtnInt == 0);
  }

  public static int confirmContinueCancelMessage(Component parent, String message)
  {
    return JOptionPane.showConfirmDialog(parent, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
  }

  public static String getPasswordMessage(Component parent, String message) {
    return JOptionPane.showInputDialog(parent, message, "Enter Password", JOptionPane.OK_CANCEL_OPTION);
  }

  public static String getTextValueMessage(Component parent, String message, String title) {
    return JOptionPane.showInputDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION);
  }

  public static void sendSystemOut(String s)
  {
    System.out.println(s);
  }

  public static void main(String[] args) {
    String tmp = SFMessageHandler.getPasswordMessage(null, "hello");
    System.out.println(tmp);
  }
}

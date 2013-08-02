package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SFErrorNotificationHandler
{
  private static String notificationAddress = null;
  private static String mailServer = null;
  private static SFErrorNotificationHandler instance = new SFErrorNotificationHandler();

  private SFErrorNotificationHandler()
  {
  }

  private SFErrorNotificationHandler getHandle() {
    return instance;
  }

  public void setMailServer(String mailServer)
  {
    this.mailServer = mailServer;
  }

  public void setNotificationAddress(String notificationAddress)
  {
    this.notificationAddress = notificationAddress;
  }

  public static void sendAllert(String message) {
    try {
      if(notificationAddress != null && mailServer != null) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", mailServer);
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
        Message tempMessage = new MimeMessage(session);
        tempMessage.setFrom(new InternetAddress("ouch.sfgiants.com"));
        tempMessage.setSubject("CALL CENTER ERROR!");
        tempMessage.setHeader("X-Mailer", "msgsend");
        InternetAddress[] addresses = {
            new InternetAddress(notificationAddress)};
        tempMessage.setRecipients(Message.RecipientType.TO, addresses);
        tempMessage.setSentDate(new Date());
        tempMessage.setText(message);
        Transport.send(tempMessage);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}

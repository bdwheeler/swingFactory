package net.saucefactory.swing.common;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;
import javax.swing.JTextField;

public class SFEmailFilter extends PlainDocument {
  public static final String start = "bob@bob.com";
  public static final String mid = "bob.com";
  public static final String end = "bob";
  public static final String mailRE = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$";

  //"(?i)[a-z0-9\\-]+@(?:[a-z0-9\\-]+(?:\\.[a-z]{2,3}){1,2}|\\[(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}\\])";

  public SFEmailFilter() {}

  public  static boolean chkMail(String source){
    return source.matches(mailRE);
  }

  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    String current = getText(0, offset);
    String check = current + str;
    int tmpAt = check.indexOf("@");
    if(tmpAt < 0)
      check = check + start;
    else if(current.indexOf(".", tmpAt) < 0)
      check = check + mid;
    else
      check = check + end;
    if(chkMail(check))
      super.insertString(offset, str, a);
    else
      beep();
  }

  private void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public static void install(JTextField textField) {
    SFEmailFilter filter = new SFEmailFilter();
    textField.setDocument(filter);
  }
}

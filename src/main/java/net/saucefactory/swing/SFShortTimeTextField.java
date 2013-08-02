package net.saucefactory.swing;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CAISO</p>
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import net.saucefactory.text.SFShortTimeFilter;
import java.util.Date;

public class SFShortTimeTextField extends JTextField {
  SFShortTimeFilter filter = new SFShortTimeFilter(true);

  public SFShortTimeTextField() {
    filter.install(this, false);
  }

  public void setTime(Date date) {
    setTime(filter.sdfLocal.format(date));
  }

  public void setTime(String time) {
    try {
      if(time.length() == 4)
        time = time.substring(0, 2) + ":" + time.substring(2);
      setText(time);
    }
    catch(Exception e) {
      setText(time); //bad time.
    }
  }

  public String getTime() {
    String rtnStr = getText();
    rtnStr = rtnStr.replaceFirst(":", "");
    return rtnStr;
  }
}

package net.saucefactory.swing.common;

import java.util.Comparator;
import java.io.File;

public class FileNameComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    try {
      int rtn = 0;
      if(o1 == null)
        rtn = o2 == null ? 0 : -1;
      else if(o2 == null)
        rtn = 1;
      String tmp1 = o1 == null ? null : ((File)o1).getName();
      String tmp2 = o2 == null ? null : ((File)o2).getName();
      if(tmp1 == null)
        rtn = tmp2 == null ? 0 : -1;
      else if(tmp2 == null)
        rtn = 1;
      else
        rtn = tmp1.compareToIgnoreCase(tmp2);
      return rtn;
    }
    catch(Exception e) {
      e.printStackTrace();
      throw new ClassCastException();
    }
  }
}

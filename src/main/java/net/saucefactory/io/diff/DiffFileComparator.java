package net.saucefactory.io.diff;

import java.util.Comparator;

public class DiffFileComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    try {
      int rtn = 0;
      if(o1 == null)
        rtn = o2 == null ? 0 : -1;
      else if(o2 == null)
        rtn = 1;
      boolean srcDir = o1 == null ? false : ((DiffFile)o1).isDirectory();
      boolean tgtDir = o2 == null ? false : ((DiffFile)o2).isDirectory();
      String tmp1 = o1 == null ? null : ((DiffFile)o1).getName();
      String tmp2 = o2 == null ? null : ((DiffFile)o2).getName();
      if(srcDir && !tgtDir)
        return -1;
      if(tgtDir && !srcDir)
        return 1;
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

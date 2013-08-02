package net.saucefactory.swing.table;

import java.util.Comparator;
import net.saucefactory.swing.data.SFTableColumnViewState;
import java.util.Arrays;

public class SFTableUtility {

  public static ViewStateComparator viewComparator = new ViewStateComparator();

  public SFTableUtility() {
  }

  public static int hasColumn(SFTableColumn tgtColumn, SFTableColumn[] searchList) {
    if(searchList != null && tgtColumn != null) {
      for(int i = 0; i < searchList.length; i++) {
        if(searchList[i] == tgtColumn)
          return i;
      }
    }
    return -1;
  }

  public static int hasColumn(int tgtColumnId, SFTableColumn[] searchList) {
    if(searchList != null) {
      for(int i = 0; i < searchList.length; i++) {
        if(searchList[i].columnID == tgtColumnId)
          return i;
      }
    }
    return -1;
  }

  public static void sortColumnViewStates(SFTableColumnViewState[] states) {
    Arrays.sort(states, viewComparator);
  }

  static class ViewStateComparator implements Comparator {

    public int compare(Object obj1, Object obj2) {
      if(obj1 == null)
        return obj2 == null ? 0 : -1;
      if(obj2 == null)
        return 1;
      int view1 = ((SFTableColumnViewState)obj1).viewIndex;
      int view2 = ((SFTableColumnViewState)obj2).viewIndex;
      if(view1 == view2)
        return 0;
      return view1 > view2 ? 1 : -1;
    }
  }
}

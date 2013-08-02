package net.saucefactory.swing.combo;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.util.Comparator;
import net.saucefactory.swing.data.ISFDataAdapter;

public class SFComboBoxItemToStringComparator implements Comparator {
  private ISFDataAdapter dataAdapter;

  public SFComboBoxItemToStringComparator(ISFDataAdapter dataAdapter) {
    this.dataAdapter = dataAdapter;
  }

  public int compare(Object o1, Object o2) {
    try {
      return dataAdapter.getValue(o1).toString().toUpperCase().compareToIgnoreCase(((String)o2).
          toUpperCase());
    }
    catch(Exception e) {
      e.printStackTrace();
      throw new ClassCastException();
    }
  }
}

package net.saucefactory.swing.combo;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.lang.reflect.*;

public class SFComboBoxFieldDisplayItem extends SFAbstractComboBoxDisplayItem {
  Field field = null;

  public SFComboBoxFieldDisplayItem(Field fld) {
    type = this.FIELD;
    value = fld;
    field = fld;
  }

}
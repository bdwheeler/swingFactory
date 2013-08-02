package net.saucefactory.swing.combo;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.lang.reflect.*;

public class SFComboBoxMethodDisplayItem extends SFAbstractComboBoxDisplayItem {
  Method method = null;

  public SFComboBoxMethodDisplayItem(Method mthd) {
    type = this.METHOD;
    value = mthd;
    method = mthd;
  }

}
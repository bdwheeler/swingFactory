package net.saucefactory.swing.combo;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public abstract class SFAbstractComboBoxDisplayItem {
  public static final int LITERAL = 0;
  public static final int FIELD = 1;
  public static final int STRING = 2;
  public static final int METHOD = 3;
  public static final int OBJECT = 4;
  public int type = 0;
  public Object value = null;
}

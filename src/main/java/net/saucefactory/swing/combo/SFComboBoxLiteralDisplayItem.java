package net.saucefactory.swing.combo;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SFComboBoxLiteralDisplayItem extends SFAbstractComboBoxDisplayItem {
  String literal = null;

  public SFComboBoxLiteralDisplayItem(String literal_) {
    type = this.LITERAL;
    value = literal_;
    literal = literal_;
  }
}
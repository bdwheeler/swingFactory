package net.saucefactory.swing.table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.Color;
import javax.swing.JLabel;
import net.saucefactory.swing.renderers.SFStringRenderer;

public class SFNameValueIndentTable extends SFObjectTable {
  public static final int INDENT_MULTIPLIER = 5;

  public static Color BG_COLOR = Color.white;

  public static SFObjectTableColumn[] headers;

  public SFNameValueIndentTable() {
    initHeaders();
    setTableColumns(headers);
  }

  public SFNameValueIndentTable(Color background) {
    BG_COLOR = background;
    initHeaders();
    setTableColumns(headers);
  }

  private void initHeaders() {
    headers = new SFObjectTableColumn[] {
      new SFObjectTableColumn(0, "Item", 90, "name", JLabel.LEFT, false, BG_COLOR, Color.black, new SFStringRenderer(), null),
      new SFObjectTableColumn(1, "Value", 90, "value", JLabel.LEFT, false, BG_COLOR, Color.black, new SFStringRenderer(), null)};
  }
}
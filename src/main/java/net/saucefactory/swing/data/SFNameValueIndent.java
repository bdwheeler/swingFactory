package net.saucefactory.swing.data;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SFNameValueIndent {
  public String name = "";
  public String value = "";
  public int indent = 0;

  public SFNameValueIndent() {}
  public SFNameValueIndent(String name, String value, int indent) {
    this.name = name;
    this.value = value;
    this.indent = indent;
  }
}
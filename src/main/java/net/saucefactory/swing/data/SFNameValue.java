package net.saucefactory.swing.data;

public class SFNameValue {

  public String name = "";
  public Object value = null;
  public int indent = 0;
  public int status = 0;

  public SFNameValue() {}

  public SFNameValue(String name, Object value) {
    this(name, value, 0);
  }

  public SFNameValue(String name, Object value, int indent) {
    this(name, value, indent, 0);
  }

  public SFNameValue(String name, Object value, int indent, int status) {
    this.name = name;
    this.value = value;
    this.indent = indent;
    this.status = status;
  }
}

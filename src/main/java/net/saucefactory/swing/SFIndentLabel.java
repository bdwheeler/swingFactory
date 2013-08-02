package net.saucefactory.swing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.Graphics;

public class SFIndentLabel extends SFSpeedLabel {
  int indent = 0;

  public SFIndentLabel() {
  }

  public SFIndentLabel(String text, int indent) {
    setText(text);
    this.indent = indent;
  }

  public void setIndent(int indent) {
    this.indent = indent;
  }

  public int getIndent() {
    return indent;
  }

  public void paint(Graphics g) {
    g.translate(indent, 0);
    super.paint(g);
    g.translate(-indent, 0);
  }

}
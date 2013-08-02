package net.saucefactory.swing;

import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.UIManager;

public class SFTitleLabel extends JLabel {

  private static Font newFont = null;

  private void initFont() {
    Font _font = getFont();
    newFont = new Font(_font.getName(), Font.BOLD, _font.getSize());
  }

  public SFTitleLabel() {
    this("", null, JLabel.LEFT);
  }

  public SFTitleLabel(Icon image) {
    this("", image, JLabel.LEFT);
  }

  public SFTitleLabel(Icon image, int horizontalAllignment) {
    this("", image, horizontalAllignment);
  }

  public SFTitleLabel(String text, int horizontalAllignment) {
    this(text, null, horizontalAllignment);
  }

  public SFTitleLabel(String text) {
    this(text, null, JLabel.LEFT);
  }

  public SFTitleLabel(String text, Icon image, int horizontalAllignment) {
    super(text, image, horizontalAllignment);
    if(newFont == null)
      initFont();
    setFont(newFont);
  }
}

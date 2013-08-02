package net.saucefactory.swing.common;

import javax.swing.*;
import java.awt.event.*;

public class SFEnterButtonTextField extends JTextField {

  private JButton tgtButton = null;

  public SFEnterButtonTextField() {
    super();
    setKeyBindings();
  }

  public SFEnterButtonTextField(JButton tgtButton) {
    super();
    this.tgtButton = tgtButton;
    setKeyBindings();
  }

  public SFEnterButtonTextField(String text, JButton tgtButton) {
    super(text);
    this.tgtButton = tgtButton;
    setKeyBindings();
  }

  public SFEnterButtonTextField(String text) {
    super(text);
    setKeyBindings();
  }

  public void setTargetButton(JButton tgtButton) {
    this.tgtButton = tgtButton;
  }

  public void setKeyBindings() {
    KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    Action action = new AbstractAction() {
      public void actionPerformed( ActionEvent e ) {
        if(tgtButton != null)
          tgtButton.doClick(10);
      }
    };
    InputMap inputMap = this.getInputMap(this.WHEN_FOCUSED);
    ActionMap actionMap = this.getActionMap();
    inputMap.put(enterKeyStroke, "MyEnter");
    actionMap.put("MyEnter", action);
  }
}

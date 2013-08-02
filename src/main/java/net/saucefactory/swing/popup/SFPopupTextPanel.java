package net.saucefactory.swing.popup;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.saucefactory.swing.buttons.SFArrowButton;
import net.saucefactory.swing.SFTextViewInternalFrame;

public class SFPopupTextPanel extends JPanel implements ActionListener
{
  private static final String POPUP_COMMAND = "POPUP";
  private String popupTitle = "";
  private BorderLayout mainLayout = new BorderLayout();
  private JTextField textField = new JTextField();
  private SFArrowButton popupButton = new SFArrowButton(SFArrowButton.SOUTH);

  public SFPopupTextPanel()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public String getText(){return textField.getText();}
  public void setText(String text){textField.setText(text);}
  public void setPopupTitle(String popupTitle){this.popupTitle = popupTitle;}

  private void jbInit() throws Exception
  {
    this.setLayout(mainLayout);
    popupButton.setActionCommand(POPUP_COMMAND);
    popupButton.addActionListener(this);
    add(textField, BorderLayout.CENTER);
    add(popupButton, BorderLayout.EAST);
  }

  public void actionPerformed(ActionEvent ae)
  {
    String command = ae.getActionCommand();
    if(command.equals(POPUP_COMMAND))
    {
      SFTextViewInternalFrame viewFrame = new SFTextViewInternalFrame(popupTitle, getText());
      SwingUtilities.getWindowAncestor(this).add(viewFrame);//, "EditPopup", 600, 500, 2, true);
      viewFrame.startModal();
      if(viewFrame.isProceed())
	setText(viewFrame.getText());
    }
  }
}
package net.saucefactory.swing;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import net.saucefactory.swing.common.SFMessageHandler;

public class SFTextViewInternalFrame extends SFModalInternalFrame implements ActionListener
{
  //Layouts
  private BorderLayout mainBorderLayout = new BorderLayout();
  private GridBagLayout centerLayout = new GridBagLayout();
  //Panels
  private JPanel centerPanel = new JPanel();
  private JScrollPane textScrollPane = new JScrollPane();
  //Labels
  private JLabel titleLabel = new JLabel();
  //Text
  public JTextArea textArea = new JTextArea();
  //Buttons
  public JButton closeButton = new JButton();
  public JButton saveButton = new JButton();
  boolean editable = true;
  String oldText;

  public SFTextViewInternalFrame()
  {
    this("", "", true);
  }

  public SFTextViewInternalFrame(String title, String text)
  {
    this(title, text, true);
  }

  public SFTextViewInternalFrame(String title, String text, boolean editable)
  {
    setTitle(title);
    setText(text);
    oldText = text;
    this.editable = editable;
    try {
      jbInit();
      textArea.setCaretPosition(0);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setTitle(String title)
  {
    titleLabel.setText(title);
  }

  public void setText(String text)
  {
    textArea.setText(text);
  }

  public String getText()
  {
    return textArea.getText();
  }

  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(mainBorderLayout);
    this.setIconifiable(true);
    this.setMaximizable(true);
    this.setResizable(true);
    this.setClosable(true);
    this.setMinimumSize(new Dimension(400, 350));
    super.setTitle("SLIC Text Editor");
    centerPanel.setLayout(centerLayout);
    textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    textScrollPane.setMinimumSize(new Dimension(300, 200));
    closeButton.setMinimumSize(new Dimension(100, 25));
    closeButton.setPreferredSize(new Dimension(100, 25));
    closeButton.setActionCommand("CLOSE");
    closeButton.setText("Cancel");
    closeButton.addActionListener(this);
    saveButton.setMinimumSize(new Dimension(100, 25));
    saveButton.setPreferredSize(new Dimension(100, 25));
    saveButton.setActionCommand("SAVE");
    saveButton.setText("Ok");
    saveButton.addActionListener(this);
    saveButton.setEnabled(editable);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(editable);
    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
    centerPanel.add(textScrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
	    ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    textScrollPane.getViewport().add(textArea, null);
    centerPanel.add(titleLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
	    ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
    centerPanel.add(closeButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
	    ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    centerPanel.add(saveButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
	    ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
  }

  public void actionPerformed(ActionEvent ae)
  {
    String command = ae.getActionCommand();
    if(command.equals("CLOSE")) {
      boolean close = true;
      if(editable && !getText().equals(oldText))
	close = SFMessageHandler.confirmContinueMessage(this, "Are you sure you want to close and lose changes?");
      if(close)
	closeMe();
    }
    else if(command.equals("SAVE"))
    {
      proceed = true;
      closeMe();
    }
  }

  public void doDefaultCloseAction() {
    if(getDefaultCloseOperation() == DISPOSE_ON_CLOSE)
      actionPerformed(new ActionEvent(this, 999, "CLOSE"));
    else
      super.doDefaultCloseAction();
  }
}
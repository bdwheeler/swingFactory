package net.saucefactory.swing.combo;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.awt.*;
import net.saucefactory.text.SFAllUpperCaseDocument;

public class SFPredictiveTextField extends JTextField {
  protected ISFPredictiveSearch predictiveSearch;
  private String lastPrediction = "";
  private String lastReplaceContent = "";

  public SFPredictiveTextField() {
    this(0, null);
  }

  public SFPredictiveTextField(int columns) {
    this(columns, null);
  }

  public SFPredictiveTextField(int columns, ISFPredictiveSearch searchImpl) {
    super(columns);
    setPredictiveSearch(searchImpl);
    addClearSelectionActionListener();
    addClearSelectionFocusListener();
    setBackground((Color)UIManager.get("ComboBox.background"));
    //addMouseListener(new TemporaryMouseListener());
    if(UIManager.getLookAndFeel().getID().equals("Windows"))
      setBorder(null);
      //setBackground(CAISOMetalTheme.GRAY_LIGHT);
      //setBorder(UIManager.getBorder("ComboBox.border"));
  }

  public void forceUpperCase() {
    this.setDocument(new SFAllUpperCaseDocument());
  }

  public void setPredictiveSearch(ISFPredictiveSearch searchImpl) {
    this.predictiveSearch = searchImpl;
  }

  public ISFPredictiveSearch getPredictiveSearch() {
    return predictiveSearch;
  }

  public void makePrediction(String guessText) {
    setCaretPosition(0);
    moveCaretPosition(getDocument().getLength());
    replaceSelection(guessText);
  }

  public void replaceSelection(String content) {
    //System.out.println("replacing selection");
    int origCaretDot = super.getCaret().getDot();
    super.replaceSelection(content);
    lastReplaceContent = getText();
    if(isEditable() == false || isEnabled() == false)
      return;
    Document doc = getDocument();
    if(doc != null && predictiveSearch != null) {
      try {
        String oldContent = doc.getText(0, doc.getLength());
        int oldLen = oldContent.length();
        String newContent = predictiveSearch.getPrediction(oldContent);
        if(newContent != null) {
          lastPrediction = newContent;
          setText(newContent);
          int newLen = newContent.length();
          setCaretPosition(newLen);
          if(oldLen < newLen)
            moveCaretPosition(oldLen);
        }
        else {
          //if (origCaretDot < oldLen)) origCaretDot++; //might have to revisit this depending on the desired functionality.
          //setCaretPosition(origCaretDot);
          if(predictiveSearch.isAutoReplace()) {
            setCaretPosition(getText().length());
            moveCaretPosition(0);
          }
        }
      }
      catch(Exception e) {
        //ignore, list was empty to search from. shouldn't happen.
      }
    }
  }

  public void setText(String text) {
    this.lastPrediction = text;
    super.setText(text);
  }

  public String getNonHighlightText() {
    String tmpText = getText();
    if(tmpText.equals(""))
      return tmpText;
    int tmpInt = getSelectionStart();
    if(tmpInt > -1 && tmpInt < tmpText.length())
      tmpText = tmpText.substring(0, tmpInt);
    return tmpText;
  }

  private void addClearSelectionActionListener() {
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if(predictiveSearch != null)
          predictiveSearch.selectionCleared();
        setCaretPosition(getDocument().getLength());
      }
    });
  }

  public void processComponentKeyEvent(final KeyEvent evt) {
    if(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      if(evt.getID() == KeyEvent.KEY_RELEASED) {
        if(!getNonHighlightText().equalsIgnoreCase(lastReplaceContent)) {
          String tmpText = getNonHighlightText();
          if(tmpText.length() > 0)
            tmpText = tmpText.substring(0, tmpText.length() - 1);
          setText(tmpText);
          //processComponentEvent(evt);
          if(tmpText.length() > 0)
            makePrediction(getText());
        }
        else {
          String tmpText = getNonHighlightText();
          if(tmpText.length() > 0)
            tmpText = tmpText.substring(0, tmpText.length() - 1);
          setText(tmpText);
          if(tmpText.length() > 0)
            makePrediction(getText());
          //processComponentEvent(evt);
        }
      }
    }
    else
      processComponentEvent(evt);
  }

  private void addClearSelectionFocusListener() {
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent fe) {
        //System.out.println("focus gained");
        setBackground(Color.white);
        setCaretPosition(getText().length());
        moveCaretPosition(0);
        //setCaretPosition(0);
        //selectAll();
      }

      public void focusLost(FocusEvent fe) {
        //System.out.println("focus lost");
        if(fe.isTemporary())
          return;
        setBackground((Color)UIManager.get("ComboBox.background")); //CAISOMetalTheme.GRAY_LIGHT);
        if(predictiveSearch.isAutoReplace()) {
          setText(lastPrediction);
          setCaretPosition(getDocument().getLength());
        }
      }
    });
  }

  //Adding for 1.4, because sun broke the focus stuff.
  public class TemporaryMouseListener implements MouseListener {
    boolean focused = false;
    public void mouseEntered(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {}

    public void mousePressed(MouseEvent me) {
      focused = hasFocus();
    }

    public void mouseClicked(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {
      if(!focused) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            setCaretPosition(0);
            selectAll();
          }
        });
      }
      else {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            moveCaretPosition(getText().length());
          }
        });
      }
    }
  }
}

package net.saucefactory.swing.common;

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
import java.util.*;

public class SFMultiLineLabel extends JLabel
{
  public static final int LINE_SPACING = 1;
  int textHeight = 0;

  public SFMultiLineLabel()
  {
    super();
  }

  public String getClippedText()
  {
    String text = super.getText();
    StringBuffer displayText = new StringBuffer();
    String currentLine = "";
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    int maxWidth = getPreferredSize().width;
    int tmpWidth;
    StringTokenizer tok = new StringTokenizer(text, " \n", true);
    int lineCount = 1;
    while(tok.hasMoreTokens()) {
      String tmpToken = tok.nextToken();
      tmpWidth = metrics.stringWidth(currentLine + tmpToken);
      if(!tmpToken.equals(" ")) {
	if(tmpToken.equals("\n")) {
	  lineCount++;
	  displayText.append(tmpToken);
	  currentLine = "";
	}
	else if(tmpWidth < maxWidth) {
	  displayText.append(tmpToken + " ");
	  currentLine = currentLine + tmpToken + " ";
	}
	else {
	  lineCount++;
	  displayText.append("\n" + tmpToken + " ");
	  currentLine = tmpToken + " ";
	}
      }
    }
    textHeight = LINE_SPACING + (lineCount * (metrics.getAscent() + LINE_SPACING));
    return displayText.toString();
  }

  public int getLineCount()
  {
    String text = getClippedText();
    StringTokenizer tok = new StringTokenizer(text, "\n");
    int tokCount = tok.countTokens();
    if(tokCount > 1) {
      setMinimumSize(new Dimension(getMinimumSize().width, textHeight));//getMinimumSize().height * tokCount));
      setPreferredSize(new Dimension(getPreferredSize().width, textHeight));//getPreferredSize().height * tokCount));
    }
    return tokCount;
  }

  public int getLineHeight()
  {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    return metrics.getAscent();
  }

  public int getTextHeight()
  {
    return textHeight;
  }

  public void paint(Graphics g)
  {
    String text = getClippedText();
    StringTokenizer tok = new StringTokenizer(text, "\n", true);
    //clear background and paint border
    Dimension size = getSize();
    if(isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, size.width, size.height);
    }
    paintBorder(g);
    //paint strings
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    int lineHeight = metrics.getAscent();
    g.setFont(getFont());
    g.setColor(getForeground());
    int tokCounter = 0;
    int totalTokens = tok.countTokens();
    if(totalTokens == 0)
      totalTokens = 1;
    String nextToken;
    int tokWidth;
    while(tok.hasMoreTokens()) {
      nextToken = tok.nextToken();
      if(!nextToken.equals("\n")) {
	tokWidth = metrics.stringWidth(nextToken);
	//paint the tokens.
	if(getHorizontalAlignment() == JLabel.RIGHT)
	  g.drawString(nextToken, size.width - tokWidth, (LINE_SPACING + lineHeight) * (tokCounter + 1));//(tokCounter + 1) * lineHeight + (size.height / totalTokens - lineHeight) / 2);
	else if(getHorizontalAlignment() == JLabel.CENTER)
	  g.drawString(nextToken, (size.width - tokWidth) / 2, (LINE_SPACING + lineHeight) * (tokCounter + 1));//(tokCounter + 1) * lineHeight + (size.height / totalTokens - lineHeight) / 2);
	else
	  g.drawString(nextToken, 0, (LINE_SPACING + lineHeight) * (tokCounter + 1));//(tokCounter + 1) * lineHeight + (size.height / totalTokens - lineHeight) / 2);
      }
      else
	tokCounter++;
    }
  }
}
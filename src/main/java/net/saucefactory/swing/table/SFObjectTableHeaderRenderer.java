package net.saucefactory.swing.table;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import net.saucefactory.swing.common.SFMetalTheme;

public class SFObjectTableHeaderRenderer extends JLabel implements TableCellRenderer {
  int sortIndex = 0;
  public static final int hGap = 3;
  public static final int textGap = 1;
  private Color fgColor;
  private Color bgColor;

  public SFObjectTableHeaderRenderer() {
    setOpaque(true);
    fgColor = UIManager.getColor("TableHeader.foreground");
    bgColor = UIManager.getColor("TableHeader.background");
    setForeground(fgColor);
    setBackground(bgColor);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(1, 2, 1, 2)));
  }

  public SFObjectTableHeaderRenderer(int cellAllignment) {
    setOpaque(true);
    fgColor = UIManager.getColor("TableHeader.foreground");
    bgColor = UIManager.getColor("TableHeader.background");
    setForeground(fgColor);
    setBackground(bgColor);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(1, 2, 1, 2)));
    setHorizontalAlignment(cellAllignment);
  }

  public void setSortIndex(int index) {
    sortIndex = index;
  }

  public int getSortIndex() {
    return sortIndex;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if(value != null) {
      if(table != null)
        setFont(table.getTableHeader().getFont());
      String textString = value.toString();
      int txtHeight = getFontMetrics(getFont()).getAscent();
      if(((String)value).indexOf("\n") > -1) {
        StringTokenizer tok = new StringTokenizer(value.toString(), "\n");
        int tokCount = tok.countTokens();
        int spacers = 2 * hGap + textGap * (tokCount - 1);
        setPreferredSize(new Dimension(4, tokCount * txtHeight + spacers));
      }
      else
        setPreferredSize(new Dimension(4, txtHeight + 2 * hGap));
      setText(textString);
      if(table != null)
        column = table.convertColumnIndexToModel(column);
      int lastSort = 0;
      if(table != null)
        lastSort = ((SFObjectTable)table).getLastSortLevel(column);
      setSortIndex(lastSort);
      if(lastSort == 0) {
        setToolTipText("");
        setBackground(bgColor);
        setForeground(fgColor);
      }
      else {
        String toolString = lastSort > 0 ? "Ascending" : "Descending";
        if(lastSort == 1 || lastSort == -1) {
          setBackground(SFMetalTheme.SF_SORT_PRIMARY);
          setForeground(Color.black);
          toolString = "Sort: Primary " + toolString;
        }
        else if(lastSort == 2 || lastSort == -2) {
          setBackground(SFMetalTheme.SF_SORT_SECONDARY);
          setForeground(Color.black);
          toolString = "Sort: Secondary " + toolString;
        }
        else {
          setBackground(SFMetalTheme.SF_SORT_TERNARY);
          setForeground(Color.white);
          toolString = "Sort: Ternary " + toolString;
        }
        setToolTipText(toolString);
      }
    }
    else
      setText(" ");
    return this;
  }

  public void paint(Graphics g) {
    //System.out.println("painging header");
    String text = getText();
    int lastSort = getSortIndex();
    StringTokenizer tok;
    tok = new StringTokenizer(text, "\n");
    //clear background and paint border
    Dimension size = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, size.width, size.height);
    paintBorder(g);
    Insets insets = getInsets();
    //paint strings
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    int lineHeight = metrics.getAscent();
    int desent = metrics.getDescent();
    g.setFont(getFont());
    g.setColor(getForeground());
    int tokCounter = 0;
    String nextToken;
    int tokWidth;
    while(tok.hasMoreTokens()) {
      nextToken = tok.nextToken();
      tokWidth = metrics.stringWidth(nextToken);
      //paint the tokens.
      if(getHorizontalAlignment() == JLabel.RIGHT)
        g.drawString(nextToken, size.width - tokWidth,
            (tokCounter * textGap) + (tokCounter + 1) * lineHeight + hGap - desent);
      else if(getHorizontalAlignment() == JLabel.CENTER)
        g.drawString(nextToken, (size.width - tokWidth) / 2,
            (tokCounter * textGap) + (tokCounter + 1) * lineHeight + hGap - desent); //(tokCounter + 1) * lineHeight + 2 - desent);
      else
        g.drawString(nextToken, insets.left,
            (tokCounter * textGap) + (tokCounter + 1) * lineHeight + hGap - desent); //(tokCounter + 1) * lineHeight + 2 - desent);
      tokCounter++;
    }
    //Paint sort stuff.
    if(lastSort != 0) {
      int[] xPoints = new int[] {
          size.width - 9, size.width - 6, size.width - 3};
      int[] yPoints;
      if(lastSort < 0)
        yPoints = new int[] {
            8, 2, 8};
      else
        yPoints = new int[] {
            2, 8, 2};
      g.setColor(Color.black);
      g.fillPolygon(xPoints, yPoints, 3);
      //g.setColor(CAISOMetalTheme.CAISO_BLUE_MEDIUM);// Color.white);
      //g.drawPolygon(xPoints, yPoints, 3);
    }
  }
}

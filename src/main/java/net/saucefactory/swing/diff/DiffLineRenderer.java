package net.saucefactory.swing.diff;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import net.saucefactory.text.diff.*;
/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

public class DiffLineRenderer extends JPanel implements ListCellRenderer {
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel lblLineNumber = new JLabel();
  private JLabel lblText = new JLabel();
//  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  public Color addedForeground = Color.green.darker();
  public Color addedBackground = new Color(225, 225, 225);
  public Color changedForeground = Color.orange.darker();
  public Color changedBackground = new Color(225, 225, 225);
  public Color deletedForeground = Color.red;
  public Color deletedBackground = new Color(225, 225, 225);
  public Color numberForeground = Color.gray;

  public DiffLineRenderer() {
    super();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    lblLineNumber.setMaximumSize(new Dimension(25, 1000));
    lblLineNumber.setMinimumSize(new Dimension(25, 10));
    lblLineNumber.setPreferredSize(new Dimension(25, 10));
    lblLineNumber.setHorizontalAlignment(SwingConstants.RIGHT);
    lblLineNumber.setText("1");
    this.setLayout(gridBagLayout1);
    lblText.setText("Text");
    this.add(lblLineNumber,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 20), 0, 0));
    this.add(lblText,      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
  }


  public Component getListCellRendererComponent(JList list
                                                , Object value
                                                , int index
                                                , boolean isSelected
                                                , boolean cellHasFocus)
  {
      setFont(list.getFont());
      lblText.setFont(list.getFont());
      lblLineNumber.setFont(list.getFont());
      lblLineNumber.setForeground(numberForeground);

      setComponentOrientation(list.getComponentOrientation());
      lblText.setComponentOrientation(list.getComponentOrientation());
      lblLineNumber.setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
          setBackground(list.getSelectionBackground());
          lblText.setForeground(list.getSelectionForeground());
      }

      if (value instanceof DiffItem && value != null) {
        DiffItem diffItem = (DiffItem)value;
        if (!isSelected) {
          switch (diffItem.status) {
            case DiffItem.ADDED:
              setBackground(addedBackground);
              lblText.setForeground(addedForeground);
              break;
            case DiffItem.CHANGED:
              setBackground(changedBackground);
              lblText.setForeground(changedForeground);
              break;
            case DiffItem.DELETED:
              setBackground(deletedBackground);
              lblText.setForeground(deletedForeground);
              break;
            default:
              setBackground(list.getBackground());
              lblText.setForeground(list.getForeground());
              break;
          }
        }
        String txt = (diffItem.textNoDelimiter == null) ? diffItem.text : diffItem.textNoDelimiter;
        lblText.setText(txt.length() > 0 ? txt : " ");
        if (diffItem.currentLineNo == 0)
          lblLineNumber.setText("*");
        else
        lblLineNumber.setText((diffItem.currentLineNo < 0) ? " " : String.valueOf(diffItem.currentLineNo));
      }
      else {
        if (!isSelected) {
          setBackground(list.getBackground());
          lblText.setForeground(list.getForeground());
        }
        lblText.setText((value == null) ? " " : value.toString());
      }
      return this;
  }

  public static void main(String[] args) {
  }

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void repaint(long tm, int x, int y, int width, int height) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void repaint(Rectangle r) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
       // Strings get interned...
       if (propertyName=="text")
           super.firePropertyChange(propertyName, oldValue, newValue);
   }

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
  public Color getAddedBackground() {
    return addedBackground;
  }
  public Color getAddedForeground() {
    return addedForeground;
  }
  public Color getChangedBackground() {
    return changedBackground;
  }
  public Color getChangedForeground() {
    return changedForeground;
  }
  public Color getDeletedBackground() {
    return deletedBackground;
  }
  public Color getDeletedForeground() {
    return deletedForeground;
  }
  public Color getNumberForeground() {
    return numberForeground;
  }
  public void setDeletedForeground(Color deletedForeground) {
    this.deletedForeground = deletedForeground;
  }
  public void setDeletedBackground(Color deletedBackground) {
    this.deletedBackground = deletedBackground;
  }
  public void setChangedForeground(Color changedForeground) {
    this.changedForeground = changedForeground;
  }
  public void setChangedBackground(Color changedBackground) {
    this.changedBackground = changedBackground;
  }
  public void setAddedForeground(Color addedForeground) {
    this.addedForeground = addedForeground;
  }
  public void setAddedBackground(Color addedBackground) {
    this.addedBackground = addedBackground;
  }
  public void setNumberForeground(Color numberForeground) {
    this.numberForeground = numberForeground;
  }



}

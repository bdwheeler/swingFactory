package net.saucefactory.swing.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.saucefactory.swing.SFTitleLabel;
import net.saucefactory.swing.common.adapters.ISFColorAdapter;

public class NameValuePanel extends JPanel {

  private GridBagLayout lytMain = new GridBagLayout();
  private int rowCount = 0;
  private JLabel lblSpacer = new JLabel("");
  private Border labelBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
  private Border itemBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
  private Border titleBorder = BorderFactory.createEmptyBorder(2, 5, 2, 0);
  private int titleHAlign = SwingConstants.LEFT;
  private int titleGridAlign = GridBagConstraints.WEST;
  private int titleVAlign = SwingConstants.TOP;
  private ISFColorAdapter colorAdapter = null;

  public NameValuePanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setTitleHorizontalAlignment(int titleHAlign) {
    this.titleHAlign = titleHAlign;
  }

  public void setTitleVerticalAlignment(int titleVAlign) {
    this.titleVAlign = titleVAlign;
  }

  public void setTitleGridAlignment(int titleGridAlign) {
    this.titleGridAlign = titleGridAlign;
  }

  public void setColorAdapter(ISFColorAdapter colorAdapter) {
    this.colorAdapter = colorAdapter;
  }

  private void jbInit() throws Exception {
    this.setLayout(lytMain);
    add(lblSpacer, new GridBagConstraints(0, 150, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  public void removeAllRows() {
    removeAll();
    setRowCount(0);
  }

  public void setRowCount(int cnt) {
    rowCount = cnt;
  }

  public void setRowVisible(JLabel lbl, JComponent component, boolean visible) {
    if(lbl != null)
      lbl.setVisible(visible);
    if(component != null)
      component.setVisible(visible);
  }

  public void addRows(JLabel[] titleArray, JComponent[] componentArray) {
    if(titleArray != null && componentArray != null) {
      for(int i = 0; i < titleArray.length; i++)
        addRow(titleArray[i], componentArray[i]);
    }
  }

  public void addRow(String title) {
    addRow(title, null);
  }

  public void addRow(String title, JComponent component) {
    addRow(new SFTitleLabel(title), component, 0.0f, false);
  }

  public void addRow(JLabel lblTitle, JComponent component) {
    addRow(lblTitle, component, 0.0f, false);
  }

  public void addRow(JLabel lblTitle, JComponent component, float weightY, boolean full) {
    addRow(lblTitle, component, weightY, full, 1);
  }
  
  public void addTitleRow(JLabel lblTitle) {
    addTitleRow(lblTitle, null, 0.0f, false, 1);
  }

  public void addRow(JLabel lblTitle, JComponent component, float weightY, boolean full, int componentHeight) {
    Color tmpColor = colorAdapter == null ? getBackground() : colorAdapter.getBackground(null, false, false, rowCount, -1);
    int titleXSpan = component == null ? 2 : 1;
    int itemXStart = lblTitle == null ? full ? 0 : 1 : 1;
    int itemXSpan = lblTitle == null ? full ? 2 : 1 : 1;
    if(lblTitle != null) {
      lblTitle.setOpaque(true);
      lblTitle.setBorder(labelBorder);
      lblTitle.setHorizontalAlignment(titleHAlign);
      lblTitle.setVerticalAlignment(titleVAlign);
      lblTitle.setBackground(tmpColor);
      add(lblTitle, new GridBagConstraints(0, rowCount, titleXSpan, 1, 0.0, 0.0, titleGridAlign,
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 2));
    }
    if(component != null) {
      component.setOpaque(true);
      component.setBorder(itemBorder);
      component.setBackground(tmpColor);
      add(component, new GridBagConstraints(itemXStart, rowCount, itemXSpan, componentHeight, 1.0, weightY, GridBagConstraints.WEST,
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 2));
    }
    rowCount++;
  }
  
  public void addTitleRow(JLabel lblTitle, JComponent component, float weightY, boolean full, int componentHeight) {
    int titleXSpan = component == null ? 2 : 1;
    int itemXStart = lblTitle == null ? full ? 0 : 1 : 1;
    int itemXSpan = lblTitle == null ? full ? 2 : 1 : 1;
    if(lblTitle != null) {
      lblTitle.setOpaque(true);
      lblTitle.setBorder(titleBorder);
      lblTitle.setHorizontalAlignment(titleHAlign);
      lblTitle.setVerticalAlignment(titleVAlign);
      add(lblTitle, new GridBagConstraints(0, rowCount, titleXSpan, 1, 0.0, 0.0, titleGridAlign,
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 2));
    }
    if(component != null) {
      component.setOpaque(true);
      component.setBorder(titleBorder);
      add(component, new GridBagConstraints(itemXStart, rowCount, itemXSpan, componentHeight, 1.0, weightY, GridBagConstraints.WEST,
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 2));
    }
    rowCount++;
  }

  public void removeSpacer() {
    remove(lblSpacer);
  }
}

package net.saucefactory.swing.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import net.saucefactory.swing.common.SFMetalTheme;
import javax.swing.table.JTableHeader.*;
import javax.swing.plaf.UIResource;
import javax.swing.border.*;

public class SFDefaultTableHeaderRenderer extends DefaultTableCellRenderer {

  private DefaultTableCellRenderer _systemRenderer;

  int hAlign;
  int sortIndex = 0;
  private Color fgColor = null;
  private Color bgColor = null;
  private static Border emptyBorder;

  static {
    if(System.getProperty("os.name").equals("Windows XP"))
      emptyBorder = new EmptyBorder(3, 3, 4, 3);
    else
      emptyBorder = new EmptyBorder(0, 3, 0, 3);
  }

  public SFDefaultTableHeaderRenderer(DefaultTableCellRenderer _systemRenderer) {
    this(_systemRenderer, JLabel.LEFT, null, null);
  }

  public SFDefaultTableHeaderRenderer(DefaultTableCellRenderer _systemRenderer, int cellAllignment) {
    this(_systemRenderer, cellAllignment, null, null);
  }

  //public Color getFgColor() {
    //return fgColor == null ? UIManager.getColor("TableHeader.foreground") : fgColor;
  //}

  //public Color getBgColor() {
    //return bgColor == null ? UIManager.getColor("TableHeader.background") : bgColor;
  //}

  public SFDefaultTableHeaderRenderer(DefaultTableCellRenderer systemRenderer, int cellAllignment, Color fgColor, Color bgColor) {
    super();
    _systemRenderer = systemRenderer;
    setBorder(emptyBorder);//systemRenderer.getBorder());
    //this.fgColor = fgColor;
    //this.bgColor = bgColor;
    hAlign = cellAllignment;
    setHorizontalAlignment(cellAllignment);
    setVerticalAlignment(SwingConstants.TOP);
  }

  public void setSortIndex(int index) {
    sortIndex = index;
  }

  public int getSortIndex() {
    return sortIndex;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if(table != null) {
      JTableHeader header = table.getTableHeader();
      if(header != null) {
        setFont(header.getFont());
      }
      column = table.convertColumnIndexToModel(column);
      int lastSort = 0;
      if(table != null)
        lastSort = ((SFTable)table).getLastSortLevel(column);
      setSortIndex(lastSort);
    }
    setText((value == null) ? "" : value.toString());
    return this;
    //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      //if(lastSort == 0) {
        //setTooltip(rtnComponent, "");
        //rtnComponent.setBackground(getBgColor());
        //rtnComponent.setForeground(getFgColor());
      //}
      //else {
        //String toolString = lastSort > 0 ? "Ascending" : "Descending";
        //if(lastSort == 1 || lastSort == -1) {
          //rtnComponent.setBackground(SFMetalTheme.SF_SORT_PRIMARY);
          //rtnComponent.setForeground(Color.white);
          //toolString = "Sort: Primary " + toolString;
        //}
        //else if(lastSort == 2 || lastSort == -2) {
          //rtnComponent.setBackground(SFMetalTheme.SF_SORT_SECONDARY);
          //rtnComponent.setForeground(Color.white);
          //toolString = "Sort: Secondary " + toolString;
        //}
        //else {
          //rtnComponent.setBackground(SFMetalTheme.SF_SORT_TERNARY);
          //rtnComponent.setForeground(Color.white);
          //toolString = "Sort: Ternary " + toolString;
        //}
        //setTooltip(rtnComponent, toolString);
      //}
    //}
    //return rtnComponent;
  }

  private void setTooltip(Component component, String tooltip) {
    if(component instanceof JComponent)
      ((JComponent)component).setToolTipText(tooltip);
  }

  public void paint(Graphics g) {
    // Make the XP renderer draw its background
	    Dimension size = getSize();
	    _systemRenderer.setSize(size);
	    _systemRenderer.setOpaque(false);
	    _systemRenderer.paint(g);

	    super.paint(g);

     int lastSort = getSortIndex();
     if(lastSort != 0) {
       int[] xPoints = new int[] {size.width - 9, size.width - 6, size.width - 3};
       int[] yPoints = lastSort > 0 ? new int[] {8, 2, 8} : new int[] {2, 8, 2};
       g.setColor(Color.black);
       g.fillPolygon(xPoints, yPoints, 3);
     }
  }

  public static void main(String[] a) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SFTableHeaderRendererFactory.setMode(SFTableHeaderRendererFactory.MODE_SF_DEFAULT_RENDERER);
    JFrame f = new JFrame();
    SFTable table = new SFTable();
    //SFTableHeaderRendererFactory.set
    f.getContentPane().add(new JScrollPane(table));
    // Replace the XP renderer with our own
    JTableHeader header = table.getTableHeader();
    TableCellRenderer oldRenderer = header.getDefaultRenderer();
    String name = oldRenderer.getClass().getName();
    if(name.equals("com.sun.java.swing.plaf.windows.WindowsTableHeaderUI$XPDefaultRenderer")) {
      header.setDefaultRenderer(new SFDefaultTableHeaderRenderer((DefaultTableCellRenderer)oldRenderer));
    }
    f.setBounds(100, 100, 200, 200);
    f.setVisible(true);
  }
}


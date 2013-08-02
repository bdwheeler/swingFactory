package net.saucefactory.swing.table;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

public class SFTableHeaderRendererFactory {

  public static JTable dummyTable = null;

  public static final int MODE_SF_RENDERER = 1;
  public static final int MODE_SF_DEFAULT_RENDERER = 2;
  public static final int MODE_DEFAULT_RENDERER = 3;
  public static final int MODE_CUSTOM = 4;

  private static Class _headerRendererClass = null;

  public static int _mode = MODE_SF_RENDERER;

  public SFTableHeaderRendererFactory() {
  }

  public static void setMode(int mode) {
    _mode = mode;
  }

  private static JTable getDummyTable() {
    if(dummyTable == null) {
      String[][] tmpVals = new String[1][1];
      String[] tmpHeaders = new String[1];
      tmpHeaders[0] = " ";
      dummyTable = new JTable(tmpVals, tmpHeaders);
    }
    return dummyTable;
  }

  public static void setHeaderRendererClass(String headerRendererClass) {
    try {
      _headerRendererClass = Class.forName(headerRendererClass);
      _mode = MODE_CUSTOM;
    }
    catch(Exception e) {
      _headerRendererClass = null;
    }
  }

  public static void initTableHeaderRenderer(ISFTable table) {
    if(_mode == MODE_SF_DEFAULT_RENDERER) {
      JTableHeader header = ((JTable)table).getTableHeader();
      TableCellRenderer systemRenderer = header.getDefaultRenderer();
      String name = systemRenderer.getClass().getName();
      if(name.equals("com.sun.java.swing.plaf.windows.WindowsTableHeaderUI$XPDefaultRenderer")) {
        header.setDefaultRenderer(new SFDefaultTableHeaderRenderer((DefaultTableCellRenderer)systemRenderer));
      }
    }
  }

  public static TableCellRenderer getIndividualHeaderRenderer(int headerAllignment, Color bgColor, Color fgColor) {
    switch(_mode) {
      case MODE_DEFAULT_RENDERER:
        return null;
      case MODE_SF_DEFAULT_RENDERER:
        return null;//new SFDefaultTableHeaderRenderer(headerAllignment, fgColor, bgColor);
      case MODE_SF_RENDERER:
        return new SFTableHeaderRenderer(headerAllignment, fgColor, bgColor);
      case MODE_CUSTOM:
        if(_headerRendererClass != null) {
          try {
            TableCellRenderer tmpRenderer = (TableCellRenderer)_headerRendererClass.newInstance();
            if(tmpRenderer instanceof Component) {
              ((Component)tmpRenderer).setBackground(bgColor);
              ((Component)tmpRenderer).setForeground(fgColor);
              if(tmpRenderer instanceof JLabel)
                ((JLabel)tmpRenderer).setHorizontalAlignment(headerAllignment);
            }
            return tmpRenderer;
          }
          catch(Exception e) {
            //ignore
          }
        }
    }
    //fall back to the sf renderer.
    return new SFTableHeaderRenderer(headerAllignment, fgColor, bgColor);
  }

  public static Component getHeaderCornerRenderer() {
    try {
      switch(_mode) {
        case MODE_DEFAULT_RENDERER:
        case MODE_SF_DEFAULT_RENDERER:
          JTable tmpTable = getDummyTable();
          JTableHeader header = tmpTable.getTableHeader();
          TableCellRenderer systemRenderer = header.getDefaultRenderer();
          return systemRenderer.getTableCellRendererComponent(tmpTable, " ", false, false, -1, 0);
        case MODE_SF_RENDERER:
          JLabel rtnLabel = new JLabel("") {
            public Color getBackground() {
              return UIManager.getColor("TableHeader.background");
            }
          };
          rtnLabel.setBorder(BorderFactory.createRaisedBevelBorder());
          rtnLabel.setOpaque(true);
          return rtnLabel;
        case MODE_CUSTOM:
          if(_headerRendererClass != null) {
            try {
              TableCellRenderer tmpRenderer = (TableCellRenderer)_headerRendererClass.newInstance();
              if(tmpRenderer instanceof Component) {
                ((Component)tmpRenderer).setBackground(UIManager.getColor("TableHeader.background"));
                ((Component)tmpRenderer).setForeground(UIManager.getColor("TableHeader.foreground"));
              }
              return tmpRenderer.getTableCellRendererComponent(getDummyTable(), "", false, false, -1, 0);
            }
            catch(Exception e) {
              //ignore
            }
          }
      }
    }
    catch(Exception e) {
      System.out.println("error building table corner");
    }
    JLabel rtnLabel = new JLabel("") {
      public Color getBackground() {
        return UIManager.getColor("TableHeader.background");
      }
    };
    rtnLabel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    rtnLabel.setOpaque(true);
    return rtnLabel;
  }
}

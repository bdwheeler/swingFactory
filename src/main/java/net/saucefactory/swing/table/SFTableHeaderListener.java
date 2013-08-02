package net.saucefactory.swing.table;

import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;
import javax.swing.table.TableColumn;

public class SFTableHeaderListener extends MouseAdapter {

  private SFTable tgtTable = null;
  private JTableHeader header = null;
  private boolean popupInstalled = false;
  private boolean sortInstalled = false;

  public SFTableHeaderListener(SFTable tgtTable) {
    this.tgtTable = tgtTable;
  }

  public void setHeader(JTableHeader header) {
    uninstallListener();
    this.header = header;
    header.addMouseListener(this);
  }

  public void uninstallListener() {
    if(header != null)
      header.removeMouseListener(this);
  }

  public void setSortInstalled(boolean sortInstalled) {
    this.sortInstalled = sortInstalled;
  }

  public void setPopupInstalled(boolean popupInstalled) {
    this.popupInstalled = popupInstalled;
  }

  public void handleMouseClick(MouseEvent e, int column) {
    try {
      if(SwingUtilities.isRightMouseButton(e)) {
        if(popupInstalled) {
          tgtTable.getHeaderPopup().setLastMouseColumn(column);
          tgtTable.getHeaderPopup().showPopup(e);
        }
      }
      else if(sortInstalled) {
        TableColumnModel columnModel = tgtTable.getColumnModel();
        SFTableColumn tmpCol = tgtTable.getTableModel().getColumnHeader(column);
        if(tmpCol != null && !tmpCol.isSortable())
          return;
        if(e.getClickCount() == 1 && column != -1) {
          int lastRow = -1;
          if(tgtTable.getTableState() != null)
            tgtTable.getTableState().saveSelectionState();
          else
            lastRow = tgtTable.getSelectedRowWithSort();
          if((e.getModifiers() & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK) {
            int depth = tgtTable.getSortAdapter().getSortDepth();
            if(depth >= tgtTable.getSortAdapter().getMaxSortDepth())
              depth = tgtTable.getSortAdapter().getMaxSortDepth() - 1;
            tgtTable.getSortAdapter().doManualSort(column, depth, true);
          }
          else
            tgtTable.getSortAdapter().sortByColumn(column);
          if(tgtTable.getTableState() != null)
            tgtTable.getTableState().restoreSelectionState();
          else
            tgtTable.refreshRowSelection(lastRow);
          tgtTable.getTableHeader().revalidate();
          tgtTable.getTableHeader().repaint();
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public void mouseClicked(final MouseEvent e) {
    try {
      if(SwingUtilities.isRightMouseButton(e)) {
        if(popupInstalled) {
          TableColumnModel columnModel = tgtTable.getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX());
          int column = tgtTable.convertColumnIndexToModel(viewColumn);
          tgtTable.getHeaderPopup().setLastMouseColumn(column);
          tgtTable.getHeaderPopup().showPopup(e);
        }
      }
      else if(sortInstalled) {
        TableColumnModel columnModel = tgtTable.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        int column = tgtTable.convertColumnIndexToModel(viewColumn);
        SFTableColumn tmpCol = tgtTable.getTableModel().getColumnHeader(column);
        if(tmpCol != null && !tmpCol.isSortable())
          return;
        if(e.getClickCount() == 1 && column != -1) {
          int lastRow = -1;
          if(tgtTable.getTableState() != null)
            tgtTable.getTableState().saveSelectionState();
          else
            lastRow = tgtTable.getSelectedRowWithSort();
          if((e.getModifiers() & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK) {
            int depth = tgtTable.getSortAdapter().getSortDepth();
            if(depth >= tgtTable.getSortAdapter().getMaxSortDepth())
              depth = tgtTable.getSortAdapter().getMaxSortDepth() - 1;
            tgtTable.getSortAdapter().doManualSort(column, depth, true);
          }
          else
            tgtTable.getSortAdapter().sortByColumn(column);
          if(tgtTable.getTableState() != null)
            tgtTable.getTableState().restoreSelectionState();
          else
            tgtTable.refreshRowSelection(lastRow);
          tgtTable.getTableHeader().revalidate();
          tgtTable.getTableHeader().repaint();
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}

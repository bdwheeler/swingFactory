package net.saucefactory.swing.table;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
import net.saucefactory.swing.renderers.*;
import net.saucefactory.swing.utils.PrintingUtility;

public class SFObjectTableTree extends SFObjectTable {
  protected SFObjectTreeTableRenderer renderer;
  private JPopupMenu popup;
  private JMenuItem expandAllItem = new JMenuItem("Expand All");
  private JMenuItem colapseAllItem = new JMenuItem("Colapse All");
  private MouseHandler handler;
  private SFObjectTableTreeModel model;
  private ListToTreeSelectionModelWrapper selectionWrapper;

  public SFObjectTableTree(ISFTableTreeModel _model, SFObjectTableColumn[] headers) {
    renderer = new SFObjectTreeTableRenderer(this, _model);
    model = new SFObjectTableTreeModel(_model, renderer);
    setTableColumns(headers, model);
    getColumn(headers[0].getColumnName()).setCellRenderer(renderer);
    getColumn(headers[0].getColumnName()).setCellEditor(new SFTreeTableEditor());
    initRenderer();
    initPopup();
  }

  private void initRenderer() {
    selectionWrapper = new ListToTreeSelectionModelWrapper();
    renderer.setSelectionModel(selectionWrapper);
    setSelectionModel(selectionWrapper.getListSelectionModel());
    renderer.setRowHeight(getRowHeight());
    setDefaultRenderer(String.class, new SFStringRenderer());
    addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent ke) {
        if(ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_RIGHT) {
          int tmpSelection = getSelectedRow();
          if(tmpSelection > -1 && model.getChildCount(tmpSelection) > 0) {
            KeyEvent newKe = new KeyEvent(renderer, ke.getID(),
                ke.getWhen(), ke.getModifiers(),
                ke.getKeyCode(), ke.getKeyChar(), ke.getKeyLocation());
            renderer.dispatchEvent(ke);
            setRowSelectionInterval(tmpSelection, tmpSelection);
          }
        }
      }
      public void keyReleased(KeyEvent ke) {}
      public void keyTyped(KeyEvent ke) {}
    });
  }

  public SFObjectTableTreeModel getSFModel() {
    return model;
  }

  private void initPopup() {
    popup = new JPopupMenu();
    popup.add(expandAllItem);
    popup.add(colapseAllItem);
    PopupHandler popHandler = new PopupHandler();
    expandAllItem.setActionCommand("EXPAND");
    colapseAllItem.setActionCommand("COLAPSE");
    expandAllItem.addActionListener(popHandler);
    colapseAllItem.addActionListener(popHandler);
    handler = new MouseHandler();
    addMouseListener(handler);
  }

  public void addPopupItem(JMenuItem addItem) {
    addPopupItem(addItem, false);
  }

  public void addPopupItem(JMenuItem addItem, boolean addSeparatorFirst) {
    if(addSeparatorFirst)
      popup.addSeparator();
    popup.add(addItem);
  }

  private class PopupHandler implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      if(ae.getActionCommand().equals("EXPAND"))
        renderer.expandAllLevels();
      else if(ae.getActionCommand().equals("COLAPSE"))
        renderer.collapseAllLevels();
    }
  }

  public int getEditingRow() {
    return (getColumnClass(editingColumn) == ISFTableTreeModel.class) ? -1 : editingRow;
  }

  public SFObjectTreeTableRenderer getTreeCellRenderer() {
    return renderer;
  }

  public void installTableSortAdapter() {
    installTableSortAdapter(1);
  }

  public void installTableSortAdapter(int maxDepth) {
    if(tableModel != null) {
      if(sortAdapter == null)
        sortAdapter = new SFTableTreeSortAdapter(this);
      else
        sortAdapter.setTableModel(tableModel);
      sortAdapter.addTableHeaderMouseListener();
      sortAdapter.setSortDepth(maxDepth);
    }
  }

  public Object getValueAt(int row, int column) {
    if(sortAdapter != null && row >= tableModel.sortIndexArray.length)
      sortAdapter.redoLastSort();
    return super.getValueAt(row, column);
  }

  public int convertRowToSort(int row) {
    try {
      if(sortAdapter == null)
        return row;
      if(tableModel.sortIndexArray == null || row >= tableModel.sortIndexArray.length)
        sortAdapter.redoLastSort();
      return tableModel.sortIndexArray[row];
    }
    catch(Exception e) {
      return row;
    }
  }

  public String getPrintValueAt(int row, int column) {
    if(column == 0)
      return (String)getValueAt(row, column);
    return super.getPrintValueAt(row, column);
  }

  public void printValueAt(Graphics g, int row, int column, int x, int y, int width, int height) {
    g.setClip(x, y, width, height);

  }

  public int convertSortToRow(int row) {
    try {
      if(sortAdapter == null)
        return row;
      if(tableModel.sortIndexArray == null || row >= tableModel.sortIndexArray.length)
        sortAdapter.redoLastSort();
      for(int i = 0; i < tableModel.sortIndexArray.length; i++)
        if(row == tableModel.sortIndexArray[i])
          return i;
      return row;
    }
    catch(Exception e) {
      return row;
    }
  }


  public class SFTreeTableEditor extends AbstractCellEditor implements TableCellEditor {
    protected EventListenerList listenerList = new EventListenerList();

    public Object getCellEditorValue() {
      return null;
    }

    public boolean isCellEditable(EventObject e) {
      if(e instanceof MouseEvent) {
        for(int counter = getColumnCount() - 1; counter >= 0;
            counter--) {
          if(getColumnClass(counter) == ISFTableTreeModel.class) {
            MouseEvent me = (MouseEvent)e;
            MouseEvent newME = new MouseEvent(renderer, me.getID(),
                me.getWhen(), me.getModifiers(),
                me.getX() - getCellRect(0, counter, true).x,
                me.getY(), me.getClickCount(),
                me.isPopupTrigger());
            renderer.dispatchEvent(newME);
            break;
          }
        }
      }
      return false;
   }

    public boolean shouldSelectCell(EventObject anEvent) {
      return false;
    }

    public boolean stopCellEditing() {
      return true;
    }

    public void cancelCellEditing() {}

    public void addCellEditorListener(CellEditorListener l) {
      listenerList.add(CellEditorListener.class, l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
      listenerList.remove(CellEditorListener.class, l);
    }

    protected void fireEditingStopped() {
      Object[] listeners = listenerList.getListenerList();
      for(int i = listeners.length - 2; i >= 0; i -= 2) {
        if(listeners[i] == CellEditorListener.class) {
          ((CellEditorListener)listeners[i + 1]).editingStopped(new ChangeEvent(this));
        }
      }
    }

    protected void fireEditingCanceled() {
      Object[] listeners = listenerList.getListenerList();
      for(int i = listeners.length - 2; i >= 0; i -= 2) {
        if(listeners[i] == CellEditorListener.class) {
          ((CellEditorListener)listeners[i + 1]).editingCanceled(new ChangeEvent(this));
        }
      }
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      return renderer;
    }
  }

  public TableCellEditor getSFTreeEditor() {
    return new SFTreeTableEditor();
  }

  public class SFTableTreeSortAdapter extends SFObjectTableSortAdapter
  {
    public SFTableTreeSortAdapter(SFObjectTableTree table) {
      super(table);
    }

    public int compareRowsByColumn(int row1, int row2, int column) {
      if(column == 0) {
        // Check for nulls.
        Object o1 = tableModel.getValueIgnorSort(row1, column);
        Object o2 = tableModel.getValueIgnorSort(row2, column);
        //int childCnt1 = ((SFObjectTableTreeModel)tableModel).getChildCount(row1);
        //int childCnt2 = ((SFObjectTableTreeModel)tableModel).getChildCount(row2);
        //if(childCnt1 > 0 && childCnt2 == 0)
          //return 1;
        //if(childCnt2 > 0 && childCnt1 == 0)
          //return -1;
        String s1 = (String)o1;
        String s2 = (String)o2;
        int result = s1.compareToIgnoreCase(s2);
        if(result < 0)
          return -1;
        else if(result > 0)
          return 1;
        else
          return 0;
      }
      else
        return super.compareRowsByColumn(row1, row2, column);
    }

    public void sort(Object sender) {
      System.out.println("sorting");
      checkModel();
      compares = 0;
      Vector parentVec = new Vector();
      for(int i = 0; i < indexes.length; i++) {
        int childCnt = 0;
        if(((SFObjectTableTreeModel)tableModel).isRowExpanded(i))
          childCnt += ((SFObjectTableTreeModel)tableModel).getChildCount(i);
        parentVec.add(new Integer(i));
        i += childCnt;
      }
      int[] parentArray = new int[parentVec.size()];
      for(int i = 0; i < parentVec.size(); i++)
        parentArray[i] = ((Integer)parentVec.elementAt(i)).intValue();
        //sort the top level.
      shuttlesort((int[])parentArray.clone(), parentArray, 0, parentArray.length);
      //if(parentArray != null) {
        //for(int i = 0; i < parentArray.length; i++) {
          //System.out.println("parentArray " + i + " value: " + parentArray[i]);
        //}
      //}
      //build grand array, and sort children.
      int[] clone = (int[])indexes.clone();
      int cntr = 0;
      for(int i = 0; i < parentArray.length; i++) {
        indexes[cntr] = parentArray[i];
        clone[cntr] = parentArray[i];
        cntr++;
        int childCnt = 0;
        if(((SFObjectTableTreeModel)tableModel).isRowExpanded(parentArray[i]))
          childCnt += ((SFObjectTableTreeModel)tableModel).getChildCount(parentArray[i]);
        if(childCnt > 0) {
          for(int j = 0; j < childCnt; j++) {
            clone[cntr + j] = parentArray[i] + j + 1;
            indexes[cntr + j] = clone[cntr + j];
          }
          if(childCnt > 1)
            shuttlesort(clone, indexes, cntr, cntr + childCnt);
          cntr += childCnt;
        }
      }
      //if(indexes != null) {
        //System.out.println("index length: " + indexes.length);
        //for(int i = 0; i < indexes.length; i++) {
          //System.out.println("index " + i + " value: " + indexes[i]);
        //}
      //}
    }
  }

  class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
    protected boolean updatingListSelectionModel;

    public ListToTreeSelectionModelWrapper() {
      super();
      getListSelectionModel().addListSelectionListener
          (createListSelectionListener());
    }

    ListSelectionModel getListSelectionModel() {
      return listSelectionModel;
    }

    public void resetRowSelection() {
      if(!updatingListSelectionModel) {
        updatingListSelectionModel = true;
        try {
          super.resetRowSelection();
        }
        finally {
          updatingListSelectionModel = false;
        }
      }
    }

    protected ListSelectionListener createListSelectionListener() {
      return new ListSelectionHandler();
    }

    protected void updateSelectedPathsFromSelectedRows() {
      if(!updatingListSelectionModel) {
        updatingListSelectionModel = true;
        try {
          int min = listSelectionModel.getMinSelectionIndex();
          int max = listSelectionModel.getMaxSelectionIndex();

          clearSelection();
          if(min != -1 && max != -1) {
            for(int counter = min; counter <= max; counter++) {
              if(listSelectionModel.isSelectedIndex(counter)) {
                TreePath selPath = renderer.getPathForRow
                    (counter);

                if(selPath != null) {
                  addSelectionPath(selPath);
                }
              }
            }
          }
        }
        finally {
          updatingListSelectionModel = false;
        }
      }
    }

    class ListSelectionHandler implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent e) {
        updateSelectedPathsFromSelectedRows();
      }
    }
  }

  private class MouseHandler extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
      if(SwingUtilities.isRightMouseButton(e)) {
        TreePath path = renderer.getPathForLocation(e.getX(), e.getY());
        if(path != null)
          renderer.setSelectionPath(path);
        popup.show((Component)e.getSource(), e.getX(), e.getY());
      }
    }
  }
}

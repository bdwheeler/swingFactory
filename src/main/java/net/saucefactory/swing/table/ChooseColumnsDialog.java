package net.saucefactory.swing.table;

import javax.swing.*;
import java.awt.*;
import net.saucefactory.swing.SFTitleLabel;
import net.saucefactory.swing.SFLineLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import net.saucefactory.swing.utils.ScreenUtility;
import net.saucefactory.swing.common.SFMessageHandler;
import java.util.Vector;

public class ChooseColumnsDialog extends JDialog {

  private JPanel pnlCenter = new JPanel();
  private JPanel pnlCommand = new JPanel();
  private GridBagLayout lytCenter = new GridBagLayout();
  private FlowLayout lytCommand = new FlowLayout();
  private JButton btnCancel = new JButton();
  private JButton btnSave = new JButton();
  private SFTitleLabel lblTitle = new SFTitleLabel();
  private SFLineLabel lblLine = new SFLineLabel();
  private SFTitleLabel lblAvail = new SFTitleLabel();
  private JScrollPane scrAvail = new JScrollPane();
  private JPanel pnlCheckbox = new JPanel();
  private GridBagLayout lytCheckbox = new GridBagLayout();

  private static int myWidth = 300;
  private static int myHeight = 350;
  private JCheckBox[] columnArray = null;
  private SFTableColumn[] allColumns = null;
  private SFTableColumn[] currentColumns = null;
  private SFTableColumn[] rtnColumns = null;
  private boolean save = false;
  private ChooseColumnsHandler handler = new ChooseColumnsHandler();
  private SFTable tgtTable;

  public ChooseColumnsDialog(Frame parent, SFTable tgtTable) {
    super(parent, true);
    this.tgtTable = tgtTable;
    try {
      jbInit();
      initCommands();
      registerHandlers();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public ChooseColumnsDialog(Dialog parent, SFTable tgtTable) {
    super(parent, true);
    this.tgtTable = tgtTable;
    try {
      jbInit();
      initCommands();
      registerHandlers();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static SFTableColumn[] chooseColumns(SFTable table, SFTableColumn[] allColumns, SFTableColumn[] currentColumns) {
    Window parent = SwingUtilities.getWindowAncestor(table);
    if(parent != null) {
      ChooseColumnsDialog dialog = parent instanceof Frame ? new ChooseColumnsDialog((Frame)parent, table) :
          new ChooseColumnsDialog((Dialog)parent, table);
      ScreenUtility.setDialogMinSize(dialog, myWidth, myHeight);
      ScreenUtility.centerOnScreen(dialog);
      dialog.setPanelData(allColumns, currentColumns);
      dialog.show();
      if(dialog.save)
        return dialog.rtnColumns;
    }
    return null;
  }

  private void setPanelData(SFTableColumn[] allColumns, SFTableColumn[] currentColumns) {
    this.allColumns = allColumns;
    this.currentColumns = currentColumns;
    columnArray = new JCheckBox[allColumns.length];
    for(int i = 0; i < allColumns.length; i++) {
      columnArray[i] = new JCheckBox(cleanColumnName(allColumns[i].getDisplayName()));
      columnArray[i].setSelected(isColumnSelected(allColumns[i]));
      columnArray[i].setOpaque(false);
      pnlCheckbox.add(columnArray[i], new GridBagConstraints(0, i, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
          GridBagConstraints.BOTH, new Insets(1, 5, 1, 5), 0, 0));
    }
    pnlCheckbox.add(new JLabel(""), new GridBagConstraints(0, 100, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  private String cleanColumnName(String name) {
    if(name == null)
      return "";
    name = name.replaceAll("<BR>", " ");
    name = name.replaceAll("\n", " ");
    return name;
  }

  private boolean isColumnSelected(SFTableColumn testColumn) {
    boolean rtn = false;
    for(int i = 0; i < currentColumns.length; i++) {
      if(currentColumns[i] == testColumn) {
        rtn = true;
        break;
      }
    }
    return rtn;
  }

  private void registerHandlers() {
    btnSave.addActionListener(handler);
    btnCancel.addActionListener(handler);
  }

  private void initCommands() {
    btnSave.setActionCommand("SAVE");
    btnCancel.setActionCommand("CLOSE");
  }

  private void saveMe() {
    try {
      Vector tmpVec = new Vector();
      int cnt = 0;
      for(int i = 0; i < columnArray.length; i++) {
        if(columnArray[i].isSelected()) {
          tmpVec.add(cnt, allColumns[i]);
          cnt++;
        }
      }
      if(tmpVec.size() > 0) {
        tmpVec = applyPreviousSelectionOrder(tmpVec);
        rtnColumns = new SFTableColumn[tmpVec.size()];
        rtnColumns = (SFTableColumn[])tmpVec.toArray(rtnColumns);
        save = true;
        closeMe();
      }
      else
        throw new Exception("You must select atleast one visible column");
    }
    catch(Exception e) {
      e.printStackTrace();
      SFMessageHandler.sendErrorMessage(this, e, "Table Error");
    }
  }

  private Vector applyPreviousSelectionOrder(Vector tmpVec) {
    if(currentColumns != null) {
      for(int i = currentColumns.length; i > 0; i--) {
        SFTableColumn tmpCol = (SFTableColumn)currentColumns[i - 1];
        if(tmpVec.contains(tmpCol)) {
          tmpVec.remove(tmpCol);
          tmpVec.add(0, tmpCol);
        }
      }
    }
    return tmpVec;
  }

  private void closeMe() {
    setVisible(false);
    dispose();
  }

  private void jbInit() throws Exception {
    setSize(myWidth, myHeight);
    setTitle("Choose Columns");
    pnlCenter.setLayout(lytCenter);
    pnlCommand.setLayout(lytCommand);
    btnCancel.setMinimumSize(new Dimension(100, 24));
    btnCancel.setPreferredSize(new Dimension(100, 24));
    btnCancel.setText("Cancel");
    btnSave.setPreferredSize(new Dimension(100, 24));
    btnSave.setText("Apply");
    lblTitle.setToolTipText("");
    lblTitle.setText("Select visible table columns");
    lblAvail.setText("Available columns");
    pnlCheckbox.setLayout(lytCheckbox);
    pnlCenter.setBorder(BorderFactory.createEtchedBorder());
    pnlCommand.setBorder(BorderFactory.createEtchedBorder());
    lytCommand.setAlignment(FlowLayout.RIGHT);
    this.getContentPane().add(pnlCenter, BorderLayout.CENTER);
    this.getContentPane().add(pnlCommand,  BorderLayout.SOUTH);
    pnlCommand.add(btnSave, null);
    pnlCommand.add(btnCancel, null);
    pnlCheckbox.setBackground(Color.white);
    scrAvail.getViewport().setBackground(Color.white);
    pnlCenter.add(lblTitle,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 2, 0), 0, 0));
    pnlCenter.add(lblLine,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 10), 0, 0));
    pnlCenter.add(lblAvail,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 10, 2, 0), 0, 0));
    pnlCenter.add(scrAvail,  new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 10, 10, 10), 0, 0));
    scrAvail.getViewport().add(pnlCheckbox, null);
  }

  class ChooseColumnsHandler implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      String command = ae.getActionCommand();
      if(command.equals("CLOSE"))
        closeMe();
      else if(command.equals("SAVE"))
        saveMe();
    }
  }
}

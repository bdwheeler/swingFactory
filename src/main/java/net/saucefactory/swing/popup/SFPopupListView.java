package net.saucefactory.swing.popup;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.saucefactory.swing.SFList;
import net.saucefactory.swing.SFTextViewInternalFrame;

public class SFPopupListView extends JDialog
{
  BorderLayout lytMain = new BorderLayout();
  JScrollPane scrList = new JScrollPane();
  JPanel pnlCenter = new JPanel();
  JButton btnClose = new JButton();
  JPanel jPanel2 = new JPanel();
  JPanel pnlTitle = new JPanel();
  JLabel lblTitle = new JLabel();
  FlowLayout lytTitle = new FlowLayout();
  FlowLayout lytCenter = new FlowLayout();
  SFList lstItems = new SFList();

  public SFPopupListView(Frame owner)
  {
    super(owner);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void showDialog(Frame parent, Collection items, ListCellRenderer renderer) {
    SFPopupListView list = new SFPopupListView(parent);
    if(renderer != null)
      list.lstItems.setCellRenderer(renderer);
    list.lstItems.setListData(items);
    list.show();
  }

  public void jbInit() {
    setLayout(lytMain);
    setSize(400, 350);
    scrList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    btnClose.setPreferredSize(new Dimension(100, 25));
    btnClose.setText("jButton1");
    pnlCenter.setLayout(lytCenter);
    lytMain.setHgap(5);
    lytMain.setVgap(5);
    lblTitle.setText("Users:");
    pnlTitle.setLayout(lytTitle);
    lytTitle.setAlignment(FlowLayout.LEFT);
    lytCenter.setAlignment(FlowLayout.RIGHT);
    lytCenter.setHgap(5);
    lytCenter.setVgap(5);
    pnlCenter.setBorder(BorderFactory.createEtchedBorder());
    this.getContentPane().add(scrList, BorderLayout.CENTER);
    this.getContentPane().add(pnlCenter,  BorderLayout.SOUTH);
    pnlCenter.add(btnClose, null);
    this.getContentPane().add(pnlTitle, BorderLayout.NORTH);
    pnlTitle.add(lblTitle, null);
    scrList.getViewport().add(lstItems, null);
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
	setVisible(false);
	dispose();
      }
    });
  }
}
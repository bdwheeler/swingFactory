package net.saucefactory.swing.common;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CAISO</p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import net.saucefactory.swing.utils.*;
import net.saucefactory.swing.SFDialog;
import net.saucefactory.swing.common.*;

public class SFPrintPreviewFrame2 extends SFDialog {
  private JPanel commandPanel = new JPanel();
  private JScrollPane centerScrollPane = new JScrollPane();
  private PrintPreviewPanel previewPanel = new PrintPreviewPanel();
  private JPanel centerPanel = new JPanel();
  private GridBagLayout centerLayout = new GridBagLayout();
  private JButton printButton = new JButton();
  private JButton closeButton = new JButton();

  int currentPage = -1;
  int panelWidth = 700; //900;
  int panelHeight = 906; //1165;
  int letterWidth = 700;
  int letterHeight = 906;
  int legalWidth = 700;
  int legalHeight = 1153;
  double originalWidth = -1;
  double originalHeight = -1;
  double resScale = 0;
  int orientation;
  boolean paperLetter = true;
  PageFormat format = null;
  Printable target = null;
  private JButton backButton = new JButton();
  private JButton nextButton = new JButton();
  private JComboBox orientationCombo = new JComboBox();
  private JComboBox paperCombo = new JComboBox();
  private JLabel orientationLabel = new JLabel();
  private JLabel paperLabel = new JLabel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel countLabel = new JLabel();
  private int suggestedWidth = 950;
  private int suggestedHeight = 700;
  public boolean doPrint = false;
  BorderLayout borderLayout1 = new BorderLayout();
  BufferedImage imgBuffer;
  BufferedImage dummyImage;
  int maxPageCount;

  public SFPrintPreviewFrame2(Frame parent, Printable _target, PageFormat _format) {
    super(parent);
    init(_target, _format);
  }

  public SFPrintPreviewFrame2(Printable _target, PageFormat _format) {
    init(_target, _format);
  }

  private void init(Printable _target, PageFormat _format) {
    try {
      setModal(true);
      target = _target;
      format = _format;
      Paper p = format.getPaper();
      resScale = panelWidth / p.getWidth();
      orientation = format.getOrientation();
      if(orientation == PageFormat.LANDSCAPE) {
        int tmpHolder = panelWidth;
        panelWidth = panelHeight;
        panelHeight = tmpHolder;
        orientationCombo.setEnabled(false);
      }
      jbInit();
      initImages();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public SFPrintPreviewFrame2 getInstance() {
    return this;
  }

  private void jbInit() throws Exception {
    setSize(new Dimension(suggestedWidth, suggestedHeight));
    this.getContentPane().setLayout(borderLayout1);
    printButton.setPreferredSize(new Dimension(100, 25));
    printButton.setText("Print");
    commandPanel.setBorder(BorderFactory.createEtchedBorder());
    commandPanel.setLayout(gridBagLayout1);
    previewPanel.setBackground(Color.white);
    previewPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    centerPanel.setLayout(centerLayout);
    closeButton.setPreferredSize(new Dimension(100, 25));
    closeButton.setText("Close");
    backButton.setPreferredSize(new Dimension(100, 25));
    backButton.setText("Previous");
    nextButton.setPreferredSize(new Dimension(100, 25));
    nextButton.setText("Next");
    orientationLabel.setText("Orientation:");
    orientationCombo.setPreferredSize(new Dimension(100, 25));
    paperLabel.setText("Paper Size:");
    paperCombo.setPreferredSize(new Dimension(100, 25));
    this.setTitle("Print Preview");
    getContentPane().add(commandPanel, BorderLayout.NORTH);
    commandPanel.add(printButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 10, 3, 0), 0, 0));
    commandPanel.add(closeButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
    commandPanel.add(orientationLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 10, 3, 0), 0, 0));
    commandPanel.add(orientationCombo, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
    commandPanel.add(paperLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 10, 3, 0), 0, 0));
    commandPanel.add(paperCombo, new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
    commandPanel.add(countLabel, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    commandPanel.add(backButton, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 0), 0, 0));
    commandPanel.add(nextButton, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 3, 10), 0, 0));
    getContentPane().add(centerScrollPane, BorderLayout.CENTER);
    centerScrollPane.getVerticalScrollBar().setUnitIncrement(30);
    centerScrollPane.getViewport().add(centerPanel, null);
    centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    centerPanel.add(previewPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    centerPanel.add(new JLabel(""), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0,
        0));

    printButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doPrint = true;
        setVisible(false);
      }
    });
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doPrint = false;
        setVisible(false);
      }
    });
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showPreview(currentPage + 1);
      }
    });
    backButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showPreview(currentPage - 1, true);
      }
    });
    orientationCombo.addItem(new String("Portrait"));
    orientationCombo.addItem(new String("Landscape"));
    if(orientation == PageFormat.LANDSCAPE)
      orientationCombo.setSelectedIndex(1);
    else
      orientationCombo.setSelectedIndex(0);
    orientationCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeOrientation();
      }
    });
    paperCombo.addItem(new String("Letter"));
    paperCombo.addItem(new String("Legal"));
    paperCombo.setSelectedIndex(0);
    paperCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changePaper();
      }
    });

  }

  private void setButtonState() {
    nextButton.setEnabled(currentPage + 1 < maxPageCount);
    backButton.setEnabled(currentPage > 0);
  }

  private void setCountLabel() {
    countLabel.setText("Page " + (currentPage + 1) + " of " + maxPageCount);
  }

  public PreviewResults getPrintResults() {
    PreviewResults rtn = new PreviewResults();
    rtn.proceed = this.doPrint;
    rtn.cnt = maxPageCount;
    return rtn;
  }

  private void changeOrientation() {
    try {
      int index = orientationCombo.getSelectedIndex();
      if((index == 0 && orientation == PageFormat.PORTRAIT) ||
          (index == 1 && orientation == PageFormat.LANDSCAPE))
        return;
      pauseMe();
      if(index == 0)
        orientation = PageFormat.PORTRAIT;
      else
        orientation = PageFormat.LANDSCAPE;
      int tmpHolder = panelWidth;
      panelWidth = panelHeight;
      panelHeight = tmpHolder;
      format.setOrientation(orientation);
      centerScrollPane.getViewport().remove(centerPanel);
      centerScrollPane.getViewport().add(centerPanel, null);
      if(target instanceof ISFPrintPreviewable)
        ((ISFPrintPreviewable)target).formatChanged(format);
      initImages();
    }
    catch(Exception e) {
      e.printStackTrace();
      SFMessageHandler.sendErrorMessage(getInstance(), "An error occured building print output",
          "Print Error");
    }
    finally {
      resumeMe();
    }
  }

  private void changePaper() {
    try {
      int index = paperCombo.getSelectedIndex();
      if((index == 0 && paperLetter) ||
          (index == 1 && !paperLetter))
        return;
      pauseMe();
      if(index == 0)
        paperLetter = true;
      else
        paperLetter = false;
      Paper p;
      if(paperLetter) {
        p = PrintingUtility.getLetterPaper();
        panelWidth = letterWidth;
        panelHeight = letterHeight;
      }
      else {
        p = PrintingUtility.getLegalPaper();
        panelWidth = legalWidth;
        panelHeight = legalHeight;
      }
      format.setPaper(p);
      if(orientation == PageFormat.LANDSCAPE) {
        int tmpHolder = panelWidth;
        panelWidth = panelHeight;
        panelHeight = tmpHolder;
      }
      centerScrollPane.getViewport().remove(centerPanel);
      centerScrollPane.getViewport().add(centerPanel, null);
      if(target instanceof ISFPrintPreviewable)
        ((ISFPrintPreviewable)target).formatChanged(format);
      initImages();
    }
    catch(Exception e) {
      e.printStackTrace();
      SFMessageHandler.sendErrorMessage(getInstance(), "An error occured building print output",
          "Print Error");
    }
    finally {
      resumeMe();
    }
  }

  private void pauseMe() {
    SFCursorManager.pauseDialogCursor(this);
  }

  private void resumeMe() {
    SFCursorManager.resumeDialogCursor(this);
  }

  private void initImages() {
    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          pauseMe();
          int pageIndex = 0;
          Paper p = format.getPaper();
          imgBuffer = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
          while(true) {
            Graphics2D g = imgBuffer.createGraphics();
            int rslt = drawImage(g, pageIndex, false);
            if(rslt == Printable.PAGE_EXISTS)
              pageIndex++;
            else
              break;
          }
          maxPageCount = pageIndex;
          showPreview(0);
        }
        catch(Exception e) {
          e.printStackTrace();
          SFMessageHandler.sendErrorMessage(getInstance(), "An error occured building print output",
              "Print Error");
        }
        finally {
          resumeMe();
        }
      }
    });
    t.start();
  }

  public int drawImage(Graphics2D g, int pageIndex, boolean loopUp) {
    try {
      int rslt;
      /*
      if(loopUp) {
        for(int i = 0; i < pageIndex; i++) {
          Graphics2D tmpG = imgBuffer.createGraphics();
          tmpG.scale(resScale, resScale);
          tmpG.addRenderingHints(PrintingUtility.renderingHints);
          //System.out.println("looped up: " + i);
          tmpG.setColor(Color.white);
          tmpG.fillRect(0, 0, (int)format.getWidth(), (int)format.getHeight());
          tmpG.setClip((int)(format.getImageableX()), (int)(format.getImageableY()),
              (int)(format.getImageableWidth()), (int)(format.getImageableHeight()));
          rslt = target.print(tmpG, format, i);
        }
        g = imgBuffer.createGraphics();
      }*/
      //System.out.println("drawing image: " + pageIndex);
      g.scale(resScale, resScale);
      g.addRenderingHints(PrintingUtility.renderingHints);
      g.setColor(Color.white);
      g.fillRect(0, 0, (int)format.getWidth(), (int)format.getHeight());
      //g.setClip((int)(format.getImageableX()), (int)(format.getImageableY()),
        //  (int)(format.getImageableWidth()), (int)(format.getImageableHeight()));
      rslt = target.print(g, format, pageIndex);
      return rslt;
    }
    catch(Exception e) {
      return Printable.NO_SUCH_PAGE;
    }
  }

  private void showPreview(int page) {
    showPreview(page, false);
  }

  private void showPreview(int page, boolean loopUp) {
    try {
      currentPage = page;
      Graphics2D g = imgBuffer.createGraphics();
      drawImage(g, currentPage, loopUp);
      previewPanel.revalidate();
      previewPanel.repaint();
      setButtonState();
      setCountLabel();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static PreviewResults showPreview(Printable target, PageFormat format) {
    SFPrintPreviewFrame2 previewFrame = new SFPrintPreviewFrame2(target, format);
    ScreenUtility.centerOnScreen(previewFrame);
    previewFrame.showNormal();
    if(!previewFrame.paperLetter)
      format.setPaper(PrintingUtility.getLegalPaper());
    format.setOrientation(previewFrame.orientation);
    return previewFrame.getPrintResults();
  }

  public static PreviewResults showPreview(Frame parent, Printable target, PageFormat format) {
    SFPrintPreviewFrame2 previewFrame = new SFPrintPreviewFrame2(parent, target, format);
    ScreenUtility.centerOnScreen(previewFrame);
    previewFrame.showNormal();
    if(!previewFrame.paperLetter)
      format.setPaper(PrintingUtility.getLegalPaper());
    format.setOrientation(previewFrame.orientation);
    return previewFrame.getPrintResults();
  }

  class PrintPreviewPanel extends JPanel {

    public PrintPreviewPanel() {
    }

    public Dimension getPreferredSize() {
      return new Dimension(panelWidth, panelHeight);
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }

    public Dimension getMaximumSize() {
      return getPreferredSize();
    }

    public void paint(Graphics g) {
      g.setColor(Color.white);
      Dimension d = getPreferredSize();
      g.fillRect(0, 0, d.width, d.height);
      ((Graphics2D)g).setRenderingHints(PrintingUtility.renderingHints);
      if(currentPage > -1)
        g.drawImage(imgBuffer, 0, 0, this);
      paintBorder(g);
    }
  }

  public static class PreviewResults {
    public boolean proceed = false;
    public int cnt = 0;
  }
}

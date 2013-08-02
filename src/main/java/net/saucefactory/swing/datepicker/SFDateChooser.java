package net.saucefactory.swing.datepicker;

import net.saucefactory.swing.buttons.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SFDateChooser
  extends JPanel
  implements ISFDateChangeListener {
        public static final int FORMAT_MM_dd_yyyy = SFDateTextField.FORMAT_MM_dd_yyyy;
        public static final int FORMAT_dd_MM_yyyy = SFDateTextField.FORMAT_dd_MM_yyyy;
        protected static final String TOOL_TIP = "For help, select this field and press F1";
        protected static String HELP_TIP = "Down arrow key displays the date picker\n" +
                                         "Arrow keys navigate dates\n" +
                                         "Page Up/Down keys increment/decrement months\n" +
                                         "Control-Page Up/Down keys increment/decrement years\n" +
                                         "Home key selects the first day of the current month\n" +
                                         "End key selects the last day of the current month\n" +
                                         "Esc key hides the date picker";

        public int format = 0;

  protected SFArrowButton popupButton = new SFArrowButton(SFArrowButton.SOUTH);
  protected SFDateTextField dtsText = null;

  protected SFPopupDatePicker picker = null;

  protected Vector listeners = new Vector(2);

  public SFDateChooser(Window parent) {
    this(new SFPopupDatePicker(parent), FORMAT_MM_dd_yyyy);
  }

  public SFDateChooser(Window parent, int format) {
    this(new SFPopupDatePicker(parent), format);
  }

  public SFDateChooser(SFPopupDatePicker existingPicker) {
    this(existingPicker, FORMAT_MM_dd_yyyy);
  }

  public SFDateChooser(SFPopupDatePicker existingPicker, int format) {
                this.format = format;
    picker = existingPicker;
    init();
    initDateChangeNotification();
  }

        public void setFormat(int format) {
                this.format = format;
                dtsText.setFormat(format);
        }

        public void setToolTipText(String txt) {
          dtsText.setToolTipText(txt);
        }

        public void setHelpText(String txt) {
          HELP_TIP = txt;
        }

  protected void init() {
    this.setLayout(new GridBagLayout());
    this.setOpaque(false);
    this.setMinimumSize(new Dimension(55, 21));
    this.setPreferredSize(new Dimension(105, 21));

    dtsText = new SFDateTextField(format, false);
                dtsText.addKeyListener(new KeyHandler(this));
    dtsText.addFocusListener(new FocusHandler());
                dtsText.setToolTipText(TOOL_TIP);

    popupButton.setMinimumSize(new Dimension(16, 21));
    popupButton.setMaximumSize(new Dimension(16, 21));
    popupButton.setPreferredSize(new Dimension(16, 21));
    popupButton.setActionCommand("POPUP");
    popupButton.addActionListener(new ButtonHandler(this));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = gbc.EAST;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = gbc.BOTH;
    gbc.gridx = 0;
    gbc.insets = new Insets(0,1,0,0);
    add(dtsText, gbc);

    gbc.anchor = gbc.WEST;
    gbc.gridx = 1;
    gbc.weightx = 0.00;
    gbc.fill = gbc.VERTICAL;
    add(popupButton, gbc);
  }

  protected void initDateChangeNotification() {
    dtsText.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        handleDateTextChange();
      }
      public void removeUpdate(DocumentEvent e) {}
      public void changedUpdate(DocumentEvent e) {}
    });
  }

        public void setKeepFocus(boolean b) {
                dtsText.setKeepFocus(b);
        }

        public void setSelectedTextColor(Color c) {
          dtsText.setSelectedTextColor(c);
        }

        public void setSelectionColor(Color c) {
          dtsText.setSelectionColor(c);
        }

        public void setTextBackground(Color c) {
          dtsText.setBackground(c);
          dtsText.setSelectedTextColor(c);
        }

        public void setTextForeground(Color c) {
          dtsText.setForeground(c);
        }

        public void setTextFont(Font c) {
          dtsText.setFont(c);
        }

        public void setButtonBackground(Color c) {
          popupButton.setBackground(c);
        }

  public void addDateChangeListener(ISFDateChangeListener listener) {
    listeners.add(listener);
  }

  public void removeDateChangeListener(ISFDateChangeListener listener) {
    listeners.remove(listener);
  }

  public void removeAllDateChangeListeners() {
    listeners.removeAllElements();
  }

  protected void notifyListeners(Date newDate) {
    Iterator iter = listeners.iterator();
    while (iter.hasNext())
      ((ISFDateChangeListener)iter.next()).dateChanged(newDate);
  }

  public void handleDateTextChange() {
    try {
      if (dtsText.getText().length() < 10) return;
      Date date = dtsText.getDate();
      notifyListeners(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setEditable(boolean editable) {
    popupButton.setEnabled(editable);
    dtsText.setEditable(editable);
  }

  public void setEnabled(boolean enabled) {
    popupButton.setEnabled(enabled);
    dtsText.setEnabled(enabled);
  }

  public void setDate(Date dts) {
    dtsText.setText(dateToString(dts));
  }

  public String getText() {
    return dtsText.getText();
  }

  public void setText(String newText) {
                Date date = stringToDate(newText);
                if (date != null) setDate(date);
  }

  public SFDateTextField getTextField() {
    return dtsText;
  }

  public Date getDateTime() {
    try {
      if(dtsText.getText().equals(""))
        return null;
      return dtsText.getDate();
    } catch(Exception e) {
                  return null;
                }
  }

  public Date getEndOfDayDateTime() {
    try {
                    Calendar cal = Calendar.getInstance();
      if(dtsText.getText().equals("")) {
                                cal.setTime(new Date());
      } else {
                                cal.setTime(stringToDate(dtsText.getText()));
                        }
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        cal.set(Calendar.MILLISECOND, 999);
      return cal.getTime();
    } catch(Exception e) {
      return new Date();
    }
  }

  public void dateChanged(Date date) {
    setText(dateToString(date));
  }

  public Date stringToDate(String dateStr) {
    try {
      return dtsText.parseDate(dateStr);
    } catch(Exception e){
      return null;
    }
  }

  public String dateToString(Date date) {
    try {
      return dtsText.formatDate(date);
    } catch(Exception e){
      return null;
    }
  }

  class KeyHandler
    implements KeyListener {
    private SFDateChooser dateChooser = null;

    public KeyHandler(SFDateChooser chooser) {
      dateChooser = chooser;
    }

    public void keyPressed(KeyEvent e) {
      if(e.getKeyCode() == KeyEvent.VK_DOWN) {
        Point p = picker.calculatePopupPosition(picker.getWidth(), picker.getHeight(), popupButton);
        Date date = getDateTime();
        if (date == null) date = new Date();
        picker.popupAt(dateChooser, date, p.x, p.y);
      } else if(e.getKeyCode() == KeyEvent.VK_F1) {
        JOptionPane.showMessageDialog(dateChooser, HELP_TIP);
      } else {
        return;
      }
      e.consume();
    }

    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}
  }

  class FocusHandler
    implements FocusListener {

    public void focusGained(FocusEvent e) {
      ((JTextField)e.getComponent()).selectAll();
    }

    public void focusLost(FocusEvent e) {}
  }


  class ButtonHandler implements ActionListener {
    private SFDateChooser dateChooser = null;

    public ButtonHandler(SFDateChooser chooser) {
      dateChooser = chooser;
    }

      public void actionPerformed(ActionEvent ae) {
      String command = ae.getActionCommand();
      if(command.equals("POPUP")) {
        Point p = picker.calculatePopupPosition(picker.getWidth(), picker.getHeight(), popupButton);
        Date date = getDateTime();
        if (date == null) date = new Date();
        dtsText.requestFocus();
        picker.popupAt(dateChooser, date, p.x, p.y);
      }
    }
  }
}

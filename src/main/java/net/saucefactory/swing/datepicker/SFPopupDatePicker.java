package net.saucefactory.swing.datepicker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.saucefactory.swing.buttons.SFArrowButton;
import net.saucefactory.swing.popup.SFPopupWindow;

public class SFPopupDatePicker extends SFPopupWindow {

	protected static final String CMD_ADDYEAR = "A";
	protected static final String CMD_MINUSYEAR = "M";
	protected static final String CMD_ADDMONTH = "AA";
	protected static final String CMD_MINUSMONTH = "MM";
	protected static final String CMD_DAYCLICK = "D";

	protected static final String[] tempMonths = new DateFormatSymbols().getMonths();

	protected ISFDateChangeListener listener = null;

	protected DateLabel[][] dayLabels = new DateLabel[6][7];
	protected JLabel[] dowLabels = new JLabel[7];
	protected JTextField sMonthName = new JTextField();
	protected JTextField sYearTextField = new JTextField();
	protected JPanel buttonPanel = new JPanel();
	protected SFArrowButton sSubtractYearButton = new SFArrowButton(SFArrowButton.WEST);
	protected SFArrowButton sAddYearButton = new SFArrowButton(SFArrowButton.EAST);
	protected SFArrowButton sSubtractMonthButton = new SFArrowButton(SFArrowButton.WEST);
	protected SFArrowButton sAddMonthButton = new SFArrowButton(SFArrowButton.EAST);

	protected Color foregroundColor = Color.black;
	protected Color backgroundColor = Color.white;
	protected Color inactiveMonthForegroundColor = Color.gray;
	protected Color selectedForegroundColor = Color.white;
	protected Color selectedBackgroundColor = Color.blue.darker();
	protected Color selectedCircleColor = Color.red;

	protected int lastDay;
	protected int currentMonth = 0;
	protected int currentYear = 2001;
	protected DateLabel lastLabel = null;

	protected Calendar calendar = Calendar.getInstance();

	private static SFPopupDatePicker internalInstance = null;

	public SFPopupDatePicker(Window parent) {
		super(parent);
		try {
			init();
			initDate(new Date());
			buildDayPanel();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void initializeSingleton(Frame toplevelFrame) {
		internalInstance = new SFPopupDatePicker(toplevelFrame);
	}

	public static SFPopupDatePicker getSingleton() {
		return internalInstance;
	}

	public void setBackground(Color c) {
		super.setBackground(c);
		if(buttonPanel != null)
			buttonPanel.setBackground(c);
	}

	public void setDayHeaderFont(Font f) {
		for(int i = 0; i < dowLabels.length; i++) {
			dowLabels[i].setFont(f);
		}
	}

	public void setDayHeaderBackground(Color c) {
		for(int i = 0; i < dowLabels.length; i++) {
			dowLabels[i].setBackground(c);
		}
	}

	public void setDayHeaderForeground(Color c) {
		for(int i = 0; i < dowLabels.length; i++) {
			dowLabels[i].setForeground(c);
		}
	}

	public void setDateForeground(Color c) {
		foregroundColor = c;
		buildDayPanel();
	}

	public void setInactiveMonthDateForeground(Color c) {
		inactiveMonthForegroundColor = c;
		buildDayPanel();
	}

	public void setDateBackground(Color c) {
		backgroundColor = c;
		buildDayPanel();
	}

	public void setSelectedDateForeground(Color c) {
		selectedForegroundColor = c;
		if(lastLabel != null)
			lastLabel.repaint();
	}

	public void setSelectedDateCircle(Color c) {
		selectedCircleColor = c;
		if(lastLabel != null)
			lastLabel.repaint();
	}

	public void setSelectedDateBackground(Color c) {
		selectedBackgroundColor = c;
		if(lastLabel != null)
			lastLabel.repaint();
	}

	public void setDateFont(Font f) {
		for(int i = 0; i < dayLabels.length; i++) {
			for(int j = 0; j < dayLabels[i].length; j++)
				dayLabels[i][j].setFont(f);
		}
	}

	public void setButtonFont(Font f) {
		Component[] comps = buttonPanel.getComponents();
		for(int i = 0; i < comps.length; i++) {
			comps[i].setFont(f);
		}
	}

	public void setMonthFont(Font f) {
		sMonthName.setFont(f);
	}

	public void setMonthForeground(Color c) {
		sMonthName.setDisabledTextColor(c);
	}

	public void setYearForeground(Color c) {
		sYearTextField.setDisabledTextColor(c);
	}

	public void setMonthBackground(Color c) {
		sMonthName.setBackground(c);
	}

	public void setYearBackground(Color c) {
		sYearTextField.setBackground(c);
	}

	public void setButtonBackground(Color c) {
		sSubtractYearButton.setBackground(c);
		sAddYearButton.setBackground(c);
		sSubtractMonthButton.setBackground(c);
		sAddMonthButton.setBackground(c);
		Component[] comps = buttonPanel.getComponents();
		for(int i = 0; i < comps.length; i++) {
			comps[i].setBackground(c);
		}
	}

	public void addMonth() {
		addMonth(true);
	}

	public void addYear() {
		addYear(true);
	}

	public void subtractMonth() {
		subtractMonth(true);
	}

	public void subtractYear() {
		subtractYear(true);
	}

	public void firstDayOfMonth() {
		firstDayOfMonth(true);
	}

	public void lastDayOfMonth() {
		lastDayOfMonth(true);
	}

	public void subtractDay() {
		subtractDay(true);
	}

	public void addDay() {
		addDay(true);
	}

	public void subtractWeek() {
		subtractWeek(true);
	}

	public void addWeek() {
		addWeek(true);
	}

	public void popupAt(ISFDateChangeListener al, int x, int y) {
		listener = al;
		popupAt(x, y);
	}

	public void popupAt(ISFDateChangeListener al, Date date, int x, int y) {
		listener = al;
		this.setSelectedDate(date);
		popupAt(x, y);
	}

	public Date getSelectedDate() {
		try {
			calendar.set(currentYear, currentMonth + lastLabel.monthAdj, lastDay);
			Date rtnDate = calendar.getTime();
			return rtnDate;
		} catch (Exception e) {
			return null;
		}
	}

	public void setSelectedDate(Date date) {
		if(date == null)
			date = new Date();
		calendar.setTime(date);
		currentYear = calendar.get(Calendar.YEAR);
		currentMonth = calendar.get(Calendar.MONTH);
		;
		sYearTextField.setText(new Integer(currentYear).toString());
		setMonth(currentMonth);
		lastDay = calendar.get(Calendar.DATE);
		buildDayPanel();
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawBorder(g);
	}

	protected void init() throws Exception {
		this.setBackground(SystemColor.lightGray);
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(204, 210));
		this.addKeyListener(new KeyHandler());
		foregroundColor = this.getForeground();

		sMonthName.setFont(new java.awt.Font("Dialog", 0, 11));
		sMonthName.setMaximumSize(new Dimension(70, 20));
		sMonthName.setMinimumSize(new Dimension(70, 20));
		sMonthName.setPreferredSize(new Dimension(70, 20));
		sMonthName.setDisabledTextColor(foregroundColor);
		sMonthName.addFocusListener(popupListener);
		sMonthName.setEditable(false);
		sMonthName.setEnabled(false);
		sMonthName.setBackground(Color.white);
		sMonthName.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
		sMonthName.setHorizontalAlignment(SwingConstants.CENTER);

		sYearTextField.setFont(new java.awt.Font("Dialog", 0, 11));
		sYearTextField.setMaximumSize(new Dimension(50, 20));
		sYearTextField.setMinimumSize(new Dimension(50, 20));
		sYearTextField.setPreferredSize(new Dimension(50, 20));
		sYearTextField.setDisabledTextColor(foregroundColor);
		sYearTextField.setEditable(false);
		sYearTextField.setEnabled(false);
		sYearTextField.setBackground(Color.white);
		sYearTextField.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
		sYearTextField.addFocusListener(popupListener);
		sYearTextField.setHorizontalAlignment(SwingConstants.CENTER);

		ActionHandler pickHandler = new ActionHandler();

		sSubtractYearButton.setActionCommand(this.CMD_MINUSYEAR);
		sSubtractYearButton.setSize(6, 6);
		sSubtractYearButton.setMnemonic('0');
		sSubtractYearButton.addActionListener(pickHandler);

		sAddYearButton.setActionCommand(this.CMD_ADDYEAR);
		sAddYearButton.setMargin(new Insets(0, 0, 0, 0));
		sAddYearButton.setMnemonic('0');
		sAddYearButton.addActionListener(pickHandler);

		sSubtractMonthButton.setActionCommand(this.CMD_MINUSMONTH);
		sSubtractMonthButton.setSize(6, 6);
		sSubtractMonthButton.setMnemonic('0');
		sSubtractMonthButton.addActionListener(pickHandler);

		sAddMonthButton.setActionCommand(this.CMD_ADDMONTH);
		sAddMonthButton.setMargin(new Insets(0, 0, 0, 0));
		sAddMonthButton.setMnemonic('0');
		sAddMonthButton.addActionListener(pickHandler);

		String[] tempDays = new DateFormatSymbols().getWeekdays();
		for(int i = 0; i < 7; i++) {
			dowLabels[i] = new JLabel();
			dowLabels[i].setBackground(Color.lightGray);
			dowLabels[i].setForeground(Color.white);
			dowLabels[i].setOpaque(true);
			dowLabels[i].setHorizontalTextPosition(SwingConstants.CENTER);
			dowLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
		}
		String day;
		char firstLetter[] = new char[1];
		for(int j = 0; j < 7; j++) {
			day = tempDays[j + 1].toUpperCase();
			firstLetter[0] = day.charAt(0);
			dowLabels[j].setText(new String(firstLetter));
		}

		JPanel sDayLayoutPanel = new JPanel();
		GridLayout dayGrid = new GridLayout();
		dayGrid.setColumns(7);
		dayGrid.setRows(0);
		dayGrid.setHgap(0);
		sDayLayoutPanel.setLayout(dayGrid);
		sDayLayoutPanel.setBackground(backgroundColor);
		sDayLayoutPanel.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 1));
		MouseHandler mouseHandler = new MouseHandler();

		for(int i = 0; i < 7; i++)
			sDayLayoutPanel.add(dowLabels[i]);
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 7; j++) {
				dayLabels[i][j] = new DateLabel();
				dayLabels[i][j].setHorizontalTextPosition(SwingConstants.CENTER);
				dayLabels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				dayLabels[i][j].addMouseListener(mouseHandler);
				dayLabels[i][j].setForeground(foregroundColor);
				dayLabels[i][j].setBackground(backgroundColor);
				dayLabels[i][j].setOpaque(true);
				dayLabels[i][j].x = j;
				dayLabels[i][j].y = i;
				sDayLayoutPanel.add(dayLabels[i][j]);
			}
		}

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
		cancelButton.setMargin(new Insets(2, 2, 2, 2));
		cancelButton.setMaximumSize(new Dimension(50, 20));
		cancelButton.setMinimumSize(new Dimension(50, 20));
		cancelButton.setPreferredSize(new Dimension(50, 20));
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});

		JButton okButton = new JButton("OK");
		okButton.setFont(new java.awt.Font("Dialog", Font.BOLD, 11));
		okButton.setMargin(new Insets(2, 2, 2, 2));
		okButton.setMaximumSize(new Dimension(50, 20));
		okButton.setMinimumSize(new Dimension(50, 20));
		okButton.setPreferredSize(new Dimension(50, 20));
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				notifyListener();
				hide();
			}
		});

		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 2),
						0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0,
						0), 0, 0));

		this.add(sSubtractMonthButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 3, 0,
						0), 0, 0));
		this.add(sMonthName, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
						new Insets(5, 0, 0, 0), 0, 0));
		this.add(sAddMonthButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),
						0, 0));
		this.add(sSubtractYearButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0,
						0), 0, 0));
		this.add(sYearTextField, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),
						0, 0));
		this.add(sAddYearButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 3),
						0, 0));
		this.add(sDayLayoutPanel, new GridBagConstraints(0, 1, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 8, 5, 8), 0, 0));
		this.add(buttonPanel,
						new GridBagConstraints(0, 2, 6, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 2, 5, 8), 0, 0));
	}

	protected void initDate(Date today) {
		calendar.setTime(today);
		currentYear = calendar.get(Calendar.YEAR);
		currentMonth = calendar.get(Calendar.MONTH);
		lastDay = calendar.get(Calendar.DATE);
		setMonth(calendar.get(Calendar.MONTH));
		sYearTextField.setText(String.valueOf(currentYear));
	}

	protected void drawBorder(Graphics g) {
		Color c = g.getColor();
		g.setColor(SystemColor.windowBorder);
		int w = this.getWidth();
		int h = this.getHeight();
		g.drawLine(0, 0, 0, h);
		g.drawLine(0, 0, w, 0);
		g.drawLine(w - 1, 0, w - 1, h);
		g.drawLine(0, h - 1, w, h - 1);
		g.setColor(c);
	}

	protected void buildDayPanel() {
		int month = currentMonth;
		int selectedDay = lastDay;
		int year = currentYear;
		calendar.set(currentYear, currentMonth, 1);
		int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int dayCount = calendar.getActualMaximum(calendar.DATE);
		calendar.add(calendar.MONDAY, -1);
		int prevMoDay = calendar.getActualMaximum(calendar.DATE);
		for(int i = firstDay - 1; i >= 0; i--) {
			dayLabels[0][i].setText(String.valueOf(prevMoDay));
			dayLabels[0][i].setForeground(inactiveMonthForegroundColor);
			dayLabels[0][i].monthAdj = -1;
			dayLabels[0][i].setHilited(false);
			prevMoDay--;
		}
		int row = 0, column = firstDay;
		for(int i = 0; i < dayCount; i++) {
			if(column > 6) {
				row++;
				column = 0;
			}
			String number = String.valueOf(i + 1);
			dayLabels[row][column].setText(number);
			dayLabels[row][column].setForeground(foregroundColor);
			dayLabels[row][column].monthAdj = 0;
			if(i == selectedDay - 1) {
				dayLabels[row][column].setHilited(true);
				lastDay = selectedDay;
				lastLabel = dayLabels[row][column];
			}
			else {
				dayLabels[row][column].setHilited(false);
			}
			column++;
		}
		int postDate = 1;
		for(int i = row; i < 6; i++) {
			for(int j = column; j < 7; j++) {
				dayLabels[i][j].setText(String.valueOf(postDate));
				dayLabels[i][j].setForeground(inactiveMonthForegroundColor);
				dayLabels[i][j].monthAdj = 1;
				dayLabels[i][j].setHilited(false);
				postDate++;
			}
			column = 0;
		}
	}

	protected void notifyListener() {
		try {
			if(listener == null)
				return;
			listener.dateChanged(getSelectedDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected DateLabel changeDayLabel(int week, int day) {
		return changeDayLabel(dayLabels[week][day]);
	}

	protected DateLabel changeDayLabel(DateLabel newLabel) {
		if(lastLabel != null)
			lastLabel.setHilited(false);
		newLabel.setHilited(true);
		return newLabel;
	}

	protected void setMonth(int month) {
		sMonthName.setText(tempMonths[month]);
		currentMonth = month;
	}

	protected void addMonth(boolean notify) {
		if(currentMonth == 11) {
			addYear(false);
			currentMonth = 0;
		}
		else
			currentMonth++;
		setMonth(currentMonth);
		buildDayPanel();
		if(notify)
			notifyListener();
	}

	protected void addYear(boolean notify) {
		currentYear++;
		sYearTextField.setText(String.valueOf(currentYear));
		buildDayPanel();
		if(notify)
			notifyListener();
	}

	protected void subtractMonth(boolean notify) {
		if(currentMonth == 0) {
			subtractYear(false);
			currentMonth = 11;
		}
		else
			currentMonth--;
		setMonth(currentMonth);
		buildDayPanel();
		if(notify)
			notifyListener();
	}

	protected void subtractYear(boolean notify) {
		currentYear--;
		sYearTextField.setText(String.valueOf(currentYear));
		buildDayPanel();
		if(notify)
			notifyListener();
	}

	protected void firstDayOfMonth(boolean notify) {
		outerLabel: for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 7; j++) {
				if(dayLabels[i][j].monthAdj == 0) {
					lastLabel = changeDayLabel(i, j);
					lastDay = Integer.parseInt(lastLabel.getText());
					break outerLabel;
				}
			}
		}
		if(notify)
			notifyListener();
	}

	protected void lastDayOfMonth(boolean notify) {
		outerLabel: for(int i = 5; i >= 4; i--) {
			for(int j = 6; j >= 0; j--) {
				if(dayLabels[i][j].monthAdj == 0) {
					lastLabel = changeDayLabel(i, j);
					lastDay = Integer.parseInt(lastLabel.getText());
					break outerLabel;
				}
			}
		}
		if(notify)
			notifyListener();
	}

	protected void subtractDay(boolean notify) {
		int x = lastLabel.x - 1;
		int y = lastLabel.y;
		if(x == -1) {
			x = 6;
			if(y != 0)
				y--;
			else {
				subtractMonth(false);
				lastDayOfMonth(false);
				return;
			}
		}
		if(dayLabels[y][x].getText().length() == 0) {
			subtractMonth(false);
			lastDayOfMonth(false);
			return;
		}
		lastLabel = changeDayLabel(y, x);
		lastDay = Integer.parseInt(lastLabel.getText());
		if(notify)
			notifyListener();
	}

	protected void addDay(boolean notify) {
		int x = lastLabel.x + 1;
		int y = lastLabel.y;
		if(x == 7) {
			x = 0;
			if(y != 5)
				y++;
			else {
				addMonth(false);
				firstDayOfMonth(false);
				return;
			}
		}
		if(dayLabels[y][x].getText().length() == 0) {
			addMonth(false);
			firstDayOfMonth(false);
			return;
		}
		lastLabel = changeDayLabel(y, x);
		lastDay = Integer.parseInt(lastLabel.getText());
		if(notify)
			notifyListener();
	}

	protected void addWeek(boolean notify) {
		int x = lastLabel.x;
		int y = lastLabel.y + 1;
		if(y == 6) {
			addMonth(false);
			y = 0;
		}
		while(dayLabels[y][x].getText().length() == 0) {
			y++;
			if(y == 6) {
				addMonth(false);
				y = 0;
			}
		}
		lastLabel = changeDayLabel(y, x);
		lastDay = Integer.parseInt(lastLabel.getText());
		if(notify)
			notifyListener();
	}

	protected void subtractWeek(boolean notify) {
		int x = lastLabel.x;
		int y = lastLabel.y - 1;
		if(y == -1) {
			subtractMonth(false);
			y = 5;
		}
		while(dayLabels[y][x].getText().length() == 0) {
			y--;
			if(y == -1) {
				subtractMonth(false);
				y = 5;
			}
		}
		lastLabel = changeDayLabel(y, x);
		lastDay = Integer.parseInt(lastLabel.getText());
		if(notify)
			notifyListener();
	}

	class ActionHandler implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			String strCommand = ae.getActionCommand();
			if(strCommand == CMD_ADDYEAR) {
				addYear(false);
			}
			else
				if(strCommand == CMD_MINUSYEAR) {
					subtractYear(false);
				}
				else
					if(strCommand == CMD_ADDMONTH) {
						addMonth(false);
					}
					else
						if(strCommand == CMD_MINUSMONTH) {
							subtractMonth(false);
						}
		}
	}

	class DateLabel extends JLabel {

		public int x = -1;
		public int y = -1;
		public int monthAdj = 0;
		public boolean hilite = false;

		public void paint(Graphics g) {
			super.paint(g);
			if(hilite)
				paintHilite(g);
		}

		public void setHilited(boolean b) {
			hilite = b;
			repaint();
		}

		protected void paintHilite(Graphics g) {
			Color c = g.getColor();
			int w = this.getWidth();
			int h = this.getHeight();
			g.setColor(selectedBackgroundColor);
			g.fillOval(4, 2, w - 5, h - 2);
			g.setColor(selectedCircleColor);
			g.drawLine((w / 2) - 5, 1, w / 2, 2);
			g.drawLine((w / 2) - 5, 1, w / 2, 3);
			g.drawLine(w / 2, 2, (w / 2) + 9, 1);
			g.drawLine(w / 2, 3, (w / 2) + 9, 1);
			g.drawOval(4, 2, w - 5, h - 2);
			g.drawOval(5, 3, w - 7, h - 4);
			g.setColor(selectedForegroundColor);
			String text = getText();
			int h1 = g.getFontMetrics().getAscent();
			int w1 = g.getFontMetrics().stringWidth(text);
			Font f = g.getFont();
			Font newF = f.deriveFont(Font.ITALIC + Font.BOLD);
			g.setFont(newF);
			g.drawString(text, (getWidth() - w1) / 2, h1);
			g.setFont(f);
			g.setColor(c);
		}
	}

	class KeyHandler implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if(e.isConsumed())
				return;
			if(e.getKeyCode() == e.VK_PAGE_DOWN) {
				if(e.getModifiers() == e.CTRL_MASK)
					subtractYear(false);
				else
					subtractMonth(false);
			}
			else
				if(e.getKeyCode() == e.VK_PAGE_UP) {
					if(e.getModifiers() == e.CTRL_MASK)
						addYear(false);
					else
						addMonth(false);
				}
				else
					if(e.getKeyCode() == e.VK_ENTER) {
						notifyListener();
						hide();
					}
					else
						if(e.getKeyCode() == e.VK_ESCAPE) {
							hide();
						}
						else
							if(e.getKeyCode() == e.VK_HOME) {
								firstDayOfMonth(false);
							}
							else
								if(e.getKeyCode() == e.VK_END) {
									lastDayOfMonth(false);
								}
								else
									if(e.getKeyCode() == e.VK_LEFT) {
										subtractDay(false);
									}
									else
										if(e.getKeyCode() == e.VK_RIGHT) {
											addDay(false);
										}
										else
											if(e.getKeyCode() == e.VK_UP) {
												subtractWeek(false);
											}
											else
												if(e.getKeyCode() == e.VK_DOWN) {
													addWeek(false);
												}
												else
													return;
			e.consume();
		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	class MouseHandler extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			int i = e.getClickCount();
			if(i > 1) {
				notifyListener();
				hide();
			}
		}

		public void mousePressed(MouseEvent e) {
			DateLabel lbl = (DateLabel)e.getSource();
			try {
				lastDay = Integer.parseInt(lbl.getText());
			} catch (NumberFormatException nfe) {
				return;
			}
			lastLabel = changeDayLabel(lbl);
		}

		public void mouseReleased(MouseEvent e) {
		}
	}
}

package net.saucefactory.swing.diff;

import javax.swing.JPanel;
import java.util.Vector;
import java.awt.Graphics;
import net.saucefactory.text.diff.Diff;

public class DiffOverviewPanel extends JPanel {

  public Vector notEqualItems = new Vector();

  public DiffOverviewPanel() {

  }

  public void setDiff(Diff diff) {

  }

  public void paint(Graphics g) {

  }

  class DiffLineHolder {
    public int line;
    public int status;

    public DiffLineHolder(int line, int status) {
      this.line = line;
      this.status = status;
    }
  }
}

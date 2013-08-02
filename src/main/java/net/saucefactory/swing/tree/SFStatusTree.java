package net.saucefactory.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.UIManager;

public class SFStatusTree extends JTree {

  public static ImageIcon dirOpenUnchanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder.png"));
  public static ImageIcon dirClosedUnchanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_closed.png"));
  public static ImageIcon fileUnchanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_unchanged.png"));
  public static ImageIcon dirOpenChanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_change_open.png"));
  public static ImageIcon dirClosedChanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_change_closed.png"));
  public static ImageIcon fileChanged = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_changed.png"));
  public static ImageIcon dirOpenAdded = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_add_open.png"));
  public static ImageIcon dirClosedAdded = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_add_closed.png"));
  public static ImageIcon fileAdded = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_added.png"));
  public static ImageIcon dirOpenDeleted = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_delete_open.png"));
  public static ImageIcon dirClosedDeleted = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_delete_open.png"));
  public static ImageIcon fileDeleted = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_deleted.png"));
  public static ImageIcon dirOpenMixed = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_mixed_open.png"));
  public static ImageIcon dirClosedMixed = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_mixed_closed.png"));
  public static ImageIcon dirGhost = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/folder_ghost.png"));
  public static ImageIcon fileGhost = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("net/saucefactory/swing/diff/images/file_ghost.png"));

  private Font treeFont = UIManager.getFont("Tree.font");
  private DefaultTreeModel defaultModel;
  private StatusTreeRenderer statusRenderer = new StatusTreeRenderer();
  public RootStatusNode rootNode = new RootStatusNode("Root", null);
  public StatusNode highlightTarget = null;

  public static final int STATUS_BLANK = 0;
  public static int TREE_MODE_SOURCE = 1;
  public static int TREE_MODE_TARGET = 2;
  public static int TREE_MODE_STANDARD = TREE_MODE_SOURCE | TREE_MODE_TARGET;

  public static final int STATUS_UNCHANGED = 1;
  public static final int STATUS_CHANGED = 2;
  public static final int STATUS_ADDED = 4;
  public static final int STATUS_DELETED = 8;
  public static final int STATUS_ALL = STATUS_UNCHANGED | STATUS_CHANGED | STATUS_ADDED | STATUS_DELETED;

  public static Color COLOR_STATUS_ADDED = new Color(45, 75, 150);
  public static Color COLOR_STATUS_DELETED = new Color(0, 100, 210);
  public static Color COLOR_STATUS_CHANGED = new Color(243, 26, 37);
  public static Color COLOR_STATUS_UNCHANGED = new Color(0, 0, 0);
  public static Color COLOR_STATUS_UNCHANGED_BG = new Color(255, 245, 200);

  public static Color COLOR_TREE_SELECTION_BACKGROUND = UIManager.getColor("Table.selectionBackground");
  public static Color COLOR_TREE_SELECTION_FOREGROUND = UIManager.getColor("Table.selectionForeground");

  private int treeMode;
  public int currentFilter = STATUS_ALL;
  private boolean forceAllAdded = false;
  private boolean forceAllDeleted = false;

  public SFStatusTree() {
    this(null, STATUS_ALL, TREE_MODE_STANDARD);
  }

  public SFStatusTree(int currentFilter, int treeMode) {
    this(null, currentFilter, treeMode);
  }

  public SFStatusTree(DefaultTreeModel treeModel, int currentFilter, int treeMode) {
    try {
      this.currentFilter = currentFilter;
      if(treeModel == null)
        defaultModel = new StatusFilterModel(rootNode);
      else
        defaultModel = treeModel;
      setCellRenderer(statusRenderer);
      setTreeMode(treeMode);
      setModel(defaultModel);
      setRootVisible(false);
      setShowsRootHandles(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setTreeMode(int treeMode) {
    this.treeMode = treeMode;
    statusRenderer.setTreeMode(treeMode);
  }

  public int getTreeMode() {
    return treeMode;
  }

  public void setCurrentFilter(int currentFilter) {
    this.currentFilter = currentFilter;
  }

  public void setRoot(TreeNode root) {
    rootNode = (RootStatusNode)root;
    setRoot(rootNode);
  }

  public DefaultTreeModel getStatusModel() {
    return defaultModel;
  }

  public void updateUI() {
    super.updateUI();
    if(statusRenderer != null) {
      COLOR_TREE_SELECTION_BACKGROUND = UIManager.getColor("Table.selectionBackground");
      COLOR_TREE_SELECTION_FOREGROUND = UIManager.getColor("Table.selectionForeground");
      int mode = statusRenderer.treeMode;
      statusRenderer = new StatusTreeRenderer();
      statusRenderer.setTreeMode(mode);
      setCellRenderer(statusRenderer);
    }
  }

  public StatusNode addUnchangedNode(StatusNode parent, String name, Object data) {
    StatusNode rtnNode = new UnchangedStatusNode(name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public StatusNode addUnchangedNode(StatusNode parent, String name, Object data, boolean loaded) {
    StatusNode rtnNode = new UnchangedStatusNode(name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    rtnNode.loaded = loaded;
    if(!loaded)
      rtnNode.insertDummyNode(getStatusModel());
    return rtnNode;
  }

  public StatusNode addBlankNode(StatusNode parent, String name, Object data) {
    StatusNode rtnNode = new StatusNode(STATUS_BLANK, name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public StatusNode addBlankNode(StatusNode parent, String name, Object data, boolean loaded) {
    StatusNode rtnNode = new StatusNode(STATUS_BLANK, name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    rtnNode.loaded = loaded;
    if(!loaded)
      rtnNode.insertDummyNode(getStatusModel());
    return rtnNode;
  }

  public StatusNode addBlankTwoNameNode(StatusNode parent, String name, String tgtName, Object data, boolean loaded) {
    StatusNode rtnNode = new TwoNameStatusNode(name, tgtName, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    rtnNode.loaded = loaded;
    if(!loaded)
      rtnNode.insertDummyNode(getStatusModel());
    return rtnNode;
  }

  public StatusNode getBlankNode(String name, Object data) {
    return new StatusNode(STATUS_BLANK, name, data);
  }

  public void insertNode(StatusNode parent, StatusNode child) {
    defaultModel.insertNodeInto(child, parent, parent.getChildCount());
  }

  public StatusNode addChangedNode(StatusNode parent, String name, Object data) {
    StatusNode rtnNode = new ChangedStatusNode(name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public StatusNode addDeletedNode(StatusNode parent, String name, Object data) {
    StatusNode rtnNode = new DeletedStatusNode(name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public StatusNode addAddedNode(StatusNode parent, String name, Object data) {
    StatusNode rtnNode = new AddedStatusNode(name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public StatusNode addMixedNode(StatusNode parent, String name, Object data, boolean hasUnchanged, boolean hasChanged, boolean hasAdded, boolean hasDeleted) {
    StatusNode rtnNode = new MixedStatusNode(hasUnchanged, hasChanged, hasAdded, hasDeleted, name, data);
    defaultModel.insertNodeInto(rtnNode, parent, parent.getChildCount());
    return rtnNode;
  }

  public void setForceAllAdded(boolean forceAllAdded) {
    this.forceAllAdded = forceAllAdded;
  }

  public boolean isForceAllAdded() {
    return forceAllAdded;
  }

  public void setForceAllDeleted(boolean forceAllDeleted) {
    this.forceAllDeleted = forceAllDeleted;
  }

  public boolean isForceAllDeleted() {
    return forceAllDeleted;
  }

  public void refreshRoot() {
    defaultModel.setRoot(rootNode);
  }

  public void clearRoot() {
    rootNode.removeAllChildren();
  }

  public void resetTree() {
    clearRoot();
    forceAllAdded = false;
    forceAllDeleted = false;
  }

  public class StatusNode extends DefaultMutableTreeNode {
    public int status;
    public String name;
    public Object data;
    public boolean loaded = true;
    public boolean forceDirectory = false;
    public StatusNode dummyNode;

    public StatusNode(int status, String name, Object data) {
      this.status = status;
      this.name = name;
      this.data = data;
    }

    public ImageIcon getImage(int treeMode, boolean isDir, boolean isOpen) {
      if(forceDirectory)
        isDir = true;
      if(forceAllAdded) {
        if((treeMode & TREE_MODE_SOURCE) == TREE_MODE_SOURCE)
          return isDir ? dirGhost : fileGhost;
        else
          return isDir ? (isOpen ? dirOpenAdded : dirClosedAdded) : fileAdded;
      }
      else if(forceAllDeleted) {
        if((treeMode & TREE_MODE_TARGET) == TREE_MODE_TARGET)
          return isDir ? dirGhost : fileGhost;
        else
          return isDir ? (isOpen ? dirOpenDeleted : dirClosedDeleted) : fileDeleted;
      }
      switch(status) {
        case STATUS_BLANK:
        case STATUS_UNCHANGED:
          return isDir ? (isOpen ? dirOpenUnchanged : dirClosedUnchanged) : fileUnchanged;
        case STATUS_CHANGED:
          return isDir ? (isOpen ? dirOpenChanged : dirClosedChanged) : fileChanged;
        case STATUS_ADDED:
          if((treeMode & TREE_MODE_TARGET) == TREE_MODE_TARGET)
            return isDir ? (isOpen ? dirOpenAdded : dirClosedAdded) : fileAdded;
          return isDir ? dirGhost : fileGhost;
        case STATUS_DELETED:
          if((treeMode & TREE_MODE_SOURCE) == TREE_MODE_SOURCE)
            return isDir ? (isOpen ? dirOpenDeleted : dirClosedDeleted) : fileDeleted;
          return isDir ? dirGhost : fileGhost;
        default:
          if(isDir)
            return isOpen ? dirOpenMixed : dirClosedMixed;
          return fileUnchanged;
      }
    }

    public Object getUserObject() {
      return data;
    }

    public int getNodeSize() {
      return getChildCount();
    }

    public void insertDummyNode(DefaultTreeModel model) {
      dummyNode = getBlankNode("", null);
      model.insertNodeInto(dummyNode, this, 0);
    }

    public void removeDummyNode(DefaultTreeModel model) {
      model.removeNodeFromParent(dummyNode);
    }

    public Color getForeground(int treeMode, boolean isSelected) {
      if(forceAllAdded) {
        if((treeMode & TREE_MODE_SOURCE) == TREE_MODE_SOURCE)
          return isSelected ? COLOR_TREE_SELECTION_BACKGROUND : getBackground();
        else
          return isSelected ? COLOR_TREE_SELECTION_FOREGROUND : COLOR_STATUS_ADDED;
      }
      else if(forceAllDeleted) {
        if((treeMode & TREE_MODE_TARGET) == TREE_MODE_TARGET)
          return isSelected ? COLOR_TREE_SELECTION_BACKGROUND : getBackground();
        else
          return isSelected ? COLOR_TREE_SELECTION_FOREGROUND : COLOR_STATUS_ADDED;
      }
      if(isSelected) {
        if(status == STATUS_ADDED &&
            (treeMode & TREE_MODE_SOURCE) == TREE_MODE_SOURCE)
          return COLOR_TREE_SELECTION_BACKGROUND;
        else if(status == STATUS_DELETED &&
            (treeMode & TREE_MODE_TARGET) == TREE_MODE_TARGET)
          return COLOR_TREE_SELECTION_BACKGROUND;
        return COLOR_TREE_SELECTION_FOREGROUND;
      }
      switch(status) {
        case STATUS_BLANK:
        case STATUS_UNCHANGED:
          return COLOR_STATUS_UNCHANGED;
        case STATUS_CHANGED:
          return COLOR_STATUS_CHANGED;
        case STATUS_ADDED:
          if((treeMode & TREE_MODE_TARGET) == TREE_MODE_TARGET)
            return COLOR_STATUS_DELETED;
          return getBackground();
        case STATUS_DELETED:
          if((treeMode & TREE_MODE_SOURCE) == TREE_MODE_SOURCE)
            return COLOR_STATUS_ADDED;
          return getBackground();
        default:
          return COLOR_STATUS_CHANGED;
      }
    }

    public boolean isVisible() {
      return ((currentFilter & status) == currentFilter);
    }

    public String toString() {
      if(name != null)
        return name;
      return "";
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public void appendStatus(int status) {
      this.status = this.status | status;
    }
  }

  class UnchangedStatusNode extends StatusNode {
    public UnchangedStatusNode(String name, Object data) {
      super(STATUS_UNCHANGED, name, data);
    }
  }

  class RootStatusNode extends StatusNode {
    public RootStatusNode(String name, Object data) {
      super(STATUS_UNCHANGED, name, data);
      forceDirectory = true;
    }

    public boolean isVisible() {
      return true;
    }
  }

  class ChangedStatusNode extends StatusNode {
    public ChangedStatusNode(String name, Object data) {
      super(STATUS_CHANGED, name, data);
    }
  }

  class AddedStatusNode extends StatusNode {
    public AddedStatusNode(String name, Object data) {
      super(STATUS_ADDED, name, data);
    }
  }

  class DeletedStatusNode extends StatusNode {
    public DeletedStatusNode(String name, Object data) {
      super(STATUS_DELETED, name, data);
    }
  }

  class MixedStatusNode extends StatusNode {
    public MixedStatusNode(boolean hasUnchanged, boolean hasChanged, boolean hasAdded, boolean hasDeleted, String name, Object data) {
      super(0, name, data);
      if(hasUnchanged)
        status = status | STATUS_UNCHANGED;
      if(hasChanged)
        status = status | STATUS_CHANGED;
      if(hasAdded)
        status = status | STATUS_ADDED;
      if(hasDeleted)
        status = status | STATUS_DELETED;
    }
  }

  class TwoNameStatusNode extends StatusNode {
    public String tgtName;

    public TwoNameStatusNode(String srcName, String tgtName, Object data) {
      super(STATUS_BLANK, srcName, data);
      this.tgtName = tgtName;
    }

    public String getSourceName() {
      return name;
    }

    public String getTargetName() {
      return tgtName;
    }
  }

  class StatusTreeRenderer extends DefaultTreeCellRenderer {

    public Font highlightFont;
    private int treeMode;

    public StatusTreeRenderer() {
      highlightFont = new Font(treeFont.getName(), Font.BOLD, treeFont.getSize());
    }

    public void setTreeMode(int treeMode) {
      this.treeMode = treeMode;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Component rtn = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      rtn.setFont(treeFont);
      ((JLabel)rtn).setOpaque(false);
      if(value != null && value instanceof StatusNode) {
        if(value instanceof TwoNameStatusNode && treeMode == TREE_MODE_TARGET)
          ((JLabel)rtn).setText(((TwoNameStatusNode)value).getTargetName());
        ImageIcon tmpIcon = ((StatusNode)value).getImage(treeMode, !leaf, expanded);
        if(tmpIcon != null)
          ((JLabel)rtn).setIcon(tmpIcon);
        rtn.setVisible(((StatusNode)value).isVisible());
        if(((StatusNode)value) == highlightTarget) {
          rtn.setForeground(COLOR_TREE_SELECTION_FOREGROUND);
          rtn.setBackground(COLOR_TREE_SELECTION_BACKGROUND);
          rtn.setFont(highlightFont);
          ((JLabel)rtn).setOpaque(true);
        }
        else {
          rtn.setForeground(((StatusNode)value).getForeground(treeMode, selected));
          if(selected)
            rtn.setBackground(COLOR_TREE_SELECTION_BACKGROUND);
          else
            rtn.setBackground(tree.getBackground());
        }
      }
      return rtn;
    }
  }

  class StatusFilterModel extends DefaultTreeModel {

    public StatusFilterModel(TreeNode root) {
      super(root);
    }

    public Object getChild(Object parent, int index) {
      if(currentFilter == STATUS_ALL)
        return super.getChild(parent, index);
      int cnt = 0;
			for(int i = 0; i < ((StatusNode)parent).getChildCount(); i++) {
				if(((StatusNode)((StatusNode)parent).getChildAt(i)).isVisible()) {
					if(cnt++ == index) {
						return ((StatusNode)parent).getChildAt(i);
					}
				}
			}
      return null;
    }

    public int getChildCount(Object parent) {
      if(currentFilter == STATUS_ALL)
        return super.getChildCount(parent);
      int rtn = 0;
			Enumeration children = ((StatusNode)parent).children();
			while(children.hasMoreElements()) {
				if(((StatusNode)children.nextElement()).isVisible())
					rtn++;
			}
			return rtn;
    }
  }
}

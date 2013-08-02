package net.saucefactory.swing.data;

public class SFTableColumnViewState {

  public int modelIndex;
  public int viewIndex;
  public int columnId;

  public SFTableColumnViewState() {
  }

  public SFTableColumnViewState(int modelIndex, int viewIndex, int columnId) {
    this.modelIndex = modelIndex;
    this.viewIndex = viewIndex;
    this.columnId = columnId;
  }

  public String toString() {
    return "model index: " + modelIndex + " view index: " + viewIndex + " columnId: " + columnId;
  }

}

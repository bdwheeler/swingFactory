package net.saucefactory.io.diff;

public interface IDiffListener {
  public void postTableCount(int count);
  public void postStart();
  public void postEnd();
  public void postFolderProcessing(String name);
}

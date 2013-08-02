package net.saucefactory.text.diff;

import net.saucefactory.io.diff.DiffFile;
import java.io.File;

public class DiffFileTest {
  public DiffFileTest() {
  }

  public void testJarPaths() {
    try {
      DiffFile tmpFile = new DiffFile();
      File testFile = new File("file://C:/DevWork/Compassoft/Repository/RevExDevHead/RevEx/unittest_extra/fs_snapshot_data_with_changes/EarJarInZIP.zip");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    System.out.println("test complete");
  }

  public static void main(String[] args) {
    DiffFileTest tmpTest = new DiffFileTest();
  }
}

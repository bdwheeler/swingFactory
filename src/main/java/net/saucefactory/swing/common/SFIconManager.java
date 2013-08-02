package net.saucefactory.swing.common;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.net.*;

public class SFIconManager {
  public static String DEFAULT_IMAGE_PACKAGE;
  public static String FRAME_ICON_IMAGE_NAME;
  public static String DEFAULT_IMAGE_DIR;
  private static Hashtable iconHash = new Hashtable();
  private static SFIconManager instance = new SFIconManager();
  private static ClassLoader loader = null;

  private SFIconManager() {
    DEFAULT_IMAGE_PACKAGE = "";
    FRAME_ICON_IMAGE_NAME = "";
    DEFAULT_IMAGE_DIR = System.getProperty("user.dir");
  }

  public static SFIconManager getHandle() {
    return instance;
  }

  public static void setClassLoader(ClassLoader classLoader) {
    loader = classLoader;
  }

  public static void setDefaultImagePackage(String imagePath) {
    DEFAULT_IMAGE_PACKAGE = imagePath;
  }

  public static void setDefaultImageDir(String imagePath) {
    DEFAULT_IMAGE_DIR = imagePath;
  }

  public static void setFrameIconImage(String imageName) {
    FRAME_ICON_IMAGE_NAME = imageName;
  }

  public static ImageIcon getFrameIcon() {
    return getImage(DEFAULT_IMAGE_PACKAGE + FRAME_ICON_IMAGE_NAME);
  }

  public static ImageIcon getImageFromDefaultPackage(String imageName) {
    return getImage(DEFAULT_IMAGE_PACKAGE + imageName);
  }

  public static ImageIcon getImageFromDefaultDirectory(String imageLocation) {
    return getImageFromFile(DEFAULT_IMAGE_DIR + imageLocation);
  }

  public static ImageIcon getImage(String imageName) {
    ImageIcon tmpImg = null;
    try {
      if(iconHash.containsKey(imageName))
        tmpImg = (ImageIcon)iconHash.get(imageName);
      else {
        URL location;
        if(loader != null)
          location = loader.getResource(imageName);
        else
          location = getHandle().getClass().getClassLoader().getResource(imageName); //ClassLoader.getSystemResource(imageName);
        tmpImg = new ImageIcon(location);
        if(tmpImg != null)
          iconHash.put(imageName, tmpImg);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return tmpImg;
  }

  public static ImageIcon getImageFromFile(String imageLocation) {
    ImageIcon tmpImg = null;
    try {
      if(iconHash.containsKey(imageLocation))
        tmpImg = (ImageIcon)iconHash.get(imageLocation);
      else {
        URL location = new URL("file:///" + imageLocation);
        tmpImg = new ImageIcon(location);
        if(tmpImg != null)
          iconHash.put(imageLocation, tmpImg);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return tmpImg;
  }
}

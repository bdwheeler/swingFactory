package net.saucefactory.swing.common;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * Title:        Slic Web Client/Server
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      California ISO
 * @author Jeremy Leng
 * @version 1.0
 */


public class SFMetalTheme extends DefaultMetalTheme {

  public final static Color SF_BLUE_DARK = new Color(0, 51, 153);
  public final static Color SF_BLUE_MEDIUM = new Color(153, 178, 204);
  public final static Color SF_BLUE_LIGHT = new Color(225, 235, 242);
  public final static Color SF_SORT_PRIMARY = new Color(125, 125, 175);
  public final static Color SF_SORT_SECONDARY = new Color(150, 150, 190);
  public final static Color SF_SORT_TERNARY = new Color(180, 180, 210);
  public final static Color SF_TABLE_HEADER = new Color(120, 120, 120);
  public final static Color GRAY_DARK = new Color(102, 102, 102);
  public final static Color GRAY_MEDIUM = new Color(153, 153, 153);
  public final static Color GRAY_MED_LIGHT = new Color(178, 178, 178);
  public final static Color GRAY_LIGHT = new Color(204, 204, 204);
  public final static Color YELLOW_DARK = new Color(255, 255, 128);
  public final static Color YELLOW_MEDIUM = new Color(255, 255, 178);
  public final static Color YELLOW_LIGHT = new Color(255, 255, 225);
  public final static Font ARIAL_14 = new Font("Arial", 0, 14);
  public final static Font ARIAL_14_BOLD = new Font("Arial", Font.BOLD, 14);
  public final static Font ARIAL_12 = new Font("Arial", 0, 12);
  public final static Font ARIAL_12_BOLD = new Font("Arial", Font.BOLD, 12);
  public final static Font ARIAL_11 = new Font("Arial", 0, 11);
  public final static Font ARIAL_11_BOLD = new Font("Arial", Font.BOLD, 11);
  public final static Font ARIAL_10 = new Font("Arial", 0, 10);
  public final static Font ARIAL_10_BOLD = new Font("Arial", Font.BOLD, 10);
  public final static Font SANS_14 = new Font("SansSerif", 0, 14);
  public final static Font SANS_14_BOLD = new Font("SansSerif", Font.BOLD, 14);
  public final static Font SANS_12 = new Font("SansSerif", 0, 12);
  public final static Font SANS_12_BOLD = new Font("SansSerif", Font.BOLD, 12);
  public final static Font SANS_10 = new Font("SansSerif", 0, 10);
  public final static Font SANS_10_BOLD = new Font("SansSerif", Font.BOLD, 10);

  public SFMetalTheme() {
    super();
  }

  protected ColorUIResource getPrimary1() {
    return new ColorUIResource(SF_BLUE_DARK);
  }

  protected ColorUIResource getSecondary1() {
    return new ColorUIResource(GRAY_DARK);
  }

  protected ColorUIResource getSecondary2() {
    return new ColorUIResource(GRAY_MEDIUM);
    /**@todo: implement this javax.swing.plaf.metal.MetalTheme abstract method*/
  }

  protected ColorUIResource getPrimary2() {
    return new ColorUIResource(SF_BLUE_MEDIUM);
  }

  public String getName() {
    return "SFMetalTheme";
    /**@todo: implement this javax.swing.plaf.metal.MetalTheme abstract method*/
  }
  protected ColorUIResource getSecondary3() {
    return new ColorUIResource(GRAY_LIGHT);
    /**@todo: implement this javax.swing.plaf.metal.MetalTheme abstract method*/
  }
  protected ColorUIResource getPrimary3() {
    return new ColorUIResource(SF_BLUE_LIGHT);
    /**@todo: implement this javax.swing.plaf.metal.MetalTheme abstract method*/
  }
}

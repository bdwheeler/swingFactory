package net.saucefactory.swing.common;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CAISO</p>
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.print.*;

public interface ISFPrintPreviewable
{
  public void formatChanged(PageFormat format);
}

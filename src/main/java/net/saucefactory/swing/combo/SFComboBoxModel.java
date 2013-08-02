package net.saucefactory.swing.combo;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1231.0
 */

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.awt.event.*;
import net.saucefactory.swing.data.ISFDataAdapter;

public class SFComboBoxModel extends AbstractListModel implements MutableComboBoxModel,
    ISFPredictiveSearch {
  protected Object[] internalData;
  protected Object selection = null;
  protected ISFDataAdapter dataAdapter;
  public boolean firstCellEmpty = false;
  public String emptyText = "";
  protected Comparator comparator = null;
  protected ISFPredictiveIndexListener listener = null;

  public void setComboData(Object[] internalData, ISFDataAdapter dataAdapter) {
    this.internalData = internalData;
    this.dataAdapter = dataAdapter;
    fireContentsChanged(this, -1, -1);
  }

  public void setComboData(Object[] internalData,  ISFDataAdapter dataAdapter,
      boolean firstCellEmpty) {
    this.internalData = internalData;
    this.dataAdapter = dataAdapter;
    this.firstCellEmpty = firstCellEmpty;
    fireContentsChanged(this, -1, -1);
  }

  public void setComboData(Object[] internalData,  ISFDataAdapter dataAdapter,
      boolean firstCellEmpty, String emptyText) {
    this.internalData = internalData;
    this.dataAdapter = dataAdapter;
    this.firstCellEmpty = firstCellEmpty;
    this.emptyText = emptyText;
    fireContentsChanged(this, -1, -1);
  }

  public ISFDataAdapter getDataAdapter() {
    return dataAdapter;
  }

  public boolean isFirstCellEmpty() {
    return firstCellEmpty;
  }

  public int getSize() {
    int tmpSize = 0;
    if(firstCellEmpty)
      tmpSize += 1;
    if(internalData != null)
      tmpSize += internalData.length;
    return tmpSize;
  }

  public Object getElementAt(int index) {
    try {
      if(firstCellEmpty) {
        if(index == 0)
          return emptyText;
        index -= 1;
      }
      if(internalData != null && index > -1)
        return dataAdapter.getValue(internalData[index]);
      return null;
    }
    catch(Exception e) {
      e.printStackTrace();
      return internalData[index];
    }
  }

  public String getElementDisplay(Object element) {
    try {
      return dataAdapter.getValue(element).toString();
    }
    catch(Exception e) {
      return element.toString();
    }
  }

  public Object getObjectAt(int index) {
    if(firstCellEmpty) {
      if(index == 0)
        return emptyText;
      index -= 1;
    }
    if(internalData != null && index > -1)
      return internalData[index];
    return null;
  }

  public void setSelectedItem(Object object) {
    selection = object;
    //fireContentsChanged(this, -1, -1);
  }

  public boolean selectionChanged(int index, boolean forceChange) {
    if(listener == null || forceChange) {
      setSelectedItem(getElementAt(index));
      return true;
    }
    else {
      if((firstCellEmpty && index > 0) || (!firstCellEmpty && index > -1)) {
        setSelectedItem(getElementAt(index));
        return true;
      }
    }
    return false;
  }

  public Object getSelectedItem() {
    return selection;
  }

  public void clearComboData() {
    internalData = null;
    selection = null;
  }

  private void setInternalData(Object[] internalData) {
    this.internalData = internalData;
  }

  public Object getSelectedObject(int index) {
    if(firstCellEmpty)
      index--;
    if(internalData != null && index > -1)
      return internalData[index];
    return null;
  }

  public Object getSelectedField(int index, String fieldName) {
    try {
      if(firstCellEmpty)
        index--;
      if(internalData != null && index > -1)
        return internalData[index].getClass().getField(fieldName).get(internalData[index]);
      return null;
    }
    catch(Exception e) {
      return null;
    }
  }

  public String getSelectedStringField(int index, String fieldName) {
    return(String)getSelectedField(index, fieldName);
  }

  public void setSelectionByField(String fieldName, Object value, SFComboBox parent) {
    if(internalData == null || value == null)
      return;
    try {
      int fndIndex = -1;
      for(int i = 0; i < internalData.length; i++) {
        Object tmpObj = internalData[i].getClass().getField(fieldName).get(internalData[i]);
        if(tmpObj.equals(value)) {
          fndIndex = i;
          break;
        }
      }
      if(firstCellEmpty)
        fndIndex += 1;
      parent.selectedIndex = fndIndex;
      if(fndIndex > -1)
        selectionChanged(fndIndex, true);
        //moded here
      setSelectedItem(getElementAt(fndIndex));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setSelectionByMethod(String methodName, Object value, SFComboBox parent) {
    if(internalData == null || value == null)
      return;
    try {
      int fndIndex = -1;
      for(int i = 0; i < internalData.length; i++) {
        Object tmpObj = internalData[i].getClass().getMethod(methodName, null).invoke(internalData[i], null);
        if(tmpObj.equals(value)) {
          fndIndex = i;
          break;
        }
      }
      if(firstCellEmpty)
        fndIndex += 1;
      parent.selectedIndex = fndIndex;
      if(fndIndex > -1)
        selectionChanged(fndIndex, true);
        //moded here
      setSelectedItem(getElementAt(fndIndex));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void addElement(Object obj) {
  }

  public void insertElementAt(Object obj, int index) {
  }

  public void removeElement(Object obj) {
  }

  public void removeElementAt(int index) {
  }

  public void setAutoReplaceMode(boolean doAutoReplace_) {
    doAutoReplace = doAutoReplace_;
  }

  //Predictive text methods.
  private boolean doAutoReplace = true;

  public boolean isAutoReplace() {
    return doAutoReplace;
  }

  public void setComparator(Comparator comparator) {
    this.comparator = comparator;
  }

  public void setPredictiveIndexListener(ISFPredictiveIndexListener listener) {
    this.listener = listener;
  }

  public void selectionCleared() {}

  public String getPrediction(String match) throws Exception {
    if(internalData == null)
      return null;
    int matchIndex;
    if(internalData.length == 1) {
      matchIndex = firstCellEmpty ? 1 : 0;
    }
    else {
      if(comparator == null)
        matchIndex = Arrays.binarySearch(internalData, match);
      else
        matchIndex = Arrays.binarySearch(internalData, match, comparator);
        //System.out.println("Match index = " + matchIndex);
      if((firstCellEmpty && matchIndex > -1)
          || (!firstCellEmpty && matchIndex < 0))
        matchIndex++;
      matchIndex = Math.abs(matchIndex);
      if(matchIndex >= (internalData.length + (firstCellEmpty ? 1 : 0)))
        matchIndex--;
    }
    String prediction = (String)getElementAt(matchIndex);
    if(prediction.toUpperCase().indexOf(match.toUpperCase()) < 0 && !doAutoReplace) {
      try {
        setSelectedItem(match);
        fireContentsChanged(this, 0, 0);
      }
      catch(Exception e) {}
      return null;
    }
    if(listener != null) {
      listener.focusGained();
      listener.indexChanged(matchIndex);
    }
    return prediction;
  }

  public ISFPredictiveSearch getNarrowingPredictionSearch() {
    return new NarrowingPredictionSearch(internalData);
  }

  public ISFPredictiveSearch getLinearPredictionSearch() {
    return new LinearPredictionSearch();
  }

  class NarrowingPredictionSearch implements ISFPredictiveSearch {
    private Object[] originalData;

    public boolean isAutoReplace() {
      return doAutoReplace;
    }

    public NarrowingPredictionSearch(Object[] originalData) {
      this.originalData = originalData;
    }

    public String getPrediction(String match) throws Exception {
      if(originalData == null)
        return null;
      if(match.equals(""))
        setComboData(originalData, dataAdapter);
      Vector tmpVector = new Vector();
      String prediction = "";
      int matchIndex = -1;
      boolean firstFound = false;
      for(int i = 0; i < originalData.length; i++) {
        String tmpStr = getElementDisplay(originalData[i]);
        if(tmpStr.startsWith(match)) {
          tmpVector.add(originalData[i]);
          if(!firstFound) {
            firstFound = true;
            prediction = tmpStr;
            matchIndex = 1;
          }
        }
      }
      if(tmpVector.size() > 0) {
        Object tmpArray = Array.newInstance(originalData[0].getClass(), tmpVector.size());
        tmpVector.copyInto((Object[])tmpArray);
        setComboData((Object[])tmpArray, dataAdapter);
      }
      else {
        clearComboData();
      }
      if(prediction.toUpperCase().indexOf(match.toUpperCase()) < 0 && !doAutoReplace) {
        try {
          setSelectedItem(match);
          fireContentsChanged(this, 0, 0);
        }
        catch(Exception e) {}
        return null;
      }
      if(listener != null) {
        listener.focusGained();
        listener.indexChanged(matchIndex);
      }
      return prediction;
    }

    public void selectionCleared() {
      setComboData(originalData, dataAdapter);
    }
  }

  class LinearPredictionSearch implements ISFPredictiveSearch {
    public boolean isAutoReplace() {
      return doAutoReplace;
    }

    public LinearPredictionSearch() {}

    public String getPrediction(String match) throws Exception {
      if(internalData == null)
        return null;
      int matchIndex = -1;
      String prediction = null;
      String tmpMatch = match.toUpperCase();
      for(int i = 0; i < internalData.length; i++) {
        String tmpStr = getElementDisplay(internalData[i]);
        if(tmpStr.toUpperCase().startsWith(tmpMatch)) {
          prediction = tmpStr;
          matchIndex = i;
          break;
        }
      }
      if(firstCellEmpty)
        matchIndex += 1;
      if(prediction != null && prediction.toUpperCase().indexOf(tmpMatch) < 0 && !doAutoReplace) {
        try {
          setSelectedItem(match);
          fireContentsChanged(this, 0, 0);
        }
        catch(Exception e) {}
        return null;
      }
      else if(!doAutoReplace) {
        setSelectedItem(match);
      }
      if(listener != null) {
        listener.focusGained();
        listener.indexChanged(matchIndex);
      }
      return prediction;
    }

    public void selectionCleared() {}
  }
}

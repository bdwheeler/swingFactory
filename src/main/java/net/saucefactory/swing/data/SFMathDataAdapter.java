package net.saucefactory.swing.data;

public class SFMathDataAdapter implements ISFDataAdapter {

  public static final int OPERATION_ADD = 1;
  public static final int OPERATION_SUBTRACT = 2;
  public static final int OPERATION_MULTIPLY = 3;
  public static final int OPERATION_DIVIDE = 4;
  public ISFDataAdapter dataX;
  public ISFDataAdapter dataY;
  public int operation;

  public SFMathDataAdapter(ISFDataAdapter dataX, ISFDataAdapter dataY, int operation) {
    this.dataX = dataX;
    this.dataY = dataY;
    this.operation = operation;
  }

  public static SFMathDataAdapter add(ISFDataAdapter dataX, ISFDataAdapter dataY) {
    return new SFMathDataAdapter(dataX, dataY, OPERATION_ADD);
  }

  public static SFMathDataAdapter subtract(ISFDataAdapter dataX, ISFDataAdapter dataY) {
    return new SFMathDataAdapter(dataX, dataY, OPERATION_SUBTRACT);
  }

  public static SFMathDataAdapter multiply(ISFDataAdapter dataX, ISFDataAdapter dataY) {
    return new SFMathDataAdapter(dataX, dataY, OPERATION_MULTIPLY);
  }

  public static SFMathDataAdapter divide(ISFDataAdapter dataX, ISFDataAdapter dataY) {
    return new SFMathDataAdapter(dataX, dataY, OPERATION_DIVIDE);
  }

  public Object getValue(Object source) {
    try {
      Object tmpValX = dataX.getValue(source);
      Object tmpValY = dataY.getValue(source);
      Object rtn = null;
      if(tmpValX instanceof Number && tmpValY instanceof Number) {
        double tmpDX = ((Number)tmpValX).doubleValue();
        double tmpDY = ((Number)tmpValY).doubleValue();
        switch(operation) {
          case OPERATION_ADD:
            rtn = new Double(tmpDX + tmpDY);
            break;
          case OPERATION_SUBTRACT:
            rtn = new Double(tmpDX - tmpDY);
            break;
          case OPERATION_MULTIPLY:
            rtn = new Double(tmpDX * tmpDY);
            break;
          case OPERATION_DIVIDE:
            rtn = new Double(tmpDX / tmpDY);
            break;
        }
      }
      return rtn;
    } catch(Exception e) {
      return null;
    }
  }

  public void setValue(Object target, Object value) {
    //not supported
  }

  public boolean isValue(Object value, Object target) {
    value = getValue(value);
    if(value == null)
      return target == null;
    if(target == null)
      return false;
    return target.equals(value);
  }
}

package net.saucefactory.text.diff;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */
import java.util.*;
import java.io.*;
import java.text.*;

public class DiffUtility implements DiffStatus {
  protected DiffUtility() {}

  protected int populateBlock(int preFirst
                               , int preSecond
                               , int first
                               , int second
                               , ArrayList baseFileMap
                               , ArrayList compFileMap
                               , Diff rtn
                               , String delim
                               , int lineCount) {
    int blockFirst = first - preFirst;
    int blockSecond = second - preSecond;

    // take care of in between stuff
    // ADDED BLOCK
    if ((blockFirst == 1) && (blockSecond > 1)) {
      for (int i = 1; i < blockSecond; i++) {
        rtn.incAddedLines();
        Line line = (Line)compFileMap.get(preSecond + i); // get text for each multiline
        DiffItem dLB = new DiffItem(lineCount, -1, "", line.text, ADDED, delim); // add blank line to base diff
        DiffItem dLC = new DiffItem(lineCount, line.lineNo, line.text, "", ADDED, delim); // add added line to comp diff
        rtn.addBaseDiffItem(dLB);
        rtn.addComparisonDiffItem(dLC);
        lineCount++;
      }
    }
    // DELETED BLOCK
    else if ((blockSecond == 1) && (blockFirst > 1)) {
      for (int i = 1; i < blockFirst; i++) {
        rtn.incDeletedLines(); // inc deleted line count
        Line line = (Line)baseFileMap.get(preFirst + i); // get text for each multiline
        DiffItem dLB = new DiffItem(lineCount, line.lineNo, line.text, "", DELETED, delim); // add deleted line to base diff
        DiffItem dLC = new DiffItem(lineCount, -1, "", line.text, DELETED, delim); // add blank line to comp diff
        rtn.addBaseDiffItem(dLB);
        rtn.addComparisonDiffItem(dLC);
        lineCount++;
      }
    }
    //CHANGED BLOCK
    else {
      int bigBlock = (blockFirst > blockSecond) ? blockFirst : blockSecond;
      // walk down the block of unmatched lines between previous match_pair and this match_pair
      for (int i = 1; i < bigBlock; i++) {
        DiffItem dLB = null;
        DiffItem dLC = null;
        if (!(preSecond + i < second)) {// line was there, now gone
          Line line = ((Line)baseFileMap.get(preFirst + i));
          dLB = new DiffItem(lineCount, line.lineNo, line.text, "", DELETED, delim);
          dLC = new DiffItem(lineCount, -1, "", line.text, DELETED, delim);
          rtn.incDeletedLines();
        }
        else if (preFirst + i < first) {// line was there, now different
          Line line = (Line)baseFileMap.get(preFirst + i); // get text
          Line lineC = (Line)compFileMap.get(preSecond + i);
          dLB = new DiffItem(lineCount, line.lineNo, line.text, lineC.text, CHANGED, delim);
          dLC = new DiffItem(lineCount, lineC.lineNo, lineC.text, line.text, CHANGED, delim);
          rtn.incChangedLines();
        }
        else {// line wasn't there
          Line line = (Line)compFileMap.get(preSecond + i);
          dLB = new DiffItem(lineCount, -1, "", line.text, ADDED, delim); // add blank line
          dLC = new DiffItem(lineCount, line.lineNo, line.text, "", ADDED, delim);
          rtn.incAddedLines();
        }
        rtn.addBaseDiffItem(dLB);
        rtn.addComparisonDiffItem(dLC);
        lineCount++;
      }
    }
    return lineCount;
  }

  protected Diff populateDiff(ArrayList baseFileMap, ArrayList compFileMap, ArrayList matchPairs, String delim) {
    Diff rtn = new Diff();
    int first = 0, second = 0;
    int preFirst = 0, preSecond = 0;
    Iterator iter = matchPairs.iterator();
    int lineCount = 1;

    while (iter.hasNext()) {
      MatchPair item = (MatchPair)iter.next();

      first = item.first;
      second = item.second;

      // special case of 1st line changed
      if (lineCount == 1
          && (preFirst == 0 && first != 0)
          && (preSecond == 0 && second != 0)) {
        Line line = baseFileMap.size() > 0 ? (Line)baseFileMap.get(0) : null; // get text
        Line lineC = compFileMap.size() > 0 ? (Line)compFileMap.get(0) : null;
        String lineText = line == null ? " " : line.text;
        String lineCText = lineC == null ? " " : lineC.text;
        if(line != null)
          rtn.addBaseDiffItem(new DiffItem(lineCount, line.lineNo, line.text, lineCText, lineC == null ? DELETED : CHANGED, delim));
        else
          rtn.addBaseDiffItem(new DiffItem(lineCount, 1, " ", lineCText, lineC == null ? CHANGED : ADDED, delim));
        if(lineC != null)
          rtn.addComparisonDiffItem(new DiffItem(lineCount, lineC.lineNo, lineC.text, lineText, line == null ? ADDED : CHANGED, delim));
        else
          rtn.addComparisonDiffItem(new DiffItem(lineCount, 1, " ", lineText, line == null ? CHANGED : DELETED, delim));
        rtn.incChangedLines();
      }
      // take care of all IN BETWEEN STUFF (stuff not in match_pairs)
        lineCount = populateBlock(preFirst, preSecond, first, second, baseFileMap, compFileMap, rtn, delim, lineCount);
      // UNCHANGED LINE (from matchPairs)
      // put into the diffFile
      // base file line
      Line line = (Line)baseFileMap.get(first);
      // comp file line (may have different line no)
      Line line2 = (Line)compFileMap.get(second);
      DiffItem dLB = new DiffItem(lineCount, line.lineNo, line.text, line2.text, NO_CHANGE, delim);
      DiffItem dLC = new DiffItem(lineCount, line2.lineNo, line2.text, line.text, NO_CHANGE, delim);

      rtn.addComparisonDiffItem(dLC);
      rtn.addBaseDiffItem(dLB);
      rtn.incUnchangedLines();
      lineCount++;

      preFirst = first;
      preSecond = second;
    }
    // special case of 1 line for each text
    if ((baseFileMap.size() < 2 //== 1
        || compFileMap.size() < 2) //== 1)
        && matchPairs.isEmpty()) {

      Line line = baseFileMap.size() > 0 ? (Line)baseFileMap.get(0) : null; // get text
      Line lineC = compFileMap.size() > 0 ? (Line)compFileMap.get(0) : null;
      String lineText = line == null ? " " : line.text;
      String lineCText = lineC == null ? " " : lineC.text;
      if(line != null)
        rtn.addBaseDiffItem(new DiffItem(lineCount, line.lineNo, line.text, lineCText, lineC == null ? DELETED : CHANGED, delim));
      else
        rtn.addBaseDiffItem(new DiffItem(lineCount, 1, " ", lineCText, lineC == null ? NO_CHANGE : ADDED, delim));
      if(lineC != null)
        rtn.addComparisonDiffItem(new DiffItem(lineCount, lineC.lineNo, lineC.text, lineText, line == null ? ADDED : CHANGED, delim));
      else
        rtn.addComparisonDiffItem(new DiffItem(lineCount, 1, " ", lineText, line == null ? NO_CHANGE : DELETED, delim));
      rtn.incChangedLines();

    }
    lineCount = populateBlock(preFirst, preSecond, baseFileMap.size(), compFileMap.size(), baseFileMap, compFileMap, rtn, delim, lineCount);
    return rtn;
  }

  public static Diff compare(String base, String comp, String delimiter) throws Exception {
    DiffUtility engine = new DiffUtility();
    ArrayList baseFileMap = engine.readInto(base, delimiter);
    ArrayList compFileMap = engine.readInto(comp, delimiter);
    ArrayList matchPairs = engine.compareFilesLCS(baseFileMap, compFileMap);
    return engine.populateDiff(baseFileMap, compFileMap, matchPairs, delimiter);
  }

  public static Diff compare(String base, String comp) throws Exception {
    DiffUtility engine = new DiffUtility();
    ArrayList baseFileMap = engine.readInto(base);
    ArrayList compFileMap = engine.readInto(comp);
    ArrayList matchPairs = engine.compareFilesLCS(baseFileMap, compFileMap);
    return engine.populateDiff(baseFileMap, compFileMap, matchPairs, " ");
  }

  public static Diff compareLines(String base, String comp) throws Exception {
    DiffUtility engine = new DiffUtility();
    ArrayList baseFileMap = engine.readIntoLines(base);
    ArrayList compFileMap = engine.readIntoLines(comp);
    ArrayList matchPairs = engine.compareFilesLCS(baseFileMap, compFileMap);
    return engine.populateDiff(baseFileMap, compFileMap, matchPairs, "");
  }

  public static String addLineBreaks(String origText, int lineLength) {
    StringBuffer buf = new StringBuffer();
    StringBuffer wordBuf = new StringBuffer();
    char[] chars = origText.toCharArray();
    int currentLineLength = 0;
    for (int i = 0; i < chars.length; i++) {
      wordBuf.append(chars[i]);
      currentLineLength++;
      if (chars[i] == ' ') {
        buf.append(wordBuf.toString());
        wordBuf = new StringBuffer();
        if (currentLineLength > lineLength) {
          buf.append('\n');
          currentLineLength = 0;
        }
      } else if (chars[i] == '\n'
                 || chars[i] == '\f') {
        buf.append(wordBuf.toString());
        wordBuf = new StringBuffer();
        currentLineLength = 0;
      }
    }
    buf.append(wordBuf.toString());
    return buf.toString();
  }

  protected ArrayList readInto(String text, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(text, delim);
    ArrayList rtn = new ArrayList();
    int i = 1;
    while (tokenizer.hasMoreTokens()) {
      rtn.add(new Line(i++, tokenizer.nextToken()));
    }
    return rtn;
  }

  protected ArrayList readInto(String text) {
    StringTokenizer tokenizer = new StringTokenizer(text, " \t\n\r\f", true);
    ArrayList rtn = new ArrayList();
    int i = 1;
    while (tokenizer.hasMoreTokens()) {
      rtn.add(new Line(i++, tokenizer.nextToken()));
    }
    return rtn;
  }

  protected ArrayList readIntoLines(String text) {
    StringTokenizer tokenizer = new StringTokenizer(text, "\t\n\r\f", false);
    ArrayList rtn = new ArrayList();
    int i = 1;
    while (tokenizer.hasMoreTokens()) {
      rtn.add(new Line(i++, tokenizer.nextToken()));
    }
    return rtn;
  }

  /*
   Following routine implements the LCS (Longest Common Subsequence) algorithm
   to find line differences between two files. This routine finds the common
   lines. Each line is treated as an individual unit of comparison (via hash codes).
   Internet.saucefactory.im results are stored in a BitMatrix.
  */
  protected ArrayList compareFilesLCS(ArrayList baseFileMap, ArrayList compFileMap) throws Exception
  {
    ArrayList matchPairs = new ArrayList();
    int m = baseFileMap.size() - 1;
    int n = compFileMap.size() - 1;
    BitMatrix LCSBitMatrix = new BitMatrix(m + 2, n + 2);
    int[] ci = new int[n + 2];
    int[] ci1 = new int[n + 2];

    // Prepare LCS matrix
    for (int i = m; i >= 0; i--) {
      for (int k = 0; k <= n; k++) {
        ci1[k] = ci[k];
        ci[k]=0;
      }
      Line line_i = (Line)baseFileMap.get(i);
      for (int j = n; j >= 0; j--) {
        Line line_j = (Line)compFileMap.get(j);
        if (line_i.text.hashCode() == line_j.text.hashCode()) {
          //LCSMatrix(i,j) = 1 + LCSMatrix(i+1, j+1);
          ci[j] = 1 + ci1[j+1];
        }
        else {
          //LCSMatrix(i,j) = max(LCSMatrix(i+1, j), LCSMatrix(i, j+1));
          if (ci1[j] > ci[j+1]) {
            LCSBitMatrix.put(i,j,true);
            //LCSMatrix(i,j) = LCSMatrix(i+1, j);
            ci[j] = ci1[j];
          }
          else {
            LCSBitMatrix.put(i,j,false);
            //LCSMatrix(i,j) = LCSMatrix(i, j+1);
            ci[j] = ci[j+1];
          }
        }
      }
    }

    int i = 0;
    int j = 0;
    // Following link walks through the bitmatrix we created by the loop above
    // and produces a LCS, A LCS here is a
    while (i <= m && j <= n) {
      String line_i = ((Line)baseFileMap.get(i)).text;
      String line_j = ((Line)compFileMap.get(j)).text;

      if (line_i.hashCode() == line_j.hashCode()) {
        matchPairs.add(new MatchPair(i, j)); // A pair match added to LCS.
        i++;
        j++;
      }
      else {
        // if (LCSMatrix(i+1,j) >= LCSMatrix(i,j+1))
        if (LCSBitMatrix.get(i,j))
          i++;
        else
          j++;
      }
    }
    return matchPairs;
  }

  public static void main(String[] args) {
    try {
      DiffUtility diffEngine1 = new DiffUtility();
      //Diff diff = diffEngine1.compare("a\nb\nb1\nc\nd\ne\nf\nf1\ng\nh\ni\nj", "a\nB\nc\nd\ne\nF\ng\nh\nH1\ni\nj");
      //System.out.println(diff.getCompHTML());
      String origText = "pooasd poos pooadsad poosda poosdad poosdsdd pooasd poos pooadsad poosda poosdad poosdsdd\npooasd\n poos pooadsad poosda poosdad poosdsdd pooasd poos pooadsad poosda poosdad poosdsdd";
      System.out.println(origText);
      System.out.println("###############");
      System.out.println(diffEngine1.addLineBreaks(origText, 14));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected class Datatype {
    byte bit0 = 1;
    byte bit1 = 1;
    byte bit2 = 1;
    byte bit3 = 1;
    byte bit4 = 1;
    byte bit5 = 1;
    byte bit6 = 1;
    byte bit7 = 1;
  }

  protected class BitMatrix {
    private int rows_, cols_;
    private int row_, col_;
    private Datatype[] data_;

    public BitMatrix(int rows, int cols) {
        rows_ = rows;
        cols_ = cols;
        int sz = rows * cols;
        data_ = new  Datatype[sz];
        for (int i = 0; i < sz; i++) {
          data_[i] = new Datatype();
        }

    }

    public boolean get(int row, int col) throws Exception {
      if (row >= rows_ || col >= cols_)
        throw new Exception("Matrix subscript out of bounds");
      int bitpos = (cols_*row + col)/8;
      boolean retval = false;
      switch (col%8)
      {
           case 0 :
             retval = data_[bitpos].bit0 == 1;
             break;
           case 1 :
             retval = data_[bitpos].bit1 == 1;
             break;
           case 2 :
             retval = data_[bitpos].bit2 == 1;
             break;
           case 3 :
             retval = data_[bitpos].bit3 == 1;
             break;
           case 4 :
             retval = data_[bitpos].bit4 == 1;
             break;
           case 5 :
             retval = data_[bitpos].bit5 == 1;
             break;
           case 6 :
             retval = data_[bitpos].bit6 == 1;
             break;
           case 7 :
             retval = data_[bitpos].bit7 == 1;
             break;
           default :
             break;
      }
      return retval;
    }

    public void put(int row, int col, boolean val) throws Exception {
      if (row >= rows_ || col >= cols_)
        throw new Exception ("Matrix subscript out of bounds");
      int bitpos = (cols_ * row + col)/8;
      switch (col % 8)
      {
           case 0 :
             data_[bitpos].bit0 = (val ? (byte)1 : (byte)0);
             break;
           case 1 :
             data_[bitpos].bit1 = (val ? (byte)1 : (byte)0);
             break;
           case 2 :
             data_[bitpos].bit2 = (val ? (byte)1 : (byte)0);
             break;
           case 3 :
             data_[bitpos].bit3 = (val ? (byte)1 : (byte)0);
             break;
           case 4 :
             data_[bitpos].bit4 = (val ? (byte)1 : (byte)0);
             break;
           case 5 :
             data_[bitpos].bit5 = (val ? (byte)1 : (byte)0);
             break;
           case 6 :
             data_[bitpos].bit6 = (val ? (byte)1 : (byte)0);
             break;
           case 7 :
             data_[bitpos].bit7 = (val ? (byte)1 : (byte)0);
             break;
           default :
             break;
      }
      return;
    }
  };

  protected class MatchPair {
    public int first = 0;
    public int second = 0;
    public MatchPair(int item1_, int item2_) {
      first = item1_;
      second = item2_;
    }
  }

  protected class Line {
    public int lineNo = 0;
    public String text = null;
    public Line(int lineNo_, String text_) {
      lineNo = lineNo_;
      text = text_;
    }
    public String toString() {
      return text;
    }
  }
}

interface DiffStatus {
  public static final int NO_CHANGE = 0;
  public static final int ADDED = 1;
  public static final int CHANGED = 2;
  public static final int DELETED = 3;
}






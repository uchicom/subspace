// (c) 2006 uchicom
package com.uchicom.subspace.table;

import java.util.List;

import javax.swing.table.DefaultTableModel;
/**
 *
 * @author UCHIYAMA
 */
public class ListTableModel extends DefaultTableModel {

    /** Creates a new instance of ListTableModel */
    public ListTableModel(List<String[]> rowList, int columnCount) {
        this.rowList = rowList;
        this.columnCount = columnCount;
    }
    public Object getValueAt(int row, int col) {
        String[] cells = (String[])rowList.get(row);
        if (cells.length > col) {
            return cells[col];
        } else {
            return null;

        }
    }
    public void setValueAt(Object value, int row, int col) {
        String[] cells = (String[])rowList.get(row);
        if (cells.length > col) {
            cells[col] = (String)value;
        }
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    public int  getColumnCount() {
        return columnCount;
    }
    public int getRowCount() {
        if (rowList != null) {
            return rowList.size();
        } else {
            return 0;
        }
    }


    public void removeRows(int[] aRow) {
    	for (int i = aRow.length - 1; i >= 0; i--) {
	    	rowList.remove(aRow[i]);
	    	fireTableRowsDeleted(aRow[i], aRow[i]);
    	}
    }


    public void addRows(int[] aRow) {
    	for (int i = aRow.length - 1; i >= 0; i--) {
	    	rowList.add(aRow[i], new String[columnCount]);
	    	fireTableRowsInserted(aRow[i], aRow[i]);
    	}
    }


    /** データ格納リスト */
    private List<String[]> rowList;

    /** 列最大数 */
    private int columnCount = 0;
}

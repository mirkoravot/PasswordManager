/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author mirko.ravot
 */
public class PasswordHistoryTableModel extends AbstractTableModel {
    
    List<Element> list;
    private String[] columnNames = {"Date", "Password"};
    
    PMTreeCellRenderer cellRenderer;
    
    
    public PasswordHistoryTableModel(List<Element> list) {
        this.list = list;
        cellRenderer = new PMTreeCellRenderer();
//        cellRenderer = new 
        //cellRender
    } 

    public Element getElementAt(int index) {
        return (Element)list.get(index);
    }
    
    @Override
    public int getRowCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    
    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }
    
    @Override
    public Class<?> getColumnClass(int column)
    {
        return JLabel.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object temp = null;
        try {
            Element element = (Element)list.get(rowIndex);
            if (columnIndex == 0) {
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Long.parseLong(element.getAttribute("date")));
                    return c.getTime().toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (columnIndex == 1) {
                return element.getAttribute("password");
            }
                    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }
    
}

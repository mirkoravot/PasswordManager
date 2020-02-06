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
public class AccountTableModel extends AbstractTableModel {
    
    List<Element> list;
    private String[] columnNames = {"Folder", "Name", "Username", "URL", "Expire"};
    
    PMTreeCellRenderer cellRenderer;
    
    
    public AccountTableModel(List<Element> list) {
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
            Element parent = (Element)element.getParentNode();
            if (columnIndex == 0) {
                return parent.getAttribute("name");
            }
            if (columnIndex == 1) {
                return element.getAttribute("name");
            }
            if (columnIndex == 2) {
                return element.getAttribute("username");
            }
            if (columnIndex == 3) {
                return element.getAttribute("url");
            }
            if (columnIndex == 4) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(element.getAttribute("expire")));
                    temp = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());   
                    
                    Calendar now = Calendar.getInstance();
                    long timediff = calendar.getTimeInMillis() - now.getTimeInMillis();
                    
                    int days = (int)(timediff / (1000 * 24 * 3600 ));
                    temp +=  " ("+days+" days)";
                    

                } catch (Exception ex) {
                    temp = "";
                }
                    
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }
    
}

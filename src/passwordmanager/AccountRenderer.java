/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author mirko.ravot
 */
public class AccountRenderer extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel jvalue = (JLabel)value;
        this.setBackground(jvalue.getBackground());
        this.setOpaque(jvalue.isOpaque());
        this.setText(jvalue.getText());
        this.setIcon(jvalue.getIcon());
        this.setSize(jvalue.getWidth(), 30);
        return this;
    }
    
    
    
}

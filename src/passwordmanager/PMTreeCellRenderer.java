/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.w3c.dom.Element;

/**
 *
 * @author mirko.ravot
 */
public class PMTreeCellRenderer extends DefaultTreeCellRenderer {
    
    
    private ImageIcon accountIcon = new ImageIcon(getClass().getResource("/icon/22x22/filesystems/user_identity.png"));
    private ImageIcon folderIcon = new ImageIcon(getClass().getResource("/icon/22x22/filesystems/folder.png"));
    private ImageIcon rootIcon = new ImageIcon(getClass().getResource("/icon/22x22/filesystems/desktop.png"));
    private ImageIcon browserIcon = new ImageIcon(getClass().getResource("/icon/22x22/apps/browser.png"));

    public ImageIcon getAccountIcon() {
        return accountIcon;
    }

    public void setAccountIcon(ImageIcon accountIcon) {
        this.accountIcon = accountIcon;
    }

    public ImageIcon getFolderIcon() {
        return folderIcon;
    }

    public void setFolderIcon(ImageIcon folderIcon) {
        this.folderIcon = folderIcon;
    }

    public ImageIcon getRootIcon() {
        return rootIcon;
    }

    public void setRootIcon(ImageIcon rootIcon) {
        this.rootIcon = rootIcon;
    }

    public ImageIcon getBrowserIcon() {
        return browserIcon;
    }

    public void setBrowserIcon(ImageIcon browserIcon) {
        this.browserIcon = browserIcon;
    }
    
    
        
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
 
        if( value instanceof DefaultMutableTreeNode){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            try {
                Element element = (Element)node.getUserObject();
                this.setText(element.getAttribute("name"));
                
                if (element.getNodeName().equals("account")) {
                    setIcon(accountIcon);
                    try {
                        if (element.getAttribute("url") != null && !element.getAttribute("url").equals("")) {
                            setIcon(browserIcon);
                            
                        }
                    } catch (Exception ex) {
                        
                    }
                }
                if (element.getNodeName().equals("folder")) {
                    setIcon(folderIcon);
                }
                if (element.getNodeName().equals("root")) {
                    setIcon(rootIcon);
                }
                
            } catch (Exception ex) {
                
                this.setText("UNKBOWN");
                
            }
         
        }
        return this;
    }
    
}

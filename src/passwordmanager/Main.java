/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import javafx.stage.FileChooser;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author mirko.ravot
 */
public class Main extends javax.swing.JFrame {
    


    AccountTableModel tableModel;
    
    public static String DATABASE_EXTENSION = "pmdb";
        
    private ImageIcon appIcon = new ImageIcon(getClass().getResource("/icon/22x22/actions/encrypted.png"));

    private Database database;
    
    String masterPassword;
    
    private Element elementMemory;
    
    boolean modified = false;
    boolean validKey = false;
    
    Settings settings;
    public static final String TITLE = "Password Manager";
    
    Document document;
    
    String filename = null;

    DefaultTreeModel dtModel = null;
    
    private void save() {
        saveDocument();
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
    
    private void setModified(boolean modified) {
        this.modified = modified;
        this.saveMenuItem.setEnabled(modified);
        this.jButtonTBSave.setEnabled(modified);
        this.setTitleFile();
    }
    
    
    
    

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setValidKey(boolean validKey) {
        this.validKey = validKey;
        this.jButtonTBNew.setEnabled(validKey);
        this.jButtonTBOpen.setEnabled(validKey);
        this.jButtonTBSave.setEnabled(validKey);
        this.jButtonTBSaveAs.setEnabled(validKey);
        this.newMenuItem.setEnabled(validKey);
        this.openMenuItem.setEnabled(validKey);
        this.saveMenuItem.setEnabled(validKey);
        this.saveAsMenuItem.setEnabled(validKey);
    }
    
    
    private void open() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(DATABASE_EXTENSION + " files", DATABASE_EXTENSION);
        fc.setFileFilter(filter);
        
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
           
            open(file.getAbsolutePath());
        } else { 
        }
        
    }

    private void open(String filename) {
        try {
            database = new Database();
            database.setFilename(filename);
            OpenDatabaseDialog d = new OpenDatabaseDialog(this, true);
            d.setLocationRelativeTo(null);
            d.setVisible(true);
            if (d.getReturnVal() != OpenDatabaseDialog.RETURN_OK) {
                this.database = null;
                return;
            }
            this.initDocument();
            this.updateTreeModel();
            this.setModified(false);
            setTitleFile();
            this.showExpiredPassword();

            
        } catch (Exception ex) {
            this.showExceptionDialog(ex);

        }
        
    }
    
    
    private void saveAs() {
        
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(DATABASE_EXTENSION + " files", DATABASE_EXTENSION);
        fc.setFileFilter(filter);
        
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (file.exists()) {
                int input = JOptionPane.showConfirmDialog(null, "File exists do you want to replace?");
                if (input != 0) {
                    return;
                }
            }
            this.database.setFilename(file.getAbsolutePath());
            this.saveDocument();
        } else {
        }
        
    }
    

    private void initDocument() {
        try {
            /*
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = dbf.newDocumentBuilder();
            document = b.newDocument();
            Element root = document.createElement("root");
            root.setAttribute("created", Calendar.getInstance().getTime().toString());
            root.setAttribute("nome", "workspace");
            document.appendChild(root);
            */
            dtModel = new DefaultTreeModel(builtTreeNode(database.getDocument().getDocumentElement()));
            this.jTreeMain.setModel(dtModel);
            updateTreeModel();
            this.jPanelTree.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }
    }
    
    private ArrayList getOpenedId() {
        ArrayList result = new ArrayList();
        
        return result;
    }
    
    public void updateTreeModel() {
        
        TreePath tp = this.jTreeMain.getSelectionPath();
        String id = "";
        if (tp != null) {
            try {
                DefaultMutableTreeNode last = (DefaultMutableTreeNode)tp.getLastPathComponent();
                Element o = (Element)last.getUserObject();
                id = o.getAttribute("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dtModel.setRoot(this.builtTreeNode(this.database.getDocument().getDocumentElement()));
        this.jTreeMain.setModel(dtModel);
        dtModel.reload();
        this.jTreeMain.setVisible(false);
        this.jTreeMain.setVisible(true);
        if (!id.equals("")) {
            DefaultMutableTreeNode node = findNode(id);
            if( node != null ) {
                TreePath path = new TreePath(node.getPath());
                this.jTreeMain.setSelectionPath(path);
                this.jTreeMain.setExpandsSelectedPaths(true);
                this.jTreeMain.scrollPathToVisible(path);
                jTreeMain.expandPath(path);
            }
        }
    }
    
    
    @Override
    public void dispose() {
        exit();
    }
       
    public void showExceptionDialog(Exception ex) {
        ex.printStackTrace();
        ErrorDialog edialog = new ErrorDialog(this, true, ex);
       
        edialog.setLocationRelativeTo(null);
        edialog.setVisible(true);
        
    }
    public void showSimpleExceptionDialog(Exception ex) {
        ex.printStackTrace();
        SimpleErrorDialog edialog = new SimpleErrorDialog(this, true, ex.getMessage());
       
        edialog.setLocationRelativeTo(null);
        edialog.setVisible(true);
        
    }


    private void newDocument() {
        this.database = new Database();

        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(DATABASE_EXTENSION + " files", DATABASE_EXTENSION);
        fc.setFileFilter(filter);
        
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            this.database.setFilename(file.getAbsolutePath());
            if (file.exists()) {
                int input = JOptionPane.showConfirmDialog(null, "File exists do you want to replace?");
                if (input != 0) {
                    this.database = null;
                    return;
                }
            }
            
        } else { 
            this.database = null;
            return;
        }
        NewDatabaseDialog d = new NewDatabaseDialog(this, true);
        d.setVisible(true);
        if (d.getReturnVal() != NewDatabaseDialog.RETURN_OK) {
            this.database = null;
        }
        this.initDocument();
        //this.showDocumentPanel();
    }

    private void showDocumentPanel() {
        getContentPane().add(this.jSplitPaneMain, java.awt.BorderLayout.CENTER);
        this.setState(JFrame.MAXIMIZED_BOTH);
        this.setState(JFrame.NORMAL);
        this.repaint();
    }

    
    private void initPost() {
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            
        }
        this.jToggleButtonShowClean.setSelected(false);
        settings = Settings.getSettings();
        
        
        UIManager.put("Table.rowHeight", 24);
        
        this.setIconImage(this.appIcon.getImage());
        
        //initDocument();
        this.jTreeMain.setCellRenderer(new PMTreeCellRenderer());
        jTreeMain.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //this.showKeyPanel();
//        this.showDetails(welcomePanel);
        int sizeX = 1000;
        if (settings.containsKey("size.x")) {
            try { sizeX = Integer.parseInt(settings.getProperty("size.x")); } catch (Exception ex) {};
        } else {
            settings.setProperty("size.x", "" + sizeX);
        }
        int sizeY = 800;
        if (settings.containsKey("size.y")) {
            try { sizeY = Integer.parseInt(settings.getProperty("size.y")); } catch (Exception ex) {};
        } else {
            settings.setProperty("size.y", "" + sizeY);
        }
        
        this.setSize(sizeX, sizeY);
        this.jSplitPaneMain.setDividerLocation(200);
        
        this.setLocationRelativeTo(null);
        this.setTitle(TITLE);
        this.jPanelTree.setVisible(false);
        tableModel = new AccountTableModel(null);
        setTableModel(tableModel);
        this.jTableAccount.getTableHeader().setReorderingAllowed(false);
        
        if (settings.containsKey(Settings.K_LAST_FILE)) {
            open(settings.getProperty(Settings.K_LAST_FILE));
        } else {
            
        }
        this.hasMemory(false);
        
    }
    
    private void setTableModel(AccountTableModel model) {
        this.tableModel = model;
        //jTableAccount.setDefaultRenderer(JLabel.class, new AccountRenderer());
        this.jTableAccount.setModel(tableModel);
        this.jTableAccount.getTableHeader().setReorderingAllowed(false);
    }
    
    
    
    
       
    public final DefaultMutableTreeNode findNode(String searchString) {
        List<DefaultMutableTreeNode> searchNodes = getSearchNodes((DefaultMutableTreeNode)this.jTreeMain.getModel().getRoot());
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)jTreeMain.getLastSelectedPathComponent();
        DefaultMutableTreeNode foundNode = null;
        int bookmark = -1;

        if( currentNode != null ) {
            for(int index = 0; index < searchNodes.size(); index++) {
                if( searchNodes.get(index) == currentNode ) {
                    bookmark = index;
                    break;
                }
            }
        }

        for(int index = bookmark + 1; index < searchNodes.size(); index++) {    
            try {
                Element e = (Element)searchNodes.get(index).getUserObject();
                if (e.getAttribute("name").toLowerCase().contains(searchString.toLowerCase())) {
                    foundNode = searchNodes.get(index);
                    break;
                }
                if (e.getAttribute("id").toLowerCase().contains(searchString.toLowerCase())) {
                    foundNode = searchNodes.get(index);
                    break;
                }
            } catch (Exception ex) {
                
            }

        }

        if( foundNode == null ) {
            for(int index = 0; index <= bookmark; index++) {    
                try {
                    Element e = (Element)searchNodes.get(index).getUserObject();
                    if (e.getAttribute("name").toLowerCase().contains(searchString.toLowerCase())) {
                        foundNode = searchNodes.get(index);
                        break;
                    }
                } catch (Exception ex) {

                }
            }
        }
        return foundNode;
    }   

    private final List<DefaultMutableTreeNode> getSearchNodes(DefaultMutableTreeNode root) {
        List<DefaultMutableTreeNode> searchNodes = new ArrayList<DefaultMutableTreeNode>();
        Enumeration<?> e = root.preorderEnumeration();
        while(e.hasMoreElements()) {
            searchNodes.add((DefaultMutableTreeNode)e.nextElement());
        }
        return searchNodes;
    }    
    
    
    private DefaultMutableTreeNode builtTreeNode(Element node) {
       DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode(node);
       NodeList nodeList = node.getChildNodes();
       for (int count = 0; count < nodeList.getLength(); count++) {
           try {
               Element tempNode = (Element)nodeList.item(count);
               dmtNode.add(builtTreeNode(tempNode));
           } catch (Exception ex) {
               
           }
       }
       return dmtNode;
     }    

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        initPost();
        //this.setValidKey(validKey);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuFolder = new javax.swing.JPopupMenu();
        jMenuItemFolderEdit = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFolderAddFolder = new javax.swing.JMenuItem();
        jMenuItemFolderAddAccount = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFolderDelete = new javax.swing.JMenuItem();
        jPopupMenuAccount = new javax.swing.JPopupMenu();
        jMenuItemAccountEdit = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAccountDelete = new javax.swing.JMenuItem();
        jToolBarMain = new javax.swing.JToolBar();
        jToolBarFile = new javax.swing.JToolBar();
        jButtonTBNew = new javax.swing.JButton();
        jButtonTBOpen = new javax.swing.JButton();
        jButtonTBSave = new javax.swing.JButton();
        jButtonTBSaveAs = new javax.swing.JButton();
        jToolBarEdit = new javax.swing.JToolBar();
        jButtonTBCut = new javax.swing.JButton();
        jButtonTBCopy = new javax.swing.JButton();
        jButtonTBPaste = new javax.swing.JButton();
        jButtonTBDelete = new javax.swing.JButton();
        jButtonTBAddFolder = new javax.swing.JButton();
        jButtonTBAddAccount = new javax.swing.JButton();
        jToolBarView = new javax.swing.JToolBar();
        jButtonTBViewExpiredPassword = new javax.swing.JButton();
        jToolBarVoid = new javax.swing.JToolBar();
        jToolBarSearch = new javax.swing.JToolBar();
        jPanelStatusbar = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jTextFieldPassword = new javax.swing.JTextField();
        jButtonPasswordCopy = new javax.swing.JButton();
        jToggleButtonShowClean = new javax.swing.JToggleButton();
        jPanelCenter = new javax.swing.JPanel();
        jSplitPaneMain = new javax.swing.JSplitPane();
        jPanelTree = new javax.swing.JPanel();
        jScrollPaneTree = new javax.swing.JScrollPane();
        jTreeMain = new javax.swing.JTree();
        jTextFieldSearch = new javax.swing.JTextField();
        jPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAccount = new javax.swing.JTable();
        jPanelTableHead = new javax.swing.JPanel();
        jLabelTableHead = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        viewExpiredPasswordMenu = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        jMenuItemFolderEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit.png"))); // NOI18N
        jMenuItemFolderEdit.setMnemonic('E');
        jMenuItemFolderEdit.setText("Edit");
        jMenuItemFolderEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFolderEditActionPerformed(evt);
            }
        });
        jPopupMenuFolder.add(jMenuItemFolderEdit);
        jPopupMenuFolder.add(jSeparator4);

        jMenuItemFolderAddFolder.setText("Add Folder");
        jMenuItemFolderAddFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFolderAddFolderActionPerformed(evt);
            }
        });
        jPopupMenuFolder.add(jMenuItemFolderAddFolder);

        jMenuItemFolderAddAccount.setText("Add Account");
        jMenuItemFolderAddAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFolderAddAccountActionPerformed(evt);
            }
        });
        jPopupMenuFolder.add(jMenuItemFolderAddAccount);
        jPopupMenuFolder.add(jSeparator2);

        jMenuItemFolderDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_delete.png"))); // NOI18N
        jMenuItemFolderDelete.setText("Delete");
        jMenuItemFolderDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFolderDeleteActionPerformed(evt);
            }
        });
        jPopupMenuFolder.add(jMenuItemFolderDelete);

        jMenuItemAccountEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit.png"))); // NOI18N
        jMenuItemAccountEdit.setMnemonic('E');
        jMenuItemAccountEdit.setText("Edit");
        jMenuItemAccountEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAccountEditActionPerformed(evt);
            }
        });
        jPopupMenuAccount.add(jMenuItemAccountEdit);
        jPopupMenuAccount.add(jSeparator5);

        jMenuItemAccountDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_delete.png"))); // NOI18N
        jMenuItemAccountDelete.setText("Delete");
        jMenuItemAccountDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAccountDeleteActionPerformed(evt);
            }
        });
        jPopupMenuAccount.add(jMenuItemAccountDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Password Manager");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBarMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarMain.setFloatable(false);
        jToolBarMain.setRollover(true);

        jToolBarFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarFile.setFloatable(false);

        jButtonTBNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/filenew.png"))); // NOI18N
        jButtonTBNew.setToolTipText("New file");
        jButtonTBNew.setFocusable(false);
        jButtonTBNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBNewActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonTBNew);

        jButtonTBOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/fileopen.png"))); // NOI18N
        jButtonTBOpen.setToolTipText("Open file");
        jButtonTBOpen.setFocusable(false);
        jButtonTBOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBOpenActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonTBOpen);

        jButtonTBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/filesave.png"))); // NOI18N
        jButtonTBSave.setToolTipText("Save");
        jButtonTBSave.setFocusable(false);
        jButtonTBSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBSaveActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonTBSave);

        jButtonTBSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/filesaveas.png"))); // NOI18N
        jButtonTBSaveAs.setToolTipText("SaveAs");
        jButtonTBSaveAs.setFocusable(false);
        jButtonTBSaveAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBSaveAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBSaveAsActionPerformed(evt);
            }
        });
        jToolBarFile.add(jButtonTBSaveAs);

        jToolBarMain.add(jToolBarFile);

        jToolBarEdit.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarEdit.setFloatable(false);
        jToolBarEdit.setRollover(true);

        jButtonTBCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/edit_cut.png"))); // NOI18N
        jButtonTBCut.setToolTipText("Cut");
        jButtonTBCut.setFocusable(false);
        jButtonTBCut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBCut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBCutActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBCut);

        jButtonTBCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/edit_copy.png"))); // NOI18N
        jButtonTBCopy.setToolTipText("Copy");
        jButtonTBCopy.setFocusable(false);
        jButtonTBCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBCopyActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBCopy);

        jButtonTBPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/edit_paste.png"))); // NOI18N
        jButtonTBPaste.setToolTipText("Paste");
        jButtonTBPaste.setFocusable(false);
        jButtonTBPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBPasteActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBPaste);

        jButtonTBDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/edit_delete.png"))); // NOI18N
        jButtonTBDelete.setToolTipText("Delete");
        jButtonTBDelete.setFocusable(false);
        jButtonTBDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBDeleteActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBDelete);

        jButtonTBAddFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/folder_new.png"))); // NOI18N
        jButtonTBAddFolder.setToolTipText("Cut");
        jButtonTBAddFolder.setEnabled(false);
        jButtonTBAddFolder.setFocusable(false);
        jButtonTBAddFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBAddFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBAddFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBAddFolderActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBAddFolder);

        jButtonTBAddAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/filesystems/user_identity_add.png"))); // NOI18N
        jButtonTBAddAccount.setToolTipText("Cut");
        jButtonTBAddAccount.setEnabled(false);
        jButtonTBAddAccount.setFocusable(false);
        jButtonTBAddAccount.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBAddAccount.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBAddAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBAddAccountActionPerformed(evt);
            }
        });
        jToolBarEdit.add(jButtonTBAddAccount);

        jToolBarMain.add(jToolBarEdit);

        jToolBarView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarView.setFloatable(false);
        jToolBarView.setRollover(true);

        jButtonTBViewExpiredPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/apps/clock.png"))); // NOI18N
        jButtonTBViewExpiredPassword.setToolTipText("Expired Password");
        jButtonTBViewExpiredPassword.setFocusable(false);
        jButtonTBViewExpiredPassword.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTBViewExpiredPassword.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTBViewExpiredPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTBViewExpiredPasswordActionPerformed(evt);
            }
        });
        jToolBarView.add(jButtonTBViewExpiredPassword);

        jToolBarMain.add(jToolBarView);

        jToolBarVoid.setBorder(null);
        jToolBarVoid.setRollover(true);
        jToolBarMain.add(jToolBarVoid);

        jToolBarSearch.setBorder(null);
        jToolBarSearch.setFloatable(false);
        jToolBarMain.add(jToolBarSearch);

        getContentPane().add(jToolBarMain, java.awt.BorderLayout.NORTH);

        jPanelStatusbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelStatus.setText("Status");
        jLabelStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextFieldPassword.setEditable(false);
        jTextFieldPassword.setBackground(new java.awt.Color(255, 255, 255));

        jButtonPasswordCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/actions/edit_copy.png"))); // NOI18N
        jButtonPasswordCopy.setToolTipText("Copy");
        jButtonPasswordCopy.setEnabled(false);
        jButtonPasswordCopy.setFocusable(false);
        jButtonPasswordCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPasswordCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPasswordCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPasswordCopyActionPerformed(evt);
            }
        });

        jToggleButtonShowClean.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/22x22/apps/applixware.png"))); // NOI18N
        jToggleButtonShowClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonShowCleanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelStatusbarLayout = new javax.swing.GroupLayout(jPanelStatusbar);
        jPanelStatusbar.setLayout(jPanelStatusbarLayout);
        jPanelStatusbarLayout.setHorizontalGroup(
            jPanelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStatusbarLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButtonShowClean, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPasswordCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelStatusbarLayout.setVerticalGroup(
            jPanelStatusbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButtonPasswordCopy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelStatusbarLayout.createSequentialGroup()
                .addComponent(jToggleButtonShowClean)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanelStatusbarLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTextFieldPassword)
        );

        getContentPane().add(jPanelStatusbar, java.awt.BorderLayout.PAGE_END);

        jPanelCenter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelCenter.setLayout(new java.awt.BorderLayout());

        jSplitPaneMain.setOneTouchExpandable(true);

        jPanelTree.setLayout(new java.awt.BorderLayout());

        jTreeMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeMainMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTreeMainMouseEntered(evt);
            }
        });
        jTreeMain.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeMainValueChanged(evt);
            }
        });
        jScrollPaneTree.setViewportView(jTreeMain);

        jPanelTree.add(jScrollPaneTree, java.awt.BorderLayout.CENTER);

        jTextFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchActionPerformed(evt);
            }
        });
        jPanelTree.add(jTextFieldSearch, java.awt.BorderLayout.NORTH);

        jSplitPaneMain.setLeftComponent(jPanelTree);

        jPanelTable.setLayout(new java.awt.BorderLayout());

        jTableAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableAccount.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableAccountMouseClicked(evt);
            }
        });
        jTableAccount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTableAccountKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTableAccount);

        jPanelTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanelTableHead.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTableHead.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelTableHead.setText("Details");

        javax.swing.GroupLayout jPanelTableHeadLayout = new javax.swing.GroupLayout(jPanelTableHead);
        jPanelTableHead.setLayout(jPanelTableHeadLayout);
        jPanelTableHeadLayout.setHorizontalGroup(
            jPanelTableHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTableHead, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
        );
        jPanelTableHeadLayout.setVerticalGroup(
            jPanelTableHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTableHead)
        );

        jPanelTable.add(jPanelTableHead, java.awt.BorderLayout.NORTH);

        jSplitPaneMain.setRightComponent(jPanelTable);

        jPanelCenter.add(jSplitPaneMain, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/filenew.png"))); // NOI18N
        newMenuItem.setMnemonic('n');
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/fileopen.png"))); // NOI18N
        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/filesave.png"))); // NOI18N
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/filesaveas.png"))); // NOI18N
        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator3);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/exit.png"))); // NOI18N
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_cut.png"))); // NOI18N
        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_copy.png"))); // NOI18N
        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_paste.png"))); // NOI18N
        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/actions/edit_delete.png"))); // NOI18N
        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        viewExpiredPasswordMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/16x16/apps/clock.png"))); // NOI18N
        viewExpiredPasswordMenu.setText("Expired Password");
        viewExpiredPasswordMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewExpiredPasswordMenuActionPerformed(evt);
            }
        });
        viewMenu.add(viewExpiredPasswordMenu);

        menuBar.add(viewMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed

        exit();
        
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void jTreeMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMainMouseClicked
        if (evt.getButton() == 1) {
            showElement();
        }
        if (evt.getButton() == 3) {
            showPopupMenu(evt);
        }
    }//GEN-LAST:event_jTreeMainMouseClicked

    
    private void exit() {
        try {
            Dimension d = this.getSize();
            settings.setProperty("size.x", "" + (int)d.getWidth());
            settings.setProperty("size.y", "" + (int)d.getHeight());
            if (database != null && database.getFilename() != null)
                settings.setProperty(Settings.K_LAST_FILE, database.getFilename());
            settings.save();
            if (modified) {
                int input = JOptionPane.showConfirmDialog(null, "Database modified do you want to save?", "Close option", JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    save();
                }                
            }
        } catch (Exception ex) {
            
        }
        System.exit(0);
    }
    
    private void showPopupMenu(java.awt.event.MouseEvent evt) {
        
        
        try {

            if (this.jTreeMain.getSelectionCount() < 1) return;

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 

            Element element = (Element)selectedNode.getUserObject();
            
            if (element.getNodeName().equals("root") || element.getNodeName().equals("folder")) {
                this.jMenuItemFolderDelete.setEnabled(element.getNodeName().equals("folder"));
                //this.jMenuItemFolderEdit.setEnabled(element.getNodeName().equals("folder"));
                this.jPopupMenuFolder.show((JComponent)evt.getSource(), evt.getX(), evt.getY());
            }
            if (element.getNodeName().equals("account")) {
                this.jPopupMenuAccount.show((JComponent)evt.getSource(), evt.getX(), evt.getY());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed

        this.showDetails(new AboutPanel());
       

        
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void editFolder() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element current = (Element)selectedNode.getUserObject();
            FolderDialog dialog = new FolderDialog(this, current, true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            if (dialog.getReturnVal() == ChildDialog.RETURN_OK) {
                updateTreeModel();
                        this.setModified(true);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showExceptionDialog(ex);
        }
        
    }
    
    private void setStatus(Element element) {
        if (element.getNodeName().equals("account")) {
            this.jTextFieldPassword.setBackground(Color.GREEN);
            if (this.jToggleButtonShowClean.isSelected())
                this.jTextFieldPassword.setForeground(Color.BLACK);
            else
                this.jTextFieldPassword.setForeground(Color.GREEN);
                
            this.jTextFieldPassword.setText(element.getAttribute("password"));
            this.jButtonPasswordCopy.setEnabled(true);
        } else {
            this.jTextFieldPassword.setForeground(Color.GRAY);
            this.jTextFieldPassword.setBackground(Color.GRAY);
            this.jTextFieldPassword.setText("");
            this.jButtonPasswordCopy.setEnabled(false);
        }
        this.jLabelStatus.setText(element.getNodeName().toUpperCase() + " - " + element.getAttribute("name"));
    }

    private void editAccount() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element current = (Element)selectedNode.getUserObject();
            editAccount(current);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showExceptionDialog(ex);
        }
        
    }
    
    private void editAccount(Element account) {
        try {
            AccountDialog dialog = new AccountDialog(this, account, true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            if (dialog.getReturnVal() == ChildDialog.RETURN_OK) {
                updateTreeModel();
                this.setModified(true);
            }
        } catch (Exception ex) {
            this.showExceptionDialog(ex);
        }
    }
    
    private void addFolder() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element parent = (Element)selectedNode.getUserObject();
            
            
            
            
            Element folder = database.createElement("folder");
            folder.setAttribute("name", "New Folder");
            FolderDialog dialog = new FolderDialog(this, folder, true);
                       
            dialog.setLocationRelativeTo(null);

            dialog.setVisible(true);
            if (dialog.getReturnVal() == ChildDialog.RETURN_OK) {
                parent.appendChild(folder);
                updateTreeModel();
                this.setModified(true);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showExceptionDialog(ex);
        }
        
    }
    private void deleteFolder() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            
             int input = JOptionPane.showConfirmDialog(null, 
                "Vuoi cancellare la cartella?", "Select an Option...",JOptionPane.YES_NO_OPTION);

             if (input == 0) {
            
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
                Element element = (Element)selectedNode.getUserObject();
                element.getParentNode().removeChild(element);
                updateTreeModel();
                        this.setModified(true);

             }
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }
        
    }
    
    private void deleteAccount() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            
             int input = JOptionPane.showConfirmDialog(null, 
                "Vuoi cancellare l'account?", "Select an Option...",JOptionPane.YES_NO_OPTION);

             if (input == 0) {
            
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
                Element element = (Element)selectedNode.getUserObject();
                element.getParentNode().removeChild(element);
                updateTreeModel();
                        this.setModified(true);

             }
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }
        
    }
    
    private void jMenuItemFolderAddFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFolderAddFolderActionPerformed
        
        addFolder();
            

    }//GEN-LAST:event_jMenuItemFolderAddFolderActionPerformed

    private void jMenuItemFolderAddAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFolderAddAccountActionPerformed

        addAccount();

    }//GEN-LAST:event_jMenuItemFolderAddAccountActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        this.saveAs();
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        save();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        open();
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void jMenuItemFolderDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFolderDeleteActionPerformed

        deleteFolder();
    }//GEN-LAST:event_jMenuItemFolderDeleteActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed

        this.newDocument();
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void jButtonTBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBNewActionPerformed

        this.newDocument();

    }//GEN-LAST:event_jButtonTBNewActionPerformed

    private void jButtonTBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBSaveActionPerformed

        this.save();
    }//GEN-LAST:event_jButtonTBSaveActionPerformed

    private void jButtonTBOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBOpenActionPerformed
    
        this.open();
    }//GEN-LAST:event_jButtonTBOpenActionPerformed

    private void jMenuItemAccountDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAccountDeleteActionPerformed
        deleteAccount();
        
    }//GEN-LAST:event_jMenuItemAccountDeleteActionPerformed

    private void jButtonTBSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBSaveAsActionPerformed
    
        saveAs();
    }//GEN-LAST:event_jButtonTBSaveAsActionPerformed

    private void jMenuItemFolderEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFolderEditActionPerformed

        editFolder();

    }//GEN-LAST:event_jMenuItemFolderEditActionPerformed

    private void jTreeMainMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMainMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jTreeMainMouseEntered

    private void jMenuItemAccountEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAccountEditActionPerformed

         editAccount();
    }//GEN-LAST:event_jMenuItemAccountEditActionPerformed

    private void jTreeMainValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeMainValueChanged

                    
        if (this.jTreeMain.getSelectionCount() < 1) return;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
        Element element = (Element)selectedNode.getUserObject();
        if (element.getNodeName().equals("folder") || element.getNodeName().equals("root")) {
            this.jButtonTBAddFolder.setEnabled(true);
            this.jButtonTBAddAccount.setEnabled(true);
            this.jLabelTableHead.setText(element.getAttribute("name") + " - accounts");
            NodeList list = element.getElementsByTagName("account");
            tableModel = new AccountTableModel(toListOfElement(list));
            this.setTableModel(tableModel);
        } else {

            if (element.getNodeName().equals("account")) {
                ArrayList alist = new ArrayList();
                alist.add(element);
                tableModel = new AccountTableModel(alist);
                this.setTableModel(tableModel);
            }


            this.jButtonTBAddFolder.setEnabled(false);
            this.jButtonTBAddAccount.setEnabled(false);

        }

        setStatus(element);


    }//GEN-LAST:event_jTreeMainValueChanged

    
    private void showExpiredPassword() {
        try {
            Element element = (Element)database.getDocument().getDocumentElement();
            this.jLabelTableHead.setText("Expired/ing password");
            NodeList list = element.getElementsByTagName("account");
            Calendar now = Calendar.getInstance();
            ArrayList alist = new ArrayList();
            for (int i = 0; i < list.getLength(); i++) {
                Element currElement = (Element)list.item(i);
                if (currElement.getAttribute("expire") != null){
                    try {
                        if (!currElement.getAttribute("expire").equals("")) {
                            Long longtime = Long.parseLong(currElement.getAttribute("expire"));
                            
                            int days = (int)((longtime - now.getTimeInMillis()) / (1000 * 24 * 3600));
                            
                            if (days < 7)
                                alist.add(list.item(i));
                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        
                    }
                }
                
            }
            if (alist.size() > 0) {
                tableModel = new AccountTableModel(alist.subList(0, alist.size()));
            } else {
                tableModel = new AccountTableModel(null);
            }
            this.setTableModel(tableModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }
    }
    
    private List<Element> toListOfElement(NodeList list) {
        return new AbstractList<Element>() {
            public int size() {
                return list.getLength();
            }

            public Element get(int index) {
                Element item = (Element)list.item(index);
                if (item == null)
                  throw new IndexOutOfBoundsException();
                return item;
            }
        };
    }
    
    
    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed

        if (this.jTextFieldSearch.getText().length()>2) {
            
            DefaultMutableTreeNode node = findNode(this.jTextFieldSearch.getText());                
            if( node != null ) {
                TreePath path = new TreePath(node.getPath());
                this.jTreeMain.setSelectionPath(path);
                this.jTreeMain.scrollPathToVisible(path);
            }  
        }
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed

        delete();
        
    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        cut();
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        copy();
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        paste();
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private void jButtonTBCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBCutActionPerformed

        cut();
    }//GEN-LAST:event_jButtonTBCutActionPerformed

    private void jButtonTBCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBCopyActionPerformed

        copy();
    }//GEN-LAST:event_jButtonTBCopyActionPerformed

    private void jButtonTBPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBPasteActionPerformed
        paste();
    }//GEN-LAST:event_jButtonTBPasteActionPerformed

    private void jButtonTBDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBDeleteActionPerformed
        delete();
    }//GEN-LAST:event_jButtonTBDeleteActionPerformed

    private void jTableAccountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAccountMouseClicked

        if (evt.getClickCount() == 1) {
            setStatus(tableModel.getElementAt(this.jTableAccount.getSelectedRow()));
        }

        if (evt.getClickCount() == 2) {
            editAccount(tableModel.getElementAt(this.jTableAccount.getSelectedRow()));
        } 

    }//GEN-LAST:event_jTableAccountMouseClicked

    private void viewExpiredPasswordMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewExpiredPasswordMenuActionPerformed
        this.showExpiredPassword();

    }//GEN-LAST:event_viewExpiredPasswordMenuActionPerformed

    private void jButtonTBViewExpiredPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBViewExpiredPasswordActionPerformed

        showExpiredPassword();
    }//GEN-LAST:event_jButtonTBViewExpiredPasswordActionPerformed

    private void jTableAccountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableAccountKeyReleased

        try {
            setStatus(tableModel.getElementAt(this.jTableAccount.getSelectedRow()));
        } catch (Exception e) {
            
        }

    }//GEN-LAST:event_jTableAccountKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exit();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonTBAddFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBAddFolderActionPerformed

        addFolder();
        
        
    }//GEN-LAST:event_jButtonTBAddFolderActionPerformed

    private void jButtonTBAddAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTBAddAccountActionPerformed
        addAccount();
    }//GEN-LAST:event_jButtonTBAddAccountActionPerformed

    private void jButtonPasswordCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPasswordCopyActionPerformed
        StringSelection selection = new StringSelection(this.jTextFieldPassword.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);            
        

    }//GEN-LAST:event_jButtonPasswordCopyActionPerformed

    private void jToggleButtonShowCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonShowCleanActionPerformed
        if (this.jToggleButtonShowClean.isSelected()) {
            this.jTextFieldPassword.setForeground(Color.BLACK);
        } else {
            this.jTextFieldPassword.setForeground(Color.GREEN);
            
        }

    }//GEN-LAST:event_jToggleButtonShowCleanActionPerformed

    
    private void copy() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element element = (Element)selectedNode.getUserObject();
            this.elementMemory = (Element)element.cloneNode(true);
            this.elementMemory.setAttribute("id", null);
            hasMemory(true);
        } catch (Exception ex) {
            
        }
    }

    private void cut() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element element = (Element)selectedNode.getUserObject();
            this.elementMemory = element;
            hasMemory(true);
            
        } catch (Exception ex) {
            
        }
    }

    private void paste() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element element = (Element)selectedNode.getUserObject();
            if (element.getNodeName().equals("folder")) {
                if (elementMemory!=null)
                element.appendChild(elementMemory);
                this.updateTreeModel();
                        this.setModified(true);

            }
            this.elementMemory = null;
            hasMemory(false);
        } catch (Exception ex) {
        }
    }
    
    public void hasMemory(boolean memory) {
        this.pasteMenuItem.setEnabled(memory);
        this.jButtonTBPaste.setEnabled(memory);
        
    }
    
    private void delete() {
        try {
            
            if (this.jTreeMain.getSelectionCount() < 1) return;

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            
            Element element = (Element)selectedNode.getUserObject();
            if (element.getNodeName().equals("account")) deleteAccount();
            if (element.getNodeName().equals("folder")) deleteFolder();

        } catch (Exception ex) {
            
        }
        
    }
    
    private void saveDocument() {
        try {
            database.save();
            setModified(false);
            setTitleFile();
        } catch (Exception ex) {
            this.showExceptionDialog(ex);
        }
    }
    
    private void setTitleFile() {
        this.setTitle(TITLE + ": " + this.getDatabase().getFilename() + ((modified)?"*":""));
    }
    
    private void showDetails(Component c) {
        this.jSplitPaneMain.setRightComponent(c);
    }
    
    
    private void showElement() {
        
        
        
        int holdSplitPosition = this.jSplitPaneMain.getDividerLocation();
         try {

             /*
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 

            Element element = (Element)selectedNode.getUserObject();
            if (element.getNodeName().equals("folder")) {
                this.jSplitPaneMain.setRightComponent(new FolderPanel(this, element));
            }
            if (element.getNodeName().equals("account")) {
                this.jSplitPaneMain.setRightComponent(new AccountPanel(this, element));
            }
            if (element.getNodeName().equals("root")) {
                this.jSplitPaneMain.setRightComponent(new WorkspacePanel(this, element));
            }
            
*/
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }
        
        
        
        this.jSplitPaneMain.setDividerLocation(holdSplitPosition);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButtonPasswordCopy;
    private javax.swing.JButton jButtonTBAddAccount;
    private javax.swing.JButton jButtonTBAddFolder;
    private javax.swing.JButton jButtonTBCopy;
    private javax.swing.JButton jButtonTBCut;
    private javax.swing.JButton jButtonTBDelete;
    private javax.swing.JButton jButtonTBNew;
    private javax.swing.JButton jButtonTBOpen;
    private javax.swing.JButton jButtonTBPaste;
    private javax.swing.JButton jButtonTBSave;
    private javax.swing.JButton jButtonTBSaveAs;
    private javax.swing.JButton jButtonTBViewExpiredPassword;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTableHead;
    private javax.swing.JMenuItem jMenuItemAccountDelete;
    private javax.swing.JMenuItem jMenuItemAccountEdit;
    private javax.swing.JMenuItem jMenuItemFolderAddAccount;
    private javax.swing.JMenuItem jMenuItemFolderAddFolder;
    private javax.swing.JMenuItem jMenuItemFolderDelete;
    private javax.swing.JMenuItem jMenuItemFolderEdit;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelStatusbar;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JPanel jPanelTableHead;
    private javax.swing.JPanel jPanelTree;
    private javax.swing.JPopupMenu jPopupMenuAccount;
    private javax.swing.JPopupMenu jPopupMenuFolder;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPaneMain;
    private javax.swing.JTable jTableAccount;
    private javax.swing.JTextField jTextFieldPassword;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JToggleButton jToggleButtonShowClean;
    private javax.swing.JToolBar jToolBarEdit;
    private javax.swing.JToolBar jToolBarFile;
    private javax.swing.JToolBar jToolBarMain;
    private javax.swing.JToolBar jToolBarSearch;
    private javax.swing.JToolBar jToolBarView;
    private javax.swing.JToolBar jToolBarVoid;
    private javax.swing.JTree jTreeMain;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem viewExpiredPasswordMenu;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    private void addAccount() {
        try {
            if (this.jTreeMain.getSelectionCount() < 1) return;
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.jTreeMain.getLastSelectedPathComponent(); 
            Element parent = (Element)selectedNode.getUserObject();
            Element account = database.createElement("account");
            account.setAttribute("name", "New Account");
            AccountDialog dialog = new AccountDialog(this, account, true);

            dialog.setLocationRelativeTo(null);

            dialog.setVisible(true);
            if (dialog.getReturnVal() == ChildDialog.RETURN_OK) {
                parent.appendChild(account);
                updateTreeModel();
                        this.setModified(true);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showExceptionDialog(ex);
        }
    }
    
    
    

}

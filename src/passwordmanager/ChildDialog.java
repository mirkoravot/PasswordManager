/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

/**
 *
 * @author mirko.ravot
 */
public class ChildDialog extends javax.swing.JDialog {
    
    Main console;
       
    private int returnVal;

    public static int RETURN_OK = 1;
    public static int RETURN_CANCEL = 0;

    /**
     * Creates new form ChildDialog
     */
    public ChildDialog(Main parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public int getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(int returnVal) {
        this.returnVal = returnVal;
    }

    protected void returnOK() {
        this.returnVal = RETURN_OK;
        this.dispose();
    }
    
    protected void returnCancel() {
        this.returnVal = RETURN_CANCEL;
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

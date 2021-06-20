/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author nguye
 */
public class DanhSachLopPanel extends javax.swing.JPanel {

    /**
     * Creates new form DanhSachLop
     */
    
    void createClassList(){
      String sql ="select * from  LOP";
        try{
          Connection  cn= JDBCConnection.ketNoiJBDC();
          Statement sta=cn.createStatement();
          ResultSet r = sta.executeQuery(sql); 
          while(r.next()){
         
             this.clasBox.addItem(r.getString("TenLop"));
          }
        }
          catch (SQLException e){
              return;
          }
        
    }
    void createYearList(){
         String sql =" select distinct  HOCKI_NAM.NAM from HOCKI_NAM";
        try{
          Connection  cn= JDBCConnection.ketNoiJBDC();
          Statement sta=cn.createStatement();
          ResultSet r = sta.executeQuery(sql); 
          while(r.next()){
            
             this.yearBox.addItem(r.getString("Nam"));
          }
        }
          catch (SQLException e){
              return;
          }
    }
     public void  loadtable(){
    String sql ="exec sp_DanhSachLop_InDanhSach "+this.yearBox.getSelectedItem().toString()+",'"+this.clasBox.getSelectedItem().toString()+"'";
        try{
          Connection  cn= JDBCConnection.ketNoiJBDC();
           CallableStatement   cst=cn.prepareCall(sql);
            ResultSet r = cst.executeQuery(); 
            int i=0;
          while(r.next()){
              
              i++;
            String arr[]={Integer.toString(i),r.getString("MaHocSinh"),r.getString("HoTen"),r.getString("MaLop"),r.getString("TBHK1"),r.getString("TBHK2")};
             DefaultTableModel tblM= (DefaultTableModel)this.infotable.getModel();
          tblM.addRow(arr);
          }
        }
          catch (SQLException e){
              return;
          }
    
    }
    public DanhSachLopPanel() {
        initComponents();
        this.createClassList();
        this.createYearList();
    }
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        infotable = new javax.swing.JTable();
        clasBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        yearBox = new javax.swing.JComboBox<>();
        seekButton = new javax.swing.JButton();

        jLabel1.setText("Lớp");

        infotable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "MSSV", "Họ tên", "Lớp", "TBHK1", "TBHK2"
            }
        ));
        jScrollPane1.setViewportView(infotable);

        jLabel2.setText("Năm:");

        seekButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/16x16/search.png"))); // NOI18N
        seekButton.setText("Tìm kiếm");
        seekButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seekButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(25, 25, 25)
                        .addComponent(clasBox, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(yearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(seekButton)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clasBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(yearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seekButton))
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void seekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seekButtonActionPerformed
        // TODO add your handling code here:
        loadtable();
        
    }//GEN-LAST:event_seekButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> clasBox;
    private javax.swing.JTable infotable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton seekButton;
    private javax.swing.JComboBox<String> yearBox;
    // End of variables declaration//GEN-END:variables
}

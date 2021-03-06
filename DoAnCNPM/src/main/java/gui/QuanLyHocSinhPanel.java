/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import popupframe.SuaHocSinh_QuanLiHocSinh;
import popupframe.ThemHocSinh_QuanLiHocSinh;
import thamso.Contact;
import thamso.LayThamSo;

/**
 *
 * @author Admin
 */
public final class QuanLyHocSinhPanel extends javax.swing.JPanel {

    SuaHocSinh_QuanLiHocSinh suahs = new SuaHocSinh_QuanLiHocSinh();
    ThemHocSinh_QuanLiHocSinh themhs = new ThemHocSinh_QuanLiHocSinh();

    int NamSinhCuaTuoiToiThieu = Calendar.getInstance().get(Calendar.YEAR) - new LayThamSo().getTuoiToiThieu();
    int NamSinhCuaTuoiToiDa = Calendar.getInstance().get(Calendar.YEAR) - new LayThamSo().getTuoiToiDa();

    DefaultTableModel bangDuLieu = new DefaultTableModel();

    public DefaultTableModel getBangDuLieu() {
        return bangDuLieu;
    }
    //goi ham return tuoi toi thieu tuoi toi da

    String DiemBanDau = "";

    public QuanLyHocSinhPanel() {

        initComponents();

        //?????i font m???c b???ng
        tableHocSinh.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        suahs.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        themhs.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] colTitle = {"STT", "M?? h???c sinh", "H??? t??n", "Ng??y sinh", "Gi???i t??nh", "?????a ch???", "Email"};
        boolean[] isEditable = {false, false, true, true, true, true, true};
        bangDuLieu = new DefaultTableModel(colTitle, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return isEditable[column];
            }
        };
        tableHocSinh.setModel(bangDuLieu);

        HocSinhTrongTruong();
        themhs.ThemMoi.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                themhs.CapNhatDataBase();
                HocSinhTrongTruong();

            }
        });

        tableHocSinh.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {
            @Override
            @SuppressWarnings("empty-statement")
            public void editingStopped(ChangeEvent e) {

                int column = tableHocSinh.getSelectedColumn();
                int row = tableHocSinh.getSelectedRow();
                String checkValue = tableHocSinh.getValueAt(row, column).toString();
                String MaHS = tableHocSinh.getValueAt(row, 1).toString();
                String sql = "";

                if (column == 2) {
                    if (checkValue.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "H??? t??n kh??ng ???????c ????? tr???ng");
                        tableHocSinh.setValueAt(DiemBanDau, row, column);

                    } else {
                        sql = "Update HocSinh set HoTen=N'" + checkValue + "' where MaHocSinh='" + MaHS + "'";
                    }

                } //xet truong hop sua ngay sinh
                else if (column == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    if (checkValue.isEmpty()) {
                        tableHocSinh.setValueAt(DiemBanDau, row, column);
                    } else {
                        try {

                            Date date = sdf.parse(checkValue);
                            int year = date.getYear() + 1900;

                            if (NamSinhCuaTuoiToiDa > year || year > NamSinhCuaTuoiToiThieu) {
                                JOptionPane.showMessageDialog(null, "Ng??y sinh b???n nh???p ???? sai");
                                tableHocSinh.setValueAt(DiemBanDau, row, column);

                            } else {
                                sql = "Update HocSinh set NgaySinh='" + checkValue + "' where MaHocSinh='" + MaHS + "'";
                            }

                        } catch (ParseException ex) {

                            JOptionPane.showMessageDialog(null, "Ng??y sinh b???n nh???p ???? sai");
                            tableHocSinh.setValueAt(DiemBanDau, row, column);
                        }

                    }

                } else if (column == 4) {

                    if (!"Nam".equals(checkValue) && !"Nu".equals(checkValue)) {
                        JOptionPane.showMessageDialog(null, "Vui l??ng nh???p 2 gi?? tr??? Nam ho???c Nu");
                        tableHocSinh.setValueAt(DiemBanDau, row, column);

                    } else {
                        sql = "Update HocSinh set GioiTinh='" + checkValue + "' where MaHocSinh='" + MaHS + "'";
                    }
                } else if (column == 5) {
                    sql = "Update HocSinh set DiaChi=N'" + checkValue + "' where MaHocSinh='" + MaHS + "'";
                } else if (column == 6) {

                    if (!(checkValue.contains("@") && checkValue.contains(".")) && !checkValue.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Email b???n nh???p ???? sai");
                        tableHocSinh.setValueAt(DiemBanDau, row, column);
                    } else {
                        sql = "Update HocSinh set Email='" + checkValue + "' where MaHocSinh='" + MaHS + "'";
                    }
                }
                Connection con = JDBCConnection.ketNoiJBDC();
                try {
                    Statement mystm = con.createStatement();

                    mystm.executeUpdate(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            @Override
            public void editingCanceled(ChangeEvent e) {

            }
        });
        suahs.CapNhatHocSinh.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                suahs.capNhatHS();
                HocSinhTrongTruong();

            }
        });
    }

    public void HocSinhTrongTruong() {

        bangDuLieu.setRowCount(0);
        tableHocSinh.setModel(bangDuLieu);
        Connection con = JDBCConnection.ketNoiJBDC();
        try {
            Statement mystm = con.createStatement();
            String sql = "SELECT * FROM HOCSINH";
            ResultSet rs = mystm.executeQuery(sql);
            int STT = 1;
            while (rs.next()) {
                String HoTenHS = rs.getString("HoTen");
                String NgaySinh = rs.getString("NgaySinh");

                if (NgaySinh != null) {
                    Date dateNgaySinh = new SimpleDateFormat("yyyy-MM-dd").parse(NgaySinh);
                    NgaySinh = new SimpleDateFormat("dd/MM/yyyy").format(dateNgaySinh);
                }

                String GioiTinh = rs.getString("GioiTinh");
                String DiaChi = rs.getString("DiaChi");
                String Email = rs.getString("Email");
                String MaHocSinh = rs.getString("MaHocSinh");
                String[] data = {Integer.toString(STT), MaHocSinh, HoTenHS, NgaySinh, GioiTinh, DiaChi, Email};
                bangDuLieu.addRow(data);

                STT++;
            }
            tableHocSinh.setModel(bangDuLieu);

        } catch (SQLException ex) {
            System.out.println("knthanh cong");
        } catch (ParseException ex) {
            Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        buttonGroup2 = new javax.swing.ButtonGroup();
        Xoa = new javax.swing.JButton();
        ThemMoi = new javax.swing.JButton();
        Sua = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        MaHS = new javax.swing.JTextField();
        HoTen = new javax.swing.JTextField();
        TimKiembutton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableHocSinh = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ExcelButton = new javax.swing.JButton();
        Sample = new javax.swing.JButton();

        setBackground(new java.awt.Color(239, 247, 248));

        Xoa.setBackground(new java.awt.Color(237, 98, 96));
        Xoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xoa.setForeground(new java.awt.Color(255, 255, 255));
        Xoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/icons8_delete_24px_1.png"))); // NOI18N
        Xoa.setText("X??a");
        Xoa.setBorder(null);
        Xoa.setFocusPainted(false);
        Xoa.setFocusable(false);
        Xoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XoaActionPerformed(evt);
            }
        });

        ThemMoi.setBackground(new java.awt.Color(128, 99, 246));
        ThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThemMoi.setForeground(new java.awt.Color(255, 255, 255));
        ThemMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/icons8_add_administrator_24px.png"))); // NOI18N
        ThemMoi.setText("Th??m m???i");
        ThemMoi.setBorder(null);
        ThemMoi.setFocusPainted(false);
        ThemMoi.setFocusable(false);
        ThemMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ThemMoiActionPerformed(evt);
            }
        });

        Sua.setBackground(new java.awt.Color(254, 193, 6));
        Sua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Sua.setForeground(new java.awt.Color(255, 255, 255));
        Sua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/icons8_pencil_24px_1.png"))); // NOI18N
        Sua.setText("S???a");
        Sua.setBorder(null);
        Sua.setFocusPainted(false);
        Sua.setFocusable(false);
        Sua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SuaActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(114, 217, 118));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/icons8_refresh_24px.png"))); // NOI18N
        jButton2.setText("L??m m???i");
        jButton2.setBorder(null);
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel2.setText("H??? t??n:");

        jLabel3.setText("M?? h???c sinh:");

        MaHS.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        HoTen.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        TimKiembutton.setBackground(new java.awt.Color(0, 176, 239));
        TimKiembutton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        TimKiembutton.setForeground(new java.awt.Color(255, 255, 255));
        TimKiembutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/icons8_search_24px_1.png"))); // NOI18N
        TimKiembutton.setText("T??m ki???m");
        TimKiembutton.setBorder(null);
        TimKiembutton.setFocusPainted(false);
        TimKiembutton.setFocusable(false);
        TimKiembutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TimKiembuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel3)
                .addGap(27, 27, 27)
                .addComponent(MaHS, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(HoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(TimKiembutton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(MaHS, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(HoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TimKiembutton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("T??m ki???m h???c sinh");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane1.setOpaque(false);

        tableHocSinh.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "M?? h???c sinh", "H??? t??n", "Ng??y sinh", "Gi???i t??nh", "?????a ch???", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableHocSinh.setFocusable(false);
        tableHocSinh.setGridColor(new java.awt.Color(255, 255, 255));
        tableHocSinh.setIntercellSpacing(new java.awt.Dimension(0, 1));
        tableHocSinh.setOpaque(false);
        tableHocSinh.setRowHeight(25);
        tableHocSinh.setSelectionBackground(new java.awt.Color(0, 176, 239));
        tableHocSinh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tableHocSinhMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tableHocSinh);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Danh s??ch h???c sinh");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(128, 99, 246));
        jLabel5.setText("QU???N L?? H???C SINH");
        jLabel5.setFocusable(false);

        ExcelButton.setBackground(new java.awt.Color(51, 102, 0));
        ExcelButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ExcelButton.setForeground(new java.awt.Color(255, 255, 255));
        ExcelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/16x16/microsoft-excel-4-722715 (1).png"))); // NOI18N
        ExcelButton.setText("Nh???p Excel");
        ExcelButton.setBorder(null);
        ExcelButton.setFocusPainted(false);
        ExcelButton.setFocusable(false);
        ExcelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExcelButtonActionPerformed(evt);
            }
        });

        Sample.setBackground(new java.awt.Color(51, 102, 0));
        Sample.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Sample.setForeground(new java.awt.Color(255, 255, 255));
        Sample.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/poly/poly/app/icons/16x16/microsoft-excel-4-722715 (1).png"))); // NOI18N
        Sample.setText("File Excel M???u");
        Sample.setBorder(null);
        Sample.setFocusPainted(false);
        Sample.setFocusable(false);
        Sample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SampleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 704, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 800, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Xoa, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Sua, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExcelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Sample, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Xoa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(Sua, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ExcelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Sample, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void XoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XoaActionPerformed
        //L???y v??? tr?? ??ang ch???n tr??n tableHocSinh
        int row = tableHocSinh.getSelectedRow();
        //check xem chua chon dong nao
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui l??ng ch???n d??ng b???n c???n x??a!");
        } else {
            int xacNhan = JOptionPane.showConfirmDialog(null, "Th??ng tin h???c sinh s??? b??? x??a v??nh vi???n", "X??c nh???n", JOptionPane.YES_NO_OPTION);
            if (xacNhan == JOptionPane.YES_OPTION) {
                Connection con = JDBCConnection.ketNoiJBDC();
                try {
                    CallableStatement mystm = con.prepareCall("{call sp_TiepNhanHocSinh_XoaHocSinh(?)}");
                    mystm.setString(1, tableHocSinh.getValueAt(row, 1).toString());
                    mystm.executeQuery();

                } catch (SQLException ex) {

                }
                JOptionPane.showMessageDialog(null, "X??a th??nh c??ng!");
                HocSinhTrongTruong();

            }
        }

        //X??a d??ng ??ang ch???n ra kh???i bangDuLieu

    }//GEN-LAST:event_XoaActionPerformed

    private void tableHocSinhMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableHocSinhMousePressed
        try {
            DiemBanDau = tableHocSinh.getValueAt(tableHocSinh.getSelectedRow(), tableHocSinh.getSelectedColumn()).toString();
        } catch (Exception e) {
            DiemBanDau = "";
        }
    }//GEN-LAST:event_tableHocSinhMousePressed

    private void ThemMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemMoiActionPerformed

        themhs.setVisible(true);

    }//GEN-LAST:event_ThemMoiActionPerformed

    private void TimKiembuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TimKiembuttonActionPerformed
        String MaHs = MaHS.getText();
        String HoTenHS = HoTen.getText();
        String sql = "";
        if (MaHs.isEmpty() && HoTenHS.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui l??ng ??i???n th??ng tin ????? t??m ki???m");

        } else {
            bangDuLieu.setRowCount(0);
            tableHocSinh.setModel(bangDuLieu);
            if (!MaHs.isEmpty()) {
                sql = "SELECT * FROM HOCSINH WHERE MaHocSinh like '%" + MaHs + "%'";
            } else if (HoTenHS.isEmpty()) {
                sql = "SELECT * FROM HOCSINH WHERE HoTen like N'%" + HoTenHS + "%'";
            } else {
                sql = "SELECT * FROM HOCSINH WHERE MaHocSinh like '%" + MaHs + "%' AND HoTen like N'%" + HoTenHS + "%'";
            }

            Connection con = JDBCConnection.ketNoiJBDC();
            try {
                Statement mystm = con.createStatement();
                ResultSet rs = mystm.executeQuery(sql);
                int STT = 1;
                while (rs.next()) {

                    String HoTenHocSinh = rs.getString("HoTen");
                    String NgaySinh = rs.getString("NgaySinh");

                    String GioiTinh = rs.getString("GioiTinh");
                    String DiaChi = rs.getString("DiaChi");
                    String Email = rs.getString("Email");
                    String MaHocSinh = rs.getString("MaHocSinh");
                    String[] data = {Integer.toString(STT), MaHocSinh, HoTenHocSinh, NgaySinh, GioiTinh, DiaChi, Email};
                    bangDuLieu.addRow(data);

                    STT++;
                }
                tableHocSinh.setModel(bangDuLieu);

            } catch (SQLException ex) {
                Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_TimKiembuttonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        HocSinhTrongTruong();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void SuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuaActionPerformed
        int row = tableHocSinh.getSelectedRow();
        if (row != -1) {
            suahs.setVisible(true);
            SuaHocSinh_QuanLiHocSinh.getMaHocSinh = tableHocSinh.getValueAt(row, 1).toString();
            suahs.txtHoTenHocSinh.setText(tableHocSinh.getValueAt(row, 2).toString());
            if ("Nam".equals(tableHocSinh.getValueAt(row, 4).toString())) {
                suahs.rbNamHocSinh.setSelected(true);
            } else {
                suahs.rbNuHocSinh.setSelected(true);
            }

            if (tableHocSinh.getValueAt(row, 5) != null) {
                suahs.txtAreaDiaChiHocSinh.setText(tableHocSinh.getValueAt(row, 5).toString());
            }
            if (tableHocSinh.getValueAt(row, 6) != null) {
                suahs.txtEmailHocSinh.setText(tableHocSinh.getValueAt(row, 6).toString());
            }
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(tableHocSinh.getValueAt(row, 3).toString());
                suahs.datechooseHocSinh.setDate(date);
            } catch (ParseException ex) {
                Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Vui l??ng ch???n m???t h??ng ????? xem th??ng tin");

        }
    }//GEN-LAST:event_SuaActionPerformed

    private void ExcelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExcelButtonActionPerformed
        File excelFile;
        FileInputStream excelFIS = null;
        BufferedInputStream excelBIS = null;
        XSSFWorkbook excelJtableImport = null;

        JFileChooser excelFilechooser = new JFileChooser("null");
        excelFilechooser.setDialogTitle("Ch???n file excel c???n nh???p");
        int ExcelChooser = excelFilechooser.showOpenDialog(null);
        if (ExcelChooser == JFileChooser.APPROVE_OPTION) {

            try {
                excelFile = excelFilechooser.getSelectedFile();

                excelFIS = new FileInputStream(excelFile);
                excelBIS = new BufferedInputStream(excelFIS);
                excelJtableImport = new XSSFWorkbook(excelBIS);
                XSSFSheet excelSheet = excelJtableImport.getSheetAt(0);

                Connection con = JDBCConnection.ketNoiJBDC();
                for (int row = 0; row < excelSheet.getLastRowNum(); row++) {
                    XSSFRow excelRow = excelSheet.getRow(row + 1);
                    XSSFCell HoTen = excelRow.getCell(0);
                    XSSFCell NgaySinh = excelRow.getCell(1);
                    XSSFCell GioiTinh = excelRow.getCell(2);
                    XSSFCell DiaChi = excelRow.getCell(3);
                    XSSFCell Email = excelRow.getCell(4);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sdf.parse(NgaySinh.toString());
                    String txtNgaySinh = new SimpleDateFormat("yyyy-MM-dd").format(date);
                
                    try {
                        CallableStatement mystm = con.prepareCall("{call sp_TiepNhanHocSinh_ThemHocSinh(?,?,?,?,?)}");

                        mystm.setString(1, HoTen.toString());
                        mystm.setString(2, txtNgaySinh);
                        mystm.setString(3, GioiTinh.toString());
                        mystm.setString(4, DiaChi.toString());

                        mystm.setString(5, Email.toString());

                        mystm.executeQuery();

                    } catch (SQLException ex) {

                    }

//                    for (int column = 0; column < 5; column++) {
//                        XSSFCell excelCell = excelRow.getCell(column);
//                        System.out.println(excelCell.toString());
//                    }
                }
                JOptionPane.showMessageDialog(null, "L??u th??nh c??ng");
                HocSinhTrongTruong();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_ExcelButtonActionPerformed

    private void SampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SampleActionPerformed
        String[] colums = {"H??? t??n", "Ng??y Sinh", "Gi???i t??nh", "?????a ch???", "Email"};
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("Nh?? long", "25/03/2005", "Nam", "IaTok", ""));
        contacts.add(new Contact("H???c Sinh A", "23/03/2005", "Nu", "Iagrai", ""));
        contacts.add(new Contact("HocSinhN", "25/03/2006", "Nam", "HoChiMinh", "nguyennhulong@gmail.com"));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Th??ng tin h???c sinh c???n nh???p");
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < colums.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colums[i]);

        }
        int rownum = 1;
        for (Contact contact : contacts) {
            Row row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(contact.HoTen);
            row.createCell(1).setCellValue(contact.NgaySinh);
            row.createCell(2).setCellValue(contact.GioiTinh);
            row.createCell(3).setCellValue(contact.DiaChi);
            row.createCell(4).setCellValue(contact.Email);

        }
        try {
            String location = "";
            JFileChooser targetDir = new JFileChooser();
            targetDir.setDialogTitle("Ch???n th?? m???c c???n l??u");
            targetDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (targetDir.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                location = targetDir.getSelectedFile().toString();
                System.out.println(location + "\\contacts.xlsx");
                FileOutputStream fileOut = new FileOutputStream(location + "\\contacts.xlsx");
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "L??u th??nh c??ng, vui l??ng m??? file t???i " + location + "\\contacts.xlsx");
                fileOut.close();
                workbook.close();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QuanLyHocSinhPanel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_SampleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ExcelButton;
    private javax.swing.JTextField HoTen;
    private javax.swing.JTextField MaHS;
    private javax.swing.JButton Sample;
    private javax.swing.JButton Sua;
    public javax.swing.JButton ThemMoi;
    private javax.swing.JButton TimKiembutton;
    private javax.swing.JButton Xoa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable tableHocSinh;
    // End of variables declaration//GEN-END:variables

    private void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

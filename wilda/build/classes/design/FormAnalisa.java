/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design;

import java.awt.Component;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Analisa;
import model.CountData;
import model.InsertData;
import model.LoadTable;
import model.MinMaxData;
import model.TestConnection;
import model.Truncate;

/**
 *
 * @author rachmad
 */
public class FormAnalisa extends javax.swing.JFrame {
    private LoadTable tb1, tb2, hasilKriteriaTable;
    private TestConnection tc;
    private FormAnalisaMobil analisaMobil;
    private DefaultTableModel tm1, tm2, tm3, tm4, tm5, tmUtility, hasilKriteriaTableModel, tmSmartMobil, tmDataMobil, tmPerhitunganUtility;
    private String lowBobot, highBobot;
    private Analisa analisa;
    private Object[] columnArray;
    private MinMaxData minMaxData;
    private String min, max;
    private Statement st;
    private ResultSet rs;
    /**
     * Creates new form Analisa
     */
    public FormAnalisa() {
        analisa = new Analisa();
        tc = new TestConnection();
        
        tmDataMobil = new LoadTable(tc.getConnection(), "select id_mobil, model from mobil").getTableModel();
        columnArray = new Object[]{"nilai model", "nilai transmisi", "nilai warna", "nilai kapasitas", "nilai bahan_bakar", "nilai harga", "nilai purna_jual"};
        for(Object column : columnArray){
            tmDataMobil.addColumn(column);
        }
        
        analisaMobil = new FormAnalisaMobil();
        tm1 = analisa.getTableModel1();
        tm2 = analisa.getTableModel2();
        tm3 = analisa.getTableModel3();
        tm4 = analisaMobil.getTableModel();
        
        String queryUtility = "SELECT id_mobil, model, transmisi, warna, kapasitas, bahan_bakar, harga, purna_jual FROM utility";
        tmUtility = new LoadTable(tc.getConnection(), queryUtility).getTableModel();
        
        tmSmartMobil = new LoadTable(tc.getConnection(), "select id_mobil, model from mobil").getTableModel();
        columnArray = new Object[]{"nilai model", "nilai transmisi", "nilai warna", "nilai kapasitas", "nilai bahan_bakar", "nilai harga", "nilai purna_jual"};
        for(Object column : columnArray){
            tmSmartMobil.addColumn(column);
        }
        
//        int row = new CountData("mobil").getCountQuery();
//        int column = new CountData("kriteria").getCountQuery();
        new Truncate("TRUNCATE hasil_analisa");
        for(int i=0; i< tmUtility.getRowCount(); i++){
            List insert = new ArrayList();
            for(int j=0; j< tmDataMobil.getColumnCount(); j++){
                if(j >= 2){
                    String value = tmUtility.getValueAt(i, j-1).toString();
                    minMaxData = new MinMaxData(tc.getConnection(), "utility", tmUtility.getColumnName(j-1));
                    max = minMaxData.getMax();
                    min = minMaxData.getMin();
                    double maxVal = Double.parseDouble(max);
                    double minVal = Double.parseDouble(min);
                    double currentVal = Double.parseDouble(value);
                    double result = ((maxVal - currentVal) / (maxVal - minVal));
                    //System.out.println("(" + max + " - " + value + ") / (" + max + " - " + min + ") = " + result);
                    tmSmartMobil.setValueAt(Double.toString(result * 100) + " %", i, j);
                    insert.add(Double.toString(result * 100));
                }
                else{
                    insert.add(tmSmartMobil.getValueAt(i,j));
                }
            }
            String table = "hasil_analisa";
            Object[] column = {"id_mobil", "nama_model", "model", "transmisi", "warna", "kapasitas", "bahan_bakar", "harga", "purna_jual"};
            Object[] data = new Object[insert.size()];
            data = insert.toArray();
            new InsertData(tc.getConnection(), table, column, data);
        }
        
        /*
         * final perhitungan smart
        */
        hasilKriteriaTable = new LoadTable(tc.getConnection(), "select * from hasil_analisa");
        hasilKriteriaTableModel = hasilKriteriaTable.getTableModel();
        Object[] column = {"id_mobil", "nama_model", "hasil"};
        tm5 = new DefaultTableModel(column, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                // semua baris dan kolom false
                return false;
            }
        };
        for(int i=0; i<hasilKriteriaTableModel.getRowCount(); i++){
            int result = 0;
            List row = new ArrayList();
            for(int j=0; j<hasilKriteriaTableModel.getColumnCount(); j++){
                if(j>1){
                    double val1 = Double.parseDouble(hasilKriteriaTableModel.getValueAt(i, j).toString());
                    double val2 = Double.parseDouble(tm3.getValueAt(j-2, 4).toString());
                    result += (val1 * val2);
                }
                else{
                    String vector = hasilKriteriaTableModel.getValueAt(i, j).toString();
                    // System.out.println(i + " - " + j + " = " + vector);
                    row.add(vector);
                }
            }
            row.add(Double.toString(result));
            Object[] addRow = new Object[row.size()];
            addRow = row.toArray();
            tm5.addRow(addRow);
            //tm5.setValueAt(Double.toString(result), i, 2);
        }
        
        tmPerhitunganUtility = new DefaultTableModel(new Object[]{"Kriteria", "Batas Bawah", "Batas Tengah", "Batas Atas"}, 0);
        for(int i=1; i< tmUtility.getColumnCount(); i++){
            System.out.println(tmUtility.getColumnName(i));
            Object[] data= sortRowByColumn(tmUtility.getColumnName(i));
            int count = 0;
            if(data.length % 3 == 0)
                count = data.length;
            else if(data.length % 3 == 1)
                count = data.length + 1;
            else if(data.length % 3 == 2)
                count = data.length -1;
            
            int q1 = count / 3;
            int q2 = (count * 2) / 3;
            String lowestData = data[0].toString();
            String higestData = data[data.length-1].toString();
            String dataQ1 = data[q1].toString();
            String dataQ2 = data[q2].toString();
            if(data.length >= 6){
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    data[0] + " - " + data[q1],
                    data[q1+1] + " - " + data[q2],
                    data[q2+1] + " - " + data[data.length-1]
                });
            }
            else if(data.length == 1){ 
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    "",
                    "",
                    data[data.length-1]
                });
            }
            else if(data.length == 2){ 
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    data[0],
                    "",
                    data[data.length-1]
                });
            }
            else if(data.length == 3){ 
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    data[0],
                    data[(data.length + 1) / 2],
                    data[data.length-1]
                });
            }
            else if(data.length == 4){ 
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    data[0] + " - " + data[1],
                    "",
                    data[data.length-2] + " - " + data[data.length-1]
                });
            }
            else if(data.length == 5){ 
                tmPerhitunganUtility.addRow(new Object[]{
                    tmUtility.getColumnName(i),
                    data[0] + " - " + data[1],
                    data[(data.length + 1) / 2],
                    data[data.length-2] + " - " + data[data.length-1]
                });
            }
        }
        
        
        initComponents();
        
        jTable1.setDefaultRenderer(Object.class, new LastRowBold());
        jTable2.setDefaultRenderer(Object.class, new LastRowBold());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(tm1);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("Bobot Paling Tidak Penting", jScrollPane1);

        jTable2.setModel(tm2);
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(30);
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Bobot Paling Penting", jScrollPane2);

        jTable3.setModel(tm3);
        jTable3.getColumnModel().getColumn(0).setPreferredWidth(10);
        jScrollPane3.setViewportView(jTable3);

        jTabbedPane1.addTab("Rata-Rata Bobot", jScrollPane3);

        jTable5.setModel(tmUtility);
        jScrollPane5.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Data Utility", jPanel1);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.GridLayout());
        jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jTable7.setModel(tmPerhitunganUtility);
        jScrollPane7.setViewportView(jTable7);

        jPanel3.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Table Pengembangan Utility", jPanel3);

        jTable6.setModel(tmSmartMobil
        );
        jTable6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable6MousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Smart Data Mobil", jPanel2);

        jTable4.setModel(tm5);
        jScrollPane4.setViewportView(jTable4);

        jTabbedPane1.addTab("Perhitungan Utility", jScrollPane4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Object[] sortRowByColumn(String columnName){
        Object[] rowArray;
        String data="";
        try {
            //DefaultTableModel tmTable = new LoadTable(tc.getConnection(), "select "+columnName+" from utility order by "+columnName+" asc").getTableModel();
            st = tc.getConnection().createStatement();
            rs = st.executeQuery("select "+columnName+" from utility order by "+columnName+" asc");
            List<String> row = new ArrayList<String>();
            while(rs.next()){
                if(!rs.getString(1).equals(data)){
                    row.add(rs.getString(1));
                    data = rs.getString(1);
                }
            }
            rowArray = row.toArray();
            return rowArray;
        } catch (SQLException ex) {
            Logger.getLogger(FormAnalisa.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void jTable6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable6MousePressed
        JTable table =(JTable) evt.getSource();
        java.awt.Point p = evt.getPoint();
        int selectedRowIndex = jTable6.getSelectedRow();
        //String selectedObject = jTable4.getModel().getValueAt(selectedRowIndex, 2)+"";
        List<Object> columnData = new ArrayList<Object>();
        List<Object> columnName = new ArrayList<Object>();
        for(int i=0; i<jTable5.getModel().getColumnCount(); i++){
            columnData.add(jTable5.getModel().getValueAt(selectedRowIndex, i));
            columnName.add(tmUtility.getColumnName(i));
        }
        if (evt.getClickCount() == 2)
        { 
            String getModelName = tmSmartMobil.getValueAt(selectedRowIndex, 1).toString();
            new RumusAnalisa(getModelName, columnData, columnName).setVisible(true);
        }
    }//GEN-LAST:event_jTable6MousePressed

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
            java.util.logging.Logger.getLogger(FormAnalisa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormAnalisa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormAnalisa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormAnalisa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormAnalisa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    // End of variables declaration//GEN-END:variables
}

class LastRowBold extends DefaultTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, 
   Object value, boolean isSelected, boolean hasFocus, int row, int column) {
     JLabel parent = (JLabel) super.getTableCellRendererComponent(table, 
      value, isSelected, hasFocus, row, column);
     if(row == table.getRowCount()-1) parent.setFont(
       parent.getFont().deriveFont(Font.BOLD));
     return parent;
   }    
}
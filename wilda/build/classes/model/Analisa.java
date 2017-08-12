/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rachmad
 */
public class Analisa {
    private LoadTable tb1, tb2;
    private TestConnection tc;
    private DefaultTableModel tm1, tm2, tm3;
    private String lowBobot, highBobot;
    private JTable jTable1, jTable2, jTable3;
    
    public Analisa(){
        String query1 = "SELECT id_kriteria, kriteria, low_bobot FROM kriteria";
        String query2 = "SELECT id_kriteria, kriteria, high_bobot FROM kriteria";
        lowBobot = new CountData("kriteria", "low_bobot").getCount();
        highBobot = new CountData("kriteria", "high_bobot").getCount();
        
        tc = new TestConnection();
        tb1 = new LoadTable(tc.getConnection(), query1);
        tb2 = new LoadTable(tc.getConnection(), query2);
        
        tm1 = tb1.getTableModel();
        tm1.addColumn("Bobot Normalisasi");
        double resultLowBobot = 0;
        for(int i=0; i<tm1.getRowCount(); i++)
        {
            double normalisasi = Double.parseDouble(tm1.getValueAt(i, 2).toString()) / Double.parseDouble(lowBobot);
            resultLowBobot += normalisasi;
            tm1.setValueAt(Double.toString(normalisasi), i, 3);
        }
        tm1.addRow(new Object[]{"","Jumlah", lowBobot, Double.toString(resultLowBobot)});
        tm2 = tb2.getTableModel();
        tm2.addColumn("Bobot Normalisasi");
        double resultHighBobot = 0;
        for(int i=0; i<tm2.getRowCount(); i++)
        {
            double normalisasi = Double.parseDouble(tm2.getValueAt(i, 2).toString()) / Double.parseDouble(highBobot);
            resultHighBobot += normalisasi;
            tm2.setValueAt(Double.toString(normalisasi), i, 3);
        }
        tm2.addRow(new Object[]{"","Jumlah", highBobot, Double.toString(resultHighBobot)});
        
        tm3 = new DefaultTableModel(new String[]{"No","Kriteria","Bobot Relatif 1", "Bobot Relatif 2", "Bobot Rata-Rata"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                // semua baris dan kolom false
                return false;
            }
        };
        for(int i=0; i<tm2.getRowCount()-1; i++)
        {
            String getKriteria = tm1.getValueAt(i, 1).toString();
            String getLowBobot = tm1.getValueAt(i, 3).toString();
            String getHighBobot = tm2.getValueAt(i, 3).toString();
            double average = (Double.parseDouble(getLowBobot) + Double.parseDouble(getHighBobot)) / 2;
            tm3.addRow(new Object[]{i+1, getKriteria, getLowBobot, getHighBobot, average});
        }
        
        jTable1 = new JTable(tm1);
        jTable2 = new JTable(tm2);
        jTable3 = new JTable(tm3);
    }
    
    public JTable getTable1(){
        return jTable1;
    }
    public JTable getTable2(){
        return jTable2;
    }
    public JTable getTable3(){
        return jTable3;
    }
    public DefaultTableModel getTableModel1(){
        return tm1;
    }
    public DefaultTableModel getTableModel2(){
        return tm2;
    }
    public DefaultTableModel getTableModel3(){
        return tm3;
    }
}

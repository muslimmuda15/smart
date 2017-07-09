/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rachmad
 */
public class AnalisaUtility {
    private LoadTable tb;
    private TestConnection tc;
    private DefaultTableModel tm;
    private JTable jTable1;
    public AnalisaUtility(){
        String query = "SELECT id_mobil, model, transmisi, warna, kapasitas, bahan_bakar, harga, purna_jual FROM mobil";
        tc = new TestConnection();
        tb = new LoadTable(tc.getConnection(), query);
        tm = tb.getTableModel();
        for(int i=0; i<tm.getColumnCount(); i++){
            for(int j=0; j<tm.getRowCount(); j++){
                
            }
        }
    }
}

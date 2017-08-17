/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class TableColumnName {
    TestConnection tc;
    String[] columnList;
    private int count;
    public TableColumnName(String table){
        try {
            tc = new TestConnection();
            String query = "select model, transmisi, warna, kapasitas, bahan_bakar, harga, purna_jual from " + table + ";";
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            columnList = new String[columnsNumber];
            count = columnList.length;
            for(int i=0;i<columnList.length; i++){
                columnList[i] = rsmd.getColumnName(i+1);
            }  
        } catch (SQLException ex) {
            Logger.getLogger(TableColumnName.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String[] getColumnName(){
        return columnList;
    }
    
    public int getCountColumn(){
        return count;
    }
}

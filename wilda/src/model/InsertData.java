/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author RACHMAD
 */
public class InsertData 
{
    Statement st;
    ResultSet rs;
    
    LoadTable tb;
    TestConnection tc;
    TableModel tm;
    public InsertData(Connection conn, String table, Object[] column, Object[] data, JTable jTable1)
    {
        try {
            st = conn.createStatement();
            String sql = "insert into " + table + "("; 
            for(int i = 0; i<column.length; i++){
                sql += "`" + column[i] + "`";
                if(i != column.length - 1){
                    sql += ",";
                }
            }
            sql += ") values (";
            for(int i = 0; i<data.length; i++){
                sql += "'" + data[i] + "'";
                if(i != data.length - 1){
                    sql += ",";
                }
            }
            sql += ")";
            st.executeUpdate(sql);
            st.close();
            
            String query = "SELECT " + Arrays.toString(column).replaceAll("^\\[|\\]$", "") + " FROM " + table;
            tc = new TestConnection();
            tb = new LoadTable(tc.getConnection(), query);
            tm = tb.getTableModel();
            
            jTable1.setModel(tm);
        } catch (SQLException ex) {
            Logger.getLogger(InsertData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public InsertData(Connection conn, String table, Object[] column, Object[] data){
        try {
            st = conn.createStatement();
            String sql = "insert into " + table + "("; 
            for(int i = 0; i<column.length; i++){
                sql += "`" + column[i] + "`";
                if(i != column.length - 1){
                    sql += ",";
                }
            }
            sql += ") values (";
            for(int i = 0; i<data.length; i++){
                sql += "'" + data[i] + "'";
                if(i != data.length - 1){
                    sql += ",";
                }
            }
            sql += ")";
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(InsertData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

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
 * @author rachmad
 */
public class UpdateData {
    Statement st;
    ResultSet rs;
    
    LoadTable tb;
    TestConnection tc;
    TableModel tm;
    public UpdateData(Connection conn, String table, Object[] column, Object[] data, JTable jTable1)
    {
        try {
            st = conn.createStatement();
            String sql = "update " + table + " set "; 
            for(int i = 0; i<column.length; i++){
                sql += "`" + column[i] + "` = '" + data[i] + "'";
                if(i != column.length - 1){
                    sql += ",";
                }
            }
            sql += " where `" + column[0] + "` = '" + data[0] + "'";
            System.out.println(sql);
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
}

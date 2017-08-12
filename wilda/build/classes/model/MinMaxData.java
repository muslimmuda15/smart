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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class MinMaxData {
    private Statement st;
    private ResultSet rs;
    boolean success = false;
    String table, column;
    public MinMaxData(Connection conn, String table, String column)
    {
        try {
            this.table = table;
            this.column = column;
            st = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(MinMaxData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getMax(){
        String query = "select max(" + column + ") from " + table;
        try {
            
            rs = st.executeQuery(query);
            if(rs.next())
            {
                return Integer.toString(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FindData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public String getMin(){
        String query = "select min(" + column + ") from " + table;
        try {
            
            rs = st.executeQuery(query);
            if(rs.next())
            {
                return Integer.toString(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FindData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}

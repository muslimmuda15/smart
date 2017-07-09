/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class Truncate {
    private TestConnection tc;
    Statement st;
    public Truncate(String query){
        try {
            tc = new TestConnection();
            st =  tc.getConnection().createStatement();
            st.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

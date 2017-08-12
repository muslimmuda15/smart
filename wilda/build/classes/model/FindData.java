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
 * @author RACHMAD
 */
public class FindData 
{
    private Statement st;
    private ResultSet rs;
    boolean success = false;
    public FindData(Connection conn, String query)
    {
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            
            if(rs.next())
            {
                success=true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FindData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isSuccess()
    {
        return success;
    }
}

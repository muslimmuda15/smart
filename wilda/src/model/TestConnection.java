/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 *
 * @author RACHMAD
 */
public class TestConnection {
    private String result;
    
    private Connection conn;
    private Statement st;
    private ResultSet rs;
    
    boolean success = false;
    public TestConnection()
    {
        /*
         * connecting database
        */
        try {
            String url = "jdbc:mysql://localhost:3306/smart";
            String username = "root";
            String password = "";
            
            System.out.println("Connecting database...");
            
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection(url,username,password);
            result = "Connection Successfull";
            success = true;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TestConnection.class.getName()).log(Level.SEVERE, null, ex);
            result = ex.getMessage();
            success = false;
        }
    }
    public String getResult()
    {
        return result;
    }
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public Connection getConnection()
    {
        return conn;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class ListData {
    TestConnection tc;
    List<String> columns=new ArrayList<>();
    List<String[]> multiColumns=new ArrayList<>();
    public ListData(String table, String column){
        String query = "Select " + column + " from " + table + ";";
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {      
              columns.add(rs.getString(column));                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ListData(String table, String column1, String column2){
        String query = "Select " + column1 + ", " + column2 + " from " + table + ";";
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {   
              String [] data = new String[]{
                  rs.getString(column1),
                  rs.getString(column2)
              };
              //System.out.println(data);
              multiColumns.add(data);                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ListData(String table, String column1, String column2, int budget){
        String query = "Select " + column1 + ", " + column2 + " from " + table + " where purna_jual <= " + Integer.toString(budget) + ";";
        System.out.println(query);
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {   
              String [] data = new String[]{
                  rs.getString(column1),
                  rs.getString(column2)
              };
              //System.out.println(data);
              multiColumns.add(data);                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ListData(Object query, String column){
//        String query = "Select " + column + " from " + table + ";";
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query.toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {      
              columns.add(rs.getString(column));                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object[] getData(){
        return columns.toArray();
    }
    
    public Object[] getMultiColumnData(){
        return multiColumns.toArray();
    }
}

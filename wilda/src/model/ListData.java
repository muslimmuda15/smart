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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class ListData {
    private TestConnection tc;
    private List<String> columns=new ArrayList<>();
    private List<String[]> multiColumns=new ArrayList<>();
    private List<String> columnData;
    private List<String[]> rowData;
    public ListData(){
        
    }
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
    
    public ListData(String table, Object[] column, int budget){
        String query;
        if(budget == -1)
            query = "Select " + Arrays.toString(column).replaceAll("^\\[|\\]$", "") + " from " + table + ";";
        else
            query = "Select " + Arrays.toString(column).replaceAll("^\\[|\\]$", "") + " from " + table + " where harga <= " + Integer.toString(budget) + ";";
        System.out.println(query);
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {   
//              String [] data = new String[]{
//                  rs.getString(column1),
//                  rs.getString(column2)
//              };
              String[] data = new String[column.length];
              for(int i=0; i<column.length; i++){
                  data[i] = rs.getString(i+1);
              }
              //System.out.println(data);
              multiColumns.add(data);                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ListData(String table, String column1, String column2, int budget){
        String query = "Select " + column1 + ", " + column2 + " from " + table + " where harga <= " + Integer.toString(budget) + ";";
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
    
    public void setColumnByContent(String table, String column, String[] selectedColumn, Object[] content){
        String col = "";
        for(int i=0; i<content.length; i++){
            if(i > 0)
                col += " or ";
            col += column + " like '" + content[i] + "'";
        }
        String query = "Select * from " + table + " where " + col + ";";
        System.out.println("Column By Content : " + query);
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) { 
                String[] data = new String[selectedColumn.length];
                for(int i=0; i<selectedColumn.length; i++){
                    data[i] = rs.getString(selectedColumn[i]);
                }
                multiColumns.add(data);                                 
           }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setColumnByContent(String query){
        System.out.println("Column By Content : " + query);
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            columnData = new ArrayList<>();
            rowData = new ArrayList<>();
            
            for(int i=0; i<metaData.getColumnCount(); i++){
                columnData.add(metaData.getColumnName(i));
            }
            
            int i=0;
            while(rs.next()){
                String[] getData = new String[metaData.getColumnCount()];
                for(int j=0; j<metaData.getColumnCount(); j++){
                    getData[j] = rs.getObject(j).toString();
                }
                rowData.add(getData);
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setColumnByContent(String table, Object[] selectByColumn , String column, String[] selectedColumn, Object[] content){
        String col = "";
        for(int i=0; i<content.length; i++){
            if(i > 0)
                col += " or ";
            col += column + " like '" + content[i] + "'";
        }
        String query = "Select " + Arrays.toString(selectByColumn).replaceAll("^\\[|\\]$","") + " from " + table + " where " + col + ";";
        System.out.println("Column By Content : " + query);
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) { 
                String[] data = new String[selectedColumn.length];
                for(int i=0; i<selectedColumn.length; i++){
                    data[i] = rs.getString(selectedColumn[i]);
                }
                multiColumns.add(data);                                 
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
    
    public String getSingleData(int row, int column){
        Object[] data = multiColumns.toArray();
        System.out.println("Row : " + row);
        for(int i=0;i<data.length; i++)
            System.out.println("Get Row Data : " + Arrays.toString((String[]) data[i]));
        String[] getRowData = (String[]) data[row];
        String getValue = getRowData[column];
        return getValue;
    }
    
    public Object[] getColumnName(){
        return columnData.toArray();
    }
    
    public Object[] getRowData(){
        return rowData.toArray();
    }
}

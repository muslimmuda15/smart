/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rachmad
 */
public class CountData {
    TestConnection tc;
    private String getCount;
    public CountData(String table, String column){
        String query = "SELECT SUM(" + column + ") FROM " + table + ";";
        try {
            tc = new TestConnection();
            PreparedStatement statement =  tc.getConnection().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            String sum = result.getString(1);
            this.getCount = sum;
        } catch (SQLException ex) {
            Logger.getLogger(CountData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String getCount(){
        return getCount;
    }       
}

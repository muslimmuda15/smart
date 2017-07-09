/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author RACHMAD
 */
public class LoadTable {
    
    private Statement st;
    private ResultSet rs;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public LoadTable(Connection conn, String query)
    {
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            tableModel = buildTableModel(rs);
            
            table = new JTable(tableModel);
            table.setDefaultEditor(Object.class, null);

            try
            {
                rs.last();
            }
            catch(SQLException | NumberFormatException ex){
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoadTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException 
    {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                // semua baris dan kolom false
                return false;
            }
        };
    }
    
    public JTable getTable()
    {
        return table;
    }
    
    public DefaultTableModel getTableModel()
    {
        return tableModel;
    }
    
    public void searchTable(String query)
    {
        try {
            rs = st.executeQuery(query);
            tableModel = buildTableModel(rs);
            
            table = new JTable(tableModel);
            table.setDefaultEditor(Object.class, null);

            try
            {
                rs.first();
            }
            catch(SQLException | NumberFormatException ex){
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoadTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

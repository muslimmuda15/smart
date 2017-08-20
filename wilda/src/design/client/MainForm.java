/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design.client;

import design.DataMobil;
import design.InsertUpdate;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import model.ListData;
import model.LoadTable;
import model.MinMaxData;
import model.TableColumnName;
import model.TestConnection;

/**
 *
 * @author rachmad
 */
public class MainForm extends javax.swing.JFrame {

    /**
     * Creates new form MainForm
     */
    private MinMaxData minMaxData;
    private DefaultTableModel tm, currtableModel, smartTableModel, alternatifPerhitunganTableModel, hasilKeputusanTableModel;
    private TestConnection tc;
    private List<JComboBox<String>> combo = new ArrayList<>();
    private List<JTextField> txtListId = new ArrayList<>();
    private List<JLabel> lblCombo = new ArrayList<>();
    private List<JCheckBox> check = new ArrayList<>();
    private List<JTextField> txtBobotPalingPenting = new ArrayList<>();
    private List<JTextField> txtBobotTidakPenting = new ArrayList<>();
    private String[] kriteriaList;
    private Vector originalTableModel;
    private String max;
    private String min;
    private String budget = "";
    private TableColumnName tcn;
    private JLabel labelKriteriaTidakPenting = new JLabel("Kriteria Paling Tidak Penting"),
                   labelKriteriaPalingPenting = new JLabel("Kriteria Paling Penting"),
                   labelBobotPalingPenting = new JLabel("Bobot"),
                   labelBobotTidakPenting = new JLabel("Bobot");
    List<String> column, originalColumn, fixColumn;
    int jumlahBobotPalingPenting;
    int jumlahBobotTidakPenting;
    double[] normalisasiPalingPenting;
    double[] normalisasiTidakPenting;
    double[] bobotRataRata;
    
    public MainForm() {
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        getRootPane().setWindowDecorationStyle(javax.swing.JRootPane.NONE);
        setLocation(0,0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        labelKriteriaTidakPenting.setHorizontalAlignment(JLabel.LEFT);
        labelKriteriaPalingPenting.setHorizontalAlignment(JLabel.LEFT);
        labelBobotPalingPenting.setHorizontalAlignment(JLabel.CENTER);
        labelBobotTidakPenting.setHorizontalAlignment(JLabel.CENTER);
        
        column = new ArrayList<>();
        originalColumn = new ArrayList<>();
        fixColumn = new ArrayList<>();
        
        tc = new TestConnection();
        tm = new LoadTable(tc.getConnection(), "select * from mobil").getTableModel();
        smartTableModel = new DefaultTableModel();
        alternatifPerhitunganTableModel = new DefaultTableModel();
        
        hasilKeputusanTableModel = new DefaultTableModel(new Object[]{"Alternatif", "Nilai"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                // semua baris dan kolom false
                return false;
            }
        };
        
        setSize((int)screenSize.getWidth()-50,(int)screenSize.getHeight()-50);
        
        /* preparation for column */
        tcn = new TableColumnName("mobil");
        kriteriaList = tcn.getColumnName();
        
        /* Check Box for Kriteria */
        for(int i=0; i<kriteriaList.length; i++){
            String kriteriaName = kriteriaList[i];
            JCheckBox checkBox = new JCheckBox(Character.toUpperCase(kriteriaName.charAt(0)) + kriteriaName.substring(1));
            checkBox.setSelected(true);
            
            check.add(checkBox);
            
            int a = i;
            checkBox.addItemListener(new ItemListener(){
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(checkBox.isSelected()){
                        kriteriaList[a] = kriteriaName;
                        System.out.println("Add : " + kriteriaName);
                    }
                    else{
                        kriteriaList[a] = "";
                        System.out.println("Remove : " + kriteriaName);
                    }
//                    System.out.print("List : ");
//                    for(String test : kriteriaList){
//                        System.out.print(test + ", ");
//                    }
                }
            });
        }
        
        /* combo list fot alternatif */
        Object[] dataModelMultiColumn = new ListData("mobil", "id_mobil", "model").getMultiColumnData();
        for(int i=0; i<8; i++){
            JLabel lblComboBox = new JLabel("Alternatif " + (i+1) + "   ");
            
            /* set multi value inside combobox */
            Vector model = new Vector();
            for(int j=i; j<dataModelMultiColumn.length; j++){
                //System.out.println(Arrays.toString((String[])dataModelMultiColumn[j]));
                String[] dataMobil = (String[])dataModelMultiColumn[j];
                model.addElement(new Item(dataMobil[0], dataMobil[1]));
            }
            
            JComboBox comboBox = new JComboBox(model);
            JTextField textField = new JTextField();
            
            Item item = (Item)comboBox.getSelectedItem();
            textField.setText(item.getId());
//            comboBox.setRenderer( new ItemRenderer() );
            
            lblCombo.add(lblComboBox);
            combo.add(comboBox);
            txtListId.add(textField);
            
            comboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    JComboBox comboBox = (JComboBox)evt.getSource();
                    Item item = (Item)comboBox.getSelectedItem();
                    //System.out.println( item.getId() + " : " + item.getDescription() );
                    textField.setText(item.getId());
                }
            });
        }
        
        initComponents();
        
        minMaxData = new MinMaxData(tc.getConnection(), "mobil", "purna_jual");
        max = minMaxData.getMax();
        
        originalTableModel = (Vector) ((DefaultTableModel) jTable2.getModel()).getDataVector().clone();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtModel = new javax.swing.JTextField();
        txtTransmisi = new javax.swing.JTextField();
        txtWarna = new javax.swing.JTextField();
        txtKapasitas = new javax.swing.JTextField();
        txtBahanBakar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        txtPurnaJual = new javax.swing.JTextField();
        txtId = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        panelKriteria = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtBudget = new javax.swing.JTextField();
        jComboBox9 = new javax.swing.JComboBox<String>();
        jButton4 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        panelLabelLaternatif = new javax.swing.JPanel();
        panelAlternatif = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        panelKriteriaTidakPenting = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        panelBobotTidakPenting = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        panelKriteriaPalingPenting = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        panelBobotPalingPenting = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(tm);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Baru");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Edit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Delete");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(170, 167, 167)));
        jPanel5.setToolTipText("");

        jLabel1.setText("Model");

        jLabel2.setText("Transmisi");

        jLabel3.setText("Warna");

        jLabel4.setText("Kapasitas");

        jLabel5.setText("Bahan Bakar");

        txtModel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtModelKeyTyped(evt);
            }
        });

        jLabel6.setText("Harga");

        jLabel7.setText("Purna Jual");

        txtId.setVisible(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel4))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtModel)
                            .addComponent(txtTransmisi)
                            .addComponent(txtWarna)
                            .addComponent(txtKapasitas, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBahanBakar)
                            .addComponent(txtHarga)
                            .addComponent(txtPurnaJual, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBahanBakar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTransmisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtWarna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtPurnaJual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtKapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1246, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Mobil", jPanel1);

        jLabel8.setText("Tentukan kriteria");

        panelKriteria.setLayout(new java.awt.GridLayout(8, 0));
        for(int i=0; i<check.size(); i++){
            panelKriteria.add(check.get(i));
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(panelKriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1077, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelKriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(427, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Kriteria", jPanel2);

        jLabel23.setText("Budget");

        jLabel24.setText("Jumlah yang di pilih");

        txtBudget.setDocument(new JTextFieldLimit(10));
        txtBudget.setText("0");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
        jComboBox9.setSelectedIndex(7);
        jComboBox9.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox9ItemStateChanged(evt);
            }
        });

        jButton4.setText("OK");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jPanel8.setLayout(new java.awt.BorderLayout());

        panelLabelLaternatif.setLayout(new java.awt.GridLayout(8, 0));
        for(int i=0; i<8; i++){
            panelLabelLaternatif.add(lblCombo.get(i));
        }
        jPanel8.add(panelLabelLaternatif, java.awt.BorderLayout.LINE_START);

        panelAlternatif.setLayout(new java.awt.GridLayout(8, 0));

        for(int i=0; i<8; i++){
            panelAlternatif.add(combo.get(i));
        }

        jPanel8.add(panelAlternatif, java.awt.BorderLayout.CENTER);

        jTable2.setModel(tm);
        jScrollPane2.setViewportView(jTable2);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jPanel11.setLayout(new java.awt.GridLayout(8, 0));
        for(int i=0; i<8; i++){
            jPanel11.add(txtListId.get(i));
        }

        jPanel11.setVisible(true);

        jLabel13.setText("Search   ");

        jButton5.setText("Hitung Bobot Kriteria");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(101, 101, 101)
                                .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel24)
                                .addGap(25, 25, 25)
                                .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 393, Short.MAX_VALUE)
                                .addComponent(jLabel13))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Alternatif", jPanel6);

        jTable3.setModel(smartTableModel);
        jScrollPane3.setViewportView(jTable3);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        panelKriteriaTidakPenting.setLayout(new java.awt.GridLayout(8, 0));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Kriteria Paling Tidak Penting");
        panelKriteriaTidakPenting.add(jLabel9);

        panelBobotTidakPenting.setLayout(new java.awt.GridLayout(8, 0));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Bobot");
        panelBobotTidakPenting.add(jLabel10);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelKriteriaTidakPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelBobotTidakPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBobotTidakPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelKriteriaTidakPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        panelKriteriaPalingPenting.setLayout(new java.awt.GridLayout(8, 0));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Kriteria Paling Penting");
        panelKriteriaPalingPenting.add(jLabel11);

        panelBobotPalingPenting.setLayout(new java.awt.GridLayout(8, 0));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Bobot");
        panelBobotPalingPenting.add(jLabel12);

        jButton6.setText("Hitung Bobot Alternatif");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(panelKriteriaPalingPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelBobotPalingPenting, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelKriteriaPalingPenting, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(panelBobotPalingPenting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(490, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Check Bobot", jPanel9);

        jTable4.setModel(alternatifPerhitunganTableModel);
        jScrollPane4.setViewportView(jTable4);

        jButton7.setText("Hitung Hasil Keputusan");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1221, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Pembobotan Alternati", jPanel12);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pembobotan Kriteria", jPanel3);

        jTable5.setModel(hasilKeputusanTableModel);
        jScrollPane5.setViewportView(jTable5);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1246, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(400, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Hasil Keputusan", jPanel4);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        JTable table =(JTable) evt.getSource();
        java.awt.Point p = evt.getPoint();
        int selectedRowIndex = jTable1.getSelectedRow();
        String selectedObject = jTable1.getModel().getValueAt(selectedRowIndex, 2)+"";

        try{
            ArrayList<Object> dataValue = new ArrayList<Object>();
            int column = 0;
            int row = jTable1.getSelectedRow();
            txtId.setText(jTable1.getModel().getValueAt(row, 0).toString());
            txtModel.setText(jTable1.getModel().getValueAt(row, 1).toString());
            txtTransmisi.setText(jTable1.getModel().getValueAt(row, 2).toString());
            txtWarna.setText(jTable1.getModel().getValueAt(row, 3).toString());
            txtKapasitas.setText(jTable1.getModel().getValueAt(row, 4).toString());
            txtBahanBakar.setText(jTable1.getModel().getValueAt(row, 5).toString());
            txtHarga.setText(jTable1.getModel().getValueAt(row, 6).toString());
            txtPurnaJual.setText(jTable1.getModel().getValueAt(row, 7).toString());
        }
        catch(Exception e){

        }
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new InsertUpdate(jTable1, 0).setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try{
            ArrayList<Object> dataValue = new ArrayList<Object>();
            int column = 0;
            int row = jTable1.getSelectedRow();
            for(int i = 0; i<jTable1.getColumnCount(); i++){
                dataValue.add(jTable1.getModel().getValueAt(row, i).toString());
            }
            
            Object[] data = new Object[dataValue.size()];
//            String value = jTable1.getModel().getValueAt(row, column).toString();
//            JOptionPane.showMessageDialog(null, dataValue);
            new InsertUpdate(jTable1, 1, dataValue.toArray(data)).setVisible(true);
        }
        catch(Exception ex){
            
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try{
            String getValue = jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0).toString();
            if(JOptionPane.showConfirmDialog(null, "Are you sure to delete data?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
                try {
                    Statement st = tc.getConnection().createStatement();
                    String sql = "DELETE FROM `mobil` WHERE id_mobil = '" + getValue + "'";
                    System.out.println(sql);
                    st.executeUpdate(sql);
                    st.close();

                    String query = "SELECT id_mobil, model, transmisi, warna, kapasitas, bahan_bakar, harga, purna_jual FROM mobil";
                    tc = new TestConnection();
                    tm = new LoadTable(tc.getConnection(), query).getTableModel();

                    jTable1.setModel(tm);
                } catch (SQLException ex) {
                    Logger.getLogger(DataMobil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch(Exception ex){
            
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox9ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            for(int i=0; i<combo.size(); i++){
                int selected = Integer.parseInt(jComboBox9.getSelectedItem().toString());
                if(i < selected)
                    combo.get(i).setEnabled(true);
                else
                    combo.get(i).setEnabled(false);
            }
//            System.out.println(jComboBox9.getItemAt(jComboBox9.getSelectedIndex()) + " : " + jComboBox9.getSelectedItem());
        }
    }//GEN-LAST:event_jComboBox9ItemStateChanged

    private void txtModelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtModelKeyTyped
        
    }//GEN-LAST:event_txtModelKeyTyped

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try{
            budget = txtBudget.getText();
            int budgetInt = Integer.parseInt(txtBudget.getText());
            /* filter table */
            currtableModel = (DefaultTableModel) jTable2.getModel();
            //To empty the table before search
            currtableModel.setRowCount(0);
            //To search for contents from original table content
            for (Object rows : originalTableModel) {
                Vector rowVector = (Vector) rows;
                Object purnaJualColumn = rowVector.get(1);
                System.out.println(purnaJualColumn);
                if(!txtBudget.getText().equals("")){
                    if ((Integer.parseInt(purnaJualColumn.toString()) <= Integer.parseInt(txtBudget.getText()))) {
                        //content found so adding to table
                        currtableModel.addRow(rowVector);
                    }
                }
                else{
                    if ((Integer.parseInt(purnaJualColumn.toString()) <= Integer.parseInt(max))) {
                        currtableModel.addRow(rowVector);
                    }
                }
            }
            
            Object[] dataModelMultiColumn = new ListData("mobil", "id_mobil", "model", budgetInt).getMultiColumnData();
            System.out.println("Data Mobil : " + dataModelMultiColumn.length);
            
            /* Add element */
            if(dataModelMultiColumn.length < 8){
                DefaultComboBoxModel countItemDisable = new DefaultComboBoxModel();
                if(dataModelMultiColumn.length == 0)
                {    
                    countItemDisable.addElement(0);
                    jComboBox9.setModel(countItemDisable);
                    jComboBox9.setSelectedIndex(0);
                }
                else{
                    for(int k=0; k< dataModelMultiColumn.length; k++){
                        countItemDisable.addElement( k+1 );
                    }
                    jComboBox9.setModel(countItemDisable);
                    jComboBox9.setSelectedIndex(dataModelMultiColumn.length - 1);
                }
            }
            else{
                DefaultComboBoxModel countItemDisable = new DefaultComboBoxModel();
                for(int k=0; k< 8; k++){
                    countItemDisable.addElement( k+1 );
                }
                jComboBox9.setModel(countItemDisable);
                jComboBox9.setSelectedIndex(7);
            }
            
            /* set enable disable */
            for(int i=0; i<combo.size(); i++){
                try{
                    //combo.get(i).removeAllItems();
                    txtListId.get(i).setText("");
                    combo.get(i).setEnabled(true);
                    
                    Vector model = new Vector();
                    if(dataModelMultiColumn.length > 0){
                        /* set multi value inside combobox */
                        if(dataModelMultiColumn.length > 8){
                            for(int j=i; j<dataModelMultiColumn.length; j++){
                                //System.out.println(Arrays.toString((String[])dataModelMultiColumn[j]));
                                String[] dataMobil = (String[])dataModelMultiColumn[j];
                                model.addElement(new Item(dataMobil[0], dataMobil[1]));
                            }
                        }
                        else{
                            for(int j=i; j<8; j++){
                                //System.out.println(Arrays.toString((String[])dataModelMultiColumn[j]));
                                try{
                                    String[] dataMobil = (String[])dataModelMultiColumn[j];
                                    model.addElement(new Item(dataMobil[0], dataMobil[1]));
                                }
                                catch(ArrayIndexOutOfBoundsException e){
                                    /* if out of index item */
                                }
                            }
                        }
            //            comboBox.setRenderer( new ItemRenderer() );
                    }
                    else{
                        model.addElement(new Item("", "--Empty--"));
                    }

                    combo.get(i).setModel(new DefaultComboBoxModel(model));
                    //combo.get(i) = new JComboBox(model);
                    Item item = (Item)combo.get(i).getSelectedItem();
                    txtListId.get(i).setText(item.getId());
                }
                catch(NullPointerException ee){
                    /* if combo out of bound */
                    combo.get(i).setEnabled(false);
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Some error detected from your input, please fix your input and try again");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        currtableModel = (DefaultTableModel) jTable2.getModel();
        //To empty the table before search
        currtableModel.setRowCount(0);
        //To search for contents from original table content
        for (Object rows : originalTableModel) {
            Vector rowVector = (Vector) rows;
            Object purnaJualColumn = rowVector.get(7);
            for (Object column : rowVector) {
                if(Pattern.compile("^\\s+$").matcher(budget).find() || budget.equals("")){
                    if (column.toString().toLowerCase().contains(txtSearch.getText())) {
                        //content found so adding to table
                        currtableModel.addRow(rowVector);
                        break;
                    }
                }
                else{
                    if (column.toString().toLowerCase().contains(txtSearch.getText()) && (Integer.parseInt(purnaJualColumn.toString()) <= Integer.parseInt(budget))) {
                        //content found so adding to table
                        currtableModel.addRow(rowVector);
                        break;
                    }
                }
            }
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try{
            processSmart();
            jTabbedPane1.setSelectedIndex(3);
            jTabbedPane2.setSelectedIndex(0);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Some error detected from your input, please fix your input and try again");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        //List smartColumn = originalColumn;
        List smartColumn = new ArrayList<>();
        for(String data : originalColumn){
            smartColumn.add(data);
        }
        
        /* INITIAL FOR TABLE PROCESS SMART */
        try{
            smartColumn.add(0, "utility.id_mobil");
            smartColumn.add(1, "mobil.model");

            for(int i=2; i<smartColumn.size(); i++){
                System.out.print((i-1) + " : ");
                smartColumn.set(i, "utility." + smartColumn.get(i));
                System.out.println(smartColumn.get(i));
            }

            String selectedWhere = "";
            for(int i=0; i<Integer.parseInt(jComboBox9.getSelectedItem().toString()); i++){
                if(i > 0)
                    selectedWhere += " or ";
                selectedWhere += "utility.id_mobil like '" + txtListId.get(i).getText() + "'";
            }

            String query = "Select " + Arrays.toString(smartColumn.toArray()).replaceAll("^\\[|\\]$","") + " from utility\n" +
                            "LEFT JOIN mobil ON utility.id_mobil = mobil.id_mobil where " + selectedWhere + ";";
            System.out.println("Table Filter : " + query);

            alternatifPerhitunganTableModel = new LoadTable(tc.getConnection(), query).getTableModel();
            
            /* SMARTING TABLE */
            for(int i=0; i< alternatifPerhitunganTableModel.getRowCount(); i++){
                for(int j=2; j< alternatifPerhitunganTableModel.getColumnCount(); j++){
                    String value = alternatifPerhitunganTableModel.getValueAt(i, j).toString();
                    minMaxData = new MinMaxData(tc.getConnection(), "utility", alternatifPerhitunganTableModel.getColumnName(j));
                    max = minMaxData.getMax();
                    min = minMaxData.getMin();
                    double maxVal = Double.parseDouble(max);
                    double minVal = Double.parseDouble(min);
                    double currentVal = Double.parseDouble(value);
                    double result = ((maxVal - currentVal) / (maxVal - minVal));
                    //System.out.println("(" + max + " - " + value + ") / (" + max + " - " + min + ") = " + result);
                    alternatifPerhitunganTableModel.setValueAt(round((result * 100), 2) + " %", i, j);
                }
            }
            
            jTable4.setModel(alternatifPerhitunganTableModel);
            
            jTabbedPane2.setSelectedIndex(1);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Some error detected from your input, please fix your input and try again");
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        normalisasiPalingPenting = new double[txtBobotPalingPenting.size()];
        normalisasiTidakPenting = new double[txtBobotTidakPenting.size()];
        bobotRataRata = new double[txtBobotTidakPenting.size()];
        String[] hasilAkhir = new String[alternatifPerhitunganTableModel.getRowCount()];
        
        hasilKeputusanTableModel.setRowCount(0);
        
        for(int i=0; i<txtBobotPalingPenting.size(); i++){
            double hitungNormalisasiTidakPenting = Double.parseDouble(txtBobotTidakPenting.get(i).getText()) / (double)jumlahBobotTidakPenting;
            normalisasiTidakPenting[i] = hitungNormalisasiTidakPenting;
            
            System.out.println(fixColumn.get(i).toString());
            
            double hitungNormalisasiPalingPenting = Double.parseDouble(txtBobotPalingPenting.get(i).getText()) / (double)jumlahBobotPalingPenting;
            normalisasiPalingPenting[i] = hitungNormalisasiPalingPenting;
            
            double rataRataBobot = (hitungNormalisasiTidakPenting + hitungNormalisasiPalingPenting) / 2;
            bobotRataRata[i] = rataRataBobot;
            
            System.out.println(hitungNormalisasiTidakPenting + " - " + hitungNormalisasiPalingPenting + " = " + rataRataBobot);
        }
        
        
        
        for(int i=0; i<alternatifPerhitunganTableModel.getRowCount(); i++){
            double result = 0;
            for(int j=2; j<alternatifPerhitunganTableModel.getColumnCount(); j++){
                result += (Double.parseDouble(alternatifPerhitunganTableModel.getValueAt(i, j).toString().replaceAll(" %", "")) * bobotRataRata[j-2]);
            }
            hasilAkhir[i] = Double.toString(result);
            hasilKeputusanTableModel.addRow(new Object[]{alternatifPerhitunganTableModel.getValueAt(i, 1), hasilAkhir[i]});
            jTable5.setModel(hasilKeputusanTableModel);
        }
        
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JPanel panelAlternatif;
    private javax.swing.JPanel panelBobotPalingPenting;
    private javax.swing.JPanel panelBobotTidakPenting;
    private javax.swing.JPanel panelKriteria;
    private javax.swing.JPanel panelKriteriaPalingPenting;
    private javax.swing.JPanel panelKriteriaTidakPenting;
    private javax.swing.JPanel panelLabelLaternatif;
    private javax.swing.JTextField txtBahanBakar;
    private javax.swing.JTextField txtBudget;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtKapasitas;
    private javax.swing.JTextField txtModel;
    private javax.swing.JTextField txtPurnaJual;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTransmisi;
    private javax.swing.JTextField txtWarna;
    // End of variables declaration//GEN-END:variables

    private void processSmart() {
        int countSelected = Integer.parseInt(jComboBox9.getSelectedItem().toString());
        column.clear();
        fixColumn.clear();
        originalColumn.clear();
        jumlahBobotPalingPenting = 0;
        jumlahBobotTidakPenting = 0;
        
        column.add("id_mobil");
        column.add("model");
        fixColumn.add("id_mobil");
        fixColumn.add("model");
        
        txtBobotTidakPenting.clear();
        txtBobotPalingPenting.clear();
        
        /* INITIALIZE NEW COLUMN FOR SELECTED KRITERIA */
        for(int j=0; j<kriteriaList.length; j++){
            if(!kriteriaList[j].equals("")){
                if(!kriteriaList[j].equals("model"))
                    column.add(kriteriaList[j]);
                fixColumn.add(kriteriaList[j]);
                originalColumn.add(kriteriaList[j]);
            }
        }
        Object[] columns = column.toArray();
        Object[] fixColumns = fixColumn.toArray();
        smartTableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                // semua baris dan kolom false
                return false;
            }
        };
        
        int budgetInt = 0;
        try{
            budgetInt = Integer.parseInt(txtBudget.getText());
        }
        catch(Exception ex){
            budgetInt = 0;
        }
        for(int i=0;i<Integer.parseInt(jComboBox9.getSelectedItem().toString()); i++){
            ListData listData = new ListData();
            listData.setColumnByContent("mobil", "id_mobil", Arrays.copyOf(columns, columns.length, String[].class), new Object[]{txtListId.get(i).getText()});
            Object[] dataSmartLevel1 = listData.getMultiColumnData();
//            Object[] dataSmartLevel1 = new ListData("mobil", columns, budgetInt).getMultiColumnData();
            System.out.println("Data : ");
            for(int j=0; j<dataSmartLevel1.length; j++){
                String[] dataMobil = (String[])dataSmartLevel1[j];
                System.out.println(Arrays.toString(dataMobil));

                smartTableModel.addRow(dataMobil);
            }
        }
        jTable3.setModel(smartTableModel);
        
        /* CONFIGURE LAYOUT ON PEMBOBOTAN KRITERIA */
        
        // Empty Krieria
        // Add Label for description
        panelKriteriaTidakPenting.removeAll();
            panelKriteriaTidakPenting.add(labelKriteriaTidakPenting);
        panelKriteriaPalingPenting.removeAll();
            panelKriteriaPalingPenting.add(labelKriteriaPalingPenting);
        panelBobotPalingPenting.removeAll();
            panelBobotPalingPenting.add(labelBobotPalingPenting);
        panelBobotTidakPenting.removeAll();
            panelBobotTidakPenting.add(labelBobotTidakPenting);
            
        System.out.println("Fix Column : " + Arrays.toString(fixColumns));
        
        for(int i=2; i<fixColumns.length; i++){
            /* Set Data Bobot (Table Kriteria) */
            ListData bobot = new ListData();
            bobot.setColumnByContent("kriteria", 
                                     "kriteria",
                                     new String[]{"kriteria", "low_bobot", "high_bobot"}, 
                                     new Object[]{fixColumns[i].toString()});
            
            /* Add label to Panel */
            panelKriteriaTidakPenting.add(new JLabel(fixColumns[i].toString()));
            panelKriteriaPalingPenting.add(new JLabel(fixColumns[i].toString()));
            
            /* Initial of textfield for bobot */
            JTextField bobotTidakPenting = new JTextField(bobot.getSingleData(0, 1));
            jumlahBobotTidakPenting += Integer.parseInt(bobot.getSingleData(0, 1));
            txtBobotTidakPenting.add(bobotTidakPenting);
            
            JTextField bobotPalingPenting = new JTextField(bobot.getSingleData(0, 2));
            jumlahBobotPalingPenting += Integer.parseInt(bobot.getSingleData(0, 2));
            txtBobotPalingPenting.add(bobotPalingPenting);
            
            /* Add text field list array to panel */
            panelBobotPalingPenting.add(txtBobotPalingPenting.get(i-2));
            panelBobotTidakPenting.add(txtBobotTidakPenting.get(i-2));
        }
        
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

class JTextFieldLimit extends PlainDocument {
    private int limit;

    JTextFieldLimit(int limit) {
     super();
     this.limit = limit;
     }

    public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit) {
          super.insertString(offset, str, attr);
        }
    }
}

class Item
{
    private String id;
    private String description;

    public Item(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public String toString()
    {
        return description;
    }
}

class ItemRenderer extends BasicComboBoxRenderer
{
    public Component getListCellRendererComponent(
        JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus){
        super.getListCellRendererComponent(list, value, index,
            isSelected, cellHasFocus);

        if (value != null)
        {
            Item item = (Item)value;
            setText( item.getDescription().toUpperCase() );
        }

        if (index == -1)
        {
            Item item = (Item)value;
            setText( "" + item.getId() );
        }


        return this;
    }
}

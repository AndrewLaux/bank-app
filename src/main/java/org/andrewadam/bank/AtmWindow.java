
package org.andrewadam.bank;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//--Class--////////////////////////////////////////////////////////////
//An interface GUI for customers to look up and make transactions on 
//their bank accounts.
public class AtmWindow extends JFrame implements ActionListener {
    
    //Database obj:
    private Data db;
    
    //Private Data:
    private String tax_id;
    private ArrayList<String[]> accounts;
    private String chosen_account;
    private String primary_of_chosen;
    
    //GUI elements:
    private javax.swing.JPanel actionsPane;
    private javax.swing.JLabel address_hdr;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel loadedactionsPane;
    private javax.swing.JPanel overviewPane;
    private javax.swing.JTextArea overviewText;
    private javax.swing.JLabel tax_hdr;
    private javax.swing.JTable transactionTable;
    private javax.swing.JPanel transactionsPane;
    private javax.swing.JLabel user_hdr;
    
    //--Constructor with Args-----------------------------------------
    //Constructs the GUI given a tax_id associated with a customer.
    public AtmWindow(String tax_id){
        
        //Initialize base data for ATM window:
        db = new Data();
        this.tax_id = tax_id; 
        initComponents();
        setVisible(true);
        
        //Register listeners for GUI elements.
        jComboBox2.addActionListener(this);
    }
    
    //--Method---------------------------------------------------------
    //Contains code for responding to GUI actions.
    public void actionPerformed(ActionEvent obj) {
        
        //Capture source:
        Object source = obj.getSource();
        
        //Account Selector Combo box:
        if( source == jComboBox2 && jComboBox2.getSelectedIndex() != 0) {
            chosen_account = accounts.get(jComboBox2.getSelectedIndex() - 1)[0];
            String over_text = "";
            
            //Attempt to fill out overview pane:
            try{
                ArrayList<String> overview = getAccountInfo();
                over_text += ("Balance: $" + overview.get(1));
                over_text +=       ("\n\n");
                over_text +=       ("Account id#:  " + overview.get(0));
                over_text +=       ("\n");
                over_text +=       ("Owner ssn#:   " + overview.get(2));
                over_text +=       ("\n");
                over_text +=       ("Branch:       " + overview.get(3));
                over_text +=       ("\n\n");
                over_text +=       ("Type: " + overview.get(4));
                over_text +=       ("\n\n");
                primary_of_chosen = overview.get(2);
                
                
            }catch (Exception e) {System.out.println("Failed to get overview. \n"+ e.getMessage());}
            
            //Attempt to fill out owners:
            try {
                over_text += "Owners \n";
                over_text += "---------\n";
                over_text += customersFromAccount(chosen_account);
            }catch (Exception e) {System.out.println("Failed to get owners. \n"+ e.getMessage());}
            
            //Post text to text area:
            overviewText.setText(over_text);
        }
        
        
        
    }
    
    //--Method----------------------------------------------------------
    //Get customer info given some tax_id
    private ArrayList<String> getCustomerInfo() throws Exception {
        String qry = "SELECT name, address FROM customers WHERE tax_id='"+ tax_id +"'";
        ResultSet rs = db.requestData(qry);
        ArrayList<String> info = new ArrayList<String>();
        while(rs.next()){
            info.add(rs.getNString("name").trim());
            info.add(rs.getString("address").trim());
        }
        rs.close();
        return info;
    }
    
    //--Method------------------------------------------------------------
    //Get ArrayList of each of customer's accounts.
    private ArrayList<String[]> getAccounts() throws Exception {
        String qry = "SELECT O.account_id, T.name, A.balance ";
        qry = qry + "FROM Owns O, type T, Account A ";
        qry = qry + "WHERE O.tax_id='" + tax_id + "' AND T.account_id = O.account_id AND A.account_id = O.account_id";
        ResultSet rs = db.requestData(qry);
        ArrayList<String[]> info = new ArrayList<String[]>();
        while(rs.next()){
            String[] row = new String[3];
            row[0] = rs.getString("account_id").trim();
            row[1] = rs.getString("name").trim();
            row[2] = rs.getString("balance").trim();
            info.add(row);
            
        }
        rs.close();
        return info;
    }
    
    //--Method----------------------------------------------------------
    //Get customer info given some tax_id
    private ArrayList<String> getAccountInfo() throws Exception {
        String qry = "SELECT A.account_id, A.balance, A.primary_owner, A.bank_branch, A.linked_account, T.name";
        qry = qry + " FROM Account A, type T";
        qry = qry + " WHERE A.account_id='" + chosen_account +"' AND T.account_id=A.account_id";
        ResultSet rs = db.requestData(qry);
        ArrayList<String> info = new ArrayList<String>();
        while(rs.next()){
            info.add(rs.getNString("account_id").trim());
            info.add(rs.getString("balance").trim());
            info.add(rs.getString("primary_owner").trim());
            info.add(rs.getString("bank_branch").trim());
            info.add(rs.getString("name").trim());
            try{info.add(rs.getString("linked_account").trim());}catch(Exception e){}
        }
        rs.close();
        return info;
    }
    
    //--Method-----------------------------------------------------------
    //Get account owners.
    public String customersFromAccount(String accountId) throws Exception {

        String customers = "";
        System.out.println("in: accountID= " + accountId);
        ArrayList<String> idList = new ArrayList<String>();
        // Getting to and from transactions
        String qry = "select c.name,c.tax_id from customers c, owns o where c.tax_id=o.tax_id and o.account_id='"
                + accountId + "'";
        System.out.println(qry);
        ResultSet rs = db.requestData(qry);
        // adding customer data
        while (rs.next()) {
            customers = customers + rs.getString("tax_id") + "  " + rs.getString("name") + "\n";
        }
        return customers;

    }
    
    //--Method----------------------------------------------------------
    //Contains code for initializing GUI elements. Gets called by constr.
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tax_hdr = new javax.swing.JLabel();
        user_hdr = new javax.swing.JLabel();
        address_hdr = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        overviewPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        overviewText = new javax.swing.JTextArea();
        actionsPane = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox<>();
        loadedactionsPane = new javax.swing.JPanel();
        transactionsPane = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ATM");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Info", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Address:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Tax ID:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Name:");

        tax_hdr.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tax_hdr.setText(tax_id);

        user_hdr.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        user_hdr.setText("default name");

        address_hdr.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        address_hdr.setText("default address, ST zip");
        
        //Get customer info;
        try {
            ArrayList<String> info = getCustomerInfo();
            user_hdr.setText(info.get(0));
            address_hdr.setText(info.get(1));
        }catch (Exception e) { System.out.println("Couldn't retrieve customer info.");}

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(address_hdr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(user_hdr, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tax_hdr)))
                .addGap(34, 34, 34))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tax_hdr, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_hdr, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(address_hdr, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Accounts"));
        
        jComboBox2.setFont(new java.awt.Font("Tahoma", 0, 15));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        
        //Try to get accounts:
        try{
            accounts = getAccounts();
            String[] account_options = new String[accounts.size()+1];
            account_options[0] = "---SELECT AN ACCOUNT----";
            for(int i = 0; i < accounts.size(); i++) {
                account_options[i+1]=(" -- #: " + accounts.get(i)[0] + " -- type: " + accounts.get(i)[1] + " ----------------- $" + accounts.get(i)[2]);
            }
            jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(account_options));
        } catch (Exception e) { System.out.println("Couldn't get accounts for customer." + e.getMessage());}
        

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        //jTabbedPane2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        //jTabbedPane2.setOpaque(true);

        overviewPane.setName(""); // NOI18N

        jScrollPane2.setOpaque(false);

        overviewText.setFont(new java.awt.Font("Monospaced", 0, 18));
        overviewText.setColumns(20);
        overviewText.setRows(5);
        jScrollPane2.setViewportView(overviewText);

        actionsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Actions", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout loadedactionsPaneLayout = new javax.swing.GroupLayout(loadedactionsPane);
        loadedactionsPane.setLayout(loadedactionsPaneLayout);
        loadedactionsPaneLayout.setHorizontalGroup(
            loadedactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        loadedactionsPaneLayout.setVerticalGroup(
            loadedactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout actionsPaneLayout = new javax.swing.GroupLayout(actionsPane);
        actionsPane.setLayout(actionsPaneLayout);
        actionsPaneLayout.setHorizontalGroup(
            actionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(actionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadedactionsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(actionsPaneLayout.createSequentialGroup()
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 206, Short.MAX_VALUE)))
                .addContainerGap())
        );
        actionsPaneLayout.setVerticalGroup(
            actionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loadedactionsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout overviewPaneLayout = new javax.swing.GroupLayout(overviewPane);
        overviewPane.setLayout(overviewPaneLayout);
        overviewPaneLayout.setHorizontalGroup(
            overviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(actionsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        overviewPaneLayout.setVerticalGroup(
            overviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overviewPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actionsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("OVERVIEW", overviewPane);

        transactionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(transactionTable);

        javax.swing.GroupLayout transactionsPaneLayout = new javax.swing.GroupLayout(transactionsPane);
        transactionsPane.setLayout(transactionsPaneLayout);
        transactionsPaneLayout.setHorizontalGroup(
            transactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transactionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                .addContainerGap())
        );
        transactionsPaneLayout.setVerticalGroup(
            transactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("TRANSACTIONS", transactionsPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        jTabbedPane2.getAccessibleContext().setAccessibleName("");

        pack();
    }
    
    
    
    
}

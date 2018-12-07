
package org.andrewadam.bank;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;

//--Class--////////////////////////////////////////////////////////////
//An interface GUI for customers to look up and make transactions on 
//their bank accounts.
public class AtmWindow extends JFrame implements ActionListener {
    
    //Database obj:
    private Data db;
    
    //Private Data:
    private String tax_id;
    private ArrayList<String[]> accounts;
    private ArrayList<String[]> transactions;
    private ArrayList<String> overview;
    private String chosen_account;
    private String primary_of_chosen;
    private String chosen_type;
    private double current_rate;
    private boolean chosen_closed;
    
    //GUI elements:
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPanel PinPanel;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JButton Refresh;
    private CardLayout cl;
    
    //Formatter
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    
    //--Constructor with Args-----------------------------------------
    //Constructs the GUI given a tax_id associated with a customer.
    public AtmWindow(String tax_id) throws Exception{
        
        //Initialize base data for ATM window:
        db = new Data();
        this.tax_id = tax_id; 
        initComponents();
        setVisible(true);
        
        cl = (CardLayout) loadedactionsPane.getLayout();
        cl.show(loadedactionsPane, "card4");
        
        //Register listeners for GUI elements.
        jComboBox2.addActionListener(this);
        Refresh.addActionListener(this);
        jButton1.addActionListener(this);
        jComboBox3.addActionListener(this);
        card1_amount.addActionListener(this);
        card2_amount.addActionListener(this);
        confirm1.addActionListener(this);
        confrim2.addActionListener(this);
        
    }
    
    //--Method---------------------------------------------------------
    //Contains code for responding to GUI actions.
    public void actionPerformed(ActionEvent obj) {
        
        //Capture source:
        Object source = obj.getSource();
        
    //--//Account Selector Combo box:
        if( source == jComboBox2 && jComboBox2.getSelectedIndex() != 0) {
            updateChosen();
            
        }
        
    //--//Transaction tab clicked
        if(source == Refresh && !chosen_account.isEmpty()){
            try{ updateTransactions(); }catch(Exception e){System.out.println(e.getMessage());}
        }
        
    //--//Update pin button pushed
        if(source == jButton1) {
            
            //Get inputs:
            char[] old = jPasswordField1.getPassword();
            char[] newer = jPasswordField2.getPassword();
            
            if((String.valueOf(newer)).length() != 4){
                String message = "PIN must 4 numbers";
                JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
            }
            
            else {
                try {
                    Integer.parseInt(String.valueOf(newer));
                    //Verify id and pin:
                    if (verifyPin(tax_id.toCharArray(), old)) {

                        //Reset fields:
                        jPasswordField1.setText("");
                        jPasswordField2.setText("");

                        //Update with new hash.
                        try {
                            String new_hash = Hmac.hash(tax_id, String.valueOf(newer));
                            String qry = "UPDATE customers SET hash='" + new_hash +"' WHERE tax_id='"+tax_id+"'";
                            db.requestData(qry);
                        } catch (Exception e) {
                            System.out.println(e.getMessage() + "/n Couldn't update Pin");
                        }

                    } //Else notify incorrect:
                    else {
                        String message = "Incorrect old Pin!";
                        JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {
                    String message = "PIN must 4 numbers";
                    JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        
    //--//Select action to perform on account 
        if(source == jComboBox3 && jComboBox3.getSelectedIndex() != 0){
            
            //Switch to appropriate card:
            String x = (String) jComboBox3.getSelectedItem();
            if(x.equals("deposit") || x.equals("withdrawl") ||x.equals("purchase") ||x.equals("collect") ||x.equals("top-up"))
                cl.show(loadedactionsPane, "card5");
            else
                cl.show(loadedactionsPane, "card6");
            
            //Set appropriate fees.
            if(x.equals("wire")){
                card2_rate.setText("2% Fee:");
                current_rate = .02;
                card2_fee.setText("$0.00");      
            } else if(x.equals("collect")){
                card1_rate.setText("3% Fee:");
                current_rate = .03;
                card1_fee.setText("$0.00"); 
            } else {
                current_rate = 0;
                card2_rate.setText("Free of Charge");
                card2_fee.setText("");
                card1_rate.setText("Free of Charge");
                card1_fee.setText(""); 
            }
            
            //Modify for write check
            if(x.equals("write check")){
                jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Check #", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
            }
            else jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "To", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
            
        }
        
    //--//Update fee according to amount
        if(source == card1_amount) {
            if(current_rate != 0) {
                try{
                    double entered = Double.parseDouble(card1_amount.getText());
                    card1_fee.setText(formatter.format(entered * current_rate));
                }catch(Exception e){card1_fee.setText("???");}
            }
        }
        
    //--//Update fee according to amount
        if(source == card2_amount) {
            if(current_rate != 0) {
                try{
                    double entered = Double.parseDouble(card2_amount.getText());
                    card2_fee.setText(formatter.format(entered * current_rate));
                }catch(Exception e){card2_fee.setText("???");}
            }
        }
        
    //--//Deposit Logic
        if(source == confirm1 && jComboBox3.getSelectedItem().equals("deposit")){
            try{ //to make deposit:
                double deposit = Double.parseDouble(card1_amount.getText());
                if(deposit < 0.01) throw new IllegalArgumentException("Must be a positive amount.");
                String qry = "UPDATE Account SET balance=balance+"+String.valueOf(deposit)+" ";
                qry += "WHERE account_id='"+chosen_account+"'";
                db.requestData(qry);
                System.out.println("Successful deposit");
                 
                //Record transaction:
                Integer next = nextTransactionId();
                qry = "INSERT INTO transactions (ID, transaction_date, amount) ";
                String curDate = App.app_date.getMonth() + 1 + "/" + App.app_date.getDate() + "/" + (App.app_date.getYear() + 1900);
                qry += "VALUES ("+next+", '"+curDate+"', "+String.valueOf(deposit)+")";
                System.out.println(qry);
                db.requestData(qry);
                
                //Record makes
                qry = "INSERT INTO makes (account_id, ID, tax_id) ";
                qry += "VALUES ('"+chosen_account+"', "+next+", '"+tax_id+"')";
                System.out.println(qry);
                db.requestData(qry);
                
                //Record has type;
                qry = "INSERT INTO has_t_type (ID, type_name) ";
                qry += "VALUES ("+next+", 'deposit')";
                System.out.println(qry);
                db.requestData(qry);
                
                card1_amount.setText("");
                
                //Todo update account and overview:
                updateChosen();
           
                
            }catch(Exception e){ System.out.println(e.getMessage() + "Failed to make deposit or record it.");}
        }
        
    //--//Withdrawl logic
        if(source == confirm1 && jComboBox3.getSelectedItem().equals("withdrawl")){
            try{
                double withdrawl = Double.parseDouble(card1_amount.getText());
                if(withdrawl < 0.01) throw new IllegalArgumentException("Must be a positive amount.");
                if(withdrawl >= Double.parseDouble(overview.get(1))) throw new IllegalArgumentException("Exceeds withdrawl limit.");
                boolean mark_for_closure = false;
                if(withdrawl == Double.parseDouble(overview.get(1)) -1) mark_for_closure = true;
                
                //Make transaction:
                String qry = "UPDATE Account SET balance=balance-"+String.valueOf(withdrawl)+" ";
                qry += "WHERE account_id='"+chosen_account+"'";
                db.requestData(qry);
                System.out.println("Successful withdrawl");

                //Record transaction:
                Integer next = nextTransactionId();
                qry = "INSERT INTO transactions (ID, transaction_date, amount) ";
                String curDate = App.app_date.getMonth() + 1 + "/" + App.app_date.getDate() + "/" + (App.app_date.getYear() + 1900);
                qry += "VALUES ("+next+", '"+curDate+"', "+String.valueOf(withdrawl)+")";
                System.out.println(qry);
                db.requestData(qry);
                
                //Make makes record:
                qry = "INSERT INTO makes (account_id, ID, tax_id) ";
                qry += "VALUES ('"+chosen_account+"', "+next+", '"+tax_id+"')";
                System.out.println(qry);
                db.requestData(qry);
                
                //Record type:
                qry = "INSERT INTO has_t_type (ID, type_name) ";
                qry += "VALUES ("+next+", 'withdrawal')";
                System.out.println(qry);
                db.requestData(qry);
                
                //posible closure:
                if(mark_for_closure) {
                    qry = "UPDATE Account SET status=0 ";
                    qry += "WHERE account_id='" + chosen_account + "'";
                    db.requestData(qry);
                }
                
                //Todo update account and overview:
                updateChosen();
                
            }catch(Exception e){ System.out.println(e.getMessage() + "Failed to make withdrawl");}
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
        String qry = "SELECT A.account_id, A.balance, A.primary_owner, A.bank_branch, A.linked_account, T.name, A.status";
        qry = qry + " FROM Account A, type T";
        qry = qry + " WHERE A.account_id='" + chosen_account +"' AND T.account_id=A.account_id";
        ResultSet rs = db.requestData(qry);
        ArrayList<String> info = new ArrayList<String>();
        while(rs.next()){
            info.add(rs.getString("account_id").trim());
            info.add(rs.getString("balance").trim());
            info.add(rs.getString("primary_owner").trim());
            info.add(rs.getString("bank_branch").trim());
            info.add(rs.getString("name").trim());
            try{info.add(rs.getString("linked_account").trim());}catch(Throwable t){info.add("n/a");}
            info.add(rs.getString("status").trim());
         
        }
        rs.close();
        return info;
    }
    
    //--Method-----------------------------------------------------------
    //Get account owners.
    public String customersFromAccount(String accountId) throws Exception {

        String customers = "";
        ArrayList<String> idList = new ArrayList<String>();
        // Getting to and from transactions
        String qry = "select c.name,c.tax_id from customers c, owns o where c.tax_id=o.tax_id and o.account_id='"
                + accountId + "'";
        ResultSet rs = db.requestData(qry);
        // adding customer data
        while (rs.next()) {
            customers = customers + rs.getString("tax_id") + "  " + rs.getString("name") + "\n";
        }
        return customers;

    }
    
    //--Method---------------------------------------------------------------
    //Gets list of transactions.
    public ArrayList<String[]> transactionsFromAccount(String account) throws Exception {
        String qry = "";
        qry += "SELECT T.id, T.transaction_date, M.tax_id, H.type_name, T.amount, T.check_number ";
        qry += "FROM makes M, transactions T, has_t_type H ";
        qry += "WHERE T.ID=M.ID AND M.account_id='" + account + "' AND H.ID=T.ID";
        ResultSet rs = db.requestData(qry);
        ArrayList<String[]> info = new ArrayList<String[]>();
        while(rs.next()){
            String[] row = new String[6];
            row[0] = rs.getString("id").trim();
            row[1] = rs.getString("transaction_date").trim();
            try{row[2] = rs.getString("tax_id").trim();}catch(Throwable t){row[2] = "";}
            row[3] = rs.getString("type_name").trim();
            row[4] = "$" + rs.getString("amount").trim();
            try{row[5] = rs.getString("check_number").trim();}catch(Throwable t){row[5]="";}
            info.add(row);
        }
        rs.close();
        return info;
    }
    
    //--Method------------------------------------------------------------------
    //Gets the primary owner of account
    public String primaryFromAccount(String account) throws Exception {
        String qry ="";
        qry += "SELECT primary_owner FROM Account WHERE account_id='" + account +"'";
        ResultSet re = db.requestData(qry);
        re.next();
        return re.getString("primary_owner").trim();
    }
    
    
    //--Method----------------------------------------------------------------
    //Updates transcations table.
    private void updateTransactions() throws Exception {
        transactions = transactionsFromAccount(chosen_account);
        String[] titles = new String [] {"Transaction #", "Date", "Customer", "Type", "Amount", "Check #"};
        String [][] table = new String [transactions.size()][6];
        for(int i = 0; i < transactions.size(); i++) {
            for(int k = 0; k < 6; k++){
                table[i][k] = transactions.get(i)[k];
            }
        }       
        transactionTable.setModel(new javax.swing.table.DefaultTableModel(table , titles));
       
    }
    
    //--Method----------------------------------------------------------------
    //Update actions options available from action combobox according to account type.
    private void  updateActionOptions() throws Exception {
        ArrayList<String> options = new ArrayList<String>();
        options.add("--Select--");
        if(chosen_type.equals("student checking") || chosen_type.equals("interest checking") ||chosen_type.equals("savings")) {
            options.add("deposit");
            options.add("withdrawl");
            if(accounts.size()>1)options.add("transfer");
            options.add("wire");   
        }
        else if(chosen_type.equals("pocket")) {
            options.add("purchase");
            options.add("pay-friend");
            boolean owner_of_linked = false;
            for(String[] owns: accounts) if(overview.get(5).equals(owns[0])){
                options.add("collect");
                if(tax_id.equals(primaryFromAccount(overview.get(5)))){
                    options.add("top-up");
                }
            }
        }
        if(chosen_type.equals("student checking") || chosen_type.equals("interest checking")){
            options.add("write check");
        }
        String[] options_list = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            options_list[i] = options.get(i);
        }
       String[] closed_list = new String[]{"CLOSED ACCOUNT"};
       if(chosen_closed){
           jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(closed_list));
           return;
       }
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(options_list));
    }
    
    //--Method----------------------------------------------------------
    //Contains code for initializing GUI elements. Gets called by constr.
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
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
        transactionsPane = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        Refresh = new javax.swing.JButton();
        overviewPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        overviewText = new javax.swing.JTextArea();
        actionsPane = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox<>();
        loadedactionsPane = new javax.swing.JPanel();
        card2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        card2_amount = new javax.swing.JTextField();
        card2_rate = new javax.swing.JLabel();
        card2_fee = new javax.swing.JLabel();
        confrim2 = new javax.swing.JButton();
        card1 = new javax.swing.JPanel();
        card1_amount = new javax.swing.JTextField();
        card1_rate = new javax.swing.JLabel();
        card1_fee = new javax.swing.JLabel();
        confirm1 = new javax.swing.JButton();
        card0 = new javax.swing.JPanel();
        PinPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();
        jPasswordField2 = new javax.swing.JPasswordField();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

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
                account_options[i+1]=(" -- #: " + accounts.get(i)[0] + " -- type: " + accounts.get(i)[1]);
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

        loadedactionsPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        loadedactionsPane.setLayout(new java.awt.CardLayout());

        jTextField1.setText("");
        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "To", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        jTextField1.setOpaque(false);

        card2_amount.setText("");
        card2_amount.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Amount", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        card2_amount.setOpaque(false);

        card2_rate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        card2_rate.setText("Fee: ");

        card2_fee.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        card2_fee.setText("");

        confrim2.setText("Confirm");
     

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(card2Layout.createSequentialGroup()
                        .addComponent(card2_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(confrim2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(card2Layout.createSequentialGroup()
                        .addComponent(card2_fee, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 60, Short.MAX_VALUE))
                    .addComponent(card2_rate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(card2_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2_rate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(confrim2)
                    .addComponent(card2_fee))
                .addContainerGap(264, Short.MAX_VALUE))
        );

        loadedactionsPane.add(card2, "card6");

        card1_amount.setText("");
        card1_amount.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Amount", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        card1_amount.setOpaque(false);

        card1_rate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        card1_rate.setText("Free of Charge");

        card1_fee.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        card1_fee.setText("");

        confirm1.setText("Confirm");
   
        javax.swing.GroupLayout card1Layout = new javax.swing.GroupLayout(card1);
        card1.setLayout(card1Layout);
        card1Layout.setHorizontalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(card1_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirm1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card1_rate, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card1_fee, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(card1_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card1_rate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(confirm1)
                    .addComponent(card1_fee))
                .addContainerGap(259, Short.MAX_VALUE))
        );

        loadedactionsPane.add(card1, "card5");

        javax.swing.GroupLayout card0Layout = new javax.swing.GroupLayout(card0);
        card0.setLayout(card0Layout);
        card0Layout.setHorizontalGroup(
            card0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        card0Layout.setVerticalGroup(
            card0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 343, Short.MAX_VALUE)
        );

        loadedactionsPane.add(card0, "card4");

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

        Refresh.setText("Refresh");
        Refresh.setBorder(null);

        javax.swing.GroupLayout transactionsPaneLayout = new javax.swing.GroupLayout(transactionsPane);
        transactionsPane.setLayout(transactionsPaneLayout);
        transactionsPaneLayout.setHorizontalGroup(
            transactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
                    .addGroup(transactionsPaneLayout.createSequentialGroup()
                        .addComponent(Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        transactionsPaneLayout.setVerticalGroup(
            transactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Refresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("TRANSACTIONS", transactionsPane);

        PinPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Set Pin", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jButton1.setText("Update");

        jPasswordField1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Old", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        jPasswordField1.setOpaque(false);

        jPasswordField2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        jPasswordField2.setOpaque(false);

        javax.swing.GroupLayout PinPanelLayout = new javax.swing.GroupLayout(PinPanel);
        PinPanel.setLayout(PinPanelLayout);
        PinPanelLayout.setHorizontalGroup(
            PinPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PinPanelLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(PinPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(PinPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        PinPanelLayout.setVerticalGroup(
            PinPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PinPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

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
                        .addGap(18, 18, 18)
                        .addComponent(PinPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PinPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        jTabbedPane2.getAccessibleContext().setAccessibleName("");

        pack();
    }
    
    //--Method------------------------------------
    //This method updates overview for selected account.
    private void updateChosen() {
        chosen_account = accounts.get(jComboBox2.getSelectedIndex() - 1)[0];
            String over_text = "";
            
            //Set card to card0:
            cl.show(loadedactionsPane, "card4");
            
            //Attempt to fill out overview pane:
            try{
                overview = getAccountInfo();
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
                over_text +=       ("Linked: " + overview.get(5));
                over_text +=       ("\n\n");
                primary_of_chosen = overview.get(2);
                chosen_type = overview.get(4);
                if(overview.get(6).equals("0")) chosen_closed = true;
                else chosen_closed = false;
                updateActionOptions();
                
                
            }catch (Exception e) {System.out.println("Failed to get overview. \n"+ e.getMessage());}
            
            //Attempt to fill out owners:
            try {
                over_text += "Owners \n";
                over_text += "---------\n";
                over_text += customersFromAccount(chosen_account);
            }catch (Exception e) {System.out.println("Failed to get owners. \n"+ e.getMessage());}
            
            over_text += ("**********************CLOSED");
            over_text += ("\n\n");
            //Post text to text area:
            overviewText.setText(over_text);
    }
    
    //--Method-----------------------------
    //Verifies given PIN given some Id
    private boolean verifyPin(char[] id, char[] pin) {

        //Try to get hash from db
        try {
            
            //Get PIN/Id hash from db. Remove whitespace from result:
            String qry = "SELECT hash FROM customers WHERE tax_id=" + String.valueOf(id);
            ResultSet r = db.requestData(qry);
            r.next();
            String found = r.getString("hash").replaceAll(" ","");

            //TODO: compute expected hash.
            String expected = Hmac.hash(String.valueOf(id), String.valueOf(pin));
            
            //Compare
            return (found.equals(expected));


        }//Could not make query to server.
        catch (Exception e) {
            
            System.out.println(e.getMessage() + "\nWasn't able to check PIN");
            return false;
        }

    }
    
    //--Method--------------------------------------------------------------------
    //Gets next available transaction id.
    public Integer nextTransactionId() throws Exception {
        // Getting next transaction ID to use
        String qry = "select max(id) from transactions";
        ResultSet rs = db.requestData(qry);
        rs.next();
        Integer transactionId = Integer.valueOf(rs.getString(1).trim());
        transactionId++;
        rs.close();
        return transactionId;

    }
    
    // Variables declaration - do not modify                     

    private javax.swing.JPanel card0;
    private javax.swing.JPanel card1;
    private javax.swing.JLabel card1_fee;
    private javax.swing.JPanel card2;
    private javax.swing.JLabel card2_fee;
    private javax.swing.JButton confirm1;
    private javax.swing.JButton confrim2;

    private javax.swing.JLabel card2_rate;
    private javax.swing.JLabel card1_rate;

    private javax.swing.JPanel jPanel3;

    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField card2_amount;
    private javax.swing.JTextField card1_amount;

    // End of variables declaration                   
}

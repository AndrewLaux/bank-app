package org.andrewadam.bank;

//Import required packages:
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//--Main Class--///////////////////////////////////////////////
//A JFrame GUI. Customer or teller interface for bank database.
//----------------------------------------------------------
public class App extends JFrame implements ActionListener {
    
    //Database object:
    private Data db;
     
    //GUI Components:
    private javax.swing.JButton atmBttn;
    private javax.swing.JTextField atmId;
    private javax.swing.JPasswordField atmPass;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton tellerBttn;
    private javax.swing.JPasswordField tellerPass;
    
    
    //--Default Constructor -------------------------------------
    //Checks connection and performs tasks necessary for the GUI.
    public App() {
        
        //Establish data object.
        db = new Data();
        
        //Popluate window with GUI elements:
        initComponents();
        
        //Register action listeners:
        atmBttn.addActionListener(this);
        tellerBttn.addActionListener(this);
    
    }
    
    //--Method-------------------------------
    //Executes code for a given action event.
    public void actionPerformed(ActionEvent obj) {
        
        //Capture source:
        Object source = obj.getSource();
        
        //Teller Button
        if(source == tellerBttn){
        	//Get password input:
            String tellerPw = String.valueOf(tellerPass.getPassword());
            
            //Open teller window if password is valid
            if( tellerPw.equals("123") ){
            	System.out.println("valid");
            	new TellerWindow().main(null);
            }
            else{
            	System.out.println("not valid");
            	
            }
        	
        }
        //ATM Button:
        if (source == atmBttn) {
            
            //Get inputs:
            char[] id = atmId.getText().toCharArray();
            char[] pin = atmPass.getPassword();

            //Verify id and pin:
            if (verifyPin(id, pin)) {

                
                //Reset fields:
                atmId.setText("");
                atmPass.setText("");
                
                //Launch atmwindow
                //String[] arg = {String.valueOf(id)};
                AtmWindow atm = new AtmWindow(String.valueOf(id));

            }
            
            //Else notify incorrect:
            else {
                String message = "Incorrect Id or Pin!";
                JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
            }        
        }
        
    }
    
    //--Main---------------------------
    //Creates an instance of App JFrame.
    public static void main( String[] args ) {
        
        //Try to create window:
        try {
            
            //Create window:
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new App().setVisible(true);
                }
            });
        } 
        
        //Catch problems generating GUI:
        catch (Exception e) {
            e.printStackTrace();
        }    
    }
    
    //--Method-----------------------------
    //Verifies given PIN given some Id
    private boolean verifyPin(char[] id, char[] pin) {

        //Try to get hash from db
        try {
            
            //Get PIN/Id hash from db. Remove whitespace from result:
            String qry = "SELECT hash FROM customers WHERE tax_id=" + String.valueOf(id);
            System.out.print(qry);
            ResultSet r = db.requestData(qry);
            r.next();
            String found = r.getString("hash").replaceAll(" ","");
            db.closeConn();

            //TODO: compute expected hash.
            String expected = Hmac.hash(String.valueOf(id), String.valueOf(pin));
            System.out.println(expected);
            
            //Compare
            return (found.equals(expected));


        }//Could not make query to server.
        catch (Exception e) {
            
            System.out.println(e.getMessage() + "\nFailed to verify PIN");
            return false;
        }

    }

    //--Method-------------------------------
    //Initializes GUI components and layouts.
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        atmId = new javax.swing.JTextField();
        atmBttn = new javax.swing.JButton();
        atmPass = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        tellerPass = new javax.swing.JPasswordField();
        tellerBttn = new javax.swing.JButton();

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bank App");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ATM Access", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        atmId.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tax ID", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        atmId.setOpaque(false);

        atmBttn.setText("Log In");
        atmBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        atmPass.setColumns(10);
        atmPass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PIN", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        atmPass.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(atmId, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(atmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(atmBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(atmId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(atmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(atmBttn, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Teller Access", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        tellerPass.setColumns(10);
        tellerPass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Authorization", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        tellerPass.setOpaque(false);

        tellerBttn.setText("Log In");
        tellerBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tellerBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tellerPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tellerPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tellerBttn, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
            .addGroup(layout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
}

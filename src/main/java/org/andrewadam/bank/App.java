package org.andrewadam.bank;

//Import required packages:
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class App extends JFrame implements ActionListener {
    
    // JDBC driver name and database URL
   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
   static final String DB_URL = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:XE";

   //Load .env variables
   static final Dotenv DOTENV = Dotenv.load();
   
   //Database credentials
   static final String USERNAME = DOTENV.get("USER");
   static final String PASSWORD = DOTENV.get("PASS");
    
    //Define GUI parameters:
    private static final String TITLE = "Bank App - Access Point"; 
    private static final int WIDTH = 400;
    private static final int HEIGHT = 150;
    
    
    
    //Default constructor:
    public App() {
        
        //Define containers for GUI elements:
        Container window = getContentPane();
        JPanel paneUser = new JPanel();
        JPanel paneEmp = new JPanel();
        
        //Specify layouts:
        window.setLayout(new GridLayout(2,1));
        
        //Set some borders:
        TitledBorder userBorder = new TitledBorder("Atm Access");
        userBorder.setTitleJustification(TitledBorder.CENTER);
        userBorder.setTitlePosition(TitledBorder.TOP);
        TitledBorder tellerBorder = new TitledBorder("Teller Access");
        tellerBorder.setTitleJustification(TitledBorder.CENTER);
        tellerBorder.setTitlePosition(TitledBorder.TOP);
        paneUser.setBorder(userBorder);
        paneEmp.setBorder(tellerBorder);
        
        //Connect containers:
        window.add(paneUser);
        window.add(paneEmp);
        
        
        
        //Set GUI Parameters.
        this.setTitle(TITLE);
        this.setSize(WIDTH, WIDTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    }
    
    //Action listener.
    public void actionPerformed(ActionEvent obj) {
    
    }
    
    //Main driver.
    public static void main( String[] args ) {
        
        //Launch GUI
        App mygui = new App();
        
    }
}

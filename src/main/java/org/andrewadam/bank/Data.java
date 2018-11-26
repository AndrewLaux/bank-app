/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.andrewadam.bank;

/**
 *
 * @author Castle
 */
//STEP 1. Import required packages
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.omg.CORBA.portable.ApplicationException;

//--Class--/////////////////////////////////////////////////
//Has methods to send requests to database. Allows client to
//retreive resulting data.
//----------------------------------------------------------
public class Data {
    
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
   static final String DB_URL = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:XE";

   //Load .env variables
   static final Dotenv DOTENV = Dotenv.load();
   
   //Database credentials
   static final String USERNAME = DOTENV.get("USER");
   static final String PASSWORD = DOTENV.get("PASS");
   
   //Data members.
   private Connection conn;
   private Statement stmt;
   private ResultSet result;
   private String last;
   
   
   //--Default Constructor-----
   //Initilizes necessary data.
    public Data() {

        //Initialize:
        conn = null;
        stmt = null;
        result = null;
        last = "";
    }
    
    //--Method-------------------------------------------
    //Executes the query statement passed as an argument.
    public ResultSet requestData(String qry) throws Exception {

        //Attempt to execute querey:
        try {
            
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connected database successfully...");

            //Make request:
            stmt = conn.createStatement();
            result = stmt.executeQuery(qry);
        }
        //Catch sql exception:
        catch (Exception e) {

            //Handle errors for Class.forName
            e.printStackTrace();
            closeConn();
            throw new RuntimeException("Problem making query!");
        }

        //Return result;
        return result;
    }
    
    //--Method----------------
    //Closes connection after use
    public void closeConn() throws Exception {
        if(conn!=null) conn.close();
    }

}
    
    
   
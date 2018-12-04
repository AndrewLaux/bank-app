/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.andrewadam.bank;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

//--Class--//////////////////////////////////////////////
//Computes the hash of a string
public class Hmac {

    //--Static--Method---------------------------------------
    //Returns MD5 hash of username + pin
    public static String hash(String username, String pin) throws Exception {
        byte[] input = (username + pin).getBytes();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(input);
        byte[] output = digest.digest();
        return DatatypeConverter.printHexBinary(output);        
    }
    
    //Try out.
    public static void main(String[] args) {
        try{
            System.out.println(hash("1234123412", "9999"));
        } catch(Exception e) {}     
    }
}
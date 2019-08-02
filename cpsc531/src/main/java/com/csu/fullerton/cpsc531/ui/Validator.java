/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csu.fullerton.cpsc531.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Peter
 */
public class Validator {

    //Pattern pattern_phone = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
    //Matcher matcher = pattern.matcher(sPhoneNumber);
    private static String PHONE_PATTERN = "\\d{3}-\\d{3}-\\d{4}";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    public static boolean isPhone(String phone){
        return phone.matches(PHONE_PATTERN);
    }
    
    public static boolean isEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }

}

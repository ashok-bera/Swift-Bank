package com.bera.swiftbank.utils;

import java.time.Year;

public class AccountUtils {
    public static String generateAccountNo(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int randomNo = (int) Math.floor(Math.random()*(max-min+1)+min);
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randomNo);
        StringBuilder accountNo = new StringBuilder();
        return accountNo.append(year).append(randomNumber).toString();
    }
}

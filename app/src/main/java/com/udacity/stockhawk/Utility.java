package com.udacity.stockhawk;


import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Utility {

    private static DecimalFormat dollarFormatWithPlus;
    private static DecimalFormat dollarFormat;
    private static DecimalFormat percentageFormat;

    private static String[] suffix = new String[]{"","k", "m", "b", "t"};

    public static String getFormattedTextFromtNumber(double number) {
        String r = new DecimalFormat("##0.##E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);

        return r;
    }

    public static DecimalFormat getDollarFormatWithPlus(){
        if(dollarFormatWithPlus == null){
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
        }
        return dollarFormatWithPlus;
    }

    public static DecimalFormat getDollarFormat(){
        if(dollarFormat == null){
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        }
        return dollarFormat;
    }

    public static DecimalFormat getPercentageFormat(){
        if(percentageFormat == null){
            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");
        }
        return percentageFormat;
    }

}

package com.example.personalexpensemanager.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MoneyFormatter {
    public static String formatVND(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount) + " ₫";
    }
}
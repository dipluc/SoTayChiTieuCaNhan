package com.example.personalexpensemanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat UI_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static String getCurrentDateDb() {
        return DB_DATE_FORMAT.format(new Date());
    }

    public static String dbToUiDate(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "";
        try {
            Date date = DB_DATE_FORMAT.parse(dbDate);
            return date != null ? UI_DATE_FORMAT.format(date) : dbDate;
        } catch (ParseException e) {
            return dbDate;
        }
    }

    public static String uiToDbDate(String uiDate) {
        if (uiDate == null || uiDate.isEmpty()) return "";
        try {
            Date date = UI_DATE_FORMAT.parse(uiDate);
            return date != null ? DB_DATE_FORMAT.format(date) : uiDate;
        } catch (ParseException e) {
            return uiDate;
        }
    }
}
package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "PersonalExpenseDB.db";
    public static final int DB_VERSION = 1;

    private static DatabaseHelper instance;

    private static final String CREATE_USERS =
            "CREATE TABLE Users(user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, full_name TEXT NOT NULL)";

    private static final String CREATE_CATEGORIES =
            "CREATE TABLE Categories(cat_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "cat_name TEXT NOT NULL, cat_type INTEGER NOT NULL CHECK(cat_type IN (0,1)),"
                    + "icon_name TEXT NOT NULL)";

    private static final String CREATE_TRANSACTIONS =
            "CREATE TABLE Transactions(trans_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "amount INTEGER NOT NULL CHECK(amount>0), cat_id INTEGER NOT NULL,"
                    + "trans_date TEXT NOT NULL, note TEXT, image_uri TEXT, user_id INTEGER NOT NULL,"
                    + "FOREIGN KEY(cat_id) REFERENCES Categories(cat_id) ON DELETE RESTRICT,"
                    + "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";

    private static final String CREATE_BUDGETS =
            "CREATE TABLE Budgets(budget_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "month_year TEXT NOT NULL, limit_amount INTEGER NOT NULL CHECK(limit_amount>0),"
                    + "user_id INTEGER NOT NULL, UNIQUE(user_id, month_year),"
                    + "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // Mandatory for Foreign Keys to take effect
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_CATEGORIES);
        db.execSQL(CREATE_TRANSACTIONS);
        db.execSQL(CREATE_BUDGETS);
        seedCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Budgets");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    private void seedCategories(SQLiteDatabase db) {
        String[][] data = {
                {"Ăn uống", "0", "ic_food"},
                {"Học phí", "0", "ic_study"},
                {"Mua sắm", "0", "ic_shopping"},
                {"Đi lại", "0", "ic_transport"},
                {"Giải trí", "0", "ic_entertainment"},
                {"Khác", "0", "ic_other"},
                {"Tiền lương", "1", "ic_salary"},
                {"Học bổng", "1", "ic_scholarship"},
                {"Thưởng", "1", "ic_bonus"},
                {"Thu nhập khác", "1", "ic_other_income"}
        };
        for (String[] row : data) {
            ContentValues cv = new ContentValues();
            cv.put("cat_name", row[0]);
            cv.put("cat_type", Integer.parseInt(row[1]));
            cv.put("icon_name", row[2]);
            db.insert("Categories", null, cv);
        }
    }
}
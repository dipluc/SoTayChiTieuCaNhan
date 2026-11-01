package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.models.Budget;
import com.example.personalexpensemanager.models.CategoryStat;

import java.util.ArrayList;
import java.util.List;

public class StatisticBudgetDAO {
    private DatabaseHelper dbHelper;

    public StatisticBudgetDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Mode: DAY (YYYY-MM-DD), MONTH (MM-YYYY), YEAR (YYYY)
     */
    public long sumByTypeAndPeriod(int userId, int type, String mode, String value) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(t.amount) FROM Transactions t " +
                        "JOIN Categories c ON t.cat_id = c.cat_id " +
                        "WHERE t.user_id = ? AND c.cat_type = ?"
        );

        String dateCondition = "";
        if ("DAY".equalsIgnoreCase(mode)) {
            dateCondition = " AND t.trans_date = ?";
        } else if ("MONTH".equalsIgnoreCase(mode)) {
            dateCondition = " AND strftime('%m-%Y', t.trans_date) = ?";
        } else if ("YEAR".equalsIgnoreCase(mode)) {
            dateCondition = " AND strftime('%Y', t.trans_date) = ?";
        }
        sql.append(dateCondition);

        Cursor cursor = db.rawQuery(sql.toString(), new String[]{String.valueOf(userId), String.valueOf(type), value});
        long sum = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            sum = cursor.getLong(0);
        }
        cursor.close();
        return sum;
    }

    public List<CategoryStat> statByCategory(int userId, String mode, String value) {
        List<CategoryStat> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long totalSum = sumByTypeAndPeriod(userId, 0, mode, value); // Total expense for percentage calculation

        StringBuilder sql = new StringBuilder(
                "SELECT c.cat_name, c.icon_name, SUM(t.amount) AS total " +
                        "FROM Transactions t JOIN Categories c ON t.cat_id = c.cat_id " +
                        "WHERE c.cat_type = 0 AND t.user_id = ?"
        );

        if ("DAY".equalsIgnoreCase(mode)) {
            sql.append(" AND t.trans_date = ?");
        } else if ("MONTH".equalsIgnoreCase(mode)) {
            sql.append(" AND strftime('%m-%Y', t.trans_date) = ?");
        } else if ("YEAR".equalsIgnoreCase(mode)) {
            sql.append(" AND strftime('%Y', t.trans_date) = ?");
        }

        sql.append(" GROUP BY c.cat_id ORDER BY total DESC");

        Cursor cursor = db.rawQuery(sql.toString(), new String[]{String.valueOf(userId), value});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String icon = cursor.getString(1);
            long catTotal = cursor.getLong(2);
            double percent = totalSum > 0 ? ((double) catTotal / totalSum) * 100 : 0;
            list.add(new CategoryStat(name, icon, catTotal, percent));
        }
        cursor.close();
        return list;
    }

    public Budget getBudget(int userId, String monthYear) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Budgets WHERE user_id = ? AND month_year = ?",
                new String[]{String.valueOf(userId), monthYear});
        Budget budget = null;
        if (cursor.moveToFirst()) {
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow("budget_id"));
            long limit = cursor.getLong(cursor.getColumnIndexOrThrow("limit_amount"));
            budget = new Budget(budgetId, monthYear, limit, userId);
        }
        cursor.close();
        return budget;
    }

    public boolean saveBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month_year", budget.getMonthYear());
        cv.put("limit_amount", budget.getLimitAmount());
        cv.put("user_id", budget.getUserId());

        Budget existing = getBudget(budget.getUserId(), budget.getMonthYear());
        if (existing == null) {
            return db.insert("Budgets", null, cv) > 0;
        } else {
            return db.update("Budgets", cv, "budget_id = ?", new String[]{String.valueOf(existing.getBudgetId())}) > 0;
        }
    }
}
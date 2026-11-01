package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private DatabaseHelper dbHelper;

    public TransactionDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(Transaction trans) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", trans.getAmount());
        cv.put("cat_id", trans.getCatId());
        cv.put("trans_date", trans.getTransDate());
        cv.put("note", trans.getNote());
        cv.put("image_uri", trans.getImageUri());
        cv.put("user_id", trans.getUserId());
        return db.insert("Transactions", null, cv);
    }

    public int update(Transaction trans) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", trans.getAmount());
        cv.put("cat_id", trans.getCatId());
        cv.put("trans_date", trans.getTransDate());
        cv.put("note", trans.getNote());
        cv.put("image_uri", trans.getImageUri());
        return db.update("Transactions", cv, "trans_id = ?", new String[]{String.valueOf(trans.getTransId())});
    }

    public int delete(int transId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Transactions", "trans_id = ?", new String[]{String.valueOf(transId)});
    }

    public Transaction getById(int transId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT t.*, c.cat_name, c.icon_name, c.cat_type FROM Transactions t " +
                "JOIN Categories c ON t.cat_id = c.cat_id " +
                "WHERE t.trans_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(transId)});
        Transaction trans = null;
        if (cursor.moveToFirst()) {
            trans = cursorToTransaction(cursor);
        }
        cursor.close();
        return trans;
    }

    public List<Transaction> getAllByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT t.*, c.cat_name, c.icon_name, c.cat_type FROM Transactions t " +
                "JOIN Categories c ON t.cat_id = c.cat_id " +
                "WHERE t.user_id = ? ORDER BY t.trans_date DESC, t.trans_id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            list.add(cursorToTransaction(cursor));
        }
        cursor.close();
        return list;
    }

    public long sumByType(int userId, int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(t.amount) FROM Transactions t " +
                "JOIN Categories c ON t.cat_id = c.cat_id " +
                "WHERE t.user_id = ? AND c.cat_type = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(type)});
        long sum = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            sum = cursor.getLong(0);
        }
        cursor.close();
        return sum;
    }

    public int deleteAllByUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Transactions", "user_id = ?", new String[]{String.valueOf(userId)});
    }

    /**
     * Stub search method for TV2 implementation (Search & Filter real-time).
     * type = -1 (All), 0 (Expense), 1 (Income)
     */
    public List<Transaction> search(int userId, String keyword, int type) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, c.cat_name, c.icon_name, c.cat_type FROM Transactions t " +
                        "JOIN Categories c ON t.cat_id = c.cat_id " +
                        "WHERE t.user_id = ?"
        );
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (t.note LIKE ? OR c.cat_name LIKE ?)");
            String param = "%" + keyword.trim() + "%";
            args.add(param);
            args.add(param);
        }

        if (type == 0 || type == 1) {
            sql.append(" AND c.cat_type = ?");
            args.add(String.valueOf(type));
        }

        sql.append(" ORDER BY t.trans_date DESC, t.trans_id DESC");

        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        while (cursor.moveToNext()) {
            list.add(cursorToTransaction(cursor));
        }
        cursor.close();
        return list;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        int transId = cursor.getInt(cursor.getColumnIndexOrThrow("trans_id"));
        long amount = cursor.getLong(cursor.getColumnIndexOrThrow("amount"));
        int catId = cursor.getInt(cursor.getColumnIndexOrThrow("cat_id"));
        String transDate = cursor.getString(cursor.getColumnIndexOrThrow("trans_date"));
        String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));

        Transaction t = new Transaction(transId, amount, catId, transDate, note, imageUri, userId);

        String catName = cursor.getString(cursor.getColumnIndexOrThrow("cat_name"));
        String iconName = cursor.getString(cursor.getColumnIndexOrThrow("icon_name"));
        int catType = cursor.getInt(cursor.getColumnIndexOrThrow("cat_type"));
        t.setDisplayInfo(catName, iconName, catType);

        return t;
    }
}
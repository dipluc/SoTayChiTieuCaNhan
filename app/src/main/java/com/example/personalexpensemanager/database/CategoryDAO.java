package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private DatabaseHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(Category cat) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cat_name", cat.getCatName());
        cv.put("cat_type", cat.getCatType());
        cv.put("icon_name", cat.getIconName());
        return db.insert("Categories", null, cv);
    }

    public int update(Category cat) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cat_name", cat.getCatName());
        cv.put("cat_type", cat.getCatType());
        cv.put("icon_name", cat.getIconName());
        return db.update("Categories", cv, "cat_id = ?", new String[]{String.valueOf(cat.getCatId())});
    }

    public int delete(int catId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("Categories", "cat_id = ?", new String[]{String.valueOf(catId)});
    }

    public boolean isInUse(int catId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Transactions WHERE cat_id = ?", new String[]{String.valueOf(catId)});
        boolean inUse = cursor.getCount() > 0;
        cursor.close();
        return inUse;
    }

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Categories ORDER BY cat_type ASC, cat_name ASC", null);
        while (cursor.moveToNext()) {
            list.add(cursorToCategory(cursor));
        }
        cursor.close();
        return list;
    }

    public List<Category> getByType(int catType) {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Categories WHERE cat_type = ? ORDER BY cat_name ASC", new String[]{String.valueOf(catType)});
        while (cursor.moveToNext()) {
            list.add(cursorToCategory(cursor));
        }
        cursor.close();
        return list;
    }

    public Category getById(int catId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Categories WHERE cat_id = ?", new String[]{String.valueOf(catId)});
        Category cat = null;
        if (cursor.moveToFirst()) {
            cat = cursorToCategory(cursor);
        }
        cursor.close();
        return cat;
    }

    private Category cursorToCategory(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("cat_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("cat_name"));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow("cat_type"));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow("icon_name"));
        return new Category(id, name, type, icon);
    }
}
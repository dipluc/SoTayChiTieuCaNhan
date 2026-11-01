package com.example.personalexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.personalexpensemanager.models.User;
import com.example.personalexpensemanager.utils.PasswordUtils;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long registerUser(String username, String rawPassword, String fullName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String salt = PasswordUtils.newSalt();
        String hashedPassword = PasswordUtils.hash(rawPassword, salt);
        String storedPassword = salt + ":" + hashedPassword;

        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", storedPassword);
        cv.put("full_name", fullName);
        return db.insert("Users", null, cv);
    }

    public User loginUser(String username, String rawPassword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?", new String[]{username});
        User user = null;
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String[] parts = storedPassword.split(":");
            if (parts.length == 2) {
                String salt = parts[0];
                String hash = parts[1];
                if (hash.equals(PasswordUtils.hash(rawPassword, salt))) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    user = new User(id, username, storedPassword, name);
                }
            }
        }
        cursor.close();
        return user;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM Users WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            String[] parts = storedPassword.split(":");
            if (parts.length == 2) {
                String salt = parts[0];
                String hash = parts[1];
                if (hash.equals(PasswordUtils.hash(oldPassword, salt))) {
                    cursor.close();
                    String newSalt = PasswordUtils.newSalt();
                    String newHash = PasswordUtils.hash(newPassword, newSalt);
                    ContentValues cv = new ContentValues();
                    cv.put("password", newSalt + ":" + newHash);
                    return db.update("Users", cv, "user_id = ?", new String[]{String.valueOf(userId)}) > 0;
                }
            }
        }
        cursor.close();
        return false;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE user_id = ?", new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            user = new User(userId, username, password, fullName);
        }
        cursor.close();
        return user;
    }
}
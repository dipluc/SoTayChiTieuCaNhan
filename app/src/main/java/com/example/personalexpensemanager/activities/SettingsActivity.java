package com.example.personalexpensemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.database.TransactionDAO;
import com.example.personalexpensemanager.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {
    private MaterialToolbar toolbarSettings;
    private SwitchCompat swDarkMode;
    private LinearLayout btnChangePassword, btnClearData, btnLogout;

    private SessionManager sessionManager;
    private TransactionDAO transactionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);
        transactionDAO = new TransactionDAO(this);

        toolbarSettings = findViewById(R.id.toolbarSettings);
        swDarkMode = findViewById(R.id.swDarkMode);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnClearData = findViewById(R.id.btnClearData);
        btnLogout = findViewById(R.id.btnLogout);

        toolbarSettings.setNavigationOnClickListener(v -> finish());

        swDarkMode.setChecked(sessionManager.isDarkMode());
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnClearData.setOnClickListener(v -> showClearDataDialog());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ giao dịch? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int count = transactionDAO.deleteAllByUser(sessionManager.getUserId());
                    Toast.makeText(this, "Đã xóa " + count + " giao dịch!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        sessionManager.clearSession();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
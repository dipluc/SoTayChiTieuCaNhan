package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.database.UserDAO;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Họ và tên không được để trống");
            return;
        }
        if (username.isEmpty()) {
            etUsername.setError("Tên đăng nhập không được để trống");
            return;
        }
        if (username.contains(" ")) {
            etUsername.setError("Tên đăng nhập không được chứa khoảng trắng");
            return;
        }
        if (userDAO.isUsernameExists(username)) {
            etUsername.setError("Tên đăng nhập đã tồn tại");
            return;
        }
        if (password.length() < 8) {
            etPassword.setError("Mật khẩu phải từ 8 ký tự trở lên");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        long id = userDAO.registerUser(username, password, fullName);
        if (id > 0) {
            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
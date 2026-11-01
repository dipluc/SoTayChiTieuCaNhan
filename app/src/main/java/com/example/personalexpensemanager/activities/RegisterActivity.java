package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.database.UserDAO;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private EditText etFullName, etUsername, etPassword, etConfirmPassword;
    private ImageButton ibTogglePassword, ibToggleConfirmPassword;
    private CheckBox cbTerms;
    private View btnRegister;
    private TextView tvLogin, tvStrengthText;

    private UserDAO userDAO;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        ibBack = findViewById(R.id.ibBack);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ibTogglePassword = findViewById(R.id.ibTogglePassword);
        ibToggleConfirmPassword = findViewById(R.id.ibToggleConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvStrengthText = findViewById(R.id.tvStrengthText);

        if (ibBack != null) {
            ibBack.setOnClickListener(v -> finish());
        }

        if (ibTogglePassword != null) {
            ibTogglePassword.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                toggleVisibility(etPassword, ibTogglePassword, isPasswordVisible);
            });
        }

        if (ibToggleConfirmPassword != null) {
            ibToggleConfirmPassword.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                toggleVisibility(etConfirmPassword, ibToggleConfirmPassword, isConfirmPasswordVisible);
            });
        }

        setupPasswordStrengthWatcher();

        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void toggleVisibility(EditText editText, ImageButton toggleBtn, boolean visible) {
        if (visible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleBtn.setImageResource(R.drawable.ic_check);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleBtn.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }

    private void setupPasswordStrengthWatcher() {
        if (etPassword == null || tvStrengthText == null) return;

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String p = s.toString();
                if (p.isEmpty()) {
                    tvStrengthText.setText("Chưa nhập");
                } else if (p.length() < 8) {
                    tvStrengthText.setText("Yếu");
                } else if (p.matches(".*[A-Z].*") && p.matches(".*[0-9].*")) {
                    tvStrengthText.setText("Mạnh");
                } else {
                    tvStrengthText.setText("Trung bình");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
        if (cbTerms != null && !cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản dịch vụ & Chính sách bảo mật", Toast.LENGTH_SHORT).show();
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
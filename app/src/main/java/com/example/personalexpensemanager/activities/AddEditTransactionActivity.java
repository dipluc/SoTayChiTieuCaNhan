package com.example.personalexpensemanager.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.database.CategoryDAO;
import com.example.personalexpensemanager.database.TransactionDAO;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_TRANS_ID = "trans_id";

    private MaterialToolbar toolbarAddEdit;
    private EditText etAmount, etTransDate, etNote;
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private Spinner spCategory;
    private ImageView ivReceipt;
    private Button btnSelectImage, btnRemoveImage, btnSave;

    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;

    private int editingTransId = -1;
    private int currentUserId = -1;
    private String selectedImageUri = null;
    private List<Category> categoryList;
    private ArrayAdapter<Category> spinnerAdapter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        transactionDAO = new TransactionDAO(this);
        categoryDAO = new CategoryDAO(this);

        toolbarAddEdit = findViewById(R.id.toolbarAddEdit);
        etAmount = findViewById(R.id.etAmount);
        etTransDate = findViewById(R.id.etTransDate);
        etNote = findViewById(R.id.etNote);
        rgType = findViewById(R.id.rgType);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        spCategory = findViewById(R.id.spCategory);
        ivReceipt = findViewById(R.id.ivReceipt);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        btnSave = findViewById(R.id.btnSave);

        toolbarAddEdit.setNavigationOnClickListener(v -> finish());

        setupImagePicker();
        setupDatePicker();
        setupTypeRadioGroup();

        editingTransId = getIntent().getIntExtra(EXTRA_TRANS_ID, -1);
        if (editingTransId != -1) {
            toolbarAddEdit.setTitle(R.string.title_edit_transaction);
            loadTransactionForEditing();
        } else {
            toolbarAddEdit.setTitle(R.string.title_add_transaction);
            etTransDate.setText(DateUtils.dbToUiDate(DateUtils.getCurrentDateDb()));
            updateCategorySpinner(0); // Default Chi (0)
        }

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // Save image locally inside internal storage so URI persists across app reboots
                            String savedPath = saveImageToInternalStorage(uri);
                            if (savedPath != null) {
                                selectedImageUri = savedPath;
                                displayImage(selectedImageUri);
                            } else {
                                Toast.makeText(this, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            File dir = new File(getFilesDir(), "receipts");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destFile = new File(dir, "receipt_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return Uri.fromFile(destFile).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void displayImage(String uriString) {
        if (uriString != null && !uriString.trim().isEmpty()) {
            try {
                ivReceipt.setImageURI(Uri.parse(uriString));
                btnRemoveImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                ivReceipt.setImageResource(R.drawable.ic_no_attachment);
                btnRemoveImage.setVisibility(View.GONE);
            }
        } else {
            ivReceipt.setImageResource(R.drawable.ic_no_attachment);
            btnRemoveImage.setVisibility(View.GONE);
        }
    }

    private void removeImage() {
        selectedImageUri = null;
        displayImage(null);
    }

    private void setupDatePicker() {
        etTransDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddEditTransactionActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etTransDate.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setupTypeRadioGroup() {
        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            int catType = (checkedId == R.id.rbIncome) ? 1 : 0;
            updateCategorySpinner(catType);
        });
    }

    private void updateCategorySpinner(int catType) {
        categoryList = categoryDAO.getByType(catType);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinnerAdapter);
    }

    private void loadTransactionForEditing() {
        Transaction t = transactionDAO.getById(editingTransId);
        if (t == null) {
            Toast.makeText(this, "Không tìm thấy giao dịch!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etAmount.setText(String.valueOf(t.getAmount()));
        etTransDate.setText(DateUtils.dbToUiDate(t.getTransDate()));
        etNote.setText(t.getNote() != null ? t.getNote() : "");
        selectedImageUri = t.getImageUri();
        displayImage(selectedImageUri);

        if (t.getCatType() == 1) {
            rbIncome.setChecked(true);
            updateCategorySpinner(1);
        } else {
            rbExpense.setChecked(true);
            updateCategorySpinner(0);
        }

        // Select the current category in spinner
        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCatId() == t.getCatId()) {
                    spCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String dateUi = etTransDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError(getString(R.string.err_empty_amount));
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
            if (amount <= 0) {
                etAmount.setError(getString(R.string.err_invalid_amount));
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError(getString(R.string.err_invalid_amount));
            return;
        }

        if (dateUi.isEmpty()) {
            etTransDate.setError(getString(R.string.err_empty_date));
            return;
        }

        Category selectedCat = (Category) spCategory.getSelectedItem();
        if (selectedCat == null) {
            Toast.makeText(this, R.string.err_empty_category, Toast.LENGTH_SHORT).show();
            return;
        }

        String dbDate = DateUtils.uiToDbDate(dateUi);

        if (editingTransId != -1) { // EDIT
            Transaction t = new Transaction(editingTransId, amount, selectedCat.getCatId(), dbDate, note, selectedImageUri, currentUserId);
            int rows = transactionDAO.update(t);
            if (rows > 0) {
                Toast.makeText(this, R.string.msg_transaction_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        } else { // ADD
            Transaction t = new Transaction(0, amount, selectedCat.getCatId(), dbDate, note, selectedImageUri, currentUserId);
            long id = transactionDAO.insert(t);
            if (id > 0) {
                Toast.makeText(this, R.string.msg_transaction_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Thêm mới thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
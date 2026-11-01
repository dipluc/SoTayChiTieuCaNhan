package com.example.personalexpensemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.TransactionAdapter;
import com.example.personalexpensemanager.database.StatisticBudgetDAO;
import com.example.personalexpensemanager.database.TransactionDAO;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.MoneyFormatter;
import com.example.personalexpensemanager.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar toolbarMain;
    private EditText etSearch;
    private Spinner spFilter;
    private TextView tvMonthlyBalance, tvTotalIncome, tvTotalExpense, tvEmpty;
    private ListView lvTransactions;
    private FloatingActionButton fabAddTransaction;

    private TransactionDAO transactionDAO;
    private StatisticBudgetDAO statisticBudgetDAO;
    private SessionManager sessionManager;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private int currentUserId;

    private ActivityResultLauncher<Intent> addEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check login status
        if (sessionManager.getUserId() == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        currentUserId = sessionManager.getUserId();
        transactionDAO = new TransactionDAO(this);
        statisticBudgetDAO = new StatisticBudgetDAO(this);

        toolbarMain = findViewById(R.id.toolbarMain);
        etSearch = findViewById(R.id.etSearch);
        spFilter = findViewById(R.id.spFilter);
        tvMonthlyBalance = findViewById(R.id.tvMonthlyBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvEmpty = findViewById(R.id.tvEmpty);
        lvTransactions = findViewById(R.id.lvTransactions);
        fabAddTransaction = findViewById(R.id.fabAddTransaction);

        setSupportActionBar(toolbarMain);

        lvTransactions.setEmptyView(tvEmpty);
        registerForContextMenu(lvTransactions);

        setupAddEditLauncher();
        setupSearchAndFilter();

        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
            addEditLauncher.launch(intent);
        });

        loadData();
    }

    private void setupAddEditLauncher() {
        addEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadData();
                    }
                }
        );
    }

    public void loadData() {
        // Calculate current month income, expense, and balance (MM-YYYY)
        String currentMonthStr = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        long totalIncome = statisticBudgetDAO.sumByTypeAndPeriod(currentUserId, 1, "MONTH", currentMonthStr);
        long totalExpense = statisticBudgetDAO.sumByTypeAndPeriod(currentUserId, 0, "MONTH", currentMonthStr);
        long balance = totalIncome - totalExpense;

        tvTotalIncome.setText("+ " + MoneyFormatter.formatVND(totalIncome));
        tvTotalExpense.setText("- " + MoneyFormatter.formatVND(totalExpense));
        tvMonthlyBalance.setText(MoneyFormatter.formatVND(balance));

        // Load transaction list filtered by search keyword & type filter
        String searchKeyword = etSearch != null ? etSearch.getText().toString().trim() : "";
        int filterPosition = spFilter != null ? spFilter.getSelectedItemPosition() : 0;
        int filterType = -1;
        if (filterPosition == 1) filterType = 1; // Income
        else if (filterPosition == 2) filterType = 0; // Expense

        transactionList = transactionDAO.search(currentUserId, searchKeyword, filterType);

        if (adapter == null) {
            adapter = new TransactionAdapter(this, transactionList);
            lvTransactions.setAdapter(adapter);
        } else {
            adapter.updateData(transactionList);
        }
    }

    // Options Menu setup (TV4 / Toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_category) {
            startActivity(new Intent(this, CategoryActivity.class));
            return true;
        } else if (id == R.id.menu_statistics) {
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Context Menu for Edit / Delete on ListView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context_transaction, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) return super.onContextItemSelected(item);

        Transaction selectedTrans = (Transaction) adapter.getItem(info.position);
        if (selectedTrans == null) return super.onContextItemSelected(item);

        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit) {
            Intent intent = new Intent(this, AddEditTransactionActivity.class);
            intent.putExtra(AddEditTransactionActivity.EXTRA_TRANS_ID, selectedTrans.getTransId());
            addEditLauncher.launch(intent);
            return true;
        } else if (itemId == R.id.menu_delete) {
            showDeleteConfirmDialog(selectedTrans.getTransId());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showDeleteConfirmDialog(int transId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage(R.string.msg_confirm_delete_trans)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rows = transactionDAO.delete(transId);
                    if (rows > 0) {
                        Toast.makeText(this, R.string.msg_transaction_deleted, Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Search & Filter setup
    private void setupSearchAndFilter() {
        String[] filters = {"Tất cả", "Thu nhập", "Chi tiêu"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
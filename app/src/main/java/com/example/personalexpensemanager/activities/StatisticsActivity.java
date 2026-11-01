package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.StatCategoryAdapter;
import com.example.personalexpensemanager.database.StatisticBudgetDAO;
import com.example.personalexpensemanager.models.CategoryStat;
import com.example.personalexpensemanager.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    private MaterialToolbar toolbarStatistics;
    private Spinner spPeriod;
    private MaterialCardView cardBudget;
    private ProgressBar pbBudget;
    private TextView tvBudgetInfo, tvBudgetWarning;
    private ListView lvStatCategory;

    private StatisticBudgetDAO statDAO;
    private SessionManager sessionManager;
    private StatCategoryAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
        statDAO = new StatisticBudgetDAO(this);

        toolbarStatistics = findViewById(R.id.toolbarStatistics);
        spPeriod = findViewById(R.id.spPeriod);
        cardBudget = findViewById(R.id.cardBudget);
        pbBudget = findViewById(R.id.pbBudget);
        tvBudgetInfo = findViewById(R.id.tvBudgetInfo);
        tvBudgetWarning = findViewById(R.id.tvBudgetWarning);
        lvStatCategory = findViewById(R.id.lvStatCategory);

        toolbarStatistics.setNavigationOnClickListener(v -> finish());

        setupPeriodSpinner();
    }

    private void setupPeriodSpinner() {
        String[] periods = {"Theo tháng", "Theo ngày", "Theo năm"};
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPeriod.setAdapter(periodAdapter);

        spPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // MONTH
                    cardBudget.setVisibility(View.VISIBLE);
                    loadStats("MONTH", new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date()));
                } else if (position == 1) { // DAY
                    cardBudget.setVisibility(View.GONE);
                    loadStats("DAY", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                } else { // YEAR
                    cardBudget.setVisibility(View.GONE);
                    loadStats("YEAR", new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadStats(String mode, String value) {
        List<CategoryStat> list = statDAO.statByCategory(currentUserId, mode, value);
        if (adapter == null) {
            adapter = new StatCategoryAdapter(this, list);
            lvStatCategory.setAdapter(adapter);
        } else {
            adapter.updateData(list);
        }
    }
}
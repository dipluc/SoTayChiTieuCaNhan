package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.CategoryAdapter;
import com.example.personalexpensemanager.database.CategoryDAO;
import com.example.personalexpensemanager.models.Category;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private GridView gvCategories;
    private FloatingActionButton fabAddCategory;
    private MaterialToolbar toolbarCategory;

    private CategoryDAO categoryDAO;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryDAO = new CategoryDAO(this);

        toolbarCategory = findViewById(R.id.toolbarCategory);
        gvCategories = findViewById(R.id.gvCategories);
        fabAddCategory = findViewById(R.id.fabAddCategory);

        toolbarCategory.setNavigationOnClickListener(v -> finish());

        loadCategories();
    }

    private void loadCategories() {
        categoryList = categoryDAO.getAll();
        if (adapter == null) {
            adapter = new CategoryAdapter(this, categoryList);
            gvCategories.setAdapter(adapter);
        } else {
            adapter.updateData(categoryList);
        }
    }
}
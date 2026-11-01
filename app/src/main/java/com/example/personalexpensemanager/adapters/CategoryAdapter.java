package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> list;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return list != null && position < list.size() ? list.get(position).getCatId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.ivCategoryIcon = convertView.findViewById(R.id.ivCategoryIcon);
            holder.tvCategoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.tvCategoryType = convertView.findViewById(R.id.tvCategoryType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category c = list.get(position);
        holder.tvCategoryName.setText(c.getCatName());
        holder.tvCategoryType.setText(c.getCatType() == 1 ? "Thu nhập" : "Chi tiêu");

        int resId = 0;
        if (c.getIconName() != null && !c.getIconName().isEmpty()) {
            resId = context.getResources().getIdentifier(c.getIconName(), "drawable", context.getPackageName());
        }
        if (resId != 0) {
            holder.ivCategoryIcon.setImageResource(resId);
        } else {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_other);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        TextView tvCategoryType;
    }
}
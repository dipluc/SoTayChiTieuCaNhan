package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.CategoryStat;
import com.example.personalexpensemanager.utils.MoneyFormatter;

import java.util.List;
import java.util.Locale;

public class StatCategoryAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryStat> list;
    private LayoutInflater inflater;

    public StatCategoryAdapter(Context context, List<CategoryStat> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<CategoryStat> newList) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_stat_category, parent, false);
            holder = new ViewHolder();
            holder.ivCategoryIcon = convertView.findViewById(R.id.ivCategoryIcon);
            holder.tvCategoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.tvPercent = convertView.findViewById(R.id.tvPercent);
            holder.pbPercent = convertView.findViewById(R.id.pbPercent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryStat stat = list.get(position);
        holder.tvCategoryName.setText(stat.getCatName());
        holder.tvAmount.setText(MoneyFormatter.formatVND(stat.getTotal()));
        holder.tvPercent.setText(String.format(Locale.getDefault(), "%.1f%%", stat.getPercent()));
        holder.pbPercent.setProgress((int) Math.round(stat.getPercent()));

        int resId = 0;
        if (stat.getIconName() != null && !stat.getIconName().isEmpty()) {
            resId = context.getResources().getIdentifier(stat.getIconName(), "drawable", context.getPackageName());
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
        TextView tvAmount;
        TextView tvPercent;
        ProgressBar pbPercent;
    }
}
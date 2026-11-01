package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.MoneyFormatter;

import java.util.List;

public class TransactionAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> list;
    private LayoutInflater inflater;

    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Transaction> newList) {
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
        return list != null && position < list.size() ? list.get(position).getTransId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_transaction, parent, false);
            holder = new ViewHolder();
            holder.ivCategoryIcon = convertView.findViewById(R.id.ivCategoryIcon);
            holder.tvCategoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.tvNote = convertView.findViewById(R.id.tvNote);
            holder.tvTransDate = convertView.findViewById(R.id.tvTransDate);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.ivHasImage = convertView.findViewById(R.id.ivHasImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Transaction t = list.get(position);

        // Category Icon
        int resId = 0;
        if (t.getIconName() != null && !t.getIconName().isEmpty()) {
            resId = context.getResources().getIdentifier(t.getIconName(), "drawable", context.getPackageName());
        }
        if (resId != 0) {
            holder.ivCategoryIcon.setImageResource(resId);
        } else {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_other);
        }

        // Category Name
        holder.tvCategoryName.setText(t.getCatName() != null ? t.getCatName() : "");

        // Note
        if (t.getNote() != null && !t.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Date
        holder.tvTransDate.setText(DateUtils.dbToUiDate(t.getTransDate()));

        // Amount & Type (0 = Chi, 1 = Thu)
        String formattedAmount = MoneyFormatter.formatVND(t.getAmount());
        if (t.getCatType() == 1) { // Thu nhập
            holder.tvAmount.setText("+ " + formattedAmount);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_green));
        } else { // Chi tiêu
            holder.tvAmount.setText("- " + formattedAmount);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_red));
        }

        // Attachment icon
        if (t.getImageUri() != null && !t.getImageUri().trim().isEmpty()) {
            holder.ivHasImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivHasImage.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        TextView tvNote;
        TextView tvTransDate;
        TextView tvAmount;
        ImageView ivHasImage;
    }
}
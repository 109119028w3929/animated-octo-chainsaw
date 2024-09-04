package com.srteam.expensetracker;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;



public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    private List<Income> incomeList;
    private OnItemClickListener listener;
    private OnItemUpdateListener updateListener;
    private OnItemDeleteListener deleteListener;

    public IncomeAdapter(List<Income> incomeList, OnItemClickListener onItemClickListener) {
        this.incomeList = incomeList;
        this.listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemDelete(int position);
    }

    public interface OnItemUpdateListener {
        void onItemUpdate(int position, Income income);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position, Income income);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemUpdateListener(OnItemUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Income income = incomeList.get(position);
        holder.amountTextView.setText(String.format(Locale.getDefault(), "%.2f", income.getAmount()));
        holder.descriptionTextView.setText(income.getDescription());
        holder.dateTextView.setText(income.getDate());

//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
////            public void onClick(View v) {
////                if (deleteListener != null) {
////                    deleteListener.onItemDelete(position, incomeList.get(position));
////                    Toast.makeText(v.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
////                }
////            }
//
//            public void onClick(View v) {
//                if (deleteListener != null) {
//                    deleteListener.onItemDelete(position, incomeList.get(position));
//                    Toast.makeText(v.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(v.getContext(), "Delete listener not set", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amountTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.income_amount);
            descriptionTextView = itemView.findViewById(R.id.income_description);
            dateTextView = itemView.findViewById(R.id.income_date);
//            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

}
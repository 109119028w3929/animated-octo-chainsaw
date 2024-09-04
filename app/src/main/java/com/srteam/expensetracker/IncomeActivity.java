package com.srteam.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class IncomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<Income> incomeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private IncomeAdapter adapter;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.incomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IncomeAdapter(incomeList, new IncomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click
            }

            @Override
            public void onItemDelete(int position) {
                Income income = incomeList.get(position);
                dbHelper.deleteIncome(income);
                incomeList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(adapter);

        addButton = findViewById(R.id.addIncomeButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIncomeDialog();
            }
        });

        loadIncomes();

    }

    private void loadIncomes() {
        incomeList.clear();
        incomeList.addAll(dbHelper.getAllIncomes());
        adapter.notifyDataSetChanged();
    }



    private void showAddIncomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Income");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_income, null);
        final EditText amountEditText = view.findViewById(R.id.amount_edittext);
        final EditText descriptionEditText = view.findViewById(R.id.description_edittext);
        final EditText dateEditText = view.findViewById(R.id.date_edittext);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amountString = amountEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String date = dateEditText.getText().toString();

                if (amountString.isEmpty() || description.isEmpty() || date.isEmpty()) {
                    Toast.makeText(IncomeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("TAG", "onClick:"+amountString+description+date);
                double amount = Double.parseDouble(amountString);
                Income income = new Income(amount, description, date);
                income.setAmount(amount);
                income.setDate(date);
                income.setDescription(description);
                Log.d("TAG", "modelincome"+income.getAmount());
                dbHelper.addIncome(income);
                incomeList.add(income);
                adapter.notifyItemInserted(incomeList.size() - 1);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder.create();
        alert11.show();

//        builder.create().show();
        Button buttonbackground = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonbackground.setBackgroundColor(Color.BLUE);

        Button buttonbg = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonbg.setBackgroundColor(Color.GREEN);
    }



}
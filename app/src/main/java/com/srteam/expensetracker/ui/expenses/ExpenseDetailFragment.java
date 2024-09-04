package com.srteam.expensetracker.ui.expenses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.picasso.Picasso;
import com.srteam.expensetracker.R;
import com.srteam.expensetracker.entities.Expense;
import com.srteam.expensetracker.interfaces.IExpensesType;
import com.srteam.expensetracker.interfaces.IUserActionsMode;
import com.srteam.expensetracker.ui.BaseFragment;
import com.srteam.expensetracker.utils.DateUtils;
import com.srteam.expensetracker.utils.RealmManager;
import com.srteam.expensetracker.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExpenseDetailFragment extends BaseFragment implements View.OnClickListener {

    public static final String EXPENSE_ID_KEY = "_expense_id";
    public static final int RQ_EDIT_EXPENSE = 1001;

    private BarChart bcWeekExpenses;
    private Expense expense;

    static ExpenseDetailFragment newInstance(String id) {
        ExpenseDetailFragment expenseDetailFragment = new ExpenseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXPENSE_ID_KEY, id);
        expenseDetailFragment.setArguments(bundle);
        return expenseDetailFragment;
    }

    public ExpenseDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = onCreateFragmentView(R.layout.fragment_expense_detail, inflater, container, true);
        bcWeekExpenses = (BarChart) rootView.findViewById(R.id.bc_expenses);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXPENSE_ID_KEY)) {
            setTitle(getString(R.string.expense_detail));
            String id = getArguments().getString(EXPENSE_ID_KEY);
            if( id != null) {
                expense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
                loadData();
                setWeekChart();
            }
        }
    }

    private void loadData() {
        TextView tvExpenseTotal = ((TextView)getView().findViewById(R.id.tv_total));
        tvExpenseTotal.setText(Util.getFormattedCurrency(expense.getTotal()));
        tvExpenseTotal.setTextColor(getResources().getColor(expense.getType() == IExpensesType.MODE_EXPENSES ? R.color.colorAccentRed : R.color.colorAccentGreen));
        ((TextView)getView().findViewById(R.id.tv_category)).setText(String.valueOf(expense.getCategory().getName()));
        ((TextView)getView().findViewById(R.id.tv_description)).setText(String.valueOf(expense.getDescription()));
        ((TextView)getView().findViewById(R.id.tv_date)).setText(Util.formatDateToString(expense.getDate(), Util.getCurrentDateFormat()));

        ImageView imgView = ((ImageView)getView().findViewById(R.id.imgView));
        String filePath = expense.getFilepath();
//        String filePath = "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20230221-WA0003.jpg";
        Log.d("TAG", "loadData"+filePath);
//        Picasso.get().load(filePath).into(imgView);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imgView.setImageBitmap(bitmap);

//       File imgFile = new  File(filePath);
//        if(imgFile.exists()){
//            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imgView.setImageBitmap(bitmap);
//        }

        (getView().findViewById(R.id.fab_edit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_edit) {
            onEditExpense();
        }
    }

    private void onEditExpense() {
        NewExpenseFragment newExpenseFragment = NewExpenseFragment.newInstance(IUserActionsMode.MODE_UPDATE, expense.getId());
        newExpenseFragment.setTargetFragment(this, RQ_EDIT_EXPENSE);
        newExpenseFragment.show(getFragmentManager(), "EDIT_EXPENSE");
    }

    private void setWeekChart() {
        List<Date> dateList = DateUtils.getWeekDates();
        List<String> days = new ArrayList<>();
        Collections.sort(dateList);

        List<BarEntry> entriesPerDay = new ArrayList<>();

        for (int i=0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            String day = Util.formatDateToString(date, "EEE");
            float value = Expense.getCategoryTotalByDate(date, expense.getCategory());
            days.add(day);
            entriesPerDay.add(new BarEntry(value, i));
        }
        BarDataSet dataSet = new BarDataSet(entriesPerDay, getString(R.string.this_week));
        dataSet.setColors(Util.getListColors());
        BarData barData = new BarData(days, dataSet);
        bcWeekExpenses.setVisibleXRangeMaximum(5);
        bcWeekExpenses.getAxisLeft().setDrawGridLines(false);
        bcWeekExpenses.getXAxis().setDrawGridLines(false);
        bcWeekExpenses.getAxisRight().setDrawGridLines(false);
        bcWeekExpenses.getAxisRight().setDrawLabels(false);
        bcWeekExpenses.setData(barData);
        bcWeekExpenses.setDescription("");
        bcWeekExpenses.animateY(2000);
        bcWeekExpenses.invalidate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_EDIT_EXPENSE && resultCode == Activity.RESULT_OK) {
            loadData();
            bcWeekExpenses.notifyDataSetChanged();
            bcWeekExpenses.invalidate();
            setWeekChart();
        }
    }

}
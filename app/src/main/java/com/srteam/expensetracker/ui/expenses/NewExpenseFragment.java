package com.srteam.expensetracker.ui.expenses;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.srteam.expensetracker.R;
import com.srteam.expensetracker.adapters.CategoriesSpinnerAdapter;
import com.srteam.expensetracker.entities.Category;
import com.srteam.expensetracker.entities.Expense;
import com.srteam.expensetracker.interfaces.IExpensesType;
import com.srteam.expensetracker.interfaces.IUserActionsMode;
import com.srteam.expensetracker.utils.DateUtils;
import com.srteam.expensetracker.utils.DialogManager;
import com.srteam.expensetracker.utils.RealmManager;
import com.srteam.expensetracker.utils.Util;
import com.srteam.expensetracker.widget.ExpensesWidgetProvider;
import com.srteam.expensetracker.widget.ExpensesWidgetService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewExpenseFragment extends DialogFragment implements View.OnClickListener{

    private TextView tvTitle;
    private Button btnDate;
    private Spinner spCategory;
    private EditText etDescription;
    private EditText etTotal;
    private static final int SELECT_FILE_REQUEST_CODE = 1234;

    private CategoriesSpinnerAdapter mCategoriesSpinnerAdapter;
    private Date selectedDate;
    private Expense mExpense;
    String filePath;

    private @IUserActionsMode int mUserActionMode;
    private @IExpensesType int mExpenseType;

    static NewExpenseFragment newInstance(@IUserActionsMode int mode, String expenseId) {
        NewExpenseFragment newExpenseFragment = new NewExpenseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IUserActionsMode.MODE_TAG, mode);
        if (expenseId != null) bundle.putString(ExpenseDetailFragment.EXPENSE_ID_KEY, expenseId);
        newExpenseFragment.setArguments(bundle);
        return newExpenseFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_new_expense, container, false);
        tvTitle = (TextView)rootView.findViewById(R.id.tv_title);
        btnDate = (Button)rootView.findViewById(R.id.btn_date);
        spCategory = (Spinner)rootView.findViewById(R.id.sp_categories);
        etDescription = (EditText)rootView.findViewById(R.id.et_description);
        etTotal = (EditText)rootView.findViewById(R.id.et_total);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button invoiceBtn = (Button)rootView.findViewById(R.id.invoiceBtn);
        invoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
            }
        });

        mExpenseType = IExpensesType.MODE_EXPENSES;
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE_REQUEST_CODE) {
                Uri selectedFileUri = data.getData();
                filePath = getRealPathFromURI(selectedFileUri);
                Log.d("TAG", "imageinvoice "+filePath);
                // You can now upload the file to your SQLite database using the filePath
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mUserActionMode = getArguments().getInt(IUserActionsMode.MODE_TAG) == IUserActionsMode.MODE_CREATE ? IUserActionsMode.MODE_CREATE : IUserActionsMode.MODE_UPDATE;
        }
        setModeViews();
        btnDate.setOnClickListener(this);
        (getView().findViewById(R.id.btn_cancel)).setOnClickListener(this);
        (getView().findViewById(R.id.btn_save)).setOnClickListener(this);
    }

    private void setModeViews() {
        List<Category> categoriesList = Category.getCategoriesExpense();
        Category[] categoriesArray = new Category[categoriesList.size()];
        categoriesArray = categoriesList.toArray(categoriesArray);
        mCategoriesSpinnerAdapter = new CategoriesSpinnerAdapter(getActivity(), categoriesArray);
        spCategory.setAdapter(mCategoriesSpinnerAdapter);
        switch (mUserActionMode) {
            case IUserActionsMode.MODE_CREATE:
                selectedDate = new Date();
                break;
            case IUserActionsMode.MODE_UPDATE:
                if (getArguments() != null) {
                    String id = getArguments().getString(ExpenseDetailFragment.EXPENSE_ID_KEY);
                    mExpense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
                    tvTitle.setText("Edit");
                    selectedDate = mExpense.getDate();
                    etDescription.setText(mExpense.getDescription());
                    etTotal.setText(String.valueOf(mExpense.getTotal()));
                    int categoryPosition = 0;
                    for (int i=0; i<categoriesArray.length; i++) {
                        if (categoriesArray[i].getId().equalsIgnoreCase(mExpense.getCategory().getId())) {
                            categoryPosition = i;
                            break;
                        }
                    }
                    spCategory.setSelection(categoryPosition);
                }
                break;
        }
        updateDate();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_date) {
            showDateDialog();
        } else if (view.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (view.getId() == R.id.btn_save) {
            onSaveExpense();
        }
    }

    private void onSaveExpense() {
        if (mCategoriesSpinnerAdapter.getCount() > 0 ) {
            if (!Util.isEmptyField(etTotal)) {
                Category currentCategory = (Category) spCategory.getSelectedItem();
                String total = etTotal.getText().toString();
                String description = etDescription.getText().toString();
                Log.d("TAG", "filepathsave"+filePath);
                if (mUserActionMode == IUserActionsMode.MODE_CREATE) {
                    RealmManager.getInstance().save(new Expense(description, selectedDate, mExpenseType, currentCategory, Float.parseFloat(total),filePath), Expense.class);
                } else {
                    Expense editExpense = new Expense();
                    editExpense.setId(mExpense.getId());
                    editExpense.setTotal(Float.parseFloat(total));
                    editExpense.setDescription(description);
                    editExpense.setCategory(currentCategory);
                    editExpense.setDate(selectedDate);
                    editExpense.setFilepath(filePath);
                    RealmManager.getInstance().update(editExpense);
                }
                // update widget if the expense is created today
                if (DateUtils.isToday(selectedDate)) {
                    Intent i = new Intent(getActivity(), ExpensesWidgetProvider.class);
                    i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
                    getActivity().sendBroadcast(i);
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, null);
                dismiss();
            } else {
                DialogManager.getInstance().showShortToast(getString(R.string.error_total));
            }
        } else {
            DialogManager.getInstance().showShortToast(getString(R.string.no_categories_error));
        }
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                selectedDate = calendar.getTime();
                updateDate();
            }
        }, calendar);
    }

    private void updateDate() {
        btnDate.setText(Util.formatDateToString(selectedDate, Util.getCurrentDateFormat()));
    }

}

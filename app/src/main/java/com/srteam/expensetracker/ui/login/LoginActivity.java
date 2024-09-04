package com.srteam.expensetracker.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.srteam.expensetracker.ExpenseTrackerApp;
import com.srteam.expensetracker.R;
import com.srteam.expensetracker.ui.BaseActivity;
import com.srteam.expensetracker.ui.MainActivity;
import com.srteam.expensetracker.ui.SplashActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean logged = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getBoolean(getString(R.string.already_accepted_user_key), false);
        if (logged) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

}

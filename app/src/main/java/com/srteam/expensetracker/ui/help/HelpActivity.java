package com.srteam.expensetracker.ui.help;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.srteam.expensetracker.R;
import com.srteam.expensetracker.ui.BaseActivity;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar(toolbar);
    }

}

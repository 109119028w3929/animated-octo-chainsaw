package com.srteam.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.srteam.expensetracker.ui.MainActivity;
import com.srteam.expensetracker.ui.login.LoginActivity;

public class signinActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin,btn_register;
    DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editTextUsername = findViewById(R.id.edittext_username);
        editTextPassword = findViewById(R.id.edittext_password);
        buttonLogin = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                boolean isValid = databaseHelper.checkUser(username, password);

                if (isValid) {

                    editor.putString("username", username);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(signinActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signinActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(signinActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signinActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
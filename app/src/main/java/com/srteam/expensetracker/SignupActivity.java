package com.srteam.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.srteam.expensetracker.ui.MainActivity;
import com.srteam.expensetracker.ui.login.LoginActivity;


public class SignupActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonRegister,btn_login;
    DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.edittext_username);
        editTextEmail = findViewById(R.id.edittext_email);
        editTextPassword = findViewById(R.id.edittext_password);
        buttonRegister = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editTextUsername.getText())) {
                    editTextUsername.setError("User Name is required");
                } else if (TextUtils.isEmpty(editTextEmail.getText())) {
                    editTextEmail.setError("Email is required");
                } else if (TextUtils.isEmpty(editTextPassword.getText())) {
                    editTextPassword.setError("Password is required");
                } else {
                    // Save user data to database
                    String username = editTextUsername.getText().toString();
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();

                    long id = databaseHelper.addUser(username, email, password);

                    if (id > 0) {

                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, signinActivity.class);
                startActivity(intent);
            }
        });
    }
}

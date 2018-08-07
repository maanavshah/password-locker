package com.developer.maanavshah.locker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class
LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "admin";
    public static String password;
    private static boolean visibility;
    EditText etPassword;
    ImageButton bVisible;
    Button bConfirm;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        password = prefs.getString(PREFS_NAME, "default_value");
        if (password.equals("default_value"))
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

        etPassword = (EditText) findViewById(R.id.etPassword);
        bVisible = (ImageButton) findViewById(R.id.bVisible);
        bConfirm = (Button) findViewById(R.id.bConfirm);
        visibility = false;

        bVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().matches("")) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (visibility) {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    bVisible.setBackgroundResource(R.drawable.show);
                    visibility = false;
                } else {
                    etPassword.setTransformationMethod(null);
                    bVisible.setBackgroundResource(R.drawable.hide);
                    visibility = true;
                }
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String password = prefs.getString(PREFS_NAME, "default_value");
                if (etPassword.getText().toString().matches("")) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().matches(password)) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    etPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
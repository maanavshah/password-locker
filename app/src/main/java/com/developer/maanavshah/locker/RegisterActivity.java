package com.developer.maanavshah.locker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "admin";
    public static boolean aboutStatus = false;
    EditText etPassA, etPassB;
    Button bRegister;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPassA = (EditText) findViewById(R.id.etPassA);
        etPassB = (EditText) findViewById(R.id.etPassB);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassA.getText().toString().matches("")) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassB.getText().toString().matches("")) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!etPassA.getText().toString().matches(etPassB.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    etPassA.setText("");
                    etPassB.setText("");
                    return;
                }
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("admin", etPassA.getText().toString());
                editor.commit();
                LoginActivity.password = etPassA.getText().toString();
                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bAboutUs:
                aboutUs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void aboutUs() {
        aboutStatus = true;
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
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
package com.developer.maanavshah.locker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ChangeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "admin";
    public static boolean aboutStatus = false;
    EditText etPassA, etPassB;
    Button bRegister;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        etPassA = (EditText) findViewById(R.id.etPassA);
        etPassB = (EditText) findViewById(R.id.etPassB);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassA.getText().toString().matches("")) {
                    Toast.makeText(ChangeActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassB.getText().toString().matches("")) {
                    Toast.makeText(ChangeActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!etPassA.getText().toString().matches(etPassB.getText().toString())) {
                    Toast.makeText(ChangeActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    etPassA.setText("");
                    etPassB.setText("");
                    return;
                }
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("admin", etPassA.getText().toString());
                editor.apply();
                LoginActivity.password = etPassA.getText().toString();
                try {
                    FileActivity.sourcePath = FileActivity.staticFile.getAbsolutePath();
                    backupTempDatabase();
                    restoreDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void backupTempDatabase() {
        String currentDBPath = Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME;
        String backupDBPath = FileActivity.root + "/temp.db";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(backupDBPath);
        try {
            //backupDB.createNewFile();
            FileChannel source = new FileInputStream(currentDB).getChannel();
            FileChannel destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreDatabase() {
        File oldDB = new File(FileActivity.sourcePath);
        //File DB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases");
        File newDB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME);
        /*if (!DB.exists()) {
            DB.mkdir();
            try {
                newDB.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (newDB.exists()) try {
            FileChannel fromChannel = new FileInputStream(oldDB).getChannel();
            FileChannel toChannel = new FileOutputStream(newDB).getChannel();
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
            fromChannel.close();
            toChannel.close();
            Toast.makeText(this, "Importing database", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Intent i = new Intent(getApplicationContext(), AboutActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
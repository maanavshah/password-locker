package com.developer.maanavshah.locker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class DisplayActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "admin";
    ListView listView;
    EditText oldPassword, newPassword, retypePassword;
    Button changePassword, cancelPassword;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Important: Must call this function before calling any SQLCipher functions
        SQLiteDatabase.loadLibs(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fbInsert);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InsertActivity.class));
            }
        });

        Helper db = new Helper(this);
        Cursor result;     // ListView sorted according to Website
        result = db.getSortedData();
        String[] columns = new String[]{Helper.USERNAME, Helper.PASSWORD, Helper.WEBSITE};
        int[] to = new int[]{R.id.tvUser, R.id.tvPass, R.id.tvWebsite};
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.content_info, result, columns, to, 0);
        listView = (ListView) findViewById(R.id.lvLogin);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String curId = cursor.getString(cursor.getColumnIndexOrThrow(Helper.ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(Helper.USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(Helper.PASSWORD));
                String website = cursor.getString(cursor.getColumnIndexOrThrow(Helper.WEBSITE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(Helper.NOTE));
                Intent intent = new Intent(getBaseContext(), UpdateActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("website", website);
                intent.putExtra("note", note);
                intent.putExtra("id", curId);
                startActivity(intent);
            }
        });

        checkImportFail(); //Check if import failed, as master-password did not match
    }

    private void checkImportFail() {
        if (FileActivity.importError) {
            new AlertDialog.Builder(DisplayActivity.this)
                    .setTitle("Could not import")
                    .setMessage("Change current Master-Password to Master-Password of the database to be imported")
                    .setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            }).show();
            FileActivity.importError = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bExit:
                finish();
                System.exit(0);
            case R.id.bDatabase:
                database();
                return true;
            case R.id.bAbout:
                aboutUs();
                return true;
            case R.id.bChange:
                changePassword();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void database() {
        startActivity(new Intent(getApplicationContext(), FileActivity.class));
    }

    private void aboutUs() {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }

    private void changePassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_password, null);
        dialogBuilder.setView(dialogView);

        oldPassword = (EditText) dialogView.findViewById(R.id.etPassOld);
        newPassword = (EditText) dialogView.findViewById(R.id.etPassNew1);
        retypePassword = (EditText) dialogView.findViewById(R.id.etPassNew2);
        changePassword = (Button) dialogView.findViewById(R.id.bNewPassword);
        cancelPassword = (Button) dialogView.findViewById(R.id.bPassCancel);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassword.getText().toString().matches("")) {
                    Toast.makeText(DisplayActivity.this, "Enter Old Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.getText().toString().matches("")) {
                    Toast.makeText(DisplayActivity.this, "Enter New Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (retypePassword.getText().toString().matches("")) {
                    Toast.makeText(DisplayActivity.this, "Retype New Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.getText().toString().matches(retypePassword.getText().toString())) {
                    Toast.makeText(DisplayActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    oldPassword.setText("");
                    newPassword.setText("");
                    retypePassword.setText("");
                    return;
                }
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String password = prefs.getString(PREFS_NAME, "default_value");
                if (oldPassword.getText().toString().matches(password)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("admin", newPassword.getText().toString());
                    editor.commit();
                    Toast.makeText(DisplayActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    oldPassword.setText("");
                    newPassword.setText("");
                    retypePassword.setText("");
                    Toast.makeText(DisplayActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
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
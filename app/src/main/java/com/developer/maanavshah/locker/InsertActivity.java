package com.developer.maanavshah.locker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class InsertActivity extends AppCompatActivity {

    EditText etWebsite, etUser, etPass, etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        etWebsite = (EditText) findViewById(R.id.etWebsite);
        etUser = (EditText) findViewById(R.id.etUser);
        etPass = (EditText) findViewById(R.id.etPass);
        etNote = (EditText) findViewById(R.id.etNote);

    }

    private void insert() {
        final Helper db = new Helper(this);
        if (etWebsite.getText().toString().matches("")) {
            Toast.makeText(InsertActivity.this, "Title Empty", Toast.LENGTH_SHORT).show();
        } else if (etUser.getText().toString().matches("")) {
            Toast.makeText(InsertActivity.this, "Username Empty", Toast.LENGTH_SHORT).show();
        } else if (etPass.getText().toString().matches("")) {
            Toast.makeText(InsertActivity.this, "Password Empty", Toast.LENGTH_SHORT).show();
        } else {
            boolean flag = db.insertData(etWebsite.getText().toString(), etUser.getText().toString(), etPass.getText().toString(), etNote.getText().toString());
            if (flag)
                Toast.makeText(InsertActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(InsertActivity.this, "Insertion Failed", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bInsert:
                insert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.developer.maanavshah.locker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {
    TextView user, pass, website, note;
    String Id, User, Pass, Website, Note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        user = (TextView) findViewById(R.id.upUser);
        pass = (TextView) findViewById(R.id.upPass);
        website = (TextView) findViewById(R.id.upWebsite);
        note = (TextView) findViewById(R.id.upNote);

        User = getIntent().getStringExtra("username");
        Pass = getIntent().getStringExtra("password");
        Website = getIntent().getStringExtra("website");
        Note = getIntent().getStringExtra("note");
        Id = getIntent().getStringExtra("id");

        user.setText(User);
        pass.setText(Pass);
        website.setText(Website);
        note.setText(Note);

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
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bUpdate:
                update();
                return true;
            case R.id.bDelete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        new AlertDialog.Builder(UpdateActivity.this)
                .setTitle("Delete")
                .setMessage("Are you sure want to delete this record?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper helper = new Helper(UpdateActivity.this);
                                if (helper.deleteData(Id))
                                    Toast.makeText(UpdateActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void update() {
        Helper helper = new Helper(UpdateActivity.this);
        if (website.getText().toString().matches("")) {
            Toast.makeText(UpdateActivity.this, "Title Empty", Toast.LENGTH_SHORT).show();
        } else if (user.getText().toString().matches("")) {
            Toast.makeText(UpdateActivity.this, "Username Empty", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().matches("")) {
            Toast.makeText(UpdateActivity.this, "Password Empty", Toast.LENGTH_SHORT).show();
        } else {
            helper.updateData(Id, user.getText().toString(), pass.getText().toString(), website.getText().toString(), note.getText().toString());
            Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

}

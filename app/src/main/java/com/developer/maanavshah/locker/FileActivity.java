package com.developer.maanavshah.locker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    public static boolean importError = false;
    public static String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static File staticFile;
    public static String sourcePath;
    ListView listView;
    private List<String> path = null;
    private TextView myPath;
    private String folderPath;

    public static void restoreTempDatabase() throws IOException {
        File oldDB = new File(root + "/temp.db");
        File DB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases");
        File newDB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME);
        if (!DB.exists()) {
            DB.mkdir();
            newDB.createNewFile();
        }
        if (newDB.exists()) try {
            FileChannel fromChannel = new FileInputStream(oldDB).getChannel();
            FileChannel toChannel = new FileOutputStream(newDB).getChannel();
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
            fromChannel.close();
            toChannel.close();
            oldDB.delete();
            importError = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        myPath = (TextView) findViewById(R.id.path);
        getDir(root);
    }

    private void getDir(String dirPath) {
        myPath.setText(dirPath);
        folderPath = dirPath;
        List<String> item = new ArrayList<>();
        path = new ArrayList<>();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            item.add("../");
            path.add(f.getParent());
        }
        for (File file : files) {
            if (file.isDirectory()) {
                item.add(file.getName() + "/");
                path.add(file.getPath());
            } else if (file.toString().endsWith(".db")) {
                item.add(file.getName());
                path.add(file.getPath());
            }
        }
        ArrayAdapter<String> fileList =
                new ArrayAdapter<>(this, R.layout.content_file, item);
        Collections.sort(item);
        Collections.sort(path);
        fileList.notifyDataSetChanged();
        listView = (ListView) findViewById(R.id.lvFile);
        listView.setAdapter(fileList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                final File file = new File(path.get(position));
                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(path.get(position));
                    else {
                        new AlertDialog.Builder(FileActivity.this)
                                .setTitle(file.getName())
                                .setMessage("Sorry, folder can't be read!")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                    }
                } else {
                    new AlertDialog.Builder(FileActivity.this)
                            .setTitle(file.getName()).setMessage("Are you sure want to import this file?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            new AlertDialog.Builder(FileActivity.this)
                                                    .setTitle(file.getName()).setMessage("Is current Master-password set to Master-password of database to be imported?")
                                                    .setPositiveButton("Yes",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    try {
                                                                        sourcePath = file.getAbsolutePath();
                                                                        backupTempDatabase();
                                                                        restoreDatabase();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                        Log.d("File", "Error");
                                                                    }
                                                                }
                                                            })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            staticFile = file;
                                                            Intent i = new Intent(getApplicationContext(), ChangeActivity.class);
                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(i);
                                                        }
                                                    }).show();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bBackup:
                backupButton();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backupButton() {
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("Important")
                .setMessage("Current Master-password will be required for importing this database")
                .setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }).show();
        backupConfirm();
    }

    private void backupConfirm() {
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("Export database")
                .setMessage("Are you sure want to export database to " + folderPath + "?")
                .setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    backupDatabase();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void backupDatabase() {
        String currentDBPath = Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String backupDBPath = folderPath + "/Locker_" + dateFormat.format(date) + ".db";
        File directory = new File(folderPath);
        if (!directory.exists())
            directory.mkdir();
        File currentDB = new File(currentDBPath);
        File backupDB = new File(backupDBPath);
        try {
            backupDB.createNewFile();
            FileChannel source = new FileInputStream(currentDB).getChannel();
            FileChannel destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Exported database to " + folderPath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backupTempDatabase() {
        String currentDBPath = Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME;
        String backupDBPath = root + "/temp.db";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(backupDBPath);
        try {
            backupDB.createNewFile();
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
        File oldDB = new File(sourcePath);
        File DB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases");
        File newDB = new File(Environment.getDataDirectory() + "/data/com.developer.maanavshah.locker" + "/databases/" + Helper.DATABASE_NAME);
        if (!DB.exists()) {
            DB.mkdir();
            try {
                newDB.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (newDB.exists()) try {
            FileChannel fromChannel = new FileInputStream(oldDB).getChannel();
            FileChannel toChannel = new FileOutputStream(newDB).getChannel();
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
            fromChannel.close();
            toChannel.close();
            Toast.makeText(this, "Importing database", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}

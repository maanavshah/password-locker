package com.developer.maanavshah.locker;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.IOException;

public class Helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "locker.db";
    public static final String TABLE_NAME = "login";
    public static final String ID = "_id";
    public static final String WEBSITE = "website";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NOTE = "note";

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS login( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT," +
                PASSWORD + " TEXT, " + WEBSITE + " TEXT, " + NOTE + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String website, String username, String password, String note) {
        SQLiteDatabase db = this.getWritableDatabase(LoginActivity.password);
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(PASSWORD, password);
        contentValues.put(WEBSITE, website);
        contentValues.put(NOTE, note);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase(LoginActivity.password);
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return result;
    }

    public Cursor getSortedData() {
        try {
            Log.d("Password", LoginActivity.password);
            SQLiteDatabase db = this.getReadableDatabase(LoginActivity.password);
            Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + WEBSITE, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                FileActivity.restoreTempDatabase();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    public boolean deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase(LoginActivity.password);
        //return db.delete(TABLE_NAME, ID + "=" + id, null) > 0;
        return db.delete(TABLE_NAME, ID + " = ?", new String[]{id}) > 0;
    }

    public void updateData(String id, String user, String pass, String website, String note) {
        SQLiteDatabase db = this.getWritableDatabase(LoginActivity.password);
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, user);
        cv.put(PASSWORD, pass);
        cv.put(WEBSITE, website);
        cv.put(NOTE, note);
        db.update(TABLE_NAME, cv, ID + "=" + id, null);
    }
}

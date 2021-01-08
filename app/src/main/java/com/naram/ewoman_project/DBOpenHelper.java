package com.naram.ewoman_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class DBOpenHelper {

    private static final String TAG = "DBOpenHelper";

    private static final String DATABASE_NAME = "userInfo.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private Drawable image;

    private class DatabaseHelper extends SQLiteOpenHelper {

//        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//            super(context, name, factory, version);
//        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(UserDatabases.UserDB.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(UserDatabases.UserDB.SQL_DROP_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }

    public DBOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DBOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    public void drop() {
        mDB.execSQL("DROP TABLE IF EXISTS " + UserDatabases.UserDB.TABLE_NAME + ";");
    }

    public void close() {
        mDB.close();
    }

    public long insertColumn(String email, String name) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserDatabases.UserDB.EMAIL, email);
        values.put(UserDatabases.UserDB.NAME, name);

        return mDB.insert(UserDatabases.UserDB.TABLE_NAME, null, values);
    }

//    public Cursor selectColumns() {
//        return mDB.query(UserDatabases.UserDB.TABLE_NAME, null, null, null, null, null, null);
//    }

    public boolean updateColumn(long id, String email, String name, @Nullable Drawable image) {
        ContentValues values = new ContentValues();
        values.put(UserDatabases.UserDB.EMAIL, email);
        values.put(UserDatabases.UserDB.NAME, name);

        return mDB.update(UserDatabases.UserDB.TABLE_NAME, values, "_id=" + id, null) > 0;
    }

    public void deleteAllColumns() {
        mDB.delete(UserDatabases.UserDB.TABLE_NAME, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id) {
        return mDB.delete(UserDatabases.UserDB.TABLE_NAME, "_id=" + id, null) > 0;
    }

    // sort by column
    public Cursor sortColumn(String sort) {
        Cursor c = mDB.rawQuery("SELECT _id, email, name FROM " + UserDatabases.UserDB.TABLE_NAME + " ORDER BY " + sort + " DESC;", null);
        return c;
    }

    public Cursor selectColumns() {
        Cursor c = mDB.rawQuery("SELECT email, name FROM " + UserDatabases.UserDB.TABLE_NAME, null);
        return c;
    }

    public Cursor selectColumn(long id) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + UserDatabases.UserDB.TABLE_NAME + " WHERE _id=" + id + ";", null);
        return c;
    }

}

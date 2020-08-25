package com.naram.ewoman_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.SQLException;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.core.view.ViewPropertyAnimatorListener;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

public class DBOpenHelper {

    private static final String TAG = "DBOpenHelper";

    private static final String DATABASE_NAME = "ReviewBoard.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

//        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//            super(context, name, factory, version);
//        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.ReviewDB.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(Databases.ReviewDB.SQL_DROP_TABLE);
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

    public void close() {
        mDB.close();
    }

    public long insertColumn(String title, String userid, String name, String content) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.USERID, userid);
        values.put(Databases.ReviewDB.NAME, name);
        values.put(Databases.ReviewDB.CONTENT, content);
        // SQLite 이미지 저장 코드 추가 필요
        return mDB.insert(Databases.ReviewDB.TABLE_NAME, null, values);
    }

    public long insertColumn_withImage(String title, String userid, String name, String content, Drawable image) {

        byte[] byteimage = getByteArrayFromDrawable(image);

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.USERID, userid);
        values.put(Databases.ReviewDB.NAME, name);
        values.put(Databases.ReviewDB.CONTENT, content);
        values.put(Databases.ReviewDB.IMAGE, byteimage);
        // SQLite 이미지 저장 코드 추가 필요
        return mDB.insert(Databases.ReviewDB.TABLE_NAME, null, values);
    }


    public Cursor selectColumns() {
        return mDB.query(Databases.ReviewDB.TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean updateColumn(long id, String title, String content) {
        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.CONTENT, content);
        return mDB.update(Databases.ReviewDB.TABLE_NAME, values, "_id=" + id, null) > 0;
    }

    public boolean updateColumn_withImage(long id, String title, String content, Drawable image) {

        byte[] byteimage = getByteArrayFromDrawable(image);

        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.CONTENT, content);
        values.put(Databases.ReviewDB.IMAGE, byteimage);
        return mDB.update(Databases.ReviewDB.TABLE_NAME, values, "_id=" + id, null) > 0;
    }

    public void deleteAllColumns() {
        mDB.delete(Databases.ReviewDB.TABLE_NAME, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id) {
        return mDB.delete(Databases.ReviewDB.TABLE_NAME, "_id=" + id, null) > 0;
    }

    // sort by column
    public Cursor sortColumn(String sort) { // 최신글이 더 위로 올라오도록
        Cursor c = mDB.rawQuery("SELECT _id, title, userid, name, like, content FROM " + Databases.ReviewDB.TABLE_NAME + " ORDER BY " + sort + " DESC;", null);
        return c;
    }

    public Cursor selectColumn(long id) {
        Cursor c = mDB.rawQuery("SELECT title, name, userid, like, content  FROM " + Databases.ReviewDB.TABLE_NAME + " WHERE _id=" + id + ";", null);
        return c;
    }

    public Bitmap selectColumn_Image(long id) {
        Cursor c = mDB.rawQuery("SELECT image FROM " + Databases.ReviewDB.TABLE_NAME + " WHERE _id=" + id + ";", null);
        Bitmap bitmap = null;

        while (c.moveToNext()) {
            byte[] b_image = c.getBlob(c.getColumnIndex("image"));

            bitmap = getBitmapFromByteArray(b_image);
        }

        return bitmap;
    }

    public byte[] getByteArrayFromDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }

    public Bitmap getBitmapFromByteArray(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        return bitmap;
    }

}

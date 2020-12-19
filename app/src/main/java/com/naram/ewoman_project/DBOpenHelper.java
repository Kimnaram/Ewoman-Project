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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewPropertyAnimatorListener;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DBOpenHelper {

    private static final String TAG = "DBOpenHelper";

    private static final String DATABASE_NAME = "ReviewBoard.db";
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

    public void drop() {
        mDB.execSQL("DROP TABLE IF EXISTS " + Databases.ReviewDB.TABLE_NAME + ";");
    }


    public void close() {
        mDB.close();
    }

    public Cursor selectIDColumn() {
        Cursor c = mDB.rawQuery("SELECT max(_id) AS postid FROM " + Databases.ReviewDB.TABLE_NAME + ";",null);
        return c;
    }

    public long insertColumn(String title, String userid, String name, String content, @Nullable Drawable image) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.USERID, userid);
        values.put(Databases.ReviewDB.NAME, name);
        values.put(Databases.ReviewDB.CONTENT, content);
        // SQLite 이미지 저장 코드 추가 필요

        if(image != null) {
            values.put(Databases.ReviewDB.IMAGE, "Y");
            insertImageColumns(image);
        }

        return mDB.insert(Databases.ReviewDB.TABLE_NAME, null, values);
    }

    public void insertImageColumns(Drawable image) {

        // firebase storage에 이미지를 저장하는 코드
        byte[] byteimage = getByteArrayFromDrawable(image);
        int _id = 0;

        Cursor c = selectIDColumn();
        while(c.moveToNext()) {
            _id = c.getInt(c.getColumnIndex("postid"));
        }

        _id += 1;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("board");
        StorageReference boardRef = storageRef.child(_id + ".png");

        UploadTask uploadTask = boardRef.putBytes(byteimage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d(TAG, "저장 완료");
            }
        });

    }

    public Cursor selectColumns() {
        return mDB.query(Databases.ReviewDB.TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean updateColumn(long id, String title, String content, @Nullable Drawable image) {
        ContentValues values = new ContentValues();
        values.put(Databases.ReviewDB.TITLE, title);
        values.put(Databases.ReviewDB.CONTENT, content);

        if(image != null) {
            values.put(Databases.ReviewDB.IMAGE, "Y");
            updateImageColumns(id, image);
        }

        return mDB.update(Databases.ReviewDB.TABLE_NAME, values, "_id=" + id, null) > 0;
    }

    public void updateImageColumns(long _id, Drawable image) {

        // firebase storage에 이미지를 저장하는 코드
        byte[] byteimage = getByteArrayFromDrawable(image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("board");
        if (storageRef.child("" + _id).getDownloadUrl() != null) {
            storageRef.child("" + _id).delete();
        }
        StorageReference boardRef = storageRef.child(_id + ".png");

        UploadTask uploadTask = boardRef.putBytes(byteimage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d(TAG, "저장 완료");
            }
        });
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
        Cursor c = mDB.rawQuery("SELECT * FROM " + Databases.ReviewDB.TABLE_NAME + " WHERE _id=" + id + ";", null);
        return c;
    }

    public Drawable selectImageColumns(long id) {

        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference("board");
        StorageReference imagePath = storageReference.child(id + ".png");

        imagePath.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                image = Drawable.createFromStream(byteArrayInputStream, "Review Image");

            }
        });

        return image;
    }

    public byte[] getByteArrayFromDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
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

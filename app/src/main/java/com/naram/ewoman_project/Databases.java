package com.naram.ewoman_project;

import android.provider.BaseColumns;


public final class Databases {

    /* Inner class that defines the table contents */
    public static final class ReviewDB implements BaseColumns {
        public static final String TABLE_NAME = "Reviews";
        public static final String TITLE = "title";
        public static final String USERID = "userid";
        public static final String NAME = "name";
        public static final String CONTENT = "content";
        public static final String LIKE = "like";
        public static final String IMAGE = "image";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + ReviewDB.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewDB.TITLE + " TEXT NOT NULL, " +
                        ReviewDB.USERID + " TEXT NOT NULL, " +
                        ReviewDB.NAME + " TEXT NOT NULL, " +
                        ReviewDB.CONTENT + " TEXT NOT NULL, " +
                        ReviewDB.LIKE + " INTEGER DEFAULT 0, " +
                        ReviewDB.IMAGE + " BLOB)";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + ReviewDB.TABLE_NAME;
    }

}

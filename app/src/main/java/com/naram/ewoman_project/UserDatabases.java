package com.naram.ewoman_project;

import android.provider.BaseColumns;


public final class UserDatabases {

    /* Inner class that defines the table contents */
    public static final class UserDB implements BaseColumns {
        public static final String TABLE_NAME = "userInfo";
        public static final String EMAIL = "email";
        public static final String NAME = "name";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + UserDB.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        UserDB.EMAIL + " TEXT NOT NULL, " +
                        UserDB.NAME + " TEXT NOT NULL)";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE " + UserDB.TABLE_NAME + ";";
    }

}

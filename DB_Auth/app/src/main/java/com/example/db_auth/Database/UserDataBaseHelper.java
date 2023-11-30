package com.example.db_auth.Database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserDataBaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME="Users.db";
    private static String TABLE_NAME="User";
    private static String USER_NAME = "USER_NAME";
    private static String EMAIL = "EMAIL";
    private static String ADDRESS="ADDRESS";
    private static String PHONE_NUMBER="PHONE_NUMBER";
    private static String GENDER="GENDER";

    public UserDataBaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+ TABLE_NAME + "" + "()");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

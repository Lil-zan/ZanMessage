package com.java.zanmessage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ContactsSQLiteOpenHelper extends SQLiteOpenHelper {
    public ContactsSQLiteOpenHelper(Context context) {
        this(context, "contacts.db", null, 1);
    }

    private ContactsSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table t_contacts(_id integer primary key,username varchar(20),contact varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

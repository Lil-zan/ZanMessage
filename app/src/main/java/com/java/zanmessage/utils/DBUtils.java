package com.java.zanmessage.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.java.zanmessage.db.ContactsSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    public static Context mContext;

    public static void initDBUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public static List<String> getContactFromDB(String username) {
        //如果没有获得上下文抛出一个异常
        if (mContext == null) throw new RuntimeException("缺失上下文,请在Application中初始化DBUtils");

        List<String> contactList = new ArrayList<>();

        ContactsSQLiteOpenHelper contactsSQLiteOpenHelper = new ContactsSQLiteOpenHelper(mContext);
        SQLiteDatabase readableDatabase = contactsSQLiteOpenHelper.getReadableDatabase();
        Cursor query = readableDatabase.query(Contant.CONTACTS_TABLE_NAME, new String[]{"contact"}, "username=?", new String[]{username}, null, null, "contact");
        while (query != null && query.moveToNext()) {
            String contact = query.getString(0);
            contactList.add(contact);
        }
        query.close();
        readableDatabase.close();
        return contactList;
    }

    //更新联系人
    public static void updateContact(String username, List<String> contactList) {
        SQLiteDatabase database = new ContactsSQLiteOpenHelper(mContext).getReadableDatabase();
        database.beginTransaction();
        database.delete(Contant.CONTACTS_TABLE_NAME, "username = ?", new String[]{username});
        ContentValues values = new ContentValues();
        values.put("username", username);
        for (int i = 0; i < contactList.size(); i++) {
            values.put("contact", contactList.get(i));
            database.insert(Contant.CONTACTS_TABLE_NAME, null, values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }
}

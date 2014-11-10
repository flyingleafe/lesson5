package com.dreamteam.app.exercise1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dreamteam.app.exercise1.Channel;

/**
 * Created by flyingleafe on 10.11.14.
 */
public class FeedDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RSSReader.db";

    public FeedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        ChannelsTable.create(db);
        NewsTable.create(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ChannelsTable.delete(db);
        NewsTable.delete(db);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}

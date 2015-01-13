package com.dreamteam.app.exercise1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.dreamteam.app.exercise1.db.ChannelsTable;
import com.dreamteam.app.exercise1.db.FeedContentProvider;

import java.util.Arrays;

/**
 * Created by flyingleafe on 10.11.14.
 */
public class Channel {
    private long id = -1;

    private String url = "", title = "", description = "";

    public Channel(long id) {
        this.id = id;
    }

    public Channel(String url) {
        this.url = url;
    }

    public Channel(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public void update(ContentResolver resolver) {
        String[] selections = {"*"};
        String[] queryArgs = {Long.toString(id)};
        Cursor cursor = resolver.query(
                FeedContentProvider.CHANNELS_CONTENT_URL,
                selections,
                ChannelsTable._ID + "=?",
                queryArgs,
                null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            title = cursor.getString(cursor.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_TITLE));
            url = cursor.getString(cursor.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_URL));
            description = cursor.getString(cursor.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_DESCRIPTION));
        }
    }

    public long save(ContentResolver resolver) {
        ContentValues row = new ContentValues();
        row.put(ChannelsTable.COLUMN_NAME_URL, url);
        row.put(ChannelsTable.COLUMN_NAME_TITLE, title);
        row.put(ChannelsTable.COLUMN_NAME_DESCRIPTION, description);
        if(id == -1) {
            Uri res = resolver.insert(FeedContentProvider.CHANNELS_CONTENT_URL, row);
            id = Long.parseLong(res.getLastPathSegment());
            return id;
        } else {
            String[] args = {Long.toString(id)};
            resolver.update(FeedContentProvider.CHANNELS_CONTENT_URL, row, ChannelsTable._ID + "=?", args);
            return id;
        }
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

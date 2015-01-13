package com.dreamteam.app.exercise1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.dreamteam.app.exercise1.db.FeedContentProvider;
import com.dreamteam.app.exercise1.db.NewsTable;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class FeedIntentService extends IntentService {

    private static final String URL_ID = "url";
    private static final String RECEIVER = "receiver";

    public static final int STATUS_OK = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_ERROR = -1;

    public static final String NEWS_URIS = "newsUris";

    protected ResultReceiver receiver;

    public static void startFeedRefresh(Context context, long channelId, ResultReceiver receiver) {
        Intent intent = new Intent(context, FeedIntentService.class);
        intent.putExtra(URL_ID, channelId);
        intent.putExtra(RECEIVER, receiver);
        context.startService(intent);
    }

    public FeedIntentService() {
        super("FeedIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final long channelId = intent.getLongExtra(URL_ID, -1);
            receiver = intent.getParcelableExtra(RECEIVER);
            try {
                Channel channel = new Channel(channelId);
                channel.update(getContentResolver());
                InputStream response = new URL(channel.getUrl()).openStream();
                Feed feed = new FeedParser().parse(response);
                onSuccess(feed, channel);
            } catch (IOException e) {
                onError(getBaseContext().getString(R.string.fetching_error));
            } catch (SAXException e) {
                onError(getBaseContext().getString(R.string.parsing_error));
            }
        }
    }

    protected void onError(String err) {
        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_TEXT, err);
        receiver.send(STATUS_ERROR, bundle);
    }

    protected void onSuccess(Feed result, Channel channel) {
        for(int i = result.getItems().size() - 1; i >= 0; i--) {
            FeedItem item = result.getItems().get(i);
            ContentValues row = new ContentValues();
            row.put(NewsTable.CHANNEL_NAME_TITLE, channel.getId());
            row.put(NewsTable.COLUMN_NAME_TITLE, item.getTitle());
            row.put(NewsTable.COLUMN_NAME_DESCRIPTION, item.getDescription());
            row.put(NewsTable.COLUMN_NAME_URL, item.getLink());
            getContentResolver().insert(FeedContentProvider.NEWS_CONTENT_URL, row);
        }
        channel.setTitle(result.getTitle());
        channel.setDescription(result.getDescription());
        channel.save(getContentResolver());
        Bundle bundle = new Bundle();
        receiver.send(STATUS_OK, bundle);
    }
}

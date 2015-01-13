package com.dreamteam.app.exercise1;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.app.exercise1.db.ChannelsTable;
import com.dreamteam.app.exercise1.db.FeedContentProvider;
import com.dreamteam.app.exercise1.db.NewsTable;


public class FeedActivity extends ListActivity implements Receiver, LoaderManager.LoaderCallbacks<Cursor> {

    private TextView channelTitle;
    private TextView channelDesc;
    private FeedAdapter adapter;
    private ListView list;
    private long channelId;

    private final int LOADER_ID_FEEDS = 2;
    private final int LOADER_ID_CHANNEL = 3;

    public static final String FEED_ID = "feedId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        channelTitle = (TextView) findViewById(R.id.channel_title);
        channelDesc = (TextView) findViewById(R.id.channel_desc);
        list = getListView();
        adapter = new FeedAdapter(new Feed());
        setListAdapter(adapter);
        channelId = getIntent().getLongExtra(FEED_ID, -1);
        getLoaderManager().initLoader(LOADER_ID_FEEDS, null, this);
        getLoaderManager().initLoader(LOADER_ID_CHANNEL, null, this);
    }

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void refreshFeed(View view) {
        showToast(getString(R.string.feed_refresh));
        FeedIntentService.startFeedRefresh(this, channelId, new RefreshReceiver(new Handler(), this));
    }

    public void startWebPreview(String url) {
        Intent intent = new Intent(this, WebPreview.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resCode, Bundle resData) {
        switch (resCode) {
            case FeedIntentService.STATUS_ERROR:
                Toast.makeText(this, resData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
                break;
            case FeedIntentService.STATUS_OK:
                getLoaderManager().restartLoader(LOADER_ID_CHANNEL, null, this);
                getLoaderManager().restartLoader(LOADER_ID_FEEDS, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_ID_FEEDS:
                return new CursorLoader(this, FeedContentProvider.NEWS_CONTENT_URL,
                        new String[]{"*"},
                        NewsTable.CHANNEL_NAME_TITLE + "=?",
                        new String[]{Long.toString(channelId)},
                        NewsTable._ID + " DESC");
            case LOADER_ID_CHANNEL:
                return new CursorLoader(this, FeedContentProvider.CHANNELS_CONTENT_URL,
                        new String[]{"*"},
                        ChannelsTable._ID + "=?",
                        new String[]{Long.toString(channelId)},
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        switch(loader.getId()) {
            case LOADER_ID_FEEDS:
                if (c == null) return;
                Feed feed = new Feed();
                while (c.moveToNext()) {
                    String title = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_TITLE));
                    String url = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_URL));
                    String description = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_DESCRIPTION));
                    feed.addItem(new FeedItem(title, description, url));
                }
                adapter.setData(feed);
                adapter.notifyDataSetChanged();
                break;
            case LOADER_ID_CHANNEL:
                c.moveToFirst();
                channelTitle.setText(c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_TITLE)));
                channelDesc.setText(c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_DESCRIPTION)));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

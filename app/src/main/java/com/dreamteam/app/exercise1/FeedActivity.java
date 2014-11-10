package com.dreamteam.app.exercise1;

import android.app.ListActivity;
import android.content.Intent;
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


public class FeedActivity extends ListActivity implements Receiver {

    private TextView channelTitle;
    private TextView channelDesc;
    private FeedAdapter adapter;
    private ListView list;
    private Channel channel;

    public static final String FEED_ID = "feedId";

    private Cursor getNewsCursor() {
        String[] proj = {"*"};
        String[] args = {Long.toString(channel.getId())};
        return getContentResolver().query(
                FeedContentProvider.NEWS_CONTENT_URL,
                proj,
                NewsTable.CHANNEL_NAME_TITLE + "=?",
                args,
                null);
    }

    private void setNewsFromDatabase() {
        Cursor c = getNewsCursor();
        if(c == null) return;
        Feed feed = new Feed(channel.getTitle(), channel.getDescription());
        while(c.moveToNext()) {
            String title = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_TITLE));
            String url = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_URL));
            String description = c.getString(c.getColumnIndexOrThrow(ChannelsTable.COLUMN_NAME_DESCRIPTION));
            feed.addItem(new FeedItem(title, description, url));
        }
        adapter.setData(feed);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        channelTitle = (TextView) findViewById(R.id.channel_title);
        channelDesc = (TextView) findViewById(R.id.channel_desc);
        list = getListView();
        adapter = new FeedAdapter(new Feed());
        setListAdapter(adapter);
        long feedId = getIntent().getLongExtra(FEED_ID, -1);
        channel = new Channel(feedId, getContentResolver());
        channelTitle.setText(channel.getTitle());
        channelDesc.setText(channel.getDescription());
        setNewsFromDatabase();
    }

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void refreshFeed(View view) {
        showToast(getString(R.string.feed_refresh));
        FeedIntentService.startFeedRefresh(this, channel.getId(), new RefreshReceiver(new Handler(), this));
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
                channel.update(getContentResolver());
                channelTitle.setText(channel.getTitle());
                channelDesc.setText(channel.getDescription());
                setNewsFromDatabase();
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
}

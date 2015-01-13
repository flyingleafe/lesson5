package com.dreamteam.app.exercise1;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamteam.app.exercise1.db.ChannelsTable;
import com.dreamteam.app.exercise1.db.FeedContentProvider;


public class ChannelsActivity extends ListActivity implements Receiver, LoaderManager.LoaderCallbacks<Cursor> {

    private TextView newChannelInput;
    private ChannelsAdapter adapter;
    private ProgressDialog dialog;

    private int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        newChannelInput = (TextView) findViewById(R.id.new_channel_input);
        adapter = new ChannelsAdapter(this, null, 0);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        setListAdapter(adapter);
    }

    public void chooseChannel(long channelId) {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.putExtra(FeedActivity.FEED_ID, channelId);
        startActivity(intent);
    }

    public void addNewChannel(View view) {
        String url = newChannelInput.getText().toString();
        Channel channel = new Channel(url);
        channel.save(getContentResolver());
        FeedIntentService.startFeedRefresh(this, channel.getId(), new RefreshReceiver(new Handler(), this));
        dialog = ProgressDialog.show(this, "Adding feed...", "Wait a bit", true);
        newChannelInput.setText("");
    }

    public void deleteChannel(long channelId) {
        String[] args = {Long.toString(channelId)};
        getContentResolver().delete(FeedContentProvider.CHANNELS_CONTENT_URL,
                ChannelsTable._ID + "=?", args);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onReceiveResult(int resCode, Bundle resData) {
        dialog.dismiss();
        switch (resCode) {
            case FeedIntentService.STATUS_ERROR:
                Toast.makeText(this, resData.getString(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
                break;
            case FeedIntentService.STATUS_OK:
                getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.channels, menu);
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
        return new CursorLoader(this, FeedContentProvider.CHANNELS_CONTENT_URL,
                new String[]{"*"}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // fuck nothing
    }
}

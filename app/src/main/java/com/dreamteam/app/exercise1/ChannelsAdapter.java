package com.dreamteam.app.exercise1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.dreamteam.app.exercise1.db.ChannelsTable;

/**
 * Created by flyingleafe on 10.11.14.
 */
public class ChannelsAdapter extends CursorAdapter {

    private ChannelsActivity activity;

    public ChannelsAdapter(ChannelsActivity activity, Cursor c, int flags) {
        super(activity, c, flags);
        this.activity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.channel_item, viewGroup, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        int titleCol = cursor.getColumnIndex(ChannelsTable.COLUMN_NAME_TITLE);
        int urlCol = cursor.getColumnIndex(ChannelsTable.COLUMN_NAME_URL);
        int idCol = cursor.getColumnIndex(ChannelsTable._ID);

        String title = cursor.getString(titleCol);
        String url = cursor.getString(urlCol);
        final long id = cursor.getLong(idCol);

        TextView titleText = (TextView) v.findViewById(android.R.id.text1);
        TextView urlText = (TextView) v.findViewById(android.R.id.text2);
        ImageButton deleteButton = (ImageButton) v.findViewById(android.R.id.button1);
        titleText.setText(title);
        urlText.setText(url);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.chooseChannel(id);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.deleteChannel(id);
            }
        });
    }
}

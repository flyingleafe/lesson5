package com.dreamteam.app.exercise1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by flyingleafe on 14.10.14.
 */
public class MyAdapter<T> extends BaseAdapter {

    private List<T> data;

    public MyAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void deleteItem(int i) {
        data.remove(i);
        notifyDataSetChanged();
    }

    public void add(T o) {
        data.add(o);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        }
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(getItem(i).toString());
        Button button = (Button) view.findViewById(android.R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteItem(i);
            }
        });
        return view;
    }
}

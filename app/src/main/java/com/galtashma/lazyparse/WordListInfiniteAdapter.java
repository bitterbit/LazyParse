package com.galtashma.lazyparse;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by gal on 2/15/18.
 */

public class WordListInfiniteAdapter extends ScrollInfiniteAdapter<WordLazy> {

    public WordListInfiniteAdapter(Context context, LazyList<WordLazy> list){
        super(context, list, android.R.layout.simple_list_item_1, 20);
    }

    @Override
    public View renderReadyLazyObject(WordLazy word, @NonNull View convertView, @NonNull ViewGroup parent) {
        final TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText(word.getWord());
        return convertView;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<WordLazy> object, @NonNull View convertView, @NonNull ViewGroup parent) {
        final TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText("Loading...");
        return convertView;
    }
}
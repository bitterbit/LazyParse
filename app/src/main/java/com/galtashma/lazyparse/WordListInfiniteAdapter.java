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
        super(context, list, android.R.layout.simple_list_item_1, 10);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LazyParseObjectHolder<WordLazy> wordLazy = getItem(position);
        Log.i("MAIN", "get view " + position + ", lazy object " + wordLazy);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        if (wordLazy.getState() == LazyParseObjectHolder.State.READY) {
            handleWordReady(tv, wordLazy);
        } else {
            handleWordFetching(tv, wordLazy);
        }

        return convertView;
    }

    private void handleWordReady(TextView tv, LazyParseObjectHolder<WordLazy> wordLazy){
        Log.d("MAIN", "handle word ready " + wordLazy);
        tv.setText(wordLazy.get().getWord());
    }

    private void handleWordFetching(final TextView tv, LazyParseObjectHolder<WordLazy> wordLazy){
        Log.d("MAIN", "handle word loading " + wordLazy);
        tv.setText("Loading...");
    }
}

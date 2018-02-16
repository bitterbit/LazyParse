package com.galtashma.lazyparse;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by gal on 2/15/18.
 */

public class WordListAdapter extends BaseAdapter {
    private LazyList<WordLazy> words;
    private Context context;

    WordListAdapter(Context context, LazyList<WordLazy> list) {
        this.context = context;
        this.words = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("MAIN", "get view " + position);
        LazyParseObjectHolder<WordLazy> wordLazy = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final TextView tv = convertView.findViewById(android.R.id.text1);
        if (wordLazy.getState() == LazyParseObjectHolder.State.READY) {
            handleWordReady(tv, wordLazy);
        } else {
            handleWordFetching(tv, wordLazy);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        if (words.getLimit() == -1){
            return 100;
        }
        return words.getLimit();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Nullable
    @Override
    public LazyParseObjectHolder<WordLazy> getItem(int position) {
        Log.i("MAIN", "get item at " + position);
        return words.get(position);
    }

    private void handleWordReady(TextView tv, LazyParseObjectHolder<WordLazy> wordLazy){
        tv.setText(wordLazy.get().getString("word"));
    }

    private void handleWordFetching(final TextView tv, LazyParseObjectHolder<WordLazy> wordLazy){
        tv.setText("Loading...");
        wordLazy.setListener(new LazyParseObjectHolder.OnReadyListener<WordLazy>() {
            @Override
            public void onReady(WordLazy word) {
                tv.setText(word.getString("word"));
            }
        });
    }
}

package com.galtashma.lazyparse;

import android.widget.AbsListView;

public class ScrollInfiniteListener implements AbsListView.OnScrollListener {
    private ScrollInfiniteAdapter adapter;

    public ScrollInfiniteListener(ScrollInfiniteAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //check if we've reached the bottom
        if(hasReachedBottom(firstVisibleItem, visibleItemCount, totalItemCount) && !adapter.hasEndReached()){
            adapter.showMore();
        }
    }
    private boolean hasReachedBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        return firstVisibleItem + visibleItemCount == totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

}
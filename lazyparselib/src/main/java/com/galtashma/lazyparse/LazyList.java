package com.galtashma.lazyparse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by gal on 2/15/18.
 */

public class LazyList<T extends LazyParseObject> implements Iterable<LazyParseObjectHolder<T>>, FindCallback<T>  {
    
    private static int BATCH_SIZE = 5;
    public static final String TAG = "LazyParse";

    private int queryPotentialCount = -1;
    private int lastFetchedIndex = 0;
    private ParseQuery<T> query;
    private List<LazyParseObjectHolder<T>> liveObjects;


    public LazyList(ParseQuery<T> query){
        this.query = query;
        this.liveObjects = new ArrayList<>();
        fetchNAsync(BATCH_SIZE);
        fetchQueryCount();
    }


    class LazyParseIterator implements Iterator<LazyParseObjectHolder<T>>{
        int current = 0;

        @Override
        public boolean hasNext() {
            // TODO: when to stop?
            // Allow to set limit by user
            // When there are no more lines in table
            return true;
        }

        @Override
        public LazyParseObjectHolder<T> next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            }

            LazyParseObjectHolder<T> item = internalGet(current);
            current++;
            return item;
        }
    }

    @NonNull
    @Override
    public Iterator<LazyParseObjectHolder<T>> iterator() {
        return new LazyParseIterator();
    }

    public int fetchedSize(){
        return lastFetchedIndex;
    }

    public int getLimit(){
        return queryPotentialCount;
    }

    public LazyParseObjectHolder<T> get(int index){
        return internalGet(index);
    }

    private LazyParseObjectHolder<T> internalGet(int index){
        if (index < this.liveObjects.size()){
            return liveObjects.get(index);
        }

        int toFetch = index - this.liveObjects.size()-1;
        fetchNAsync(Math.max(toFetch, BATCH_SIZE)); // fetch at least min batch size

        return this.liveObjects.get(index);
    }

    /* ----------- PARSE ----------- */


    private void fetchNAsync(int count){
        int fetchFrom = this.liveObjects.size();
        int fetchTo = fetchFrom + count;

        for (int i=0; i<count; i++){
            LazyParseObjectHolder<T> holder = new LazyParseObjectHolder<T>();
            holder.onStartLoading();
            this.liveObjects.add(holder);
        }

        fetchAsync(fetchFrom, fetchTo);
    }

    private void fetchAsync(int fromIndex, int toIndex){
        query.setSkip(fromIndex);
        query.setLimit(toIndex);
        Log.i(TAG, "started fetching from " + fromIndex + " to " + toIndex);
        query.findInBackground(this);
    }

    @Override
    public void done(List<T> objects, ParseException e) {
        // TODO: maybe this should be synced

        if (e != null){
            Log.e(TAG, "Error on fetch done", e);
            return;
        }

        Log.d(TAG, "done fetching " + objects.size() + " objects");

        for (T obj : objects){

            Log.d(TAG, "done fetching object " + lastFetchedIndex);

            if (liveObjects.size() <= lastFetchedIndex){
                Log.w(TAG, "trying to finish fetching an object that wasn't in liveobjects");
                liveObjects.add(new LazyParseObjectHolder<T>());
            }

            this.liveObjects.get(lastFetchedIndex).onFetchResolved(obj);
            lastFetchedIndex++;
        }
    }

    private void fetchQueryCount(){
        query.setSkip(0);
        query.setLimit(-1); // unlimited
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {

                if(e != null){
                    Log.e(TAG, "Error on fetch count done", e);
                    return;
                }

                Log.d(TAG, "query has count of " + count);
                queryPotentialCount = count;

                if (liveObjects.size() > count) {
                    Log.w(TAG, "more live lazy objects than the queries potential");

                    // Remove all objects that should die
                    for (int i=queryPotentialCount; i<liveObjects.size(); i++){

                        // Object should have been waiting forever to fetch but instead is ready. Someone is lying
                        if (liveObjects.remove(i).getState() == LazyParseObjectHolder.State.READY){
                            Log.w(TAG, "live object removed due low query potential size but was READY and not FETCHING");
                        }
                    }

                }
            }
        });
    }
}

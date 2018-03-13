package com.galtashma.lazyparse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by gal on 2/15/18.
 */

public class LazyList<T extends ParseObject & LazyParseObject> implements Iterable<LazyParseObjectHolder<T>>, FindCallback<T>  {
    
    private static int DEFAULT_STEP_SIZE = 5;
    public static final String TAG = "LazyParse";

    private int stepSize = DEFAULT_STEP_SIZE;
    private int lastFetchedIndex = 0;
    private int queryPotentialCount = -1;
    private boolean blockUntilFetchedCount;

    private ParseQuery<T> query;
    private List<LazyParseObjectHolder<T>> liveObjects;
    private Task<Integer> fetchCountTask = null;

    public LazyList(ParseQuery<T> query, int stepSize){
        this.query = query;
        this.stepSize = stepSize;
        this.liveObjects = Collections.synchronizedList(new ArrayList<LazyParseObjectHolder<T>>());
        this.blockUntilFetchedCount = true;
        fetchNAsync(stepSize);

        fetchCountTask = fetchQueryCount();
    }

    public LazyList(ParseQuery<T> query){
        this(query, DEFAULT_STEP_SIZE);
    }

    class LazyParseIterator implements Iterator<LazyParseObjectHolder<T>>{
        int current = 0;

        @Override
        public boolean hasNext() {
            return isInBounds(current);
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

    public int getLimit(){
        if (blockUntilFetchedCount){
            waitForFetchCount();
        }

        return queryPotentialCount;
    }

    public LazyParseObjectHolder<T> get(int index){
        if (isInBounds(index)) {
            return internalGet(index);
        }

        return null;
    }

    // Check whether the given index is the bounds of our collection.
    // May block until the total object count is returned from the server.
    // See blockUntilFetchedCount flag.
    public boolean isInBounds(int index){
        if (!blockUntilFetchedCount && !fetchCountTask.isCompleted()){
            // We don't know if the index is in bounds, we will know better after queryPotential is populated
            // For now just return true
            return true;
        }

        if (fetchCountTask.isCompleted()) {
            return index < queryPotentialCount;
        }

        this.waitForFetchCount();
        return index < queryPotentialCount;
    }

    // Wait for the fetch count task to complete, safely.
    private void waitForFetchCount(){
        try {
            fetchCountTask.waitForCompletion();
        } catch (InterruptedException e) {
            Log.w(TAG, "Waiting for fetch count raised error", e);
            e.printStackTrace();
        }
    }

    private LazyParseObjectHolder<T> internalGet(int index){
        if (index < this.liveObjects.size()){
            return liveObjects.get(index);
        }

        int toFetch = index - this.liveObjects.size()-1;
        fetchNAsync(Math.max(toFetch, stepSize)); // fetch at least min batch size

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
        if (e != null){
            Log.e(TAG, "Error on fetch done", e);
            return;
        }

        Log.d(TAG, "done fetching " + objects.size() + " objects");
        addFetchedObjects(objects);
    }

    private synchronized void addFetchedObjects(List<T> objects){
        for (T obj : objects){
            Log.d(TAG, "adding fetched object " + lastFetchedIndex);

            if (liveObjects.size() <= lastFetchedIndex){
                Log.w(TAG, "trying to finish fetching an object that wasn't in live objects");
                liveObjects.add(new LazyParseObjectHolder<T>());
            }

            this.liveObjects.get(lastFetchedIndex).onFetchResolved(obj);
            lastFetchedIndex++;
        }
    }

    private Task<Integer> fetchQueryCount(){
        query.setSkip(0);
        query.setLimit(-1); // unlimited

        Task<Integer> task = query.countInBackground();
        task.continueWith(new Continuation<Integer, Integer>() {
            @Override
            public Integer then(Task<Integer> task) throws Exception {
                if (task.isFaulted()){
                    Log.e(TAG, "Error on fetch count done", task.getError());
                    throw task.getError();
                }
                queryPotentialCount = task.getResult();
                return task.getResult();
            }
        }).onSuccess(new Continuation<Integer, Void>() {
            @Override
            public Void then(Task<Integer> task) throws Exception {
                int parseObjectsCount = task.getResult();
                int numObjectsToRemove = parseObjectsCount - liveObjects.size();

                if (numObjectsToRemove > 0){
                    Log.d(TAG, "To many live objects, some will never be fetched");
                }

                return null;
            }
        });

        return task;
    }
}
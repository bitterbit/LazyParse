package com.galtashma.lazyparse;

import android.util.Log;

import com.parse.ParseObject;

/**
 * Created by gal on 2/13/18.
 */

public class LazyParseObjectHolder<T extends LazyParseObject> {

    public static final String TAG = "LazyParse";

    private T object = null;
    private State state = State.NOT_INITIALIZED;
    private OnReadyListener<T> listener = null;

    interface OnReadyListener<T> {
        void onReady(T object);
    }

    public enum State {
        NOT_INITIALIZED, LOADING, READY
    }

    public State getState(){
        return this.state;
    }

    public T get(){
        return object;
    }

    void onStartLoading() {
        this.state = State.LOADING;
    }

    void onFetchResolved(T object){
        Log.i(TAG, "onFetchResolved");
        this.state = State.READY;
        this.object = object;
        if (this.listener != null){
            Log.d(TAG, "calling listener");
            this.listener.onReady(this.object);
        }
    }

    public void setListener(OnReadyListener<T> listener){
        this.listener = listener;
    }

    public String toString(){
        String msg = "LazyObject state: " + getState();
        if (this.getState() == State.READY) {
            msg += " obj: " + this.object.toString();
        }

        return msg;
    }
}

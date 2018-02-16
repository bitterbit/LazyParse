package com.galtashma.lazyparse;

import com.parse.ParseObject;

/**
 * Created by gal on 2/13/18.
 */

public class LazyParseObjectHolder<T extends LazyParseObject> {
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
        if (object == null){
            return null;
        }

        return object;
    }

    void onStartLoading() {
        this.state = State.LOADING;
    }

    void onFetchResolved(T object){
        this.state = State.READY;
        this.object = object;
        if (this.listener != null){
            this.listener.onReady(this.object);
        }
    }

    public void setListener(OnReadyListener<T> listener){
        this.listener = listener;
    }
}

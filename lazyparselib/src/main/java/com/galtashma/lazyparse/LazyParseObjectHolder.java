package com.galtashma.lazyparse;

import android.os.Handler;
import android.util.Log;

import com.parse.ParseObject;

/**
 * Created by gal on 2/13/18.
 */

public class LazyParseObjectHolder<T extends LazyParseObject> {

    public static final String TAG = "LazyParse";
    private static final int TIMEOUT = 5000;

    private T object = null;
    private State state = State.NOT_INITIALIZED;
    private OnReadyListener<T> listener = null;

    private Handler loadingTimeout;

    public interface OnReadyListener<T extends LazyParseObject> {
        // Will be called when the LazyParseObject has been fetched
        void onReady(T object);

        // Called in case there is no actual ParseObject to load to the given LazyParseObject
        void onDeleted(LazyParseObjectHolder<T> holder);
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
         if (this.loadingTimeout != null){
             // Cancel all callbacks
             this.loadingTimeout.removeCallbacksAndMessages(null);
         }

         this.loadingTimeout = new Handler();
         this.loadingTimeout.postDelayed(new Runnable() {
             @Override
             public void run() {
                 if (getState() == State.LOADING){
                     Log.w(TAG, "Timeout fired while live object is still loading. Sending delete event to listener. object: " + object);
                     onDeleted();
                 }
             }
         }, TIMEOUT);
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

    private void onDeleted(){
        if (listener != null){
            listener.onDeleted(this);
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

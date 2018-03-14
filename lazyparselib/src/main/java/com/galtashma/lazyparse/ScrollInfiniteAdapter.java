package com.galtashma.lazyparse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.Parse;
import com.parse.ParseObject;

public abstract class ScrollInfiniteAdapter<T extends ParseObject>
        extends ArrayAdapter<LazyParseObjectHolder<T>>
        implements LazyParseObjectHolder.OnReadyListener<T> {

    public interface OnClickListener<T> {
        void onClick(T object);
    }

    public static final String TAG = "LazyParse";

    // Lazy Data objects to be shown in the list when needed
    private LazyList<T> valuesGenerator;
    private int stepSize;
    private int rowLayout;

    private OnClickListener<T> listener;

    /**
     * C'tor
     * @param context the context that you come, to call the super of the class.
     * @param rowLayout the layout of row items, to call the super of the class
     * @param stepSize the quantity that you need to load when the user get in the "final" of the list.
     */
    public ScrollInfiniteAdapter(Context context, LazyList<T> lazyValues, int rowLayout, int stepSize) {
        super(context, rowLayout);
        this.valuesGenerator = lazyValues;
        this.rowLayout = rowLayout;
        this.stepSize = stepSize;

        showMore();
    }

    /**
     * This method override is for load the data of the list in the items of the listView,
     * but in this case you have to write this override when you create a instance of this class.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        final LazyParseObjectHolder<T> lazyObject = getItem(position);

        if (lazyObject == null){
            Log.w(TAG, "getItem at position " + position + " returned null");
            return convertView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(rowLayout, parent, false);
        }

        if (lazyObject.getState() == LazyParseObjectHolder.State.READY) {
            final T object = lazyObject.get();

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyOnClickListener(object);
                }
            });
            return renderReadyLazyObject(object, convertView, parent);
        }

        return renderLoadingLazyObject(lazyObject, convertView, parent);
    }


    public abstract View renderReadyLazyObject(T object, View convertView, @NonNull ViewGroup parent);
    public abstract View renderLoadingLazyObject(LazyParseObjectHolder<T> object, View convertView, @NonNull ViewGroup parent);

    /**
     * Try to show more list entries if any available
     */
    public void showMore(){
        Log.i(TAG, "trying to show more");
        boolean changed = false;
        int currentCount = getCount();
        for (int i=currentCount ; i<currentCount+stepSize && valuesGenerator.isInBounds(i); i++){
            LazyParseObjectHolder<T> holder = valuesGenerator.get(i);
            holder.setListener(this);
            Log.i(TAG, "got lazy object "+ holder);
            add(holder);
            changed = true;
        }

        if (changed) {
            notifyDataSetChanged(); //notify when the data count has changes.
        }
    }

    @Override
    public void onReady(T object) {
        Log.d(TAG, "on data ready " + object);
        notifyDataSetChanged(); // An object was fetched, refresh view
    }

    public void onDeleted(LazyParseObjectHolder<T> holder){
        Log.d(TAG, "removed LazyParseObjectHolder from ListView after it was deleted");
        remove(holder);
    }

    public boolean hasEndReached() {
        if (valuesGenerator.getLimit() > -1){
            if (getCount() < valuesGenerator.getLimit()){
                return false;
            }

            return true;
        }

        return false;
    }

    public void setOnClickListener(OnClickListener<T> listener){
        this.listener = listener;
    }

    protected void notifyOnClickListener(T object){
        if(listener != null){
            listener.onClick(object);
        }
    }
}
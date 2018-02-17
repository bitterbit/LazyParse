package com.galtashma.lazyparse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ScrollInfiniteAdapter<T extends LazyParseObject>
        extends ArrayAdapter<LazyParseObjectHolder<T>>
        implements LazyParseObjectHolder.OnReadyListener<T> {

    public static final String TAG = "LazyParse";

    // Lazy Data objects to be shown in the list when needed
    private LazyList<T> valuesGenerator;
    private int stepSize;

    /**
     * C'tor
     * @param context the context that you come, to call the super of the class.
     * @param rowLayout the layout of row items, to call the super of the class
     * @param stepSize the quantity that you need to load when the user get in the "final" of the list.
     */
    public ScrollInfiniteAdapter(Context context, LazyList<T> lazyValues, int rowLayout, int stepSize) {
        super(context, rowLayout);
        this.valuesGenerator = lazyValues;
        this.stepSize = stepSize;

        showMore();
    }

    /**
     * This method override is for load the data of the list in the items of the listView,
     * but in this case you have to write this override when you create a instance of this class.
     */
    @NonNull
    @Override
    public abstract View getView(int position, View convertView, @NonNull ViewGroup parent);

    /**
     * Try to show more list entries if any available
     */
    public void showMore(){
        if (shouldShowMore()){
            Log.i(TAG, "trying to show more");
            for (int i=getCount(); i<stepSize; i++){
                LazyParseObjectHolder<T> holder = valuesGenerator.get(i);
                holder.setListener(this);
                add(holder);
            }
            notifyDataSetChanged(); //notify when the data count has changes.
        }
    }

    /**
     * @return If the valuesGenerator list have more data to shown return true, against of this return false
     */
    private boolean shouldShowMore(){
        int limit = valuesGenerator.getLimit();
        if (limit < 0){
            return true;
        }

        if(getCount() >= limit) {
            return false;
        }

        int count = Math.min(getCount() + stepSize, valuesGenerator.getLimit()); //don't go past the end
        return !isEnd(count);
    }

    /**
     * @return true if then entire data set is being displayed, false otherwise
     */
    private boolean isEnd(int count){
        return count == valuesGenerator.getLimit();
    }

    @Override
    public void onReady(T object) {
        Log.d(TAG, "on data ready " + object);
        notifyDataSetChanged(); // An object was fetched, refresh view
    }
}
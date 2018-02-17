package com.galtashma.lazyparse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LazyList<WordLazy> list = newStyle();
        WordListInfiniteAdapter adapter = new WordListInfiniteAdapter(this, list);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private LazyList<WordLazy> newStyle(){
        ParseQuery<WordLazy> query = new ParseQuery<WordLazy>(WordLazy.class);
        return new LazyList<>(query);
    }

    private void oldStyle(){
        ParseQuery<QuoteLazy> query = new ParseQuery<QuoteLazy>(QuoteLazy.class);
        query.findInBackground(new FindCallback<QuoteLazy>() {
            @Override
            public void done(List<QuoteLazy> objects, ParseException e) {
                Log.i("MAIN", "FOUND QUOTES " + objects);

                if (objects == null){
                    return;
                }

                for (QuoteLazy quote : objects) {
                    Log.i("MAIN", quote.toString());
                }
            }
        });
    }
}

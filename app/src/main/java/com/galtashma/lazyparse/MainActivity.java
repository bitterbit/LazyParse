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

        ParseQuery<WordLazy> query = new ParseQuery<WordLazy>(WordLazy.class);
        query.orderByAscending("value");
        LazyList<WordLazy> list = new LazyList<>(query);

        WordListInfiniteAdapter adapter = new WordListInfiniteAdapter(this, list);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollInfiniteListener(adapter));
    }
}

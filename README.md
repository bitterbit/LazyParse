# LazyParse
Helper for parse-sdk making loading and using parse objects easy  

Demo
-----------
<img src="https://github.com/bitterbit/LazyParse/raw/master/images/demolist.gif" width=200>

Installation
-----------
Root gradle.build
``` gradle
repositories {
    maven {
        url 'https://jitpack.io'
    }
}
```

App gradle.build
``` gradle
compile ('com.github.bitterbit:LazyParse:1ff5ff65a0') {
    exclude group: 'com.android.support'
}
```

Usage
-----------

### Simple Usage 
``` java
ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseObject.class);
query.orderByAscending("value"); // Manipulate the query to your liking
LazyList<ParseObject> list = new LazyList<>(query);

Iterator<LazyParseObjectHolder<ParseObject>> it = list.iterator();

while(it.hasNext()){
    LazyParseObjectHolder<ParseObject> item = it.next()
    Log.i("TAG", "is ready? " + item.getState());
}
```

### Load LazyList to Endless ListView

Using LazyList to render an "endless" ListView is made out of two parts:
1. Query the data
2. Choose how to render each list item when it is ready


#### Querying the data
``` java
ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseObject.class);
query.orderByAscending("value"); // Manipulate the query to your liking
LazyList<ParseObject> list = new LazyList<>(query);
CustomRenderAdapter adapter = new CustomRenderAdapter(this, list);
ListView listView = findViewById(R.id.list_view);
listView.setAdapter(adapter);

// Let lazylist know when the user is at the end of the list and fetch more objects
listView.setOnScrollListener(new ScrollInfiniteListener(adapter));
```

#### Rendering the objects according to thier state
``` java
class CustomRenderAdapter <T extends ParseObject> extends ScrollInfiniteAdapter {
        
    public A(Context context, LazyList lazyValues) {
        // Change the layout and stepSize values to change your adapters behaviour
        super(context, lazyValues, android.R.layout.simple_list_item_1, 20);
    }

    @Override
    public View renderReadyLazyObject(ParseObject object, View convertView, @NonNull ViewGroup parent) {
        final TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText(object.getObjectId());
        return convertView;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder object, View convertView, @NonNull ViewGroup parent) {
        final TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText("Loading...");
        return convertView;
    }
}
```

Troubleshooting
-----------
In case of the error   `java.lang.ClassCastException: com.parse.ParseObject cannot be cast to com.galtashma.lazyparse.LazyParseObject`, check that you have not forgotten to call `ParseOject.registerSubclass(YourClass.class);`

package com.galtashma.lazyparse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by gal on 2/17/18.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Let parse know of our sample class
        ParseObject.registerSubclass(WordLazy.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .server(getString(R.string.server))
                .applicationId(getString(R.string.application_id))
                .clientKey(getString(R.string.client_key))
                .build()
        );
    }
}

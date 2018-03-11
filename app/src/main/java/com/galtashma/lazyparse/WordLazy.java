package com.galtashma.lazyparse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gal on 2/15/18.
 */

@ParseClassName("Word")
public class WordLazy extends ParseObject implements LazyParseObject {

    public String getWord(){
        return getString("value");
    }

    public int getLevel(){
        return getInt("level");
    }

    public String toString(){
        return "Word("+getWord() + " level: " + getLevel()+")";
    }

}

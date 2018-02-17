package com.galtashma.lazyparse;

import com.parse.ParseClassName;

/**
 * Created by gal on 2/15/18.
 */

@ParseClassName("Word")
public class WordLazy extends LazyParseObject {

    public String getWord(){
        return getString("value");
    }

//    public String getCategory(){
//        return getString("category_id");
//    }

    public int getLevel(){
        return getInt("level");
    }

    public String toString(){
        return "Word("+getWord() + " level: " + getLevel()+")";
    }

}

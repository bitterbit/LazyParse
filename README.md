# [Alpha] LazyParse
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
``` java
```

Troubleshoot
-----------
In case of the error   `java.lang.ClassCastException: com.parse.ParseObject cannot be cast to com.galtashma.lazyparse.LazyParseObject`, check that you have not forgotten to call `ParseOject.registerSubclass(YourClass.class);`

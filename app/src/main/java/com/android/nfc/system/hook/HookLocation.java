package com.android.nfc.system.hook;



public class HookLocation {

    private ClassLoader classLoader;


    public void start(ClassLoader classLoader) {
        this.classLoader = classLoader;

    }


}
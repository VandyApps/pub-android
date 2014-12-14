package com.vandyapps.pubandroid;

public class PubApp extends android.app.Application {
  
    @Override
    public void onCreate() {
        super.onCreate();
        banana = "Banana";
    }
    
    private String banana = "Orange";
    
    public String getBanana() {
        return banana;
    }
    
}
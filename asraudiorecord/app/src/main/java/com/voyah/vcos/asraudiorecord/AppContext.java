package com.voyah.vcos.asraudiorecord;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context instant;

    @Override
    public void onCreate() {
        super.onCreate();
        instant = getApplicationContext();
    }
}

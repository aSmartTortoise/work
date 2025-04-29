package com.voyah.vcos.ttsservices;

import android.content.Context;

public class ContextUtils {

    private static Context appContext;

    private ContextUtils() {

    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        ContextUtils.appContext = appContext;
    }

}

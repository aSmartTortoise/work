package com.voyah.ai.basecar.viewcmd.strategy;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({ViewCmdType.TYPE_GLOBAL, ViewCmdType.TYPE_KWS, ViewCmdType.TYPE_NORMAL})
@Retention(RetentionPolicy.SOURCE)
public @interface ViewCmdType {
    String TYPE_GLOBAL = "Global";
    String TYPE_KWS = "KWS";
    String TYPE_NORMAL = "Normal";
}
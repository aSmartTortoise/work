package com.voyah.h37z;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * voyah平台
 */
@StringDef({VcosPlatform.H37A, VcosPlatform.H53B, VcosPlatform.H56C})
@Retention(RetentionPolicy.SOURCE)
public @interface VcosPlatform {
    String H37A = "H37A";

    String H53B = "H53B";

    String H56C = "H56C";
}

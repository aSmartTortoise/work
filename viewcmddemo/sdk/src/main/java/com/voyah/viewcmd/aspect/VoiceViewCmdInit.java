package com.voyah.viewcmd.aspect;

import android.view.WindowManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VoiceViewCmdInit {
    boolean isCompatibleMode() default false;

    boolean isCustomResponse() default false;

    int windowType() default WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

    String processName() default "";
}
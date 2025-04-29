package com.voyah.viewcmd.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface VoiceRegisterView {
    boolean isSticky() default false;

    boolean isTopCoverView() default false;
}
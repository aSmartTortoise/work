package com.voyah.ai.basecar.recorder;

/**
 * Created by lcy on 2023/12/18.
 */

public class DhError extends Exception {

    public static final String RECORDER_TYPE_NONEXISTENT="recorder type nonexistent";
    public static final String RECORDER_STATE_UNINITIALIZED="recorder state uninitialized";

   public DhError(){
       super();
   }
    public DhError(String detailMessage){
       super(detailMessage);
    }

   public DhError(String detailMessage, Throwable throwable){
        super(detailMessage,throwable);
   }
}

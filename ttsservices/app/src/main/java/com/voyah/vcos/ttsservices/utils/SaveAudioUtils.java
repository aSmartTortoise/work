package com.voyah.vcos.ttsservices.utils;

import com.voyah.vcos.ttsservices.Constant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:lcy
 * @data:2024/1/20
 **/
public class SaveAudioUtils {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void saveByte(byte[] bytes, String str) {

        File file = new File(Constant.Path.BASE_PATH + "audio");
        if (!file.exists()) {
            boolean mk = file.mkdir();
            LogUtils.d("SaveAudioUtils", "mk:" + mk);
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("SaveAudioUtils", "str:" + str);
                if (null == bytes) {
                    return;
                }
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                try {
                    fos = new FileOutputStream(str, true);
                    bos = new BufferedOutputStream(fos);
                    bos.write(bytes);
                    bos.flush();
                    bos.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fos)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (null != bos)
                            bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

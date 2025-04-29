package com.voyah.ai.common.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author:lcy
 * @data:2024/1/20
 **/
public class SaveAudioUtils {

    public static void saveByte(byte[] bytes, String str) {
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
}

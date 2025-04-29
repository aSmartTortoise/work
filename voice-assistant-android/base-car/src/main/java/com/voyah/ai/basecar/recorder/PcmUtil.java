package com.voyah.ai.basecar.recorder;

/**
 * Created by lcy on 2023/12/18.
 */

public class PcmUtil {
    public static long generateRandom() {
        long random = System.currentTimeMillis() / 1000;
        return random;
    }

    public static void transferRecordData(int channelNum, byte[] pcm_data, byte[] transBuf) {

//        if (micNum == 6) {
//            transferRecordData6Mic(pcm_data, transBuf);
//        } else
        if (channelNum == 6) {
            transferRecordDataFor1920(pcm_data, transBuf);
        } else if (channelNum == 4) {
            transferRecordDataFor1280(pcm_data, transBuf);
        } else if (channelNum == 1) {
            transferRecordDataFor320(pcm_data, transBuf);
        }

    }

    private static void transferRecordData6Mic(byte[] pcm_data, byte[] transBuf) {
        for (int i = 0; i < pcm_data.length; i++) {
            transBuf[i] = pcm_data[i];
        }
    }

    private static void transferRecordDataFor320(byte[] pcm_data, byte[] transBuf) {
        int j = 0;
        for (int i = 0; i < pcm_data.length; i = i + 16) {
            transBuf[j++] = pcm_data[i];
            transBuf[j++] = pcm_data[i + 1];
        }
    }

    private static void transferRecordDataFor1280(byte[] pcm_data, byte[] transBuf) {
        int j = 0;
        for (int i = 0; i < pcm_data.length; i = i + 16) {
            transBuf[j++] = pcm_data[i];
            transBuf[j++] = pcm_data[i + 1];
            transBuf[j++] = pcm_data[i + 2];
            transBuf[j++] = pcm_data[i + 3];
            transBuf[j++] = pcm_data[i + 12];
            transBuf[j++] = pcm_data[i + 13];
            transBuf[j++] = pcm_data[i + 14];
            transBuf[j++] = pcm_data[i + 15];
        }
    }

    //4096  1920
    private static void transferRecordDataFor1920(byte[] pcm_data, byte[] transBuf) {
        int j = 0;
        for (int i = 0; i < pcm_data.length; i = i + 16) {
            transBuf[j++] = pcm_data[i];
            transBuf[j++] = pcm_data[i + 1];

            transBuf[j++] = pcm_data[i + 2];
            transBuf[j++] = pcm_data[i + 3];

            transBuf[j++] = pcm_data[i + 4];
            transBuf[j++] = pcm_data[i + 5];

            transBuf[j++] = pcm_data[i + 6];
            transBuf[j++] = pcm_data[i + 7];

            transBuf[j++] = pcm_data[i + 8];
            transBuf[j++] = pcm_data[i + 9];

            transBuf[j++] = pcm_data[i + 10];
            transBuf[j++] = pcm_data[i + 11];
        }
    }
}

package com.voyah.ai.common.utils;

/**
 * @author:lcy
 * @data:2024/1/31
 **/
public class RecordDataUtils {
    private static final String TAG = RecordDataUtils.class.getSimpleName();

//    public static void transferRecordData(byte[] pcm_data, byte[] transBuf, int micNum) {
//
////        int micNum = VehicleModelUtils.getInstance().getMicNumForVehicleModel();
//
//        if (micNum == 6) { //6麦克
//            transferRecordData6Mic(pcm_data, transBuf);
//        } else if (micNum == 4) { //4麦克
//            transferRecordDataFor1920(pcm_data, transBuf);
//        } else {
//            transferRecordDataFor1280(pcm_data, transBuf);
//        }
//
//    }
//
//    private static void transferRecordDataFor1280(byte[] pcm_data, byte[] transBuf) {
//        int j = 0;
//        for (int i = 0; i < pcm_data.length; i = i + 16) {
//            transBuf[j++] = pcm_data[i];
//            transBuf[j++] = pcm_data[i + 1];
//            transBuf[j++] = pcm_data[i + 2];
//            transBuf[j++] = pcm_data[i + 3];
//            transBuf[j++] = pcm_data[i + 12];
//            transBuf[j++] = pcm_data[i + 13];
//            transBuf[j++] = pcm_data[i + 14];
//            transBuf[j++] = pcm_data[i + 15];
//        }
//    }
//
//    private static void transferRecordData6Mic(byte[] pcm_data, byte[] transBuf) {
//        for (int i = 0; i < pcm_data.length; i++) {
//            transBuf[i] = pcm_data[i];
//        }
//    }

    public static byte[] transferRecordDataFor2560(byte[] pcm_data, byte[] transBuf) {
        int j = 0;
        for (int i = 0; i < pcm_data.length; i = i + 20) {
            transBuf[j++] = pcm_data[i];
            transBuf[j++] = pcm_data[i + 1];

            transBuf[j++] = pcm_data[i + 2];
            transBuf[j++] = pcm_data[i + 3];

            transBuf[j++] = pcm_data[i + 4];
            transBuf[j++] = pcm_data[i + 5];

            transBuf[j++] = pcm_data[i + 6];
            transBuf[j++] = pcm_data[i + 7];

//            transBuf[j++] = pcm_data[i + 8];
//            transBuf[j++] = pcm_data[i + 9];

//            transBuf[j++] = pcm_data[i + 10];
//            transBuf[j++] = pcm_data[i + 11];

            transBuf[j++] = pcm_data[i + 12];
            transBuf[j++] = pcm_data[i + 13];

            transBuf[j++] = pcm_data[i + 14];
            transBuf[j++] = pcm_data[i + 15];

            transBuf[j++] = pcm_data[i + 16];
            transBuf[j++] = pcm_data[i + 17];

            transBuf[j++] = pcm_data[i + 18];
            transBuf[j++] = pcm_data[i + 19];
        }
        return transBuf;
    }
}

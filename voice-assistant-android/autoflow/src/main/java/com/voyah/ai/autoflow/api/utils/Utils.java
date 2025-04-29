package com.voyah.ai.autoflow.api.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    private Context mContext;

    private Utils() {
    }

    public static Utils getInstance() {
        return Holder.Instance;
    }

    private static class Holder {

        public static final Utils Instance = new Utils();
    }

    public void init(Context context) {
        mContext = context;
    }

    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                Result += line;
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void copyAssetsToSD(final String srcPath, final String sdPath) {
        copyAssetsToDst(mContext, srcPath, sdPath);
    }


    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName,
                                dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            Log.e("xq", "拷贝文件出错；" + e.toString());
            e.printStackTrace();
        }
    }

    public interface FileOperateCallback {

        void onSuccess();

        void onFailed(String error);
    }


    /**
     * 判断一个字符串是否是数字。
     * @param str
     * @return
     */
    public final static boolean isNumeric(String str) {
        if (str != null && !"".equals(str.trim()))
            return str.matches("^[0-9]*$");
        else
            return false;
    }

}

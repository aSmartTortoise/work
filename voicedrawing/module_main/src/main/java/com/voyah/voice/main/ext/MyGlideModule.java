package com.voyah.voice.main.ext;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    private static final String TAG = "MyGlideModule";
    private static final long BIG_DISK_CACHE_SIZE = 2048L * 1024 * 1024;
    private static final long SMALL_DISK_CACHE_SIZE = 40L * 1024 * 1024;
    private static final String GLIDE_FOLDER = "glide_ai_draw";


    public MyGlideModule() {
        super();
        Log.i(TAG, "MyGlideModule");
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDiskCache(new DiskLruCacheFactory(context.getFilesDir().getPath(), GLIDE_FOLDER, BIG_DISK_CACHE_SIZE));
        Log.i(TAG, "applyOptions:" + context.getFilesDir().getPath() + File.separator + GLIDE_FOLDER);
    }
}
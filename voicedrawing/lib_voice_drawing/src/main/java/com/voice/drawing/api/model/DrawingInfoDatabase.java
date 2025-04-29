package com.voice.drawing.api.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = DrawingInfo.class, version = 1)
public abstract class DrawingInfoDatabase extends RoomDatabase {
    public abstract DrawingInfoDao drawingInfoDao();


    private static volatile DrawingInfoDatabase db = null;

    public static DrawingInfoDatabase getInstance(Context context) {
        if (db == null) {
            synchronized (DrawingInfoDatabase.class) {
                if (db == null) {
                    db = Room.databaseBuilder(context.getApplicationContext(), DrawingInfoDatabase.class, "history").addCallback(roomCallback).build();
                }
            }
        }
        return db;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i("DrawingInfo", "Database created successfully");
        }

    };


}

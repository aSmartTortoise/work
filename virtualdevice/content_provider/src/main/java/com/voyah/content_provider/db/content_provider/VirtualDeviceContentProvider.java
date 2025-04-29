package com.voyah.content_provider.db.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.google.gson.Gson;
import com.voyah.content_provider.db.Car;
import com.voyah.content_provider.db.dao.CarDao;
import com.voyah.content_provider.db.database.CarDatabase;

public class VirtualDeviceContentProvider extends ContentProvider {

    private CarDatabase db;
    private CarDao dao;
    private static String uri = "content://com.voyah.virtual.device.provider/";
    public static Uri CONTENT_URI = Uri.parse(uri);
    private Gson gson = new Gson();


    @Override
    public boolean onCreate() {

        //初始化数据库
        CarDatabase db = Room.databaseBuilder(getContext(),
                CarDatabase.class, "virtual_car").build();
        dao = db.myCarDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //获取uri中的id
        Log.e("lzl", "进入到ContentProvider");
        // 查询数据并返回Cursor对象
        Car car = dao.getCar(selection);
        if (car == null) {
            Log.e("lzl", "ContentProvider 通过" + selection + "查到的数据为null");
            return null;
        }
        Log.e("lzl", car.toString());

//        AirDevice airDevice = car.airDevice;
//        FragranceDevice fragranceDevice = car.fragranceDevice;

        ContentValues values = new ContentValues();
        values.put("jsonData", gson.toJson(car));


        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "jsonData"}, 1);
        cursor.addRow(new Object[]{1, values.get("jsonData")});
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // 返回MIME类型
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // 插入数据并返回新插入数据的URI

        //插入的数据转换成
        String jsonString = (String) values.get("jsonData");
        Car car = gson.fromJson(jsonString, Car.class);

        long rowId = dao.insertCar(car);
        Log.e("lzl", "rowId的结果：" + rowId);
        if (rowId > 0) {
            // 构建表示新插入数据的URI
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            // 通知任何监听这个URI变化的观察者
            getContext().getContentResolver().notifyChange(newUri, null);
            // 返回新URI
            return newUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // 删除数据并返回删除的行数
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // 更新数据并返回更新的行数

        // 插入数据并返回新插入数据的URI

        String jsonString = (String) values.get("jsonData");
        Car car = gson.fromJson(jsonString, Car.class);

        int rowsUpdated = dao.updataCar(car);
        return rowsUpdated;
    }
}

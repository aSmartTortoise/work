package com.voyah.vcos.virtualdevice.param;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.voyah.vcos.virtualdevice.param.bean.Car;

public class ContentProviderHelper {
    private static final String TAG = "ContentProviderHelper";
    private static String uri = "content://com.voyah.virtual.device.provider/";
    public static Uri CONTENT_URI = Uri.parse(uri);
    private Gson gson = new Gson();
    private Context mContext;
    ContentResolver resolver;

    private ContentProviderHelper() {

    }

    public static ContentProviderHelper getInstance() {
        return Holder.Instance;
    }

    private static class Holder {
        private static final ContentProviderHelper Instance = new ContentProviderHelper();
    }

    public ContentProviderHelper init(Context context) {
        mContext = context;
        resolver = mContext.getContentResolver();
        return this;
    }

    public void getData(String s) {

    }

    /**
     * 修改数据
     * @param methodName
     * @param value
     */
    public void setData(String methodName, Object value) {

        //1.先拿到当前的数据库里的数据
        Cursor cursor = resolver.query(CONTENT_URI, null, methodName, null, null);
        //cursor.getInt(cursor.getColumnIndex("fragranceState")) != 0 boolean类型处理

        Car car = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String carString = cursor.getString(cursor.getColumnIndex("jsonData"));
            car = gson.fromJson(carString, Car.class);
        }
        //2。根据数据库里的数据去根据类型和范围进行约束判断
        //1）先判断类型是否符合要求
        switch (car.paramsType) {
            case ParamsType.BOOLEAN:
                if (value instanceof Boolean) {

                } else {
                    throw new ClassCastException("当前" + methodName + "的类型应该是:boolean类型的但当前传入的值是：" + value);

                }
                break;
            case ParamsType.FLOAT:
                if (value instanceof Float) {
                    if (car.max != null && "".equals(car.max)) {
                        float max = Float.valueOf(car.max);
                        float min = Float.valueOf(car.min);
                        float cur = (float) value;
                        if (cur > max || cur < min) {
                            throw new IllegalArgumentException("当前参数的取之范围是：max" + max + " min:" + min + " 当前传入的值是：" + cur);
                        }
                    }
                } else {
                    throw new ClassCastException("当前" + methodName + "的类型应该是:float类型的但当前传入的值是：" + value);
                }
                break;
            case ParamsType.INT:
                if (value instanceof Integer) {
                    if (car.max != null && "".equals(car.max)) {
                        float max = Integer.valueOf(car.max);
                        float min = Integer.valueOf(car.min);
                        float cur = (int) value;
                        if (cur > max || cur < min) {
                            throw new IllegalArgumentException("当前参数的取之范围是：max" + max + " min:" + min + " 当前传入的值是：" + cur);
                        }
                    }
                } else {
                    throw new ClassCastException("当前" + methodName + "的类型应该是:int类型的但当前传入的值是：" + value);
                }
                break;
            case ParamsType.STRING:
                if (value instanceof String) {

                } else {
                    throw new ClassCastException("当前" + methodName + "的类型应该是:string类型的但当前传入的值是：" + value);
                }
                break;
            default:
                Log.e(TAG, "ParamsType 不存在的类型，需要进行补充");
                break;
        }

        //3.都满足要求的可以直接设置数据
        car.value = value + "";

        ContentValues values = new ContentValues();
//                        car.airDevice.airSwitchState = false;
//                        car.airDevice.airSynSwitchState = false;
//                        car.fragranceDevice.fragranceState = true;
        values.put("jsonData", gson.toJson(car));
        // 更新数据
        int size = resolver.update(CONTENT_URI, values, null, null);
        Log.e("lzl","更新数据");

        Log.e("lzl","size:"+size +" 插入对象内容："+values.toString());
    }

    public void addParams(String paramsName, String paramsType, Object max, Object min, Object defaultValue, String paramDescribe) {
        // 插入
        Car car = new Car();
        car.paramsName = paramsName;
        car.paramsType = paramsType;
        car.max = max + "";
        car.min = min + "";
        car.value = defaultValue + "";
        car.paramDescribe = paramDescribe;

        ContentValues values = new ContentValues();
        values.put("jsonData", gson.toJson(car));

        Uri curUri = resolver.insert(CONTENT_URI, values);
        Log.e("lzl", "插入数据");

        Log.e("lzl", "uri_path:" + curUri.getPath() + " 插入对象内容：" + car.toString());

    }

    public Car query(String paramsName) {
        // 查询数据
        Cursor cursor = resolver.query(CONTENT_URI, null, paramsName, null, null);
        if(cursor == null){
            return null;
        }
        Car car = createCarToCursor(cursor);
        return car;
    }

    @SuppressLint("Range")
    private Car createCarToCursor(Cursor cursor) {
        Car car = null;
        if (cursor.moveToFirst()) {
            String carString = cursor.getString(cursor.getColumnIndex("jsonData"));
            car = gson.fromJson(carString, Car.class);
//            car.airDevice = new AirDevice();
//            car.fragranceDevice = new FragranceDevice();
//            car.airDevice.airSynSwitchState = (cursor.getString(cursor.getColumnIndex("airSynSwitchState")).equals("true"));
//            car.airDevice.airSwitchState = (cursor.getString(cursor.getColumnIndex("airSwitchState")).equals("true"));
//            car.fragranceDevice.fragranceState = (cursor.getString(cursor.getColumnIndex("fragranceState")).equals("true"));
        }
        return car;
    }
}

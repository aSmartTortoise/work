package com.voyah.vcos.virtualdevice;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.voyah.vcos.virtualdevice.param.ContentProviderHelper;
import com.voyah.vcos.virtualdevice.param.bean.Car;
import com.voyah.vcos.virtualdevice.param.intent.AllDeviceControlManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //添加数据库里的数据的逻辑
        ContentProviderHelper contentProviderHelper = ContentProviderHelper.getInstance();
        contentProviderHelper.init(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //FIXME 先用一个不存在的key,方便调试
                Car car = contentProviderHelper.query("sad");
                if(car == null){
                    Log.e("lzl","当前数据库为null，进行初始化。。。。。");
                    AllDeviceControlManager allDeviceControlManager = new AllDeviceControlManager();
                    allDeviceControlManager.init();
                    allDeviceControlManager.exe();
                }else{
                    Log.e("lzl","数据库存在，不用创建了。");
                }
            }
        }).start();

    }
}
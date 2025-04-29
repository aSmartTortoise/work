package com.voyah.content_provider.db;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * String paramsName,String paramsType,Object max,Object min,Object defultValue
 */
@Entity
public class Car {
    @PrimaryKey(autoGenerate = true)
    public int id;
    //参数名字
    public String paramsName;
    //参数类型
    public String paramsType;
    //最大值
    public String max;
    //最小值
    public String min;
    //参数的值
    public String value;
    //参数的值
    public String paramDescribe;

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", paramsName='" + paramsName + '\'' +
                ", paramsType='" + paramsType + '\'' +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                ", value='" + value + '\'' +
                ", paramDescribe='" + paramDescribe + '\'' +
                '}';
    }

}

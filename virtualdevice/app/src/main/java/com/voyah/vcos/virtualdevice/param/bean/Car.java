package com.voyah.vcos.virtualdevice.param.bean;

/**
 * String paramsName,String paramsType,Object max,Object min,Object defultValue
 */
public class Car {
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
    //参数的值的描述
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

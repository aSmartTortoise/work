package com.voyah.content_provider.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.voyah.content_provider.db.Car;

import java.util.List;

@Dao
public interface CarDao {

    // 根据车ID查找对应的虚拟车
    @Query("SELECT * FROM Car WHERE paramsName = :paramsName")
    Car getCar(String paramsName);

    @Query("SELECT * FROM Car")
    List<Car> getAllCar();

    // inSert
    @Insert
    long insertCar(Car car);

    // updata
    @Update
    int updataCar(Car car);


//    // 根据用户ID查找用户
//    @Query("SELECT airSynSwitchState FROM AirDevice WHERE id = 0")
//    AirDevice findUserById2();


}

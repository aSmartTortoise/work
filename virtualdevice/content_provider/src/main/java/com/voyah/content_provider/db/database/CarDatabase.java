package com.voyah.content_provider.db.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.voyah.content_provider.db.Car;
import com.voyah.content_provider.db.dao.CarDao;


/**
 * 定义数据库抽象类
 */
@Database(entities = {Car.class}, version = 1,exportSchema = false)
public abstract class CarDatabase extends RoomDatabase {
    public abstract CarDao myCarDao();
}

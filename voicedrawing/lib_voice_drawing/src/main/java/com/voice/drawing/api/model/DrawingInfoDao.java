package com.voice.drawing.api.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DrawingInfoDao {
    @Query("SELECT * FROM DrawingInfo  ORDER BY id DESC")
    List<DrawingInfo> getAll();

    @Insert
    void insert(DrawingInfo user);

    @Update
    void update(List<DrawingInfo> user);

    @Delete
    void delete(List<DrawingInfo> user);

    @Delete
    void delete(DrawingInfo user);

    @Query("DELETE FROM DrawingInfo")
    void deleteAll();
}

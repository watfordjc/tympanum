package uk.johncook.android.tympanum.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Ostn15Dao {
    @Query("SELECT * FROM ostn15_shifts")
    List<Ostn15Point> getAll();

    @Query("SELECT * FROM ostn15_shifts WHERE Point_ID IN (:pointIds)")
    List<Ostn15Point> loadAllByIds(Integer[] pointIds);

    @Insert
    void insertAll(Ostn15Point... ostn15Points);

    @Delete
    void delete(Ostn15Point ostn15Point);
}

package com.udacity.popularmovies2.movietime.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM fav_movies")
    LiveData<List<MovieEntry>> loadAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(MovieEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(MovieEntry taskEntry);

    @Delete
    void deleteTask(MovieEntry taskEntry);

    @Query("DELETE from fav_movies WHERE tmdbid = :id;")
    void DeleteMovie(int id);


    @Query("SELECT details FROM fav_movies WHERE tmdbid = :id")
    LiveData<String> loadMovieById(int id);

    @Query("SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) FROM fav_movies WHERE tmdbid = :id;")
    LiveData<Boolean> checkIfMovieExists(int id);

}

package com.example.kalkulatorkalorii.model.db.daos

import androidx.room.*
import com.example.kalkulatorkalorii.model.db.entities.DishEntity
import java.util.*


@Dao
interface DishEntityDao {
    @Query("SELECT * from DishEntity WHERE date BETWEEN :dateFrom AND :dateTo ")
    fun byDate(dateFrom: Date, dateTo: Date): List<DishEntity>

    @Insert
    fun insert(item: DishEntity): Long

    @Delete
    fun delete(item: DishEntity)

    @Update
    fun update(list: DishEntity)
}
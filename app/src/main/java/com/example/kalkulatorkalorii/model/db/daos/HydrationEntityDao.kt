package com.example.kalkulatorkalorii.model.db.daos

import androidx.room.*
import com.example.kalkulatorkalorii.model.db.entities.HydrationEntity
import java.util.*


@Dao
interface HydrationEntityDao {
    @Insert
    fun insert(item: HydrationEntity)

    @Update
    fun update(item: HydrationEntity)

    @Query("SELECT * from HydrationEntity WHERE date BETWEEN :dateFrom AND :dateTo ")
    fun byDate(dateFrom: Date, dateTo: Date): List<HydrationEntity>
}
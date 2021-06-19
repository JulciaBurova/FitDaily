package com.example.kalkulatorkalorii.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DishEntity(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    @ColumnInfo var count: Int,
    @ColumnInfo var name: String,
    @ColumnInfo var calories: Int,
    @ColumnInfo var mealType: Int,
    @ColumnInfo var date: Date
)
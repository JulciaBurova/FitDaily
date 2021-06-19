package com.example.kalkulatorkalorii.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class HydrationEntity (
    @PrimaryKey(autoGenerate = true) var uid: Long,
    @ColumnInfo var milliliters: Int,
    @ColumnInfo var date: Date
)
package com.velocitypulse.dicecustomrules.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings_table")
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = -1,
    @ColumnInfo(name = "dice_sum_enabled") var isDiceSumEnabled: Boolean = false,
    @ColumnInfo(name = "song_enabled") var isSongEnabled: Boolean = true,
    @ColumnInfo(name = "number_of_dice") var numberOfDice: Int = 1
    )
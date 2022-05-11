package com.velocitypulse.dicecustomrules.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SettingsProfile.tableName)
data class SettingsProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = -1,
    @ColumnInfo(name = "dice_sum_enabled") var isDiceSumEnabled: Boolean = false,
    @ColumnInfo(name = "song_enabled") var isSongEnabled: Boolean = true,
    @ColumnInfo(name = "number_of_dice") var numberOfDice: Int = 1
) {
    companion object {
        const val tableName = "settings_profile_table"
    }
}
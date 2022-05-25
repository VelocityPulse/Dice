package com.velocitypulse.dicecustomrules.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Entity(tableName = SettingsProfile.tableName)
data class SettingsProfile(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "title") var title: String = "Default title",
    @ColumnInfo(name = "is_selected") var isSelected: Boolean = false,
    @ColumnInfo(name = "dice_sum_enabled") var isDiceSumEnabled: Boolean = false,
    @ColumnInfo(name = "song_enabled") var isSongEnabled: Boolean = true,
    @ColumnInfo(name = "number_of_dice") var numberOfDice: Int = 1,
    @ColumnInfo(name = "text_definition_enabled") var isDiceDescriptionEnabled: Boolean = false,
    @ColumnInfo(name = "map_definition") var serializedMapDefinition: String = ""
) {
    companion object {
        const val tableName_V1 = "app_settings_table"
        const val tableName = "settings_profile_table"
    }

    @Ignore
    val typeOfMapDefinition: Type = object : TypeToken<Map<Int?, String?>?>() {}.type

    @Ignore
    val gson: Gson = GsonBuilder().create()

    fun getMapDefinition(): MutableMap<Int, String> {
        return gson.fromJson<MutableMap<Int, String>?>(serializedMapDefinition, typeOfMapDefinition)
            ?: mutableMapOf()
    }

    fun setMapDefinition(map: Map<Int, String>) {
        serializedMapDefinition = gson.toJson(map)
    }

    override fun equals(other: Any?): Boolean {
        if (super.equals(other))
            return true
        if (other?.javaClass != javaClass)
            return false

        other as SettingsProfile
        return other.id == this.id
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
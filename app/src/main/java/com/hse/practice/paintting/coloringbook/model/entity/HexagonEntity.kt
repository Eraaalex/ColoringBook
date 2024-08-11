package com.hse.practice.paintting.coloringbook.model.entity

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hse.practice.paintting.coloringbook.utils.Converters


@Entity(tableName = "hexagons")
@androidx.room.TypeConverters(Converters::class)
data class HexagonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "image_id") val imageId: Long,
    @ColumnInfo(name = "center") val center: String,
    @ColumnInfo(name = "vertices") val vertices: String,
    @ColumnInfo(name = "color") var color: Int = Color.WHITE,
    @ColumnInfo(name = "current_color") var currentColor: Int = Color.WHITE,
    @ColumnInfo(name = "number") var number: Int
)


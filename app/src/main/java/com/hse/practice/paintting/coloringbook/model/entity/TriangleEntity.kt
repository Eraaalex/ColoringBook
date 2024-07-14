package com.hse.practice.paintting.coloringbook.model.entity

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "triangles")
data class TriangleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "image_id") val imageId: Long,
    @ColumnInfo(name = "v0_x") val v0X: Float,
    @ColumnInfo(name = "v0_y") val v0Y: Float,
    @ColumnInfo(name = "v1_x") val v1X: Float,
    @ColumnInfo(name = "v1_y") val v1Y: Float,
    @ColumnInfo(name = "v2_x") val v2X: Float,
    @ColumnInfo(name = "v2_y") val v2Y: Float,
    @ColumnInfo(name = "color") var color: Int = Color.WHITE,
    @ColumnInfo(name = "current_color") var currentColor : Int = Color.WHITE,
    @ColumnInfo(name = "number") var number: Int = -1
)

package com.hse.practice.paintting.coloringbook.model.entity

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0L,
    @ColumnInfo("name")
    val name: String = "",
    @ColumnInfo("image-data")
    val imageData: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
    @ColumnInfo("started")
    val isStarted: Boolean = false,
    @ColumnInfo("uploaded")
    val isUploaded: Boolean = false,
    @ColumnInfo("uri")
    val uri: String = Uri.encode(Uri.EMPTY.toString()),
    @ColumnInfo("selected_option")
    val selectedOption : String = "triangles"
) {}
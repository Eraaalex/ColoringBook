package com.hse.practice.paintting.coloringbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.Point
import com.hse.practice.paintting.coloringbook.model.Triangle
import com.hse.practice.paintting.coloringbook.model.entity.HexagonEntity
import com.hse.practice.paintting.coloringbook.model.entity.TriangleEntity
import java.io.ByteArrayOutputStream
import java.io.IOException

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}


class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private val gson = Gson()

    @TypeConverter
    fun fromPoint(point: Point): String {
        return gson.toJson(point)
    }

    @TypeConverter
    fun toPoint(data: String): Point {
        return gson.fromJson(data, Point::class.java)
    }

    @TypeConverter
    fun fromPointList(pointList: List<Point>): String {
        return gson.toJson(pointList)
    }

    @TypeConverter
    fun toPointList(data: String): List<Point> {
        val listType = object : TypeToken<List<Point>>() {}.type
        return gson.fromJson(data, listType)
    }
}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun TriangleEntity.toTriangle(): Triangle {
    return Triangle(
        v0 = Point(v0X.toDouble(), v0Y.toDouble()),
        v1 = Point(v1X.toDouble(), v1Y.toDouble()),
        v2 = Point(v2X.toDouble(), v2Y.toDouble()),
        color = color,
        currentColor = currentColor,
        number = number
    )
}

fun Triangle.toEntity(imageId: Long): TriangleEntity {
    return TriangleEntity(
        imageId = imageId,
        v0X = v0.x.toFloat(),
        v0Y = v0.y.toFloat(),
        v1X = v1.x.toFloat(),
        v1Y = v1.y.toFloat(),
        v2X = v2.x.toFloat(),
        v2Y = v2.y.toFloat(),
        currentColor = currentColor,
        color = color,
        number = number
    )
}

fun Hexagon.toEntity(imageId: Long): HexagonEntity {
    val convert = Converters()
    return HexagonEntity(
        imageId = imageId,
        center = convert.fromPoint(center),
        vertices = convert.fromPointList(vertices),
        color = color,
        currentColor = currentColor,
        number = number
    )
}

fun HexagonEntity.toHexagon(): Hexagon {
    val convert = Converters()
    return Hexagon(
        center = convert.toPoint(center),
        vertices = convert.toPointList(vertices),
        color = color,
        currentColor = currentColor,
        number = number
    )
}
package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import android.graphics.Color

class FilterServiceImpl : FilterService {

    override fun applyBlackAndWhiteFilter(bitmap : Bitmap) : Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blackAndWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val gray = (0.21 * red + 0.72 * green + 0.07 * blue).toInt()
                val newPixel = Color.rgb(gray, gray, gray)
                blackAndWhiteBitmap.setPixel(x, y, newPixel)
            }
        }

        return blackAndWhiteBitmap
    }
}

interface FilterService {
    fun applyBlackAndWhiteFilter(bitmap : Bitmap) : Bitmap
}

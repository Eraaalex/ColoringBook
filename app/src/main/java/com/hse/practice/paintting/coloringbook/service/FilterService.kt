package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import android.graphics.Color
import java.util.Collections

class FilterServiceImpl : FilterService {

    override fun applyBlackAndWhiteFilter(bitmap: Bitmap): Bitmap {
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

    fun getDominantColor(bitmap: Bitmap): Int {
        val colorCountMap = mutableMapOf<Int, Int>()
        val width = bitmap.width
        val height = bitmap.height

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = bitmap.getPixel(x, y)
                if (colorCountMap.containsKey(color)) {
                    colorCountMap[color] = colorCountMap[color]!! + 1
                } else {
                    colorCountMap[color] = 1
                }
            }
        }

        return Collections.max(colorCountMap.entries, Comparator.comparingInt { it.value }).key
    }

    override fun applyProportionalColorFilter(targetColor: Int, bitmap: Bitmap): Bitmap {
        val mainColor: Int = getDominantColor(bitmap)
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val mainHSV = FloatArray(3)
        Color.colorToHSV(mainColor, mainHSV)

        val targetHSV = FloatArray(3)
        Color.colorToHSV(targetColor, targetHSV)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = bitmap.getPixel(x, y)
                val pixelHSV = FloatArray(3)
                Color.colorToHSV(pixelColor, pixelHSV)

                val hue = pixelHSV[0]
                val saturation = pixelHSV[1]
                val value = pixelHSV[2]
                val newHue = targetHSV[0] * (hue / mainHSV[0])
                val newSaturation = targetHSV[1] * (saturation / mainHSV[1])
                val newValue = targetHSV[2] * (value / mainHSV[2])

                val newColor = Color.HSVToColor(
                    floatArrayOf(
                        newHue.coerceIn(0f, 360f),
                        newSaturation.coerceIn(0f, 1f),
                        newValue.coerceIn(0f, 1f)
                    )
                )

                resultBitmap.setPixel(x, y, newColor)
            }
        }

        return resultBitmap
    }

}

interface FilterService {
    fun applyBlackAndWhiteFilter(bitmap: Bitmap): Bitmap
    fun applyProportionalColorFilter(targetColor: Int, bitmap: Bitmap): Bitmap
}

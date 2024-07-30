package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.Point
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HexagonService {

    fun processImage(image: Bitmap, numHexagons: Int): List<Hexagon> {
        val hexagons = mutableListOf<Hexagon>()
        val imageWidth = image.width
        val imageHeight = image.height
        val hexRadius = calculateHexRadius(imageWidth, imageHeight, numHexagons)
        Log.e("MyApp", "hex radius $hexRadius")
        val hexHeight = hexRadius * sqrt(3.0)
        val hexWidth = hexRadius * 2
        val numCols = (imageWidth / (hexWidth * 0.75)).toInt() + 1
        val numRows = (imageHeight / hexHeight).toInt() + 1
        var hexCount = 0
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val centerX = hexWidth * 0.75 * col
                val centerY = hexHeight * row + (if (col % 2 == 0) 0.0 else hexHeight / 2.0)

                val hexagon = createHexagon(centerX, centerY, hexRadius, image, hexCount++)
                if (hexagon != null) {
                    hexagons.add(hexagon)
                }
            }
        }

        return hexagons
    }

    private fun calculateHexRadius(imageWidth: Int, imageHeight: Int, numHexagons: Int): Double {
        val imageArea = imageWidth * imageHeight
        val hexArea = imageArea / numHexagons
        return sqrt(hexArea / (1.5 * sqrt(3.0)))
    }

    private fun createHexagon(
        centerX: Double,
        centerY: Double,
        radius: Double,
        image: Bitmap,
        number: Int
    ): Hexagon? {
        val vertices = mutableListOf<Point>()
        for (i in 0 until 6) {
            val angle = Math.PI / 3 * i
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            if (x < 0 || x >= image.width || y < 0 || y >= image.height) {
                return null
            }
            vertices.add(Point(x, y))
        }
        val color = getHexagonColor(centerX, centerY, image)
        return Hexagon(
            center = Point(centerX, centerY),
            vertices = vertices,
            color = color,
            number = number
        )
    }

    private fun getHexagonColor(centerX: Double, centerY: Double, image: Bitmap): Int {
        val pixelX = centerX.toInt().coerceIn(0, image.width - 1)
        val pixelY = centerY.toInt().coerceIn(0, image.height - 1)
        return image.getPixel(pixelX, pixelY)
    }

    fun getAverageColor(image: Bitmap, hexagon: Hexagon): Int {
        val pixels = mutableListOf<Int>()
        val centerX = hexagon.center.x.toInt()
        val centerY = hexagon.center.y.toInt()
        val radius = ((hexagon.vertices[0].x - hexagon.center.x).absoluteValue).toInt()

        for (y in (centerY - radius)..(centerY + radius)) {
            for (x in (centerX - radius)..(centerX + radius)) {
                if (x in 0 until image.width && y in 0 until image.height && isPointInHexagon(
                        Point(
                            x.toDouble(),
                            y.toDouble()
                        ), hexagon
                    )
                ) {
                    pixels.add(image.getPixel(x, y))
                }
            }
        }
        val averageColor = pixels
            .fold(intArrayOf(0, 0, 0)) { acc, pixel ->
                acc[0] += Color.red(pixel)
                acc[1] += Color.green(pixel)
                acc[2] += Color.blue(pixel)
                acc
            }
            .map { it / pixels.size }
            .let { Color.rgb(it[0], it[1], it[2]) }
        return averageColor
    }

    private fun isPointInHexagon(point: Point, hexagon: Hexagon): Boolean {
        val dx = (point.x - hexagon.center.x).absoluteValue
        val dy = (point.y - hexagon.center.y).absoluteValue
        val radius = ((hexagon.vertices[0].x - hexagon.center.x).absoluteValue)
        if (dx > radius || dy > sqrt(3.0) * radius / 2) return false
        return radius * sqrt(3.0) - dy >= sqrt(3.0) * dx
    }
}
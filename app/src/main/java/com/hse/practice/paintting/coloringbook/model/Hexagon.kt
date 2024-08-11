package com.hse.practice.paintting.coloringbook.model

import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size


data class Hexagon(
    val center: Point,
    val vertices: List<Point>,
    var color: Int = Color.WHITE,
    var currentColor: Int = Color.WHITE,
    var number: Int = -1
) {
    var colorState by mutableIntStateOf(color)
    var currentColorState by mutableIntStateOf(currentColor)
    var showNumber by mutableStateOf(true)

    fun contains(point: Offset, canvasSize : Size, scale : Float, offset : Offset): Boolean {
        Log.d("MyApp", "HEX: check if $point is in $vertices")
        var intersects = 0
        val canvasWidth = canvasSize.width
        val canvasHeight = canvasSize.height
        val pointList = ArrayList<Point>()
        for (point1 in vertices){
            val centeredX = point1.x - canvasWidth / 2
            val centeredY = point1.y - canvasHeight / 2

            val scaledX = centeredX * scale
            val scaledY = centeredY * scale

            val x = scaledX + canvasWidth / 2 + offset.x
            val y = scaledY + canvasHeight / 2 + offset.y
            pointList.add(Point(x, y))
        }

        val x = point.x.toDouble()
        val y = point.y.toDouble()

        for (i in pointList.indices) {
            val v1 = pointList[i]
            val v2 = pointList[(i + 1) % pointList.size]

            if (rayIntersectsSegment(x, y, v1, v2)) {
                intersects++
            }
        }
        return (intersects % 2) == 1


    }

    private fun rayIntersectsSegment(px: Double, py: Double, v1: Point, v2: Point): Boolean {
        val (x1, y1) = v1
        val (x2, y2) = v2

        if (y1 > y2) {
            return rayIntersectsSegment(px, py, v2, v1)
        }

        if (py == y1 || py == y2) {
            return rayIntersectsSegment(px, py + 0.0001, v1, v2)
        }

        if (py < y1 || py > y2) {
            return false
        }

        if (px >= maxOf(x1, x2)) {
            return false
        }

        if (px < minOf(x1, x2)) {
            return true
        }

        val red = if (x1 != x2) (py - y1) * (x2 - x1) / (y2 - y1) + x1 else x1
        return px < red
    }
}

package com.hse.practice.paintting.coloringbook.model

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Triangle(
    val v0: Point,
    val v1: Point,
    val v2: Point,
    var color: Int = Color.WHITE,
    var currentColor: Int = Color.WHITE,
    var number: Int = -1
) {
    val circumCirc: CircumCircle = calcCircumCirc(v0, v1, v2)

    var colorState by mutableIntStateOf(color)
    var currentColorState by mutableIntStateOf(currentColor)
    var showNumber by mutableStateOf(true)

    fun inCircumcircle(v: Point): Boolean {
        val dx = circumCirc.c.x - v.x
        val dy = circumCirc.c.y - v.y
        return sqrt(dx * dx + dy * dy) <= circumCirc.r
    }

    fun calcCircumCirc(v0: Point, v1: Point, v2: Point): CircumCircle {
        val A = v1.x - v0.x
        val B = v1.y - v0.y
        val C = v2.x - v0.x
        val D = v2.y - v0.y

        val E = A * (v0.x + v1.x) + B * (v0.y + v1.y)
        val F = C * (v0.x + v2.x) + D * (v0.y + v2.y)

        val G = 2.0 * (A * (v2.y - v1.y) - B * (v2.x - v1.x))

        val center: Point
        val dx: Double
        val dy: Double

        if (abs(G).roundToInt() == 0) {
            val minx = minOf(v0.x, v1.x, v2.x)
            val miny = minOf(v0.y, v1.y, v2.y)
            val maxx = maxOf(v0.x, v1.x, v2.x)
            val maxy = maxOf(v0.y, v1.y, v2.y)

            center = Point((minx + maxx) / 2, (miny + maxy) / 2)

            dx = center.x - minx
            dy = center.y - miny
        } else {
            val cx = (D * E - B * F) / G
            val cy = (A * F - C * E) / G

            center = Point(cx, cy)

            dx = center.x - v0.x
            dy = center.y - v0.y
        }
        val radius = sqrt(dx * dx + dy * dy)

        return CircumCircle(center, radius)
    }


    fun contains(point: Offset): Boolean {
        // Проверка попадания точки в треугольник без изменения координат треугольника
        val b1 = sign(point, v0.toOffset(), v1.toOffset()) < 0.0
        val b2 = sign(point, v1.toOffset(), v2.toOffset()) < 0.0
        val b3 = sign(point, v2.toOffset(), v0.toOffset()) < 0.0
        return (b1 == b2) && (b2 == b3)
    }

    private fun sign(p1: Offset, p2: Offset, p3: Offset): Float {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }

    private fun Point.toOffset() = Offset(this.x.toFloat(), this.y.toFloat())

}
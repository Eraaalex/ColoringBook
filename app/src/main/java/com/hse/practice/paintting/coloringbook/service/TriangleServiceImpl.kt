package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import android.graphics.Color
import com.hse.practice.paintting.coloringbook.model.CircumCircle
import com.hse.practice.paintting.coloringbook.model.Edge
import com.hse.practice.paintting.coloringbook.model.Point
import com.hse.practice.paintting.coloringbook.model.Triangle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin


interface TriangleServiceA {
    fun triangulate(vertices: List<Point>): List<Triangle>
    fun getAverageColor(triangle: Triangle, bitmap: Bitmap): Int

}

class TriangleServiceImpl : TriangleServiceA {
    private fun getBounding(enclosing: CircumCircle): Triangle {
        val ang1 = 0.0
        val ang2 = PI * 2 / 3
        val ang3 = PI * 2 / 3 * 2

        val ev0 = Point(
            enclosing.c.x + enclosing.r * cos(ang1) * 2,
            enclosing.c.y + enclosing.r * sin(ang1) * 2
        )
        val ev1 = Point(
            enclosing.c.x + enclosing.r * cos(ang2) * 2,
            enclosing.c.y + enclosing.r * sin(ang2) * 2
        )
        val ev2 = Point(
            enclosing.c.x + enclosing.r * cos(ang3) * 2,
            enclosing.c.y + enclosing.r * sin(ang3) * 2
        )

        return Triangle(ev0, ev1, ev2)
    }

    private fun uniqueEdges(edges: List<Edge>): List<Edge> {
        return edges.filter { edge1 -> edges.count { it.equals(edge1) } == 1 }
    }

    private fun addVertex(point: Point, triangles: List<Triangle>): List<Triangle> {
        val edges = mutableListOf<Edge>()
        val filteredTriangles = triangles.filter { triangle ->
            if (triangle.inCircumcircle(point)) {
                edges.add(Edge(triangle.v0, triangle.v1))
                edges.add(Edge(triangle.v1, triangle.v2))
                edges.add(Edge(triangle.v2, triangle.v0))
                false
            } else {
                true
            }
        }
        val uniqueEdges = uniqueEdges(edges)
        return filteredTriangles + uniqueEdges.map { edge -> Triangle(edge.v0, edge.v1, point) }
    }

    override fun triangulate(vertices: List<Point>): List<Triangle> {
        val e = makeCircle(vertices)
        val st = getBounding(e ?: return emptyList())
        var triangles = listOf(st)
        vertices.forEach { vertex ->
            triangles = addVertex(vertex, triangles)
        }
        return triangles.filterNot { triangle ->
            listOf(
                triangle.v0,
                triangle.v1,
                triangle.v2
            ).any { it.equals(st.v0) || it.equals(st.v1) || it.equals(st.v2) }
        }
    }

    private fun makeCircle(points: List<Point>): CircumCircle? {
        val shuffled = points.shuffled()
        var c: CircumCircle? = null
        shuffled.forEachIndexed { i, p ->
            if (c == null || !isInCircle(c!!, p)) {
                c = makeCircleOnePoint(shuffled.subList(0, i + 1), p)
            }
        }
        return c
    }

    private fun makeCircleOnePoint(points: List<Point>, p: Point): CircumCircle {
        var c = CircumCircle(p, 0.0)
        points.forEachIndexed { i, q ->
            if (!isInCircle(c, q)) {
                c = if (c.r == 0.0) makeDiameter(p, q)
                else makeCircleTwoPoints(points.subList(0, i + 1), p, q)
            }
        }
        return c
    }

    private fun makeCircleTwoPoints(points: List<Point>, p: Point, q: Point): CircumCircle {
        val circ = makeDiameter(p, q)
        var left: CircumCircle = CircumCircle(Point(0.0, 0.0), 0.0)
        var right: CircumCircle = CircumCircle(Point(0.0, 0.0), 0.0)
        points.forEach { r ->
            if (isInCircle(circ, r)) return@forEach
            val cross = crossProduct(p.x, p.y, q.x, q.y, r.x, r.y)
            val c = makeCircumcircle(p, q, r)
            if (c != null) {
                when {
                    cross > 0 && (left.r == 0.0 || crossProduct(
                        p.x,
                        p.y,
                        q.x,
                        q.y,
                        c.c.x,
                        c.c.y
                    ) > crossProduct(p.x, p.y, q.x, q.y, left.c.x, left.c.y)) -> left = c

                    cross < 0 && (right.r == 0.0 || crossProduct(
                        p.x,
                        p.y,
                        q.x,
                        q.y,
                        c.c.x,
                        c.c.y
                    ) < crossProduct(p.x, p.y, q.x, q.y, right.c.x, right.c.y)) -> right = c
                }
            }
        }
        return when {
            left.r == 0.0 && right.r == 0.0 -> circ
            left.r == 0.0 && right.r != 0.0 -> right
            left.r != 0.0 && right.r == 0.0 -> left
            else -> if (left.r <= right.r) left else right
        }
    }

    private fun makeDiameter(a: Point, b: Point): CircumCircle {
        val cx = (a.x + b.x) / 2
        val cy = (a.y + b.y) / 2
        val r0 = distance(cx, cy, a.x, a.y)
        val r1 = distance(cx, cy, b.x, b.y)
        return CircumCircle(Point(cx, cy), max(r0, r1))
    }

    private fun makeCircumcircle(a: Point, b: Point, c: Point): CircumCircle? {
        val ox = (minOf(a.x, b.x, c.x) + maxOf(a.x, b.x, c.x)) / 2
        val oy = (minOf(a.y, b.y, c.y) + maxOf(a.y, b.y, c.y)) / 2
        val ax = a.x - ox
        val ay = a.y - oy
        val bx = b.x - ox
        val by = b.y - oy
        val cx = c.x - ox
        val cy = c.y - oy
        val d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2
        if (d == 0.0) return null
        val x =
            ox + ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d
        val y =
            oy + ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d
        val ra = distance(x, y, a.x, a.y)
        val rb = distance(x, y, b.x, b.y)
        val rc = distance(x, y, c.x, c.y)
        return CircumCircle(Point(x, y), maxOf(ra, rb, rc))
    }

    private fun isInCircle(c: CircumCircle, p: Point): Boolean {
        return distance(p.x, p.y, c.c.x, c.c.y) <= c.r * (1 + 1e-14)
    }

    private fun crossProduct(
        x0: Double,
        y0: Double,
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): Double {
        return (x1 - x0) * (y2 - y0) - (y1 - y0) * (x2 - x0)
    }

    private fun distance(x0: Double, y0: Double, x1: Double, y1: Double): Double {
        return hypot(x0 - x1, y0 - y1)
    }

    override fun getAverageColor(triangle: Triangle, bitmap: Bitmap): Int {
        val bounds = getTriangleBounds(triangle)
        var red = 0
        var green = 0
        var blue = 0
        var count = 0

        for (y in bounds.top until bounds.bottom) {
            for (x in bounds.left until bounds.right) {
                if (isPointInTriangle(Point(x.toDouble(), y.toDouble()), triangle)) {
                    val pixel = bitmap.getPixel(x, y)
                    red += Color.red(pixel)
                    green += Color.green(pixel)
                    blue += Color.blue(pixel)
                    count++
                }
            }
        }

        return if (count > 0) {
            Color.rgb(red / count, green / count, blue / count)
        } else {
            Color.BLACK
        }
    }

    private fun getTriangleBounds(triangle: Triangle): android.graphics.Rect {
        val l = listOf(triangle.v0, triangle.v1, triangle.v2)
        val minX = l.minOf { it.x }.toInt()
        val maxX = l.maxOf { it.x }.toInt()
        val minY = l.minOf { it.y }.toInt()
        val maxY = l.maxOf { it.y }.toInt()
        return android.graphics.Rect(minX, minY, maxX, maxY)
    }

    private fun isPointInTriangle(point: Point, triangle: Triangle): Boolean {
        val (x, y) = point
        val (x1, y1) = triangle.v0
        val (x2, y2) = triangle.v1
        val (x3, y3) = triangle.v2

        val denominator = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3)
        val a = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator
        val b = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator
        val c = 1 - a - b

        return 0 <= a && a <= 1 && 0 <= b && b <= 1 && 0 <= c && c <= 1
    }
}
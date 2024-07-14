package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import com.hse.practice.paintting.coloringbook.model.Point
import javax.inject.Inject
import kotlin.random.Random


interface PointsGenerator {
    fun generatePoints(image: Bitmap, numberOfTriangle: Int): List<Point>
}

class PointsGeneratorImpl @Inject constructor() : PointsGenerator {

    override fun generatePoints(image: Bitmap, numberOfTriangle: Int): List<Point> {
        val numberOfPoints = (numberOfTriangle + 4) / 2
        val numberOfBoundaryPoints = numberOfPoints / 24
        val points: MutableList<Point> = mutableListOf()

        val width = image.width.toDouble()
        val height = image.height.toDouble()

        points.run {
            add(Point(0.0, 0.0))
            add(Point(width, 0.0))
            add(Point(0.0, height))
            add(Point(width, height))
        }

        for (i in 0..<numberOfPoints) {
            points.add(
                Point(
                    Random.nextDouble(width*0.15, width*0.85),
                    Random.nextDouble(height*0.15, height*0.85)
                )
            )
        }

        // Generation of boundaries' points
        for (i in 1..<numberOfBoundaryPoints) {
            points.run {
                val t = i / numberOfBoundaryPoints.toDouble()
                points.add(Point((t * image.width), 0.0))
                points.add(Point((t * image.width), image.height.toDouble()))
                points.add(Point(0.0, (t * image.height)))
                points.add(Point(image.width.toDouble(), (t * image.height)))
            }
        }

        return points
    }
}
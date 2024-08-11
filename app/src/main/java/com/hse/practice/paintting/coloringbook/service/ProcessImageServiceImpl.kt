package com.hse.practice.paintting.coloringbook.service

import android.graphics.Bitmap
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.Triangle
import javax.inject.Inject

interface ProcessImageService {
    public fun processImageHexagons(image: Bitmap, numTriangles: Int): List<Hexagon>
    public fun processImageTriangles(image: Bitmap, numTriangles: Int): List<Triangle>
}

class ProcessImageServiceImpl @Inject constructor(
    private val pointsGenerator: PointsGenerator,
    private val triangleService: TriangleServiceA,
    private val hexagonService: HexagonService
) : ProcessImageService {

    private val colorToNumberMap_ = mutableMapOf<Int, Int>()
    val colorToNumberMap = colorToNumberMap_

    override fun processImageHexagons(image: Bitmap, numTriangles: Int): List<Hexagon> {
        val hexes = hexagonService.processImage(image, numTriangles)
        val colorToNumberMap = mutableMapOf<Int, Int>()
        var nextColorNumber = 1
        return hexes.map { hexagon ->
            val averageColor = hexagonService.getAverageColor(image, hexagon)
            val colorNumber = colorToNumberMap.getOrPut(averageColor) {
                nextColorNumber++
            }
            hexagon.copy(color = averageColor, number = colorNumber)
        }
    }

    override fun processImageTriangles(image: Bitmap, numTriangles: Int): List<Triangle> {
        val points = pointsGenerator.generatePoints(image, numTriangles)
        val triangles = triangleService.triangulate(points)
        var nextColorNumber = 1
        return triangles.map { triangle ->
            val averageColor = triangleService.getAverageColor(triangle, image)
            val colorNumber = colorToNumberMap_.getOrPut(averageColor) {
                nextColorNumber++
            }
            triangle.copy(color = averageColor, number = colorNumber)
        }
    }

}
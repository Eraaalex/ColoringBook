package com.hse.practice.paintting.coloringbook.data.triangle

import android.util.Log
import com.hse.practice.paintting.coloringbook.model.Triangle
import com.hse.practice.paintting.coloringbook.model.entity.TriangleEntity
import javax.inject.Inject

class TriangleRepository  @Inject constructor(private val triangleDao: TriangleDao) {

    suspend fun insertTriangle(triangleEntity: TriangleEntity) {
        triangleDao.insert(triangleEntity)
    }
    suspend fun insertTriangles(triangles: List<Triangle>, imageId: Long) {
        triangleDao.insertTrianglesAtomic(triangles, imageId)
    }

    suspend fun updateTriangle(triangle: Triangle, imageId : Long) {
        val triangleEntity = triangleDao.findTriangleByCoordinates(
            triangle.v0.x.toFloat(), triangle.v0.y.toFloat(),
            triangle.v1.x.toFloat(), triangle.v1.y.toFloat(),
            triangle.v2.x.toFloat(), triangle.v2.y.toFloat(), imageId
        )
        if (triangleEntity != null) {
            val updatedEntity = triangleEntity.copy(
                color = triangle.color,
                currentColor = triangle.currentColor,
                number = triangle.number
            )
            triangleDao.update(updatedEntity)
        } else {
            Log.e("TriangleRepository", "Triangle not found for update: $triangle")
        }
    }

    suspend fun getTrianglesForImage(imageId: Long): List<TriangleEntity> {
        return triangleDao.getTrianglesForImage(imageId)
    }

    suspend fun getAllTriangles(): List<TriangleEntity> {
        return triangleDao.getAllTriangles()

    }
}
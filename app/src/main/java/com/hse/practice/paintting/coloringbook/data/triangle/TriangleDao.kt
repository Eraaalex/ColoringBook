package com.hse.practice.paintting.coloringbook.data.triangle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hse.practice.paintting.coloringbook.model.Triangle
import com.hse.practice.paintting.coloringbook.model.entity.TriangleEntity
import com.hse.practice.paintting.coloringbook.utils.toEntity

@Dao
interface TriangleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(triangle: TriangleEntity)

    @Update
    suspend fun update(triangle: TriangleEntity)

    @Query("SELECT * FROM triangles WHERE image_id = :imageId")
    suspend fun getTrianglesForImage(imageId: Long): List<TriangleEntity>

    @Query("SELECT * FROM triangles")
    suspend fun getAllTriangles(): List<TriangleEntity>

    @Transaction
    suspend fun insertTrianglesAtomic(triangles: List<Triangle>, imageId: Long) {
        triangles.forEach {
            insert(it.toEntity(imageId))
        }
    }

    @Query("SELECT * FROM triangles WHERE image_id = :imageId AND v0_x = :v0X AND v0_y = :v0Y AND v1_x = :v1X AND v1_y = :v1Y AND v2_x = :v2X AND v2_y = :v2Y")
    suspend fun findTriangleByCoordinates(
        v0X: Float,
        v0Y: Float,
        v1X: Float,
        v1Y: Float,
        v2X: Float,
        v2Y: Float,
        imageId: Long
    ): TriangleEntity?


}
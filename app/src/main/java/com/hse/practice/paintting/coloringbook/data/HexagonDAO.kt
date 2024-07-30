package com.hse.practice.paintting.coloringbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.entity.HexagonEntity
import com.hse.practice.paintting.coloringbook.toEntity

@Dao
interface HexagonDAO {

    @Insert
    fun insert(hexagon: HexagonEntity)

    @Query("SELECT * FROM hexagons WHERE image_id = :imageId")
    fun getHexagonsByImageId(imageId: Long): List<HexagonEntity>

    @Query("SELECT * FROM hexagons")
    fun getAllHexagons(): List<HexagonEntity>

    @Update
    suspend fun update(hexagon: HexagonEntity)

    @Transaction
    suspend fun insertTrianglesAtomic(hexagons: List<Hexagon>, imageId: Long) {
        hexagons.forEach {
            insert(it.toEntity(imageId))
        }
    }

    @Query("SELECT * FROM hexagons WHERE image_id = :imageId AND vertices = :vertices")
    suspend fun findHexagonByCoordinates(vertices: String, imageId: Long): HexagonEntity?


}
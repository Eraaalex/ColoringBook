package com.hse.practice.paintting.coloringbook.data

import android.util.Log
import com.hse.practice.paintting.coloringbook.utils.Converters
import com.hse.practice.paintting.coloringbook.model.Hexagon
import com.hse.practice.paintting.coloringbook.model.entity.HexagonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HexagonRepository @Inject constructor(private val hexagonDao: HexagonDAO) {

    suspend fun insertHexagon(hexagon: HexagonEntity) {
        return withContext(Dispatchers.IO) {
            hexagonDao.insert(hexagon)
        }
    }

    suspend fun insertAllHexagons(hexagonEntity: List<Hexagon>, imageId: Long) {
        Log.e("MyApp", "started insert hexes")
        hexagonDao.insertTrianglesAtomic(hexagonEntity, imageId)
    }

    suspend fun getAllHexagons(): List<HexagonEntity> {
        return withContext(Dispatchers.IO) {
            hexagonDao.getAllHexagons()
        }
    }

    suspend fun getHexagonsForImage(imageId: Long): List<HexagonEntity> {
        return withContext(Dispatchers.IO) {
            hexagonDao.getHexagonsByImageId(imageId)
        }
    }

    suspend fun updateHexagon(hexagon: Hexagon, imageId: Long) {
        val convert = Converters()
        val verticesString = convert.fromPointList(hexagon.vertices)

        val hexagonEntity = hexagonDao.findHexagonByCoordinates(verticesString, imageId)

        if (hexagonEntity != null) {
            val updatedEntity = hexagonEntity.copy(
                color = hexagon.color,
                currentColor = hexagon.currentColor,
                number = hexagon.number
            )

            hexagonDao.update(updatedEntity)
        } else {
            Log.e("HexagonRepository", "Hexagon not found for update: $hexagon")
        }
    }

}
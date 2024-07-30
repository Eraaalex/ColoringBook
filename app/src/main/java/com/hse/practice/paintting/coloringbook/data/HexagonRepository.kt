package com.hse.practice.paintting.coloringbook.data

import android.util.Log
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

    suspend fun updateHexagon(hexagonEntity: HexagonEntity) {
        withContext(Dispatchers.IO) {
            hexagonDao.update(hexagonEntity)
        }
    }

}
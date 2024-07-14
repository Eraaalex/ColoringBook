package com.hse.practice.paintting.coloringbook.data.image

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity

@Dao
interface ImageDAO {
    @Query("SELECT * FROM images WHERE name LIKE :name LIMIT 1")
    fun fetchImageByName(name: String): ImageEntity?

    @Query("SELECT * FROM images")
    fun getAllImages(): List<ImageEntity>

    @Insert
    fun insertImages(vararg imageEntities: ImageEntity): List<Long>

    @Transaction
    suspend fun insertImages(imageEntities: List<ImageEntity>): List<Long> {
        return imageEntities.map {
            insert(it)
        }
    }

    @Delete
    fun delete(imageEntity: ImageEntity)

    @Query("SELECT * FROM images WHERE uri LIKE :uri LIMIT 1")
    fun getByUri(uri: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity) : Long

    @Query("SELECT * FROM images WHERE uid = :uid")
    fun getById(uid: Long): ImageEntity?

    @Update
    suspend fun update(image: ImageEntity)
}


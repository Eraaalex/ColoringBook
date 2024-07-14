package com.hse.practice.paintting.coloringbook.data.image

import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ImageRepository  @Inject constructor(private val imageDao: ImageDAO) {

    suspend fun insertImage(imageEntity: ImageEntity) : Long {
        return withContext(Dispatchers.IO) {
            imageDao.insertImages(imageEntity).first()
        }
    }

    suspend fun updateImage(imageEntity: ImageEntity) {
        withContext(Dispatchers.IO) {
            imageDao.update(imageEntity)
        }
    }

    suspend fun getImageByName(name: String): ImageEntity? {
        return withContext(Dispatchers.IO) {
            imageDao.fetchImageByName(name)
        }
    }

    suspend fun getAllImages(): List<ImageEntity> {
        return withContext(Dispatchers.IO) {
            imageDao.getAllImages()
        }
    }

    suspend fun getImageByUri(uri: String): ImageEntity? {
        return withContext(Dispatchers.IO) {
            imageDao.getByUri(uri)
        }
    }

    suspend fun getImageById(uid: Long): ImageEntity? {
        return withContext(Dispatchers.IO) {
            imageDao.getById(uid)
        }
    }

}
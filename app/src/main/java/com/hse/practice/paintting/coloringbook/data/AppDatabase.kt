package com.hse.practice.paintting.coloringbook.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hse.practice.paintting.coloringbook.Converters
import com.hse.practice.paintting.coloringbook.R
import com.hse.practice.paintting.coloringbook.data.image.ImageDAO
import com.hse.practice.paintting.coloringbook.data.triangle.TriangleDao
import com.hse.practice.paintting.coloringbook.model.entity.HexagonEntity
import com.hse.practice.paintting.coloringbook.model.entity.ImageEntity
import com.hse.practice.paintting.coloringbook.model.entity.TriangleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

@Database(entities = [ImageEntity::class, TriangleEntity::class, HexagonEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDAO
    abstract fun triangleDao(): TriangleDao
    abstract fun hexagonDao(): HexagonDAO

    private class AppDatabaseCallback(
        private val scope: CoroutineScope,
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.imageDao(), context)
                }
            }
        }


        private suspend fun populateDatabase(imageDao: ImageDAO, context: Context) {
            val imageUrls = fetchImageUrlsFromStorage()
            imageUrls.forEach { imageUrl ->
                val name = imageUrl.substringAfterLast("/")
                saveImageToLocalDatabase(context, imageDao, name, imageUrl)
                Log.e("MyApp", "saved images = $imageUrl")

            }
            Log.e("MyApp", "saved images = ${imageDao.getAllImages()}")
            val images: List<ImageEntity> = listOf(
                ImageEntity(
                    name = "bulbazaur",
                    uri = getUriForResource(context, R.drawable.bulbazaur)
                ),
            )
            imageDao.insertImages(images)
        }


        suspend fun fetchImageUrlsFromStorage(): List<String> {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("images")
            val result = storageRef.listAll().await()
            Log.e("MyApp", "ыещкфпу = $storage, result = ${result.items}")
            return result.items.mapNotNull { it.downloadUrl.await().toString() }
        }
        fun loadBitmapFromUrl(context: Context, url: String, onComplete: (Bitmap?) -> Unit) {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        onComplete(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }

        suspend fun saveImageToLocalDatabase(context: Context, imageDao: ImageDAO, name: String, imageUrl: String) {
            loadBitmapFromUrl(context, imageUrl) { bitmap ->
                if (bitmap != null) {
                    val imageEntity = ImageEntity(
                        name = name,
                        imageData = bitmap,
                        uri = imageUrl
                    )
                    imageDao.insertImages(imageEntity)
                    Log.d("MyApp", "Image saved to local database")
                } else {
                    Log.e("MyApp", "Failed to load bitmap from URL")
                }
            }
        }


    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "image_database"
                )
                    .addCallback(AppDatabaseCallback(scope, context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

fun getBitmapFromDrawable(context: Context, resId: Int): Bitmap {
    return BitmapFactory.decodeResource(context.resources, resId)
}

fun getUriForResource(context: Context, resId: Int): String {
    return "android.resource://${context.packageName}/$resId"
}


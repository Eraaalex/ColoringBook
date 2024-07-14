package com.hse.practice.paintting.coloringbook.di

import android.content.Context
import com.hse.practice.paintting.coloringbook.data.AppDatabase
import com.hse.practice.paintting.coloringbook.data.HexagonDAO
import com.hse.practice.paintting.coloringbook.data.image.ImageDAO
import com.hse.practice.paintting.coloringbook.data.image.ImageRepository
import com.hse.practice.paintting.coloringbook.data.triangle.TriangleDao
import com.hse.practice.paintting.coloringbook.data.triangle.TriangleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun provideImageDao(database: AppDatabase): ImageDAO {
        return database.imageDao()
    }

    @Singleton
    @Provides
    fun provideTriangleDao(database: AppDatabase): TriangleDao {
        return database.triangleDao()
    }

    @Singleton
    @Provides
    fun provideHexagonDao(database: AppDatabase): HexagonDAO {
        return database.hexagonDao()
    }

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }


    @Singleton
    @Provides
    fun provideImageDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, scope)
    }

    @Singleton
    @Provides
    fun provideImageRepository(imageDao: ImageDAO): ImageRepository {
        return ImageRepository(imageDao)
    }

    @Singleton
    @Provides
    fun provideTriangleRepository(dao: TriangleDao): TriangleRepository {
        return TriangleRepository(dao)
    }
}


package com.hse.practice.paintting.coloringbook.di

import com.hse.practice.paintting.coloringbook.service.FilterService
import com.hse.practice.paintting.coloringbook.service.FilterServiceImpl
import com.hse.practice.paintting.coloringbook.service.HexagonService
import com.hse.practice.paintting.coloringbook.service.PointsGenerator
import com.hse.practice.paintting.coloringbook.service.PointsGeneratorImpl
import com.hse.practice.paintting.coloringbook.service.ProcessImageService
import com.hse.practice.paintting.coloringbook.service.ProcessImageServiceImpl
import com.hse.practice.paintting.coloringbook.service.TriangleServiceA
import com.hse.practice.paintting.coloringbook.service.TriangleServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun providePointsGenerator(): PointsGenerator {
        return PointsGeneratorImpl()
    }

    @Provides
    @Singleton
    fun provideTriangleService(): TriangleServiceA {
        return TriangleServiceImpl()
    }

    @Provides
    @Singleton
    fun provideHexagonService(): HexagonService {
        return HexagonService()
    }

    @Provides
    @Singleton
    fun provideFilterSevice() : FilterService {
        return FilterServiceImpl()
    }

    @Provides
    @Singleton
    fun provideProcessImageService(
        pointsGenerator: PointsGenerator,
        triangleService: TriangleServiceA,
        hexagonService: HexagonService
    ): ProcessImageService {
        return ProcessImageServiceImpl(pointsGenerator, triangleService, hexagonService)
    }


}
package com.ma.tehro.di

import com.ma.tehro.services.DefaultLocationClient
import com.ma.tehro.services.LocationClient
import android.app.Application
import android.content.Context
import com.ma.tehro.R
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.DataCorrectionRepository
import com.ma.tehro.data.repo.DataCorrectionRepositoryImpl
import com.ma.tehro.data.repo.LineRepository
import com.ma.tehro.data.repo.LineRepositoryImpl
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.data.repo.PathRepositoryImpl
import com.ma.tehro.data.repo.TrainScheduleRepository
import com.ma.tehro.data.repo.TrainScheduleRepositoryImpl
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.LocationTrackerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideStations(context: Application, json: Json): Map<String, Station> {
        val stationsJson = context.resources.openRawResource(R.raw.stations)
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString(stationsJson)
    }

    @Provides
    @Singleton
    fun providePathRepo(stations: Map<String, Station>): PathRepository =
        PathRepositoryImpl(stations)

    @Provides
    @Singleton
    fun provideLineRepo(stations: Map<String, Station>): LineRepository =
        LineRepositoryImpl(stations)

    @Provides
    @Singleton
    fun provideStationCorrectionRepo(json: Json): DataCorrectionRepository =
        DataCorrectionRepositoryImpl(json)

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context,
    ): LocationClient {
        return DefaultLocationClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        locationClient: LocationClient,
        stations: Map<String, Station>
    ): LocationTracker {
        return LocationTrackerImpl(locationClient, stations)
    }

    @Provides
    @Singleton
    fun provideTrainScheduleRepository(
        @ApplicationContext context: Context,
        json: Json
    ): TrainScheduleRepository {
        return TrainScheduleRepositoryImpl(context, json)
    }
}

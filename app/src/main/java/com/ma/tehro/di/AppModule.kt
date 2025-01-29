package com.ma.tehro.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ma.tehro.R
import com.ma.tehro.data.Station
import com.ma.tehro.services.DefaultLocationClient
import com.ma.tehro.services.LocationClient
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.LocationTrackerImpl
import com.ma.tehro.data.repo.LineRepository
import com.ma.tehro.data.repo.LineRepositoryImpl
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.data.repo.PathRepositoryImpl
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
    fun provideStations(context: Application): Map<String, Station> {
        val stationsJson = context.resources.openRawResource(R.raw.stations_updated)
            .bufferedReader()
            .use { it.readText() }

        return Json.decodeFromString(stationsJson)
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
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationClient(
        fusedLocationClient: FusedLocationProviderClient
    ): LocationClient {
        return DefaultLocationClient(fusedLocationClient)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        locationClient: LocationClient,
        stations: Map<String, Station>
    ): LocationTracker {
        return LocationTrackerImpl(locationClient, stations)
    }
}

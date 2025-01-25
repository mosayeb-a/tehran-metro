package com.ma.tehro.di

import android.app.Application
import com.ma.tehro.R
import com.ma.tehro.data.Station
import com.ma.tehro.ui.detail.repo.LineRepository
import com.ma.tehro.ui.detail.repo.LineRepositoryImpl
import com.ma.tehro.ui.detail.repo.PathRepository
import com.ma.tehro.ui.detail.repo.PathRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}

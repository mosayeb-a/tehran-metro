package com.ma.tehro.di

import com.ma.tehro.services.DefaultLocationClient
import com.ma.tehro.services.LocationClient
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ma.tehro.R
import com.ma.tehro.data.Place
import com.ma.tehro.data.PlaceCategory
import com.ma.tehro.data.PlaceCategorySerializer
import com.ma.tehro.data.PlaceType
import com.ma.tehro.data.PlaceTypeSerializer
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.DataCorrectionRepository
import com.ma.tehro.data.repo.DataCorrectionRepositoryImpl
import com.ma.tehro.data.repo.LineRepository
import com.ma.tehro.data.repo.LineRepositoryImpl
import com.ma.tehro.data.repo.PathRepository
import com.ma.tehro.data.repo.PathRepositoryImpl
import com.ma.tehro.data.repo.PlacesRepository
import com.ma.tehro.data.repo.PlacesRepositoryImpl
import com.ma.tehro.data.repo.PreferencesRepository
import com.ma.tehro.data.repo.PreferencesRepositoryImpl
import com.ma.tehro.data.repo.TrainScheduleRepository
import com.ma.tehro.data.repo.TrainScheduleRepositoryImpl
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.LocationTrackerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
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
            serializersModule = SerializersModule {
                contextual(PlaceType::class, PlaceTypeSerializer)
                contextual(PlaceCategory::class, PlaceCategorySerializer)
            }
        }
    }

    @Provides
    @Singleton
    fun provideStations(
        context: Application,
        json: Json,
        ioDispatcher: CoroutineDispatcher
    ): Map<String, Station> = runBlocking {
        withContext(ioDispatcher) {
            val stationsJson = context.resources.openRawResource(R.raw.stations)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString(stationsJson)
        }
    }

    @Provides
    @Singleton
    fun providePlaces(
        context: Application,
        json: Json,
        ioDispatcher: CoroutineDispatcher
    ): List<Place> = runBlocking {
        withContext(ioDispatcher) {
            val placesJson = context.resources.openRawResource(R.raw.places)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString(placesJson)
        }
    }

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO


    @Provides
    @Singleton
    fun providePlacesRepo(places: List<Place>): PlacesRepository =
        PlacesRepositoryImpl(places)

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

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("app_settings")
        }
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(dataStore)
    }
}

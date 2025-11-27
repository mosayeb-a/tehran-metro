package com.ma.tehro.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ma.tehro.R
import com.ma.tehro.data.Place
import com.ma.tehro.data.PlaceCategory
import com.ma.tehro.data.PlaceCategorySerializer
import com.ma.tehro.data.PlaceType
import com.ma.tehro.data.PlaceTypeSerializer
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.DataCorrectionRepositoryImpl
import com.ma.tehro.data.repo.LineRepositoryImpl
import com.ma.tehro.data.repo.PathRepositoryImpl
import com.ma.tehro.data.repo.PlacesRepositoryImpl
import com.ma.tehro.data.repo.PreferencesRepositoryImpl
import com.ma.tehro.data.repo.TrainScheduleRepository
import com.ma.tehro.data.repo.TrainScheduleRepositoryImpl
import com.ma.tehro.domain.repo.DataCorrectionRepository
import com.ma.tehro.domain.repo.LineRepository
import com.ma.tehro.domain.repo.PathRepository
import com.ma.tehro.domain.repo.PlacesRepository
import com.ma.tehro.domain.repo.PreferencesRepository
import com.ma.tehro.domain.usecase.PathTimeCalculator
import com.ma.tehro.feature.line.LineViewModel
import com.ma.tehro.feature.line.stations.StationsViewModel
import com.ma.tehro.feature.more.PreferencesViewModel
import com.ma.tehro.feature.shortestpath.pathfinder.PathViewModel
import com.ma.tehro.feature.shortestpath.places.PlaceSelectionViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelectionViewModel
import com.ma.tehro.feature.submit_suggestion.SubmitSuggestionViewModel
import com.ma.tehro.feature.train_schedule.TrainScheduleViewModel
import com.ma.tehro.services.DefaultLocationClient
import com.ma.tehro.services.LocationClient
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.LocationTrackerImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(PlaceType::class, PlaceTypeSerializer)
                contextual(PlaceCategory::class, PlaceCategorySerializer)
            }
        }
    }

    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(json = get())
            }
        }
    }

    single<CoroutineDispatcher> { Dispatchers.IO }

    single<Map<String, Station>> {
        val context = androidContext()
        val json: Json = get()
        val ioDispatcher: CoroutineDispatcher = get()

        runBlocking {
            withContext(ioDispatcher) {
                val stationsJson = context.resources.openRawResource(R.raw.stations)
                    .bufferedReader()
                    .use { it.readText() }
                json.decodeFromString<Map<String, Station>>(stationsJson)
            }
        }
    }

    single<List<Place>> {
        val json: Json = get()
        val ioDispatcher: CoroutineDispatcher = get()

        runBlocking {
            withContext(ioDispatcher) {
                val placesJson = androidContext().resources.openRawResource(R.raw.places)
                    .bufferedReader()
                    .use { it.readText() }
                json.decodeFromString(placesJson)
            }
        }
    }

    singleOf(::PlacesRepositoryImpl) { bind<PlacesRepository>() }
    singleOf(::PathRepositoryImpl) { bind<PathRepository>() }
    singleOf(::LineRepositoryImpl) { bind<LineRepository>() }
    singleOf(::DataCorrectionRepositoryImpl) { bind<DataCorrectionRepository>() }
    singleOf(::TrainScheduleRepositoryImpl) { bind<TrainScheduleRepository>() }

    single<LocationClient> { DefaultLocationClient(androidContext()) }
    single<LocationTracker> { LocationTrackerImpl(locationClient = get(), stations = get()) }

    single {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("app_settings")
        }
    }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    factory { PathTimeCalculator(trainScheduleRepository = get()) }

    viewModel<PreferencesViewModel> { PreferencesViewModel(preferencesRepository = get()) }
    viewModel<LineViewModel> { LineViewModel(lineRepository = get()) }
    viewModel<StationsViewModel> {
        StationsViewModel(
            lineRepository = get(),
            savedStateHandle = get()
        )
    }
    viewModel<PathViewModel> {
        PathViewModel(
            pathRepository = get(),
            pathTimeCalculator = get(),
            savedStateHandle = get()
        )
    }
    viewModel<PlaceSelectionViewModel> {
        PlaceSelectionViewModel(
            showPlacesByCategory = get(),
            getNearbyPlaceStations = get()
        )
    }
    viewModel<StationSelectionViewModel> {
        StationSelectionViewModel(
            pathRepository = get(),
            locationTracker = get()
        )
    }
    viewModel<SubmitSuggestionViewModel> {
        SubmitSuggestionViewModel(dataCorrectionRepository = get())
    }
    viewModel<TrainScheduleViewModel> {
        TrainScheduleViewModel(savedStateHandle = get(), scheduleRepository = get())
    }
}
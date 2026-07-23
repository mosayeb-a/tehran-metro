package com.ma.tehro.di

import com.ma.tehro.data.feedback.repository.FeedbackRepositoryImpl
import com.ma.tehro.data.line.repository.LineRepositoryImpl
import com.ma.tehro.data.path.repository.PathRepositoryImpl
import com.ma.tehro.data.place.PlaceCategory
import com.ma.tehro.data.place.PlaceCategorySerializer
import com.ma.tehro.data.place.PlaceType
import com.ma.tehro.data.place.PlaceTypeSerializer
import com.ma.tehro.data.place.repository.PlacesRepositoryImpl
import com.ma.tehro.data.podcast.repository.PodcastRepositoryImpl
import com.ma.tehro.data.podcast.repository.source.local.PodcastLocalDataSource
import com.ma.tehro.data.podcast.repository.source.local.PodcastLocalDataSourceImpl
import com.ma.tehro.data.podcast.repository.source.remote.PodcastRemoteDataSource
import com.ma.tehro.data.podcast.repository.source.remote.PodcastRemoteDataSourceImpl
import com.ma.tehro.data.preferences.repository.PreferencesRepositoryImpl
import com.ma.tehro.data.schedule.repository.ScheduleRepositoryImpl
import com.ma.tehro.domain.feedback.repository.FeedbackRepository
import com.ma.tehro.domain.line.repository.LineRepository
import com.ma.tehro.domain.path.PathTimeCalculator
import com.ma.tehro.domain.path.repository.PathRepository
import com.ma.tehro.domain.place.FindNearbyStations
import com.ma.tehro.domain.place.GetPlacesByCategory
import com.ma.tehro.domain.place.repository.PlacesRepository
import com.ma.tehro.domain.podcast.repository.PodcastRepository
import com.ma.tehro.domain.preferences.repository.PreferencesRepository
import com.ma.tehro.domain.schedule.repository.ScheduleRepository
import com.ma.tehro.feature.feedback.FeedbackViewModel
import com.ma.tehro.feature.line.LineViewModel
import com.ma.tehro.feature.line.stations.StationsViewModel
import com.ma.tehro.feature.map.city.StationsMapViewModel
import com.ma.tehro.feature.more.PreferencesViewModel
import com.ma.tehro.feature.podcast.PodcastViewModel
import com.ma.tehro.feature.schedule.TrainScheduleViewModel
import com.ma.tehro.feature.shortestpath.pathfinder.PathViewModel
import com.ma.tehro.feature.shortestpath.selection.StationSelectorViewModel
import com.ma.tehro.services.LocationTracker
import com.ma.tehro.services.LocationTrackerImpl
import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger

expect val platformModule: Module

@OptIn(ExperimentalSettingsApi::class, ExperimentalXmlUtilApi::class)
val appModule = module {
    includes(platformModule)

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
        HttpClient(get<HttpClientEngine>()) {
            install(ContentNegotiation) {
                json(json = get())
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.BODY
            }
        }
    }

    single<CoroutineDispatcher> { Dispatchers.Default }

    single<PodcastRemoteDataSource> {
        PodcastRemoteDataSourceImpl(httpClient = get())
    }
    single<PodcastLocalDataSource> {
        PodcastLocalDataSourceImpl(driver = get())
    }

    singleOf(::PodcastRepositoryImpl) { bind<PodcastRepository>() }
    singleOf(::PlacesRepositoryImpl) { bind<PlacesRepository>() }
    singleOf(::PathRepositoryImpl) { bind<PathRepository>() }
    singleOf(::LineRepositoryImpl) { bind<LineRepository>() }
    singleOf(::FeedbackRepositoryImpl) { bind<FeedbackRepository>() }
    single<ScheduleRepository> { ScheduleRepositoryImpl(json = get()) }

    single<LocationTracker> { LocationTrackerImpl(locationClient = get(), stations = get()) }
    single<FindNearbyStations> { FindNearbyStations(stations = get()) }
    single<GetPlacesByCategory> { GetPlacesByCategory(placesRepository = get()) }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    factory { PathTimeCalculator(scheduleRepository = get()) }

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
    viewModel<StationSelectorViewModel> {
        StationSelectorViewModel(
            pathRepository = get(),
            locationTracker = get(),
            getPlacesByCategory = get(),
            findNearbyStations = get()
        )
    }
    viewModel<FeedbackViewModel> {
        FeedbackViewModel(feedbackRepository = get())
    }
    viewModel<TrainScheduleViewModel> {
        TrainScheduleViewModel(savedStateHandle = get(), scheduleRepository = get())
    }
    viewModel<StationsMapViewModel> {
        StationsMapViewModel(
            locationClient = get(),
            stations = get()
        )
    }
    viewModel<PodcastViewModel> { PodcastViewModel(repository = get()) }
}
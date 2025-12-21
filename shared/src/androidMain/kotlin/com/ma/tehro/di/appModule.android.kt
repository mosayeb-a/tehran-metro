package com.ma.tehro.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ma.tehro.data.Place
import com.ma.tehro.data.Station
import com.ma.tehro.services.DefaultLocationClient
import com.ma.tehro.services.LocationClient
import com.ma.thero.resources.Res
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private val Context.settingsStore by preferencesDataStore(name = "tehran_metro_preferences")

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Android.create() }

        single { androidContext().settingsStore }
        single<FlowSettings> { DataStoreSettings(get<DataStore<Preferences>>()) }

        single<LocationClient> { DefaultLocationClient(androidContext()) }

        single<Map<String, Station>> {
            val json: Json = get()
            runBlocking {
                val text = Res.readBytes("files/stations.json").decodeToString()
                json.decodeFromString<Map<String, Station>>(text)
            }
        }

        single<List<Place>> {
            val json: Json = get()
            val ioDispatcher: CoroutineDispatcher = get()

            runBlocking {
                withContext(ioDispatcher) {
                    val text = Res.readBytes("files/places.json").decodeToString()
                    json.decodeFromString(text)
                }
            }
        }
    }
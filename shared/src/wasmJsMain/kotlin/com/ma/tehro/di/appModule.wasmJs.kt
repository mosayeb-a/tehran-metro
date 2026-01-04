package com.ma.tehro.di

import com.ma.tehro.common.WasmPreloadedData
import com.ma.tehro.data.Place
import com.ma.tehro.data.Station
import com.ma.tehro.services.LocationClient
import com.ma.tehro.services.WasmLocationClient
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Js.create() }
        single<FlowSettings> { StorageSettings().makeObservable().toFlowSettings() }

        single<Map<String, Station>> {
            WasmPreloadedData.stations
        }

        single<List<Place>> {
            WasmPreloadedData.places
        }

        single<LocationClient> { WasmLocationClient() }
    }
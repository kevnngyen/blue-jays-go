package com.app.blue_jays_go.di

import com.app.blue_jays_go.data.remote.SportsApi
import com.app.blue_jays_go.data.repository.SportsRepositoryImpl
import com.app.blue_jays_go.domain.repository.SportsRepository
import com.squareup.moshi.Moshi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.app.blue_jays_go.presentation.ViewModel.SportsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

// single → "Koin, only make one copy of this thing (singleton)."
// get() → "Koin, grab the ApiService (or whatever) that is already registered above."

// This module provides all network-related dependencies (Moshi + Retrofit + SportsApi)
// SportsApi needs Retrofit → grab it.
val ApiCallModule = module {

    // 1. JSON parser (Moshi) -> Retrofit needs this to parse JSON into DTOs
    single { Moshi.Builder().build() }

    // 2. Retrofit builder -> Base URL is set here (ESPN API root)
    //    Koin provides this Retrofit wherever it's needed
    single {
        Retrofit.Builder()
            .baseUrl("https://site.api.espn.com/") // Required base URL
            .addConverterFactory(MoshiConverterFactory.create(get())) // uses Moshi from above
            .build()
    }

    // 3. SportsApi -> Retrofit creates an implementation of your API interface
    //    Used in SportsRepositoryImpl to actually call the API
    single<SportsApi> { get<Retrofit>().create(SportsApi::class.java) }
}

//Repository needs a SportsApi → grab it.
val repositoryModule = module {
    single<SportsRepository> { SportsRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { SportsViewModel(get()) } // get() -> SportsRepository
}




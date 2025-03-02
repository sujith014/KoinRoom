package com.sujith.kotlin.koinnew.di

import androidx.room.Room
import com.sujith.kotlin.koinnew.Repository.MyRepository
import com.sujith.kotlin.koinnew.Repository.MyRepositoryImp
import com.sujith.kotlin.koinnew.data.db.MyDatabase
import com.sujith.kotlin.koinnew.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val module = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MyDatabase::class.java,
            "my_database"
        ).build()
    }

    single { get<MyDatabase>().getTweetsDao() }

    single<MyRepository> { MyRepositoryImp(androidContext(), get()) }

    viewModel { MainViewModel(get()) }
}

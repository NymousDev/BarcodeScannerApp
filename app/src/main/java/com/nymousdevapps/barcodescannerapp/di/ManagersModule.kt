package com.nymousdevapps.barcodescannerapp.di

import com.nymousdevapps.barcodescannerapp.BarcodeScannerAppApplication
import com.nymousdevapps.barcodescannerapp.data.room.db.BarcodeScannerDB
import com.nymousdevapps.barcodescannerapp.data.room.interfaces.LocalDatabaseRepository
import com.nymousdevapps.barcodescannerapp.data.room.managers.LocalDatabaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagersModule {

    @Singleton
    @Provides
    fun provideLocalDataBaseRepository(): LocalDatabaseRepository {
        return LocalDatabaseManager(BarcodeScannerAppApplication.barcodeScannerDB as BarcodeScannerDB)
    }

    @Singleton
    @Provides
    fun provideRoutesDB(): BarcodeScannerDB {
        return BarcodeScannerAppApplication.barcodeScannerDB as BarcodeScannerDB
    }
}
package com.nymousdevapps.barcodescannerapp.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nymousdevapps.barcodescannerapp.data.models.ProductEntity
import com.nymousdevapps.barcodescannerapp.data.room.dao.ProductDao

@Database(entities = [ProductEntity::class], version = 2, exportSchema = false)
abstract class BarcodeScannerDB : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: BarcodeScannerDB? = null

        fun create(context: Context): BarcodeScannerDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, BarcodeScannerDB::class.java, "BarcodeScannerDB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `ProductEntity` ADD COLUMN quantity INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
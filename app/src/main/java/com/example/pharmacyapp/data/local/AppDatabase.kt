package com.example.pharmacyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun productDao(): ProductDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "pharmacy_database"


        fun getInstance(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )

                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }

    }
}
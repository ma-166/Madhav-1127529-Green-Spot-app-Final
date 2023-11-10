package com.example.myapplication

import android.content.Context
import androidx.room.Database
import com.example.myapplication.models.PlantRecord
import androidx.room.RoomDatabase
import com.example.myapplication.dao.PlantRecordDao
import androidx.room.Room


@Database(entities = [PlantRecord::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantRecordDao(): PlantRecordDao?

    companion object {

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    AppDatabase::class.java, "plant-database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
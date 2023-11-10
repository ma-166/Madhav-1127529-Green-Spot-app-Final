package com.example.myapplication.dao

import androidx.room.*
import com.example.myapplication.models.PlantRecord

@Dao
interface PlantRecordDao {
    @Query("SELECT * FROM plant_records WHERE id = :recordId")
    fun getPlantRecordById(recordId: Long): PlantRecord?

    @Insert
    fun insert(plantRecord: PlantRecord?)

    @Update
    fun update(plantRecord: PlantRecord?)

    @Delete
    fun delete(plantRecord: PlantRecord?)

    @get:Query("SELECT * FROM plant_records")
    val allPlantRecords: List<PlantRecord?>?
}
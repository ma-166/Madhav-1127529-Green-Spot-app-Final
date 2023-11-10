package com.example.myapplication.models

import androidx.room.*

@Entity(tableName = "plant_records")
class PlantRecord {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var title: String? = null

    var place: String? = null

    var date: String? = null

    var latitude = 0.0

    var longitude = 0.0

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    lateinit var image: ByteArray
}
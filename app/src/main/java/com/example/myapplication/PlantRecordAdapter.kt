package com.example.myapplication

import com.example.myapplication.models.PlantRecord
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class PlantRecordAdapter(private var plantRecords: List<PlantRecord>) :
    RecyclerView.Adapter<PlantRecordAdapter.ViewHolder>() {
    private var onRecordClickListener: OnRecordClickListener? = null
    fun setPlantRecords(plantRecords: List<PlantRecord>) {
        this.plantRecords = plantRecords
    }

    interface OnRecordClickListener {
        fun onRecordClick(record: PlantRecord)
    }

    fun setOnRecordClickListener(listener: OnRecordClickListener?) {
        onRecordClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plantRecord = plantRecords[position]
        holder.titleTextView.text = plantRecord.title
        holder.dateTextView.text = plantRecord.date
        holder.placeTextView.text = plantRecord.place
        holder.itemView.setOnClickListener { view: View? ->
            if (onRecordClickListener != null) {
                onRecordClickListener!!.onRecordClick(plantRecords[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return plantRecords.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        var dateTextView: TextView
        var placeTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.titleTextView)
            placeTextView = itemView.findViewById(R.id.placeTextView)
            dateTextView = itemView.findViewById(R.id.dateTextView)
        }
    }
}
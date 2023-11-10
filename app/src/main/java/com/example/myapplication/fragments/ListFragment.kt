package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.PlantRecord
import com.example.myapplication.AppDatabase
import com.example.myapplication.MainActivity
import com.example.myapplication.PlantRecordAdapter
import com.example.myapplication.PlantRecordAdapter.OnRecordClickListener

class ListFragment : Fragment(), OnRecordClickListener {
    private var plantRecords: List<PlantRecord> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var adapter: PlantRecordAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlantRecordAdapter(plantRecords)
        adapter?.setOnRecordClickListener(this)
        recyclerView?.adapter = adapter

        // Load data from the database and set it to the adapter
        adapter?.setPlantRecords(
            AppDatabase.getInstance(requireContext())?.plantRecordDao()?.allPlantRecords as List<PlantRecord>
        )
        return view
    }

    override fun onResume() {
        super.onResume()
        recordsList
    }

    private val recordsList: Unit
        get() {
            plantRecords =
                AppDatabase.getInstance(requireContext())?.plantRecordDao()?.allPlantRecords as List<PlantRecord>
            adapter?.setPlantRecords(plantRecords)
            adapter?.notifyDataSetChanged()
        }

    override fun onRecordClick(record: PlantRecord) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.switchToItemFragment(record.id)
    }
}


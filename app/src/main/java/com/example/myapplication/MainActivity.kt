package com.example.myapplication

import com.example.myapplication.models.PlantRecord
import androidx.room.Room
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.PlantRecordAdapter.OnRecordClickListener
import android.os.Bundle
import android.util.Log
import com.example.myapplication.fragments.HelpFragment
import com.example.myapplication.fragments.ItemFragment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.fragments.ListFragment

class MainActivity : AppCompatActivity(), OnRecordClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Room.databaseBuilder<AppDatabase>(
            applicationContext,
            AppDatabase::class.java,
            "plant-database"
        )
            .build()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            val listFragment = ListFragment()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.fragmentContainer, listFragment)
            transaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new -> {
                switchToItemFragment(-1)
                true
            }
            android.R.id.home -> {
                switchToListFragment()
                true
            }
            R.id.action_help -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HelpFragment.newInstance())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    override fun onRecordClick(record: PlantRecord) {
        Log.d("Click", "Record clicked: " + record.title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Handle item click, switch to ItemFragment
        switchToItemFragment(record.id)
    }

    fun switchToListFragment() {
        val listFragment = ListFragment()
        replaceFragment(listFragment, false)
    }

    fun switchToItemFragment(recordId: Long) {
        val itemFragment = ItemFragment.newInstance(recordId)
        replaceFragment(itemFragment, true)
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    companion object {
        var database: AppDatabase? = null
    }
}
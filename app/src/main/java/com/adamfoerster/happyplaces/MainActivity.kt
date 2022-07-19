package com.adamfoerster.happyplaces

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adamfoerster.happyplaces.utils.Utils.ADD_ACTIVITY_REQ_CODE
import com.adamfoerster.happyplaces.activities.AddActivity
import com.adamfoerster.happyplaces.activities.DetailsActivity
import com.adamfoerster.happyplaces.database.PlaceDAO
import com.adamfoerster.happyplaces.databinding.ActivityMainBinding
import com.adamfoerster.happyplaces.utils.Utils
import kotlinx.coroutines.launch
import pl.kitek.rvswipetodelete.SwipeToEditCallback
import pl.kitek.rvswipetodelete.SwipeToRemoveCallback

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dao: PlaceDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAdd.setOnClickListener {
            startActivityForResult(
                Intent(
                    this, AddActivity::class.java
                ),
                ADD_ACTIVITY_REQ_CODE
            )
        }
        dao = (application as HappyPlaces).db.placeDao()
        getAllPlaces()

    }

    private fun getAllPlaces() {
        lifecycleScope.launch {
            val places = dao.getAll()
            binding.mainRecycler.layoutManager =
                LinearLayoutManager(this@MainActivity)
            val adapter = PlaceListAdapter(this@MainActivity, places.toTypedArray())
            binding.mainRecycler.adapter = adapter
            adapter.onItemClick = {
                val intent = Intent(
                    this@MainActivity,
                    DetailsActivity::class.java
                )
                intent.putExtra("details", it)
                startActivity(intent)
            }
            val editSwipeHandler = object : SwipeToEditCallback(this@MainActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.notifyEditItem(
                        this@MainActivity,
                        viewHolder.adapterPosition,
                        Utils.ADD_ACTIVITY_REQ_CODE
                    )
                }
            }

            val removeSwipeHandler = object : SwipeToRemoveCallback(this@MainActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.notifyRemoveItem(
                        dao,
                        viewHolder.adapterPosition
                    )
                }
            }

            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            val removeItemTouchHelper = ItemTouchHelper(removeSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(binding.mainRecycler)
            removeItemTouchHelper.attachToRecyclerView(binding.mainRecycler)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACTIVITY_REQ_CODE && resultCode == Activity.RESULT_OK) {
            getAllPlaces()
        }
    }
}
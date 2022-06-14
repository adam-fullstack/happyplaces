package com.adamfoerster.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adamfoerster.happyplaces.MainActivity
import com.adamfoerster.happyplaces.database.PlaceEntity
import com.adamfoerster.happyplaces.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailsBinding
    lateinit var model: PlaceEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressed()
        }
        if (intent.hasExtra("details")) {
            model = intent.getSerializableExtra("details") as PlaceEntity
            try {
                supportActionBar?.title = model.title
                binding.image.setImageURI(Uri.parse(model.image))
                binding.description.text = model.description
                binding.location.text = model.location
            } catch (e: Error) {
                e.printStackTrace()
            }
        }
    }
}
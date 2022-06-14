package com.adamfoerster.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adamfoerster.happyplaces.HappyPlaces
import com.adamfoerster.happyplaces.Utils.CAMERA_ACTIVITY
import com.adamfoerster.happyplaces.Utils.CAMERA_PERM_REQ
import com.adamfoerster.happyplaces.Utils.GALLERY_CODE
import com.adamfoerster.happyplaces.Utils.IMAGE_DIR
import com.adamfoerster.happyplaces.Utils.STORAGE_PERM_REQ
import com.adamfoerster.happyplaces.Utils.sdf
import com.adamfoerster.happyplaces.database.PlaceEntity
import com.adamfoerster.happyplaces.databinding.ActivityAddBinding
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddActivity : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {
    lateinit var binding: ActivityAddBinding
    var calendar = Calendar.getInstance()
    lateinit var dateListener: DatePickerDialog.OnDateSetListener
    lateinit var place: PlaceEntity
    var image = ""
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add a Happy Place"
        binding.toolbarAdd.setNavigationOnClickListener {
            onBackPressed()
        }
        dateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateView()
            }
        binding.dateInput.setOnClickListener(this)
        binding.btnAddImage.setOnClickListener(this)
        binding.dateInput.setOnFocusChangeListener { view, b ->
            if (b) onClick(
                view
            )
        }
        binding.btnSave.setOnClickListener {
            var isValid = true
            when {
                binding.nameInput.text.isNullOrBlank() -> isValid = false
                binding.dateInput.text.isNullOrBlank() -> isValid = false
                binding.descriptionInput.text.isNullOrBlank() -> isValid = false
                binding.locationInput.text.isNullOrBlank() -> isValid = false
            }
            if (!isValid) {
                Toast.makeText(
                    this,
                    "All fields are required",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            place = PlaceEntity(
                binding.nameInput.text.toString(),
                image,
                binding.descriptionInput.text.toString(),
                binding.dateInput.text.toString(),
                binding.locationInput.text.toString(),
                0,
                0
            )
            addPlaceToDB()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        when (perms[0]) {
            "android.permission.READ_EXTERNAL_STORAGE" -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("PERMISSION REFUSED")
                dialog.setMessage("You won't be able to pick a photo from the gallery")
                dialog.setPositiveButton("OK") { _, _ -> }
                dialog.show()
            }
            "android.permission.CAMERA" -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("PERMISSION REFUSED")
                dialog.setMessage("You won't be able to take a picture")
                dialog.setPositiveButton("OK") { _, _ -> }
                dialog.show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (perms[0]) {
            "android.permission.READ_EXTERNAL_STORAGE" -> choosePhotoFromGallery()
            "android.permission.CAMERA" -> takePicture()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.dateInput.id -> {
                DatePickerDialog(
                    this@AddActivity,
                    dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            binding.btnAddImage.id, binding.imageView.id -> {
                val pictureDialog = AlertDialog.Builder(this@AddActivity)
                pictureDialog.setTitle("Image from")
                pictureDialog.setItems(
                    arrayOf(
                        "Gallery",
                        "Camera"
                    )
                ) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePicture()
                    }
                    dialog.dismiss()
                }
                pictureDialog.show()
            }
        }
    }

    fun addPlaceToDB() {
        val dao = (application as HappyPlaces).db.placeDao()
        lifecycleScope.launch {
            val result = dao.insertAll(place)
            Toast.makeText(
                this@AddActivity,
                "Happy Place saved",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("Bleh", "add result: $result")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    fun requestCameraPermission() = EasyPermissions.requestPermissions(
        this,
        "We need access to the camera to be able to take pictures",
        CAMERA_PERM_REQ,
        Manifest.permission.CAMERA
    )

    fun requestGalleryPermission() {
        EasyPermissions.requestPermissions(
            this,
            "We need access to the device storage to be able to select a picture",
            STORAGE_PERM_REQ,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    fun choosePhotoFromGallery() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, GALLERY_CODE)
        } else {
            requestGalleryPermission()
        }
    }

    fun takePicture() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_ACTIVITY)
        } else {
            requestCameraPermission()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CAMERA_ACTIVITY -> setImageFromCamera(data!!)
            GALLERY_CODE -> setImageFromGallery(data?.data as Uri)
        }
    }

    fun setImageFromCamera(data: Intent) {
        try {
            val dataFromActivity = data.extras!!.get("data")
            binding.imageView.setImageBitmap(dataFromActivity!! as Bitmap)
            image = saveImageToAppDir(dataFromActivity as Bitmap).toString()
        } catch (e: Error) {
            Log.e("Bleh", e.toString())
            e.printStackTrace()
        }
    }

    fun setImageFromGallery(it: Uri) {
        try {
            binding.imageView.setImageURI(it)
            val source = ImageDecoder.createSource(this.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)
            image = saveImageToAppDir(bitmap).toString()
        } catch (e: Error) {
            e.printStackTrace()
        }
    }

    fun updateDateView() {
        binding.dateInput.setText(sdf.format(calendar.time).toString())
    }

    fun saveImageToAppDir(bmp: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val uri = Uri.parse(file.absolutePath)
        Log.d("Bleh", uri.toString())
        return uri
    }
}
package com.adamfoerster.happyplaces

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val dateFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
    val CAMERA_PERM_REQ = 1
    val STORAGE_PERM_REQ = 2
    val CAMERA_ACTIVITY = 3
    val GALLERY_CODE = 4
    val IMAGE_DIR = "HappyImages"
    val ADD_ACTIVITY_REQ_CODE = 5
}
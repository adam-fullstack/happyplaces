package com.adamfoerster.happyplaces.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val dateFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
    const val CAMERA_PERM_REQ = 1
    const val STORAGE_PERM_REQ = 2
    const val CAMERA_ACTIVITY = 3
    const val GALLERY_CODE = 4
    const val IMAGE_DIR = "HappyImages"
    const val ADD_ACTIVITY_REQ_CODE = 5
    const val EDIT_PLACE_EXTRA = "edit_place_extra"
}
package com.golfpvcc.bookreader.utils

import android.icu.text.DateFormat
import com.google.firebase.Timestamp

fun formatDate(timeStamp: Timestamp): String{
    val date = DateFormat.getDateInstance()
        .format(timeStamp.toDate())
        .toString().split(",")[0]
    return date
}
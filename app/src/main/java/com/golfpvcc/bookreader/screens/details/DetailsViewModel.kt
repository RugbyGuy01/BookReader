package com.golfpvcc.bookreader.screens.details

import android.content.ClipData.Item
import androidx.lifecycle.ViewModel
import com.golfpvcc.bookreader.data.Resource
import com.golfpvcc.bookreader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: BookRepository)
    : ViewModel(){
        suspend fun getBookInfo(bookId: String) : Resource<com.golfpvcc.bookreader.model.Item> {
            return repository.getBookInfo(bookId)
        }

}
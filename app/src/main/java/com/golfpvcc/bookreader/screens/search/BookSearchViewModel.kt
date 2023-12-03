package com.golfpvcc.bookreader.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfpvcc.bookreader.data.DataOrException
import com.golfpvcc.bookreader.data.Resource
import com.golfpvcc.bookreader.model.Item
import com.golfpvcc.bookreader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var isLoading: Boolean by mutableStateOf(true)
    var list: List<Item> by mutableStateOf(listOf())


    init {
        searchBooks("android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                return@launch
            }
            try {
                when (val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if(list.isNotEmpty()) isLoading = false
                    }

                    is Resource.Error -> {
                        Log.e("VIN", "Search Books failed")
                        isLoading = false
                    }

                    else -> {isLoading = false}
                }

            } catch (exception: Exception) {
                isLoading = false
                Log.d("VIN", "Search Books: ${exception.message.toString()}")
            }
        }
    }
}
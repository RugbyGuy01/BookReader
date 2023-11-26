package com.golfpvcc.bookreader.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfpvcc.bookreader.data.DataOrException
import com.golfpvcc.bookreader.model.MBook
import com.golfpvcc.bookreader.repository.FireRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRespository
): ViewModel(){
    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>>
    = mutableStateOf(DataOrException(listOf(), true, Exception("")))

    init{
        getAllBoooksFromDatabase()
    }

    private fun getAllBoooksFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()
            if(!data.value.data.isNullOrEmpty())
                data.value.loading = false
        } // end of launch
        Log.d("VIN", "getAllBoooksFromDatabase: ${data.value.data?.toList().toString()}")
    }
}
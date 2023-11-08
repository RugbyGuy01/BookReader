/*
Email - SoPi@golfpvcc.com  Password Magaww
        Vgamble@golfpvcc.com  Password Magaww
 */
package com.golfpvcc.bookreader.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("VIN", "signInWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("VIN", "signInWithEmail:failure", task.exception)
                }

            }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ) {

        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        home()
                    } else {
                        Log.d("VIN", "CreateUserWithEmailANd Password: $(task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }
}
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
import com.golfpvcc.bookreader.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        home: (String, Boolean) -> Unit) = viewModelScope.launch {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    home("", true)
                } else {
                    val SignInResult : String? = task.exception.toString().split(':')?.get(1)
                    Log.w("VIN", "signInWithEmail:failure", task.exception) //// If sign in fails, display a message to the user.
                    home("Failed: $SignInResult", false)
                }
            }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: (String, Boolean) -> Unit ) {

        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {    // get the user name
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName) // add user name to the firebase database
                        home("", true)
                    } else {
                        val SignInResult : String? = task.exception.toString().split(':')?.get(1)
                        home("Failed: $SignInResult", false)
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "New User",
            profession = "Tech",
            id = null)
            .toMap()


        FirebaseFirestore.getInstance().collection("users")
            .add(user)

    }
}
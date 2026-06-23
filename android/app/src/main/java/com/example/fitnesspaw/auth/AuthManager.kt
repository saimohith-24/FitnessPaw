package com.example.fitnesspaw.auth

import com.google.firebase.auth.FirebaseAuth

class AuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    onSuccess()

                } else {

                    onError(
                        it.exception?.message
                            ?: "Signup Failed"
                    )
                }
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    onSuccess()

                } else {

                    onError(
                        it.exception?.message
                            ?: "Login Failed"
                    )
                }
            }
    }
}
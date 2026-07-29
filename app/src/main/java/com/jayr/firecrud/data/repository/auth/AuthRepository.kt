package com.jayr.firecrud.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await


class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
): AuthService {
    //    current User
    val currentUser: FirebaseUser? = auth.currentUser

    //     Auth methods
    override suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                print("DONE")
            } else {
                print("NOT DONE")
            }
        }.await()
    }

    override suspend fun login(email: String, password: String):String {
        var message = ""
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            message = if (task.isSuccessful) {
                "Success!"
            } else if(task.isCanceled) {
                "Process Cancelled"
            }else{
                "Oop! something went wrong"
            }
        }.await()

        return message
    }
}
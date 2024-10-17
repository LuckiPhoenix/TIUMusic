package com.example.TIUMusic.Login

import androidx.lifecycle.ViewModel

class AuthViewModel(): ViewModel() {
    fun onLogin(authInputs: AuthInputs) : Boolean {
        //login logic
        return true
    }
    fun onRegister(authInputs: AuthInputs) : Boolean {
        //register logic
        return true
    }
    fun onReset(password: String) : Boolean {
        //reset logic
        return true
    }
    fun onRecover(email: String) : Boolean {
        //recover logic
        return true

    }


}
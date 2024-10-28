package com.example.TIUMusic.Login

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    var fullName: String ,
    @PrimaryKey var email: String ,
    var password: String ,
)


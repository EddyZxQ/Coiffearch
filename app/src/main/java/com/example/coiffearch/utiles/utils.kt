package com.example.coiffearch.utiles

import android.content.Context
import android.widget.Toast


//Hacer TOAST EASY
fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()



fun String.isValidEmail():Boolean{
    val pattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
    return pattern.matches(this)
}
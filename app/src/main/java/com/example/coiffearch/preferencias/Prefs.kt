package com.example.coiffearch.preferencias

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {
    val SHARED_NAME = "Recuerdame"
    val SHARED_RECUERDAME = "Recordar"
    val SHARED_USUARIO = "usuario"
    val SHARED_PASS = "pass"

    val SHARED_RECUERDAME_EMPLEADO = "Recordar"

    val storage = context.getSharedPreferences(SHARED_NAME,0)

    fun saveRecordar(name:Boolean){
        storage.edit().putBoolean(SHARED_RECUERDAME, name).apply()
    }

    fun getRecordar():Boolean{
        return storage.getBoolean(SHARED_RECUERDAME, false)
    }

    fun saveRecordarEmpleado(name:Boolean){
        storage.edit().putBoolean(SHARED_RECUERDAME_EMPLEADO, name).apply()
    }

    fun getRecordarEmpleado():Boolean{
        return storage.getBoolean(SHARED_RECUERDAME_EMPLEADO, false)
    }


    fun saveUser(usuario:String){
        storage.edit().putString(SHARED_USUARIO, usuario).apply()
    }

    fun getUser():String{
        return storage.getString(SHARED_USUARIO, "")?:""
    }


    fun savePass(pass:String){
        storage.edit().putString(SHARED_PASS, pass).apply()
    }

    fun getPass():String{
        return storage.getString(SHARED_PASS, "")?:""
    }
}
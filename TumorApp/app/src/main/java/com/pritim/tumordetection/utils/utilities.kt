package com.pritim.tumordetection.utils

import androidx.appcompat.app.AppCompatActivity
import com.pritim.tumordetection.data.User

object utilities   {
    public fun setHashmap(hashMapDat: HashMap<String,Any>, user: User) : HashMap<String,Any>{
        hashMapDat["id"]  = user.id.toString()
        hashMapDat["email"] = user.email.toString()
        hashMapDat["password"] = user.password.toString()
        hashMapDat["nomor_hp"] = user.nomor_hp.toString()
        hashMapDat["confidence"] = user.confidence.toString().toDouble()
        hashMapDat["nama"] = user.nama.toString()
        return hashMapDat
    }
    }
package com.pritim.tumordetection.data

import com.pritim.tumordetection.utils.utilities
import java.io.Serializable

class User : Serializable{
    var id : String ?= null
    var email  : String ?= null;
    var password : String ?= null;
    var nomor_hp : String ?= "";
    var nama : String ?= null;
    var hasil_prediksi : Double ?= 0.0;
    var alamat : String ?= "";

    fun update_data(hashMap: HashMap<String,Any>) : User {
        var userGans : User =User()
        userGans.nama = hashMap["nama"] as String
        userGans.nomor_hp = hashMap["nomor_hp"] as String
        userGans.email = hashMap["email"] as String
        userGans.hasil_prediksi = hashMap["hasil_prediksi"] as Double
        userGans.alamat = hashMap["alamat"] as String
        userGans.password = hashMap["password"] as String


        return userGans
    }
}


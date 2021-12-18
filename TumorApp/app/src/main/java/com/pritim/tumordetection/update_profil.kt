package com.pritim.tumordetection

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import com.pritim.tumordetection.utils.utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class update_profil : AppCompatActivity() {

    lateinit var et_email : EditText
    lateinit var  et_nama : EditText
    lateinit var et_nomor_hp : EditText
    lateinit var buttonSimpan : Button
    lateinit var buttonDelete : Button
    var user : User = User()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ubahprofil_layout)
        init()
        getUserFromIntent()
        setTheHint()

        buttonSimpan.setOnClickListener{ v ->
            var hashMapDat : HashMap<String,Any> = HashMap()
            getNewData()
            hashMapDat = utilities.setHashmap(hashMapDat,user)

            NetworkModule.service().update(hashMapDat).enqueue(object : Callback<responseGetUser> {
                override fun onResponse(call: Call<responseGetUser>, response: Response<responseGetUser>) {
                        if (response.body()?.code == 200.toString()){
                            val DataHashmap : HashMap<String,Any> = response.body()!!.data!!
                            Log.d("DEBUG","CONTENT HASHMAP ${DataHashmap}, User DATA ${user.id}")
                            user.update_data(DataHashmap)
                            val alertDialog : AlertDialog = AlertDialog.Builder(this@update_profil).create()
                            alertDialog.apply {
                                setTitle("Sukses")
                                setMessage("Berhasil Mengubah Data")
                            }.show()
                            val handler : Handler = Handler()
                            handler.postDelayed( Runnable{
                                alertDialog.dismiss()
                            },1500)
                            setTheHint()
                        }
                }

                override fun onFailure(call: Call<responseGetUser>, t: Throwable) {

                }

            })
        }

        buttonDelete.setOnClickListener{v ->
            
        }

    }

    private fun getNewData() {
        user.email = et_email.text.toString()
        user.nomor_hp = et_nomor_hp.text.toString()
        user.nama = et_nama.text.toString()
    }
    private fun setTheHint() {
        et_email.setText(user.email)
        et_nomor_hp.setText(user.nomor_hp)
        et_nama.setText(user.nama)
    }
    private fun getUserFromIntent() : Unit {
        user = intent.getSerializableExtra("DATA_USER") as User
    }
    private fun init () {
        et_email = findViewById(R.id.u_et_email)
        et_nama = findViewById(R.id.up_et_Nama)
        et_nomor_hp = findViewById(R.id.u_et_nohp)
        buttonSimpan = findViewById(R.id.u_btn_simpan)
        buttonDelete = findViewById(R.id.u_btn_delete)
    }
}
package com.pritim.tumordetection

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import com.pritim.tumordetection.responses.responseRequestOnly
import com.pritim.tumordetection.utils.utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class update_profil : AppCompatActivity() {

    private  val TAG : String = "update_profil"
    lateinit var et_email : EditText
    lateinit var  et_nama : EditText
    lateinit var et_nomor_hp : EditText
    lateinit var buttonSimpan : Button
    lateinit var buttonDelete : Button
    lateinit var  bottomNavigationBar : BottomNavigationView
    var user : User = User()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ubahprofil_layout)
        init()
        getUserFromIntent()
        setTheHint()
        settingUpTheBottomNavigation()

        buttonSimpan.setOnClickListener{ v ->
            var hashMapDat : HashMap<String,Any> = HashMap()
            getNewData()
            hashMapDat = utilities.setHashmap(hashMapDat,user)

            NetworkModule.service().update(hashMapDat["id"].toString(),hashMapDat).enqueue(object : Callback<responseGetUser> {
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
            val handler: Handler = Handler()
            NetworkModule.service().deleteGans(user.id.toString()).enqueue(object : Callback<responseRequestOnly> {
                override fun onResponse(call: Call<responseRequestOnly>, response: Response<responseRequestOnly>) {
                    val alertDialog : AlertDialog = AlertDialog.Builder(this@update_profil).create()
                    alertDialog.setTitle("Sukses")
                    alertDialog.setMessage("Data Telah Dihapus, Anda akan dikembalikan kepada Halaman Login")
                    alertDialog.show()

                    handler.postDelayed(Runnable {
                        alertDialog.dismiss()
                    }, 1500)


                }
                override fun onFailure(call: Call<responseRequestOnly>, t: Throwable) {
                    Log.i(TAG, t.message.toString())
                }

            })

            val intentObj: Intent = Intent(this@update_profil, MainActivity::class.java)
            handler.postDelayed(Runnable {
                startActivity(intentObj)
                finish()
            },3000)
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
        bottomNavigationBar = findViewById(R.id.cv_beranda3)
    }
    private fun settingUpTheBottomNavigation() {
        bottomNavigationBar.selectedItemId = R.id.nav_update
        bottomNavigationBar.itemBackground = ColorDrawable(resources.getColor(R.color.white, null))

        bottomNavigationBar.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.nav_beranda-> {
                    val nav_settings: Intent = Intent(this@update_profil, CovidViz::class.java)
                    nav_settings.putExtra("DATA_USER", user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }
                else -> {
                    val nav_settings: Intent = Intent(this@update_profil, InferenceModel::class.java)
                    nav_settings.putExtra("DATA_USER", user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }

            }

            true
        }
    }

}
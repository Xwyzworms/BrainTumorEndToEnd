package com.pritim.tumordetection

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class LoginActivity : AppCompatActivity (){
    lateinit var btnLogin : Button
    lateinit var etEmail : EditText
    lateinit var etPassword : EditText
    lateinit var btnDaftar : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        btnLogin.setOnClickListener(View.OnClickListener {
            handleLogin()
    })
        btnDaftar.setOnClickListener { it ->
            handleDaftar()
        }
}

    private fun handleDaftar() {
        val intentObj : Intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intentObj)
        finish()
    }
    private fun handleLogin() {
        val email : String = etEmail.text.toString()
        val password : String = etPassword.text.toString()
        if ( email.isNotEmpty() && password.isNotEmpty() ){
            var hashmap : HashMap<String,Any> = HashMap()
            hashmap["email"] = email
            hashmap["password"] = password
            hashmap["status"] = true
            NetworkModule.service().getUserData(hashmap).enqueue(object : Callback<responseGetUser> {
                val alertDialog : AlertDialog = AlertDialog.Builder(this@LoginActivity).create()
                override fun onResponse(call: Call<responseGetUser>, response: Response<responseGetUser>) {
                    Log.d("DEBUG", "GET data ${response}")
                    if (response.code() == 200) {
                        if (response.body()?.data != null) {
                            alertDialog.setMessage("Berhasil Login")
                            alertDialog.setTitle("Sukses")
                            alertDialog.show()
                            val user : User = User()
                            var hashmap : HashMap<String, Any> = response.body()!!.data!!
                            Log.d("DEBUG", "GET data ${response.body()!!.data!!}")
                            Log.d("DEBUG", "Get The Data ${hashmap["email"] as String}")
                            user.id = hashmap["_id"] as String
                            user.email = hashmap["email"] as String
                            user.password = hashmap["password"] as String
                            user.confidence= hashmap["confidence"] as Double
                            user.nama = hashmap["nama"] as String
                            user.nomor_hp = hashmap["nomor_hp"] as String
                            user.status = hashmap["status"] as Boolean
                            Log.d("DEBUG", user.toString())
                            val intentObj = Intent(this@LoginActivity, CovidViz::class.java)
                            intentObj.putExtra("DATA_USER", user )
                            alertDialog.dismiss()
                            startActivity(intentObj)
                            finish()
                        }
                        else {
                            alertDialog.setMessage("Gagal Login, Password/Email Salah")
                            alertDialog.setTitle("Kesalahan")
                            alertDialog.show()
                        }
                    }

                }

                override fun onFailure(call: Call<responseGetUser>, t: Throwable) {
                    alertDialog.setTitle("Gagal Request")
                    alertDialog.setMessage("Gagal Mengambil Informasi Anda ${t.message}")
                    alertDialog.show()
                }

            })
        }
        else {
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
            alertDialog.setTitle("Kesalahan")
            alertDialog.setMessage("Gan Tolong Isi Semua Kolom")
        }


    }

    private fun init() {
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnDaftar = findViewById(R.id.btnLoginDaftar)
    }


}
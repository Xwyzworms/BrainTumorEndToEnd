package com.pritim.tumordetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.responses.responseGetUser
import com.pritim.tumordetection.responses.responseRequestOnly
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var etNama : EditText
    lateinit var etPassword : EditText
    lateinit var etEmail : EditText
    lateinit var btnDaftar  : Button
    lateinit var btnGotoLogin : TextView
    lateinit var retrofit: Retrofit
    lateinit var retrofitInterface: FunctionalInterface


    private fun handleSignUp(){
        var nama : String = etNama.text.toString()
        var password : String = etPassword.text.toString()
        var email : String = etEmail.text.toString()
        var addUser : HashMap<String, String> = HashMap()
        addUser.put("nama", nama)
        addUser.put("password",password)
        addUser.put("email",email)
        NetworkModule.service().signup(addUser).enqueue(object : Callback<responseRequestOnly> {
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            override fun onResponse(call: Call<responseRequestOnly>, response: Response<responseRequestOnly>) {
                Log.d("DEBUG", "Succesfully Fetching Data ${response.code()}")
                Log.d("DEBUG",response.toString())
                if(response.code() == 200) {
                    alertDialog.setTitle("Sukses")
                    alertDialog.setMessage("Berhasil mendaftar")
                    alertDialog.show()
                }
                else {
                    alertDialog.setTitle("Gagal")
                    alertDialog.setMessage("Gagal Mendaftar, Email Telah Terdaptar")
                    alertDialog.show()
                }
            }
            override fun onFailure(call: Call<responseRequestOnly>, t: Throwable) {
                alertDialog.setTitle("Kesalahan")
                alertDialog.setMessage("Aplikasi Gagal Request Ke Server ${call.toString()}")
                alertDialog.show()
            }
        })

    }

    private fun init() {
        etNama = findViewById<EditText>(R.id.etDaftarNama)
        etPassword = findViewById<EditText>(R.id.etDaftarPassword)
        etEmail = findViewById<EditText>(R.id.etDaftarEmail)
        btnDaftar = findViewById<Button>(R.id.btnDaftar)
        btnGotoLogin = findViewById(R.id.btnDaftarLogin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        init()
        btnDaftar.setOnClickListener{
            handleSignUp()
            goToLogin()
        }
        btnGotoLogin.setOnClickListener(View.OnClickListener {
            goToLogin()
        })
    }

    private fun goToLogin() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
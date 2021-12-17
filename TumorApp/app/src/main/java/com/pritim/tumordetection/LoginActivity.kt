package com.pritim.tumordetection

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pritim.tumordetection.NetworkModule.NetworkModule
import com.pritim.tumordetection.responses.responseGetUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity (){
    lateinit var btnLogin : Button
    lateinit var etEmail : EditText
    lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        btnLogin.setOnClickListener(View.OnClickListener {
            handleLogin()
    })
}
    private fun handleLogin() {
        val email : String = etEmail.text.toString()
        val password : String = etPassword.text.toString()
        if ( email.isNotEmpty() && password.isNotEmpty() ){
            var hashmap : HashMap<String,Any> = HashMap()
            hashmap["email"] = email
            hashmap["password"] = password

            NetworkModule.service().getUserData(hashmap).enqueue(object : Callback<responseGetUser> {
                val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                override fun onResponse(call: Call<responseGetUser>, response: Response<responseGetUser>) {
                    if (response.code() == 200) {
                        alertDialog.setMessage("Berhasil Login")
                        alertDialog.setTitle("Sukses").show()
                    }
                    else {
                        alertDialog.setMessage("Gagal Login, Password/Email Salah")
                        alertDialog.setTitle("Kesalahan").show()
                    }
                }

                override fun onFailure(call: Call<responseGetUser>, t: Throwable) {
                    alertDialog.setTitle("Gagal Request")
                    alertDialog.setMessage("Gagal Mengambil Informasi Anda ${t.message}").show()
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
    }


}
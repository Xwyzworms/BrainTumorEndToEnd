package com.pritim.tumordetection

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
                val alertDialog : AlertDialog = AlertDialog.Builder(this@LoginActivity).create()
                override fun onResponse(call: Call<responseGetUser>, response: Response<responseGetUser>) {
                    if (response.code() == 200) {
                        alertDialog.setMessage("Berhasil Login")
                        alertDialog.setTitle("Sukses")
                        alertDialog.show()
                        val user : User = User()
                        Log.d("DEBUG", "GET data ${response.body()}")
                        if (response.body()?.data != null) {
                            var hashmap : HashMap<String, Any> = response.body()!!.data!!
                            Log.d("DEBUG", "Get The Data ${hashmap["email"] as String}")
                            user.id = hashmap["id"] as String
                            user.email = hashmap["email"] as String
                            user.password = hashmap["password"] as String
                            user.alamat = hashmap["alamat"] as String
                            user.hasil_prediksi = hashmap["hasil_prediksi"] as Double
                            user.nama = hashmap["nama"] as String
                            user.nomor_hp = hashmap["nomor_hp"] as String
                            Log.d("DEBUG", user.toString())
                            val intentObj = Intent(this@LoginActivity, update_profil::class.java)
                            intentObj.putExtra("DATA_USER", user )
                            alertDialog.dismiss()
                            startActivity(intentObj)
                            finish()
                        }
                    }
                    else {
                        alertDialog.setMessage("Gagal Login, Password/Email Salah")
                        alertDialog.setTitle("Kesalahan")
                        alertDialog.show()
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
    }


}
package com.pritim.tumordetection

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pritim.tumordetection.data.Recognize
import com.pritim.tumordetection.data.User
import com.pritim.tumordetection.utils.Classifier
import java.math.RoundingMode

private const val TAG : String = "InferenceModel"
class InferenceModel : AppCompatActivity(){
    private lateinit var btn_predict : Button
    private lateinit var ivGambar : ImageView
    private lateinit var tvPredksi : TextView
    private var getResult : ActivityResultLauncher<Intent> = setImageBitmap()

    private lateinit var user : User
    private var mInputSize : Int = 64
    private lateinit var classifier : Classifier
    private var mModelPath : String = "model.tflite"
    private var mLabelPath : String = "label.txt"
    private lateinit var bottomNAvigationBar : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prediction_layout)
        init()
        user = intent.getSerializableExtra("DATA_USER") as User
        settingUpTheBottomNavigation()
        ivGambar.setOnClickListener { v ->
            val intent  = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getResult.launch(intent)
        }
        btn_predict.setOnClickListener {it ->
           doInference()
        }
    }

    private fun doInference() {
        val image : Bitmap = (ivGambar.drawable as BitmapDrawable).bitmap
        classifier = Classifier(assets,mModelPath,mLabelPath,mInputSize)
        val answer : Recognize = classifier.recognizeData(image)
        tvPredksi.text = answer.result+ " , " + (answer.confidence.toBigDecimal().setScale(5,RoundingMode.UP).toDouble() * 100).toString()
    }
    private fun setImageBitmap() : ActivityResultLauncher<Intent> {

        val Result = registerForActivityResult(ActivityResultContracts.StartActivityForResult() ) {
            if(it.resultCode == Activity.RESULT_OK) {
                val selectedBitmap : Uri? = it.data?.data
                val image : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedBitmap)
                ivGambar.setImageBitmap(image)

            }
            else {
                Log.w(TAG,"Not Getting The Data")
            }
        }
        return Result
    }
    private fun settingUpTheBottomNavigation() {
        bottomNAvigationBar.selectedItemId = R.id.nav_predict
        bottomNAvigationBar.itemBackground = ColorDrawable(resources.getColor(R.color.white, null))

        bottomNAvigationBar.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.nav_beranda-> {
                    val nav_settings: Intent = Intent(this@InferenceModel, CovidViz::class.java)
                    nav_settings.putExtra("DATA_USER", user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }
                else -> {
                    val nav_settings: Intent = Intent(this@InferenceModel, update_profil::class.java)
                    nav_settings.putExtra("DATA_USER", user)
                    nav_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(nav_settings)
                    finish()
                }

            }

            true
        }
    }

        private fun init() {
        btn_predict = findViewById(R.id.p_btn_predict)
        ivGambar = findViewById(R.id.p_iv_inferensi)
        tvPredksi = findViewById(R.id.p_tv_hasilPrediksiNumber)
        bottomNAvigationBar = findViewById(R.id.cv_beranda2)

    }
}
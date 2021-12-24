package com.pritim.tumordetection.utils

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.pritim.tumordetection.data.Recognize
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
private const val TAG : String = "classifier"
class Classifier (assetManager: AssetManager, modelPath : String, labelPath : String, input_size : Int){

    private val assetManager = assetManager
    private val modelPath : String = modelPath
    private val labelPath : String = labelPath
    private lateinit var interpreter : Interpreter
    private lateinit var  labelList : List<String>
    private val INPUT_SIZE : Int = input_size
    private val PIXEL_SIZE : Int = 3
    private val IMAGE_MEAN : Int = 0
    private val IMAGE_STD : Float = 255.0f

    init {
        val options : Interpreter.Options = Interpreter.Options()

        options.setNumThreads(5)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile())
        labelList = loadLabelFile()
    }

    private fun loadModelFile() : MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream : FileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel : FileChannel = inputStream.channel

        val startOffSet : Long = fileDescriptor.startOffset // Where The Datastart ( Byte Offset)
        val length : Long = fileDescriptor.declaredLength // Actual Byte TO Processed

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffSet, length)

    }

    private fun loadLabelFile() : List<String> {
        return assetManager.open(labelPath).bufferedReader().useLines {
            Log.d(TAG, it.toString())
            it.toList()
        }
    }

    fun recognizeData(bitmap : Bitmap) : Recognize{
        val preprocessBitmap = Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false) // Preprocess Input data
        val byteBuffer : ByteBuffer = convertBitmapToByteBuffer(preprocessBitmap)
        val res = Array(1) {FloatArray(3)}
        interpreter.run(byteBuffer, res)
        return getLabel(res)
    }
    private fun getLabel(res : Array<FloatArray>) : Recognize {
        var max : Float = res[0][0]
        var counter : Int = 0
        var answer : Int = 0
        var recognize : Recognize
        for (j in res) {
            for(dat in j) {
                if ( max <= dat) {
                    Log.i(TAG,counter.toString() + "GAMS")
                    max = dat
                }
            }
        }
        answer = res[0].indexOfFirst { it ->
            it == max
        }
        return when(answer) {
            0 -> {
             Recognize(1,"No",max)
            }
            1 -> {
                Recognize(2,"Yes",max)
            }
            else -> {
                Recognize(3,"Random",max)
            }
        }

    }
    private fun convertBitmapToByteBuffer(bitmap : Bitmap) : ByteBuffer {
        val byteBuffer : ByteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE*INPUT_SIZE)

        bitmap.getPixels(intValues, 0, bitmap.width, 0,0,bitmap.width,bitmap.height)
        var pixel : Int = 0
        for(i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val input = intValues[pixel++]
                byteBuffer.putFloat((((input.shr(16)  and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((input.shr(8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((input and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
            }
        }

        return byteBuffer
    }




}
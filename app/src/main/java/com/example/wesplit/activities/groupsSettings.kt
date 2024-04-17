package com.example.wesplit.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.MultiLayoutAdapter
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class groupsSettings : AppCompatActivity() {
    private lateinit var x:RecyclerView
    private lateinit var y:adaptorforfriendslist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups_settings)
        val groupID:String = intent.getStringExtra("groupID") as String

        findViewById<ImageView>(R.id.qrcode).setImageBitmap(generateQRCode(groupID))


        FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {

            findViewById<TextView>(R.id.groupName).setText(it.data?.get("Name").toString())
            val participants:MutableList<String> = it.data?.get("Participants") as MutableList<String>
            val result:MutableList<String> = mutableListOf()
            for(i in participants){
                if(i != FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                    result.add("!" + groupID + i)
                }
            }

            if(result.size == 0){
                findViewById<RecyclerView>(R.id.recycler).visibility = View.GONE
                findViewById<TextView>(R.id.part).visibility = View.GONE
                findViewById<TextView>(R.id.noPart).visibility = View.VISIBLE
                findViewById<LottieAnimationView>(R.id.lottie).visibility = View.VISIBLE
            }
            else{
                findViewById<RecyclerView>(R.id.recycler).visibility = View.VISIBLE
                findViewById<TextView>(R.id.part).visibility = View.VISIBLE
                findViewById<TextView>(R.id.noPart).visibility = View.GONE
                findViewById<LottieAnimationView>(R.id.lottie).visibility = View.GONE
            }


            x = findViewById<RecyclerView>(R.id.recycler)
            y = adaptorforfriendslist(this, result)
            x.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            x.adapter = y

        }

    }

    fun generateQRCode(text: String): Bitmap? {
        val width = 500
        val height = 500
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
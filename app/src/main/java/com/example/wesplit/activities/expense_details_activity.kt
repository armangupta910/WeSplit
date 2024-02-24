package com.example.wesplit.activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforsplitdisplay
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class expense_details_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)
        val dateTime:String = intent.getStringExtra("key") as String
//        val amount:String = intent.getStringExtra("amount") as String
        val name:String = intent.getStringExtra("name") as String
        val paidby:String = intent.getStringExtra("paidby") as String
        val note:String = intent.getStringExtra("note") as String
        val split = intent.getSerializableExtra("split") as HashMap<String,String>

        findViewById<ImageView>(R.id.goback).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        Log.d(TAG,"DateTime: ${dateTime}")
        Log.d(TAG,"name: ${name}")
        Log.d(TAG,"PaidBy: ${paidby}")
//        Log.d(TAG,"Amount: ${amount}")
        Log.d(TAG,"Note: ${note}")
        Log.d(TAG,"Split: ${split}")

        findViewById<TextView>(R.id.name).setText(name.toString())


        var totalAmount:Int = 0
        val data1:MutableList<String> = mutableListOf()
        val data2:MutableList<String> = mutableListOf()
        for(i in split){
            data1.add(i.key)
            data2.add(i.value)
            totalAmount += i.value.toInt()
        }
        val x = findViewById<RecyclerView>(R.id.recyclerdetailsactivity)
        val y = adaptorforsplitdisplay(data1,data2)
        x.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        x.adapter = y

        findViewById<TextView>(R.id.amount).setText("Amount: ₹ " + totalAmount.toString() )

        FirebaseFirestore.getInstance().collection("Users").document(paidby.toString()).get().addOnSuccessListener {
            findViewById<TextView>(R.id.paidby).setText("${it.data?.get("name")} " + "paid ₹ ${totalAmount}" )
            findViewById<ImageView>(R.id.image).setImageDrawable(createLetterDrawable(this,it.data?.get("name").toString()))

        }

        findViewById<ImageButton>(R.id.share).setOnClickListener {
            val screenshot = takeScreenshot(this)
            val screenshotUri = saveScreenshotToCache(this, screenshot)
            screenshotUri?.let {
                shareImage(it, this)
            }
        }



    }

    fun shareImage(uri: Uri, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    fun saveScreenshotToCache(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "screenshot${System.currentTimeMillis()}.png")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun takeScreenshot(activity: Activity): Bitmap {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }



    fun createLetterDrawable(context: Context, name: String): BitmapDrawable {
        val width = 200
        val height = 200

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background Color from Resource
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.green1) // Use your color resource here
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Text settings
        paint.color = Color.WHITE
        paint.textSize = 100f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        // Calculate the positions
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()

        // Extracting initials
        val words = name.split(" ")
        val initials = if (words.isNotEmpty()) {
            val firstInitial = words.first().firstOrNull()?.toUpperCase() ?: '?'
            val lastInitial = if (words.size > 1) words.last().firstOrNull()?.toUpperCase() ?: '?' else firstInitial
            "$firstInitial$lastInitial"
        } else {
            "?"
        }

        // Draw the initials on the Bitmap
        canvas.drawText(initials, xPos.toFloat(), yPos.toFloat(), paint)

        // Return a Drawable
        return BitmapDrawable(context.resources, bitmap)
    }






}
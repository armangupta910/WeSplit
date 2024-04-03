package com.example.wesplit.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class settlingActivity : AppCompatActivity() {
    var friendUID:String = ""
    var amount:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settling)

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)

        friendUID = intent.getStringExtra("friendUID") as String
        amount = intent.getStringExtra("amount") as String

        FirebaseFirestore.getInstance().collection("Users").document(friendUID).get().addOnSuccessListener {
            findViewById<TextView>(R.id.email).setText(it.data?.get("email").toString())
            val name = it.data?.get("name")

            findViewById<TextView>(R.id.name).setText("You paid ${name.toString()}")

            findViewById<EditText>(R.id.amount).setText(amount)
            findViewById<ImageView>(R.id.person2).setImageDrawable(createLetterDrawable(this,name.toString()))
        }
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            val name = it.data?.get("name")
            findViewById<ImageView>(R.id.person1).setImageDrawable(createLetterDrawable(this,name.toString()))
        }

        findViewById<ImageButton>(R.id.settle).setOnClickListener {

            it.startAnimation(scaleAnimation)

            findViewById<ImageButton>(R.id.settle).visibility = View.GONE
            findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE

            var data:HashMap<String,String> = hashMapOf()

            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
                data = it.data?.get("Loans") as HashMap<String, String>
                val curramount:Int = data[friendUID]?.toInt() ?: 0
                amount = findViewById<EditText>(R.id.amount).text.toString()
                Log.d(TAG,"Amount: ${amount}")
                val newamount:Int = curramount + amount.toInt()
                Log.d(TAG,"New Amount: ${newamount}")
                data[friendUID] = newamount.toString()

                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("Loans",data)
            }

            FirebaseFirestore.getInstance().collection("Users").document(friendUID).get().addOnSuccessListener {
                data = it.data?.get("Loans") as HashMap<String, String>
                val curramount:Int = data[FirebaseAuth.getInstance().currentUser?.uid.toString()]?.toInt() ?: 0
                amount = findViewById<EditText>(R.id.amount).text.toString()
                val newamount:Int = curramount - amount.toInt()

                data[FirebaseAuth.getInstance().currentUser?.uid.toString()] = newamount.toString()

                FirebaseFirestore.getInstance().collection("Users").document(friendUID).update("Loans",data)
            }

            Toast.makeText(this,"Settled",Toast.LENGTH_SHORT).show()

            findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
            findViewById<ImageButton>(R.id.settle).visibility = View.VISIBLE

            finish()


        }
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
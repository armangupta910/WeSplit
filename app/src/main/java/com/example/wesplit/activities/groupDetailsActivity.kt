package com.example.wesplit.activities

import android.app.Dialog
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
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorForGroupsFragment
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class groupDetailsActivity : AppCompatActivity() {
    var groupID:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        findViewById<Button>(R.id.remind).visibility = View.GONE

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)

        groupID = intent.getStringExtra("key").toString()

        findViewById<ImageView>(R.id.settings).setOnClickListener {
            val intent = Intent(this,groupsSettings::class.java)
            intent.putExtra("groupID",groupID)

            startActivity(intent)
        }


        findViewById<TextView>(R.id.name).setText(groupID)

        var friend:MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {

            findViewById<Button>(R.id.settle).setOnClickListener {it1->
                friend.clear()
                it1.startAnimation(scaleAnimation)
                val demo = it.data?.get("Loans") as HashMap<String,String>
                for(i in demo){
                    friend.add(i.key)
                }

                friend.remove(FirebaseAuth.getInstance().currentUser?.uid.toString())

                val dialog = Dialog(this) // Use 'requireContext()' instead of 'this' in a Fragment
                dialog.setContentView(R.layout.customdialoglayout)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                // Set up the RecyclerView
                val x = dialog.findViewById<RecyclerView>(R.id.recyclerFriends)
                x.layoutManager = LinearLayoutManager(this)
                x.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                x.adapter = adaptorforfriendslist(this,friend)
                dialog.setCancelable(true)
                dialog.show()
            }





            findViewById<Button>(R.id.remind).setOnClickListener {it1 ->
                it1.startAnimation(scaleAnimation)

                var demo:HashMap<String,String> = hashMapOf()
                demo = it.data?.get("Loans") as HashMap<String, String>

                var participants:MutableList<String> = mutableListOf()

                for(i in demo){
                    participants.add(i.key)
                }



                participants.remove(FirebaseAuth.getInstance().currentUser?.uid.toString())
                var friends:MutableList<String> = mutableListOf()
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {it2 ->
                    val loans:HashMap<String,String> = it2.data?.get("Loans") as HashMap<String, String>

                    for(i in participants){
                        if(loans[i]?.toInt()!! > 0){
                            friends.add(i)
                        }
                    }

                    val dialog = Dialog(this) // Use 'requireContext()' instead of 'this' in a Fragment
                    dialog.setContentView(R.layout.customdialoglayout)
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    // Set up the RecyclerView
                    val x = dialog.findViewById<RecyclerView>(R.id.recyclerFriends)
                    x.layoutManager = LinearLayoutManager(this)
                    x.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    x.adapter = adaptorforfriendslist(this,friends)
                    dialog.setCancelable(true)
                    dialog.show()


                }
            }





            findViewById<TextView>(R.id.toolbarName).setText(it.get("Name").toString())
            findViewById<TextView>(R.id.name).setText(it.get("Name").toString())

            findViewById<ImageView>(R.id.toolbarImage).setImageDrawable(createLetterDrawable(this,it.get("Name").toString()))

            findViewById<ImageView>(R.id.image).setImageDrawable(createLetterDrawable(this,it.get("Name").toString()))

            var Loans:HashMap<String,String> = hashMapOf()

            Loans = it.data?.get("Loans") as HashMap<String, String>

            val amount:Int = Loans[FirebaseAuth.getInstance().currentUser?.uid.toString()]?.toInt()!!

            if(amount<0){
                findViewById<TextView>(R.id.amount).setText("You've borrowed ₹ " + ((-1)*amount).toString() + " in this Group")
                findViewById<TextView>(R.id.amount).setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            if(amount>0){
                findViewById<TextView>(R.id.amount).setText("You've lent ₹ " + (amount).toString() + " in this Group")
                findViewById<TextView>(R.id.amount).setTextColor(ContextCompat.getColor(this, R.color.green1))
            }
            if(amount == 0){
                findViewById<TextView>(R.id.amount).setText("You're all settled in this Group")
                findViewById<TextView>(R.id.amount).setTextColor(ContextCompat.getColor(this, R.color.black))
            }

        }


        findViewById<Button>(R.id.addExpenseFloatingButton).setOnClickListener {
            it.startAnimation(scaleAnimation)
            val intent = Intent(this, addExpenseforGroup::class.java)

            // Pass any data you need to the new activity here
            intent.putExtra("key", groupID)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        }

        var preData:HashMap<String,MutableList<HashMap<String,String>>> = hashMapOf()

        FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {
            preData = it.data?.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>
            var data: MutableList<HashMap<String,MutableList<HashMap<String,String>>>> = mutableListOf()

            for(i in preData){
                val demodata = hashMapOf<String,MutableList<HashMap<String,String>>>(
                    i.key to i.value
                )
                data.add(demodata)
            }

            val x = findViewById<RecyclerView>(R.id.recyclerFriendsDetailedExpenses)
            val y = adaptorforexpenses(this,data)
            x.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false)
            x.adapter = y
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
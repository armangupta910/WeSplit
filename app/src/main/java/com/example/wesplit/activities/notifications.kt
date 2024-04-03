package com.example.wesplit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.example.wesplit.recyclerviews.adaptorfornotifications
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        var data: HashMap<String,String> = hashMapOf()

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            data = it.data?.get("Notification") as HashMap<String, String>

            if(data.size == 0){
                findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                findViewById<LottieAnimationView>(R.id.empty).visibility = View.VISIBLE
                findViewById<RecyclerView>(R.id.notificationrecycler).visibility = View.GONE
            }

            var finalData:MutableList<HashMap<String,String>> = mutableListOf()



                for (i in data) {
                    finalData.add(hashMapOf(i.key to i.value))
                }

            val x = findViewById<RecyclerView>(R.id.notificationrecycler)
            x.layoutManager = LinearLayoutManager(this)
            x.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            x.adapter = adaptorfornotifications(this,finalData)

        }


    }
}
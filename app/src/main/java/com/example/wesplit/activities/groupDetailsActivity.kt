package com.example.wesplit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.google.firebase.firestore.FirebaseFirestore

class groupDetailsActivity : AppCompatActivity() {
    var groupID:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        groupID = intent.getStringExtra("key").toString()


        findViewById<TextView>(R.id.name).setText(groupID)

        FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {
            findViewById<TextView>(R.id.toolbarName).setText(it.get("Name").toString())
            findViewById<TextView>(R.id.name).setText(it.get("Name").toString())
        }

        findViewById<Button>(R.id.addExpenseFloatingButton).setOnClickListener {
            val intent = Intent(this, addExpenseforGroup::class.java)

            // Pass any data you need to the new activity here
            intent.putExtra("key", groupID)
            startActivity(intent)
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
}
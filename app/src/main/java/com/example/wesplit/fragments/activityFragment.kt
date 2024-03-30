package com.example.wesplit.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class activityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag = inflater.inflate(R.layout.fragment_activity, container, false)

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())

        userDocumentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val expensesField:HashMap<String, MutableList<HashMap<String, String>>> = document.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>
                val data:MutableList<HashMap<String, MutableList<HashMap<String, String>>>> = mutableListOf()
                for(i in expensesField){
                    var demo:HashMap<String, MutableList<HashMap<String, String>>> = hashMapOf()
                    demo[i.key] = i.value
                    data.add(demo)
                }
                    // Convert and sort the entries by date-time in descending order
                data.sortByDescending { it.keys.first() }
                Log.d(TAG,"Data: ${data}")
                    val x = frag.findViewById<RecyclerView>(R.id.recyclerActivity)
                    val y = context?.let { adaptorforexpenses(it,data) }
                    x.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                    x.adapter = y


            } else {
                println("Document does not exist")
            }
        }.addOnFailureListener { exception ->
            println("Error fetching document: $exception")
        }

        return frag
    }




}
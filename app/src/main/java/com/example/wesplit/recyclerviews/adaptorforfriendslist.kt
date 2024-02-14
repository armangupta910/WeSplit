package com.example.wesplit.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.google.firebase.firestore.FirebaseFirestore


class adaptorforfriendslist(private val listioffriends:MutableList<String>):RecyclerView.Adapter<adaptorforfriendslist.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.name)
        val email:TextView = itemView.findViewById(R.id.email)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_friends_list,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return listioffriends.size
    }

    override fun onBindViewHolder(holder: view_holder, position: Int) {
        val userId = listioffriends[position]
        val db = FirebaseFirestore.getInstance()

        // Reference to the user's document in the "Users" collection
        val docRef = db.collection("Users").document(userId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Extract the name and email from the document
                val name = documentSnapshot.getString("name")
                val email = documentSnapshot.getString("email")
                holder.name.setText(name.toString())
                holder.email.setText(email.toString())
            } else {
                // Handle the case where the document does not exist
                println("No such document!")
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occur during the retrieval
            println("Error getting document: $exception")
        }

    }
}
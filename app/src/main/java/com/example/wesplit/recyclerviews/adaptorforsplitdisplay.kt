package com.example.wesplit.recyclerviews

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.activities.expense_details_activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import kotlin.math.abs


class adaptorforsplitdisplay(val UID:MutableList<String>,val amount:MutableList<String>):RecyclerView.Adapter<adaptorforsplitdisplay.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val text:TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_table,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return amount.size
    }

    override fun onBindViewHolder(holder: view_holder, position: Int) {
        val UID:String = UID[position]
        val amount:String = amount[position]

        FirebaseFirestore.getInstance().collection("Users").document(UID).get().addOnSuccessListener {
            holder.text.setText("${it.data?.get("name")} - ₹ " + amount)
        }

    }
}
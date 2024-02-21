package com.example.wesplit.recyclerviews

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.google.firebase.firestore.FirebaseFirestore


class adaptorforexpenses(val data:MutableList<HashMap<String,MutableList<HashMap<String,String>>>>):RecyclerView.Adapter<adaptorforexpenses.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.expenseName)
        val paidBy:TextView = itemView.findViewById(R.id.paidBy)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_expense,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: view_holder, position: Int) {

        holder.name.setText(data[position].values.first()[2]["Description"])
        FirebaseFirestore.getInstance().collection("Users").document(data[position].values.first()[0]["Paid by"].toString()).get().addOnSuccessListener {
            if(it.data?.get("name") == null){
                Log.d(TAG,"Yeh NULL hai: ${it.id.toString()}")
            }
            holder.paidBy.setText("Paid by: " + it.data?.get("name"))
        }


    }
}
package com.example.wesplit.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R


class adaptorForGroupsFragment(val data:MutableList<HashMap<String,Any>>):RecyclerView.Adapter<adaptorForGroupsFragment.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.name)
        val type:TextView = itemView.findViewById(R.id.type)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_recycler,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: view_holder, position: Int) {

        holder.name.setText(data[position].get("Name").toString())
        holder.type.setText("Type: " + data[position].get("Type").toString())
    }
}
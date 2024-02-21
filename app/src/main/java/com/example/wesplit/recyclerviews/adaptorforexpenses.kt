package com.example.wesplit.recyclerviews

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class adaptorforexpenses(val data:MutableList<HashMap<String,MutableList<HashMap<String,String>>>>):RecyclerView.Adapter<adaptorforexpenses.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.expenseName)
        val paidBy:TextView = itemView.findViewById(R.id.paidBy)
        val date:TextView = itemView.findViewById(R.id.date)
        val month:TextView = itemView.findViewById(R.id.month)
        val oweorowed:TextView = itemView.findViewById(R.id.oweorowed)
        val amount:TextView = itemView.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_expense,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: view_holder, position: Int) {

        holder.name.setText(data[position].values.first()[2]["Description"])

        FirebaseFirestore.getInstance().collection("Users").document(data[position].values.first()[0]["Paid by"].toString()).get().addOnSuccessListener {
            if(it.data?.get("name") == null){
                Log.d(TAG,"Yeh NULL hai: ${it.id.toString()}")
            }
            holder.paidBy.setText("Paid by: " + it.data?.get("name"))
        }

        val dateTimeKey = data[position].keys.first()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateTimeKey, formatter)

        val dayOfMonth = dateTime.dayOfMonth.toString()
        val monthName = dateTime.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        holder.date.setText(dayOfMonth)
        holder.month.setText(monthName)

        // Assuming 'currentUserUid' is the UID of the current user
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString() // Replace with actual current user UID
        val expenseDetails = data[position].values.first()
        Log.d(TAG,"Expense Details: ${expenseDetails}")

        val paidByUid = expenseDetails[0]["Paid by"].toString()
        val expenseAmount = expenseDetails[1][paidByUid]?.toDoubleOrNull() ?: 0.0 // Assuming this is the total amount paid for the expense
        val currentUserShare = expenseDetails[1][currentUserUid]?.toDoubleOrNull() ?: 0.0 // This is how much the current user is supposed to contribute

        // Determine if the current user owes money or is owed money
        if (paidByUid == currentUserUid) {
            // Current user paid for the expense, check if others owe him/her
            val totalContribution = expenseDetails[1].values.sumOf { it.toDoubleOrNull() ?: 0.0 } //Total Kharcha
            val balance = totalContribution - expenseAmount // Pay krne wale(Khud user) ne kitna zyada Pay kiya?

            if (balance > 0) {
                // Current user is owed money
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You are owed: $balance")
                holder.oweorowed.setText("Lent")
                holder.amount.setText("₹ " + balance.toString())
            } else if (balance < 0) {
                // Current user owes money (unlikely scenario if current user paid)
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You owe: ${-balance}")
                holder.oweorowed.setText("Borrowed")
                holder.amount.setText("₹ " + (-balance).toString())
                holder.amount.setTextColor(Color.RED)
            } else {
                // Balanced, no money owed either way
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},All settled")
                holder.oweorowed.setText("Settled")
                holder.amount.setText("₹ 0.00")
            }
        } else {
            // Someone else paid for the expense, check if current user owes them
            if (currentUserShare > 0) {
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You owe: $currentUserShare")
                holder.oweorowed.setText("Borrowed")
                holder.amount.setText("₹ " + currentUserShare.toString())
                holder.amount.setTextColor(Color.RED)
            } else {
                // Current user did not participate or owes nothing
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You do not owe anything")
            }
        }


    }


}
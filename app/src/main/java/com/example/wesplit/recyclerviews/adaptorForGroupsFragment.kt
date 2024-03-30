package com.example.wesplit.recyclerviews

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.activities.groupDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class adaptorForGroupsFragment(val context:Context,val data:MutableList<HashMap<String,Any>>):RecyclerView.Adapter<adaptorForGroupsFragment.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.name)
        val type:TextView = itemView.findViewById(R.id.type)

        val card:LinearLayout = itemView.findViewById(R.id.card)

        val image:ImageView = itemView.findViewById(R.id.image)

        val amount:TextView = itemView.findViewById(R.id.amount)

        val progi:CircularProgressBar = itemView.findViewById(R.id.progressBar)
        val lottie1:LottieAnimationView = itemView.findViewById(R.id.lottieprogi)

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

        holder.progi.visibility = View.VISIBLE

        holder.name.setText(data[position].get("Name").toString())
        holder.type.setText("Type: " + data[position].get("Type").toString())
        holder.image.setImageDrawable(createLetterDrawable(context,data[position].get("Name").toString()))

        val Loans:HashMap<String,String> = data[position].get("Loans") as HashMap<String, String>
        val money:Int = Loans[FirebaseAuth.getInstance().currentUser?.uid.toString()]?.toInt()!!

        if(money < 0){
            holder.amount.setText("You've borrowed ₹ " + ((-1)*money).toString() + " from this group")
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        if(money > 0){
            holder.amount.setText("You've lent ₹ " + ((money).toString() + " to this group"))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green1))
        }

        if(money == 0){
            holder.amount.setText("You're all settled with this group")
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.card.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, groupDetailsActivity::class.java)

            // Pass any data you need to the new activity here
            intent.putExtra("key", data[position]["key"].toString())

            context.startActivity(intent)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            holder.progi.visibility = View.GONE

            holder.name.visibility = View.VISIBLE
            holder.amount.visibility = View.VISIBLE
            holder.type.visibility = View.VISIBLE

            holder.lottie1.visibility = View.GONE
        }, 500) // Delay of 1 second
    }

    fun createLetterDrawable(context: Context, name: String): BitmapDrawable {
        val width = 100
        val height = 100

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background Color from Resource
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.green1) // Use your color resource here
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Text settings
        paint.color = Color.WHITE
        paint.textSize = 50f
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
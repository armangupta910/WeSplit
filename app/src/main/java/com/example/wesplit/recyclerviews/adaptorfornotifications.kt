package com.example.wesplit.recyclerviews

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class adaptorfornotifications(val conti:Context,val data:MutableList<HashMap<String,String>>): RecyclerView.Adapter<adaptorfornotifications.view_holder>() {

    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){

        val profile:ImageView = itemView.findViewById(R.id.profileImage)
        val type2:TextView = itemView.findViewById(R.id.type)
        val name:TextView = itemView.findViewById(R.id.name)
        val accept:Button = itemView.findViewById(R.id.accept)
        val reject:Button = itemView.findViewById(R.id.reject)
        val card:LinearLayout = itemView.findViewById(R.id.card)
        val animation:LottieAnimationView = itemView.findViewById(R.id.lottieprogi)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adaptorfornotifications.view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notificationcard,parent,false)
        return adaptorfornotifications.view_holder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: adaptorfornotifications.view_holder, position: Int) {
        val db = FirebaseFirestore.getInstance()

        val demo = data[position].values

        val inputString:String = demo.first()

        val uid1 = inputString.split(" ")[1]

        db.collection("Users").document(uid1).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Extract the name field
                val name = document.data?.get("name") as String
                // Do something with the name, e.g., display it
                holder.name.setText(name)
                holder.profile.setImageDrawable(createLetterDrawable(conti,name))
                holder.name.visibility = View.VISIBLE
                holder.animation.visibility = View.GONE

            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "get failed with ", exception)
        }

        if(inputString.split(" ")[0] == "1"){
            holder.type2.setText("Friend Request")
        }
        else{
            holder.type2.setText("Group Request")
        }

        holder.accept.setOnClickListener {
            addFriend(uid1)
            holder.card.visibility = View.GONE
            val key:String = data[position].keys.first()

            val demodata = data

            demodata.removeAt(position)

            if(demodata.size == 0){
                (conti as? Activity)?.finish()
            }

            val hashi:HashMap<String,String> = hashMapOf()
            for(i in demodata){
                hashi.put(i.keys.first(),i.values.first())
            }

            db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("Notification",hashi).addOnSuccessListener {
//                Toast.makeText(conti,"Friend Added",Toast.LENGTH_SHORT).show()
            }
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

    private fun addFriend(uid:String) {
        val db = FirebaseFirestore.getInstance()
        var ref = true
        Toast.makeText(conti,"Clicked", Toast.LENGTH_SHORT).show()
        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            val friends = it.get("friends") as? List<String> ?: listOf()
            Log.d(ContentValues.TAG,"Friend 2: $friends")
            if (!friends.contains(uid)) {
                db.collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update("friends", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener {
                        Toast.makeText(
                            conti,
                            "Friend added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .get().addOnSuccessListener {
                        val loans =
                            it.get("Loans") as? Map<String, String>?: mapOf()
                        val updatedLoans = HashMap(loans)

                        updatedLoans.putAll(mapOf(uid to "0"))
                        FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .update("Loans", updatedLoans)
                            .addOnSuccessListener {

                            }
                    }
            }
            else{
                Toast.makeText(conti,"Friend already added!",Toast.LENGTH_SHORT).show()
            }
        }

        db.collection("Users").document(uid).get().addOnSuccessListener {
            val friends = it.get("friends") as? List<String> ?: listOf()
            if (!friends.contains(uid)) {
                db.collection("Users")
                    .document(uid)
                    .update("friends", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid.toString()))
                    .addOnSuccessListener {
                        Toast.makeText(
                            conti,
                            "Friend added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                FirebaseFirestore.getInstance().collection("Users")
                    .document(uid)
                    .get().addOnSuccessListener {
                        val loans = it.get("Loans") as? Map<String, String> ?: mapOf()
                        val updatedLoans = HashMap(loans)

                        updatedLoans.putAll(mapOf(FirebaseAuth.getInstance().currentUser?.uid.toString() to "0"))
                        FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(uid)
                            .update("Loans", updatedLoans)
                            .addOnSuccessListener {

                            }
                    }
            }
            else{
                Toast.makeText(conti,"Friend already added!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
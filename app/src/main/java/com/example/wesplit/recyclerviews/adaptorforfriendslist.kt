package com.example.wesplit.recyclerviews

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.activities.expense_details_activity
import com.example.wesplit.activities.friend_expense_details_activity
import com.example.wesplit.activities.groupDetailsActivity
import com.example.wesplit.activities.groupsSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularprogressbar.CircularProgressBar
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import kotlin.math.abs


class adaptorforfriendslist(val conext:Context,private var listioffriends:MutableList<String>):RecyclerView.Adapter<adaptorforfriendslist.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.name)
        val email:TextView = itemView.findViewById(R.id.email)
        val oweorlent:TextView = itemView.findViewById(R.id.oweorlent)
        val amount:TextView = itemView.findViewById(R.id.amount)
        val friend:LinearLayout = itemView.findViewById(R.id.friend)
        val image:ImageView = itemView.findViewById(R.id.profileimage)

        val progi:CircularProgressBar = itemView.findViewById(R.id.progressBar)
        val lotti1:LottieAnimationView = itemView.findViewById(R.id.lottieprogi1)
        val lotti2:LottieAnimationView = itemView.findViewById(R.id.lottieprogi2)
        val delete:ImageView = itemView.findViewById(R.id.delete)

        val card:LinearLayout = itemView.findViewById(R.id.deleteremove)
        val view:View = itemView.findViewById(R.id.view)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_friends_list,parent,false)
        return view_holder(itemView)
    }

    fun updateData(newItems: MutableList<String>) {
        listioffriends = newItems
        notifyDataSetChanged() // Notify any registered observers that the data set has changed.
    }

    override fun getItemCount(): Int {
        return listioffriends.size
    }

    override fun onBindViewHolder(holder: view_holder, position: Int) {
        var userId = listioffriends[position]
        if(listioffriends[position][0] == '!'){
            userId = listioffriends[position].substring(37)
        }
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
                holder.image.setImageDrawable(createLetterDrawable(conext,name.toString()))

                holder.friend.setOnClickListener {
                    val intent = Intent(holder.itemView.context,friend_expense_details_activity::class.java)
                    intent.putExtra("friendUID",userId)
                    holder.itemView.context.startActivity(intent)
                }
            } else {
                // Handle the case where the document does not exist
                println("No such document!")
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occur during the retrieval
            println("Error getting document: $exception")
        }

        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            val loans:HashMap<String,String> = it.data?.get("Loans") as HashMap<String, String>
            var price = loans[userId]?.toInt()
            if (price != null) {
                if(price<0){
                    holder.oweorlent.setText("Borrowed")
                    holder.oweorlent.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                    holder.amount.setTextColor(ContextCompat.getColor(holder.amount.context, R.color.red))
                    holder.amount.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.down,0)
                }
                if(price == 0){
                    holder.oweorlent.setText("Settled")
                    holder.oweorlent.setTextColor(Color.BLACK)
                    holder.amount.setTextColor(ContextCompat.getColor(holder.amount.context, R.color.black))
                    holder.amount.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.settled,0)
                }
                if(price>0){
                    holder.oweorlent.setText("Lent")
                    holder.oweorlent.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green1))
                    holder.amount.setTextColor(ContextCompat.getColor(holder.amount.context, R.color.green1))
                    holder.amount.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.up,0)
                }
                price = abs(price)
                holder.amount.setText("₹ " + price.toString())

            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            holder.progi.visibility = View.GONE

            holder.name.visibility = View.VISIBLE
            holder.email.visibility = View.VISIBLE
            holder.oweorlent.visibility = View.VISIBLE
            holder.amount.visibility = View.VISIBLE

            holder.progi.visibility = View.GONE
            holder.lotti1.visibility = View.GONE
            holder.lotti2.visibility = View.GONE

            if(listioffriends[position][0] == '!'){
                val groupID1111:String = listioffriends[position].substring(1,37)
                FirebaseFirestore.getInstance().collection("Groups").document(groupID1111).get().addOnSuccessListener {
                    val admin:String = it.data?.get("Admin") as String
                    if(admin == FirebaseAuth.getInstance().currentUser?.uid.toString()){
                        holder.delete.visibility = View.VISIBLE
                    }
                }
//                holder.delete.visibility = View.VISIBLE
                holder.oweorlent.visibility = View.GONE
                holder.amount.visibility = View.GONE

            }



        }, 500) // Delay of 1 second



        holder.delete.setOnClickListener {
            val groupID:String = listioffriends[position].substring(1,37)
            FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {
                val demo:MutableList<String> = it.data?.get("Participants") as  MutableList<String>
                val loans:HashMap<String,String> = it.data?.get("Loans") as HashMap<String, String>
                    val builder = AlertDialog.Builder(conext)
                    builder.setTitle("Confirmation")
                    builder.setMessage("You have Pending settlements with the user. Do you still want to remove the user from the Group?")

                    // Set the Positive Button (Yes)
                    builder.setPositiveButton("Yes") { dialog, which ->
                        // Handle Yes button click event
                        demo.remove(listioffriends[position].substring(37))
                        FirebaseFirestore.getInstance().collection("Groups").document(groupID).update("Participants",demo)



                        Log.d(TAG,"Removing :- ${listioffriends[position].substring(37)}")

                        FirebaseFirestore.getInstance().collection("Users").document(listioffriends[position].substring(37)).get().addOnSuccessListener {it1->
                            val groups:MutableList<String> = it1.data?.get("groups") as MutableList<String>
                            groups.remove(groupID)
                            FirebaseFirestore.getInstance().collection("Users").document(listioffriends[position].substring(37)).update("groups",groups).addOnSuccessListener {
                                Toast.makeText(conext,"Participant removed!",Toast.LENGTH_SHORT).show()
                                val newdata = listioffriends
                                newdata.remove(listioffriends[position])
                                updateData(newdata)

                            }
                        }.addOnFailureListener {
                            Toast.makeText(conext,"Data not found",Toast.LENGTH_SHORT).show()
                        }

                    }

                    // Set the Negative Button (No)
                    builder.setNegativeButton("No") { dialog, which ->
                        // Handle No button click event


                    }

                    // Create and show the AlertDialog
                    val dialog = builder.create()
                    dialog.show()



            }
        }
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
package com.example.wesplit.recyclerviews

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.activities.expense_details_activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


class adaptorforexpenses(val conti:Context, var data:MutableList<HashMap<String,MutableList<HashMap<String,String>>>>):RecyclerView.Adapter<adaptorforexpenses.view_holder>() {
    class view_holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.expenseName)
        val date:TextView = itemView.findViewById(R.id.date)
        val month:TextView = itemView.findViewById(R.id.month)
        val amount:TextView = itemView.findViewById(R.id.amount)
        val time:TextView = itemView.findViewById(R.id.time)
        val card:LinearLayout = itemView.findViewById(R.id.card)
        val money:ImageView = itemView.findViewById(R.id.money)
        val progi:CircularProgressBar = itemView.findViewById(R.id.progressBar)
        val heheprogi:LottieAnimationView = itemView.findViewById(R.id.lottieprogi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): view_holder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_expense,parent,false)
        return view_holder(itemView)
    }

    fun updateData(newData: MutableList<HashMap<String,MutableList<HashMap<String,String>>>>) {
        data = newData
        notifyDataSetChanged() // Notifies the adapter to refresh based on new data
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun createAmountDrawableWithColors(context: Context, amount: String, widthInDp: Int, heightInDp: Int, textColor: Int, bgColor: Int): Drawable? {
        val metrics = context.resources.displayMetrics

        // Convert dp dimensions to pixels
        val widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp.toFloat(), metrics).toInt()
        val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp.toFloat(), metrics).toInt()

        // Initialize a Bitmap and Canvas to draw
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set the background color
        bitmap.eraseColor(bgColor) // This will fill the bitmap with the background color

        // Initialize a Paint object for drawing the text
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            // Start with a default text size; adjust below
            textSize = heightPx.toFloat() * 0.4f
        }

        // Measure text and adjust text size to fit within the specified dimensions
        val textBounds = Rect()
        paint.getTextBounds(amount, 0, amount.length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

        // Adjust text size based on the actual width
        if (textWidth > widthPx || textHeight > heightPx) {
            val widthRatio = widthPx.toFloat() / textWidth.toFloat()
            val heightRatio = heightPx.toFloat() / textHeight.toFloat()
            val ratio = minOf(widthRatio, heightRatio)

            paint.textSize = paint.textSize * ratio * 0.9f // Scale down a bit more for padding
        }

        // Re-measure with new size
        paint.getTextBounds(amount, 0, amount.length, textBounds)

        // Calculate the position to center the text
        val x = widthPx / 2f - textBounds.width() / 2f - textBounds.left
        val y = heightPx / 2f + textBounds.height() / 2f - textBounds.bottom

        // Draw the text on the canvas
        canvas.drawText(amount, x, y, paint)

        // Return the bitmap as a Drawable
        return BitmapDrawable(context.resources, bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: view_holder, position: Int) {

//        holder.card.visibility = View.GONE

        holder.heheprogi.playAnimation()

        val uid = data[position].values.first()[0]["Paid by"]
        if(uid.toString() == FirebaseAuth.getInstance().currentUser?.uid.toString()){
//            Toast.makeText(conti,"Ghusa",Toast.LENGTH_SHORT).show()
//            val text1 = "You added "
//            val text2 = data[position].values.first()[2]["Description"].toString()
//            val finalText = text1 + "'${text2}'"
//            Log.d(TAG,"Text1: ${text1} Text2: ${text2} Final: ${finalText}")
//            holder.name.setText(finalText)

            val fullText = "You added '${data[position].values.first()[2]["Description"]}'"

            // Create a SpannableStringBuilder
            val spannableString = SpannableStringBuilder(fullText)

            // Apply normal style to "added"
            val addedIndex = fullText.indexOf("added")
            if (addedIndex != -1) {
                spannableString.setSpan(
                    StyleSpan(Typeface.NORMAL), // Apply normal style
                    addedIndex,
                    addedIndex + "added".length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

// Assuming the rest of the text should be bold by default as per your requirement
// If not, you can apply bold style to specific parts using a similar approach with StyleSpan(Typeface.BOLD)

// Set the SpannableString to the TextView
            holder.name.setText(spannableString)
        }
        else {
            FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get()
                .addOnSuccessListener {

//                    holder.name.setText("${it.get("name")} added '" + "${data[position].values.first()[2]["Description"]}'")
                    val name = it.get("name").toString()
                    val description = data[position].values.first()[2]["Description"].toString()

                    // Construct the full text
                    val fullText = "$name added '${description}'"

                    // Create a SpannableStringBuilder from the full text
                    val spannableString = SpannableStringBuilder(fullText)

                    // Find the start index of "added"
                    val addedIndex = fullText.indexOf("added")
                    if (addedIndex != -1) {
                        // Apply a normal style span to "added"
                        spannableString.setSpan(
                            StyleSpan(Typeface.NORMAL), // Apply normal style
                            addedIndex,
                            addedIndex + "added".length,
                            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

// Set the SpannableString to the TextView
                    holder.name.text = spannableString
                }
        }

        FirebaseFirestore.getInstance().collection("Users").document(data[position].values.first()[0]["Paid by"].toString()).get().addOnSuccessListener {
            if(it.data?.get("name") == null){
                Log.d(TAG,"Yeh NULL hai: ${it.id.toString()}")
            }
        }

        val dateTimeKey = data[position].keys.first()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateTimeKey, formatter)

        val timing = DateTimeFormatter.ofPattern("HH:mm")

        val time = dateTime.toLocalTime().format(timing)
        holder.time.setText(time)
        holder.time.setTextColor(Color.BLACK)


        val dayOfMonth = dateTime.dayOfMonth.toString()
        val monthName = dateTime.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        holder.date.setText(" " + dayOfMonth)
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

//            holder.money.setImageDrawable(createAmountDrawableWithColors(conti,balance.toInt().toString(),40,40,ContextCompat.getColor(conti,R.color.white),ContextCompat.getColor(conti,R.color.green1)))

            if (balance > 0) {
                // Current user is owed money
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You are owed: $balance")
//                holder.amount.setText("You lent " + "₹ " + balance.toString())

                val fullText = "You lent " + "₹ " + balance.toString()
                val spannableString = SpannableStringBuilder(fullText)

                holder.money.setImageDrawable(createAmountDrawableWithColors(conti,balance.toInt().toString(),20,20,ContextCompat.getColor(conti,R.color.white),ContextCompat.getColor(conti,R.color.green1)))

                // Find the start and end indexes of the part you want to color
                val startIndex = fullText.indexOf("₹")
                val endIndex = startIndex + balance.toString().length + 2

                // Set the color to the specific part of the text
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(conti, R.color.green1)), // Set your desired color here
                    startIndex,
                    endIndex,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                holder.amount.setText(spannableString)

            } else if (balance < 0) {
                // Current user owes money (unlikely scenario if current user paid)
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You owe: ${-balance}")
//                holder.oweorowed.setText("Borrowed")
//                holder.amount.setText("You borrowed " + "₹ " + (-balance).toString())

                val fullText = "You borrowed " + "₹ " + (-balance).toString()
                val spannableString = SpannableStringBuilder(fullText)

                holder.money.setImageDrawable(createAmountDrawableWithColors(conti,((-1)*balance).toInt().toString(),20,20,ContextCompat.getColor(conti,R.color.white),ContextCompat.getColor(conti,R.color.green1)))

                // Find the start and end indexes of the part you want to color
                val startIndex = fullText.indexOf("₹")
                val endIndex = startIndex + balance.toString().length + 2

                // Set the color to the specific part of the text
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(conti, R.color.red)), // Set your desired color here
                    startIndex,
                    endIndex,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                holder.amount.setText(spannableString)
            } else {
                // Balanced, no money owed either way
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},All settled")
//                holder.oweorowed.setText("Settled")
                holder.amount.setText("Settled")
                holder.money.setImageDrawable(createAmountDrawableWithColors(conti,balance.toInt().toString(),20,20,ContextCompat.getColor(conti,R.color.white),ContextCompat.getColor(conti,R.color.black)))

            }
        } else {
            // Someone else paid for the expense, check if current user owes them
            if (currentUserShare > 0) {
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You owe: $currentUserShare")
//                holder.oweorowed.setText("Borrowed")
//                holder.amount.setText("You borrowed " + "₹ " + currentUserShare.toString())
//                holder.amount.setTextColor(Color.RED)

                val fullText = "You borrowed " + "₹ " + currentUserShare.toString()
                val spannableString = SpannableStringBuilder(fullText)

                holder.money.setImageDrawable(createAmountDrawableWithColors(conti,currentUserShare.toInt().toString(),40,40,ContextCompat.getColor(conti,R.color.white),ContextCompat.getColor(conti,R.color.red)))

                // Find the start and end indexes of the part you want to color
                val startIndex = fullText.indexOf("₹")
                val endIndex = startIndex + currentUserShare.toString().length + 2

                // Set the color to the specific part of the text
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(conti, R.color.red)), // Set your desired color here
                    startIndex,
                    endIndex,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                holder.amount.setText(spannableString)



            } else {
                // Current user did not participate or owes nothing
                Log.d(TAG,"${data[position].values.first()[2]["Description"]},You do not owe anything")
            }
        }

        holder.card.setOnClickListener {
            val intent = Intent(conti,expense_details_activity::class.java)
            intent.putExtra("key",data[position].keys.first())
//            intent.putExtra("amount",data[position].values.first()[2]["Amount"])
            intent.putExtra("name",data[position].values.first()[2]["Description"])
            intent.putExtra("paidby",data[position].values.first()[0]["Paid by"])
            intent.putExtra("split",data[position].values.first()[1])
            intent.putExtra("note",data[position].values.first()[2]["Note"])
            conti.startActivity(intent)
        }



        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

            holder.progi.visibility = View.GONE

            holder.name.visibility = View.VISIBLE
            holder.amount.visibility = View.VISIBLE
            holder.heheprogi.visibility = View.GONE
        }, 500) // Delay of 1 second

        holder.card.visibility = View.VISIBLE



    }


}
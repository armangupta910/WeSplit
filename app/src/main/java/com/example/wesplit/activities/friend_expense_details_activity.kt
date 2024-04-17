package com.example.wesplit.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforsplitdisplay
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Math.abs
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


class friend_expense_details_activity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_friend_expense_details)

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val friendUID: String? = intent.getStringExtra("friendUID")
        Toast.makeText(this, friendUID, Toast.LENGTH_SHORT).show()
        FirebaseFirestore.getInstance().collection("Users").document(friendUID.toString()).get()
            .addOnSuccessListener {
                findViewById<TextView>(R.id.name).setText(it.data?.get("name").toString())
                findViewById<ImageView>(R.id.image).setImageDrawable(
                    createLetterDrawable(
                        this,
                        it.data?.get("name").toString()
                    )
                )

                findViewById<TextView>(R.id.toolbarName).setText(it.data?.get("name").toString())
                findViewById<ImageView>(R.id.toolbarImage).setImageDrawable(
                    createLetterDrawable(
                        this,
                        it.data?.get("name").toString()
                    )
                )

            }
        FirebaseFirestore.getInstance().collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                val expenses: HashMap<String, MutableList<HashMap<String, String>>> =
                    it.data?.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>
                val loans: HashMap<String, String> =
                    it.data?.get("Loans") as HashMap<String, String>
                for (i in loans) {
                    if (i.key.toString() == friendUID) {
                        val amount: Int = i.value.toInt()
                        if (amount < 0) {
                            findViewById<TextView>(R.id.amount).setText("You borrowed ₹ ${-amount} in total.")
                            findViewById<TextView>(R.id.amount).setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.red
                                )
                            )
//                        findViewById<Button>(R.id.remind).visibility = View.GONE
                        }
                        if (amount > 0) {
                            findViewById<TextView>(R.id.amount).setText("You lent ₹ ${amount} in total.")
                            findViewById<TextView>(R.id.amount).setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.green1
                                )
                            )
//                        findViewById<Button>(R.id.settle).visibility = View.GONE
                        }
                        if (amount == 0) {
                            findViewById<TextView>(R.id.amount).setText("You are all settled.")
                            findViewById<TextView>(R.id.amount).setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.black
                                )
                            )
//                        findViewById<Button>(R.id.remind).visibility = View.GONE
//                        findViewById<Button>(R.id.settle).visibility = View.GONE
                        }
                        findViewById<Button>(R.id.settle).setOnClickListener {
                            it.startAnimation(scaleAnimation)
                            if (amount > 0) {
                                Toast.makeText(
                                    this,
                                    "You don't need to pay your friend!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (amount == 0) {
                                Toast.makeText(
                                    this,
                                    "Everything is settled up!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (amount < 0) {
                                //initiatePayment(it, abs(amount).toString())



                                val demo = -1 * amount
                                val intent = Intent(this,settlingActivity::class.java)
                                intent.putExtra("friendUID",friendUID)
                                intent.putExtra("amount", demo.toString())
                                startActivity(intent)
                                overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)








                            }
                        }
                        findViewById<Button>(R.id.remind).setOnClickListener {
                            it.startAnimation(scaleAnimation)
                            if (amount > 0) {
                                val text: String =
                                    "Hi there, you've ₹${amount} to settle up with me. Please pay me back as soon as possible. Thank You. (This message has been sent using WeSplit App) You can use this Link to pay me back upi://pay?pa=guptaarman910-1@oksbi&pn=Armaan%20Gupta&am=${amount}&cu=INR\n"
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(intent, null)
                                startActivity(shareIntent)
                            }
                            if (amount == 0) {
                                Toast.makeText(
                                    this,
                                    "Everything is settled up!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (amount < 0) {
                                Toast.makeText(
                                    this,
                                    "You should settle up with your friend!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                val relevantExpenses: HashMap<String, MutableList<HashMap<String, String>>> =
                    findEntriesWithKey(expenses, friendUID.toString())
                val data: MutableList<HashMap<String, MutableList<HashMap<String, String>>>> =
                    mutableListOf()
                for (i in relevantExpenses) {
                    val newhashi = hashMapOf<String, MutableList<HashMap<String, String>>>(
                        i.key to i.value
                    )
                    data.add(newhashi)
                }

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                // Sort the list in descending order based on the date-time keys
                data.sortByDescending { entry ->
                    val dateTimeString =
                        entry.keys.first() // Get the first (and only) key from the HashMap
                    LocalDateTime.parse(
                        dateTimeString,
                        formatter
                    ) // Parse the string to LocalDateTime
                }


                Log.d(TAG, "Data: $data")


                val x = findViewById<RecyclerView>(R.id.recyclerFriendsDetailedExpenses)
                val y = adaptorforexpenses(this, data)
                x.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL, false
                )
                x.adapter = y

                val exportCsvButton: Button = findViewById(R.id.export)
                exportCsvButton.setOnClickListener {
                    it.startAnimation(scaleAnimation)
                    // If permissions are not granted, the request flow is started,
                    // and the outcome will be handled in onRequestPermissionsResult()
                }


            }
    }
    fun extractAndFormatPaymentsAsCsv(
        data: MutableList<HashMap<String, MutableList<HashMap<String, String>>>>,
        user1: String,
        user2: String
    ): String {
        val header = "Timestamp,Paid by,Amount,Description,Note\n"
        val csvBuilder = StringBuilder(header)

        data.forEach { entry ->
            val timestamp = entry.keys.first()
            val paymentDetails = entry.values.first()
            val paidBy = paymentDetails[0]["Paid by"] as String
            val splitDetails = paymentDetails[1] as Map<String, Int>
            val description = paymentDetails[2]["Description"] as String
            val note = paymentDetails[2].getOrDefault("Note", "") as String

            if (paidBy == user1 && splitDetails.containsKey(user2)) {
                val amount = splitDetails[user2] ?: 0
                csvBuilder.append("$timestamp,$paidBy,$amount,\"$description\",\"$note\"\n")
            } else if (paidBy == user2 && splitDetails.containsKey(user1)) {
                val amount = splitDetails[user1] ?: 0
                csvBuilder.append("$timestamp,$paidBy,$amount,\"$description\",\"$note\"\n")
            }
        }

        return csvBuilder.toString()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (data != null) {
                val text = data.getStringExtra("response")
                val dataList = ArrayList<String>()
                text?.let {
                    dataList.add(it)
                }
                if (dataList.isNotEmpty()) {
                    val response = dataList[0]
                    // Process the response here
                    // Example: txnId=UPI_TRANSACTION_ID&responseCode=00&Status=Success&txnRef=YOUR_REF_ID
                    val transactionStatus = parseUPIResponse(response, "Status")
                    when (transactionStatus) {
                        "Success" -> {
                            // Handle success
                            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                        }
                        "Failure" -> {
                            // Handle failure
                            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Handle other statuses
                            Toast.makeText(this, "Payment Status Unknown", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Handle case when there's no data received (user cancelled the transaction)
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun parseUPIResponse(response: String, keyToFind: String): String {
        val keyValuePairs = response.split("&")
        keyValuePairs.forEach { pair ->
            val keyValue = pair.split("=")
            if (keyValue.size == 2 && keyValue[0] == keyToFind) {
                return keyValue[1]
            }
        }
        return "Unknown"
    }


    // Define a request code
    val UPI_PAYMENT_REQUEST_CODE = 100

    fun initiatePayment(view: View, amount: String) {
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", "guptaarman910-1@oksbi") // UPI ID
            .appendQueryParameter("pn", "Armaan Gupta") // Payee Name
            .appendQueryParameter("tn", "Demo Transaction") // Transaction Note
            .appendQueryParameter("am", amount) // Amount
            .appendQueryParameter("cu", "INR") // Currency
            .appendQueryParameter("tr", "hehe") // Transaction Reference ID
            .build()

        val upiPaymentIntent = Intent(Intent.ACTION_VIEW).apply { data = uri }

        // Create chooser
        val chooser = Intent.createChooser(upiPaymentIntent, "Pay with")

        // Verify if any app can handle this intent
        if (upiPaymentIntent.resolveActivity(view.context.packageManager) != null) {
            // Start activity for result
            (view.context as Activity).startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE)
        } else {
            Toast.makeText(view.context, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show()
        }
    }




    fun findEntriesWithKey(
        data: HashMap<String, MutableList<HashMap<String, String>>>,
        keyToFind: String
    ): HashMap<String, MutableList<HashMap<String, String>>> {
        // Initialize a new HashMap to store the matching entries
        val matchingEntries = HashMap<String, MutableList<HashMap<String, String>>>()

        // Iterate through each entry in the provided data structure
        data.forEach { (key, list) ->
            // Ensure the list has at least two items to avoid IndexOutOfBoundsException
            if (list.size > 1) {
                // Access the second item in the list
                val secondItem = list[1]
                val firstItem = list[0]

//                if(firstItem["Paid by"] == keyToFind.toString() || firstItem["Paid by"] == FirebaseAuth.getInstance().currentUser?.uid.toString()){
//                    matchingEntries[key] = list
//                }

                // Check if the requested key is one of the keys in the second item's HashMap
                if (keyToFind in secondItem.keys) {
                    // If the condition is satisfied, add the original pair to the new HashMap
                    matchingEntries[key] = list
                }
            }
        }

        // Return the HashMap containing all matching entries
        return matchingEntries
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
}
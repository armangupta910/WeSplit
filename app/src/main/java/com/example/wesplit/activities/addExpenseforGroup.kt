package com.example.wesplit.activities

import android.app.Activity
import android.content.ContentValues
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.wesplit.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class addExpenseforGroup : AppCompatActivity() {
    private var groupID: String = ""

    var paidBy:String = "You"
    var equallyorunequally:String = "Equally"

    val names:MutableList<String> = mutableListOf()
    val namestoAmounts:HashMap<String,String> = hashMapOf()

    var namestoUID:Map<String,String> = mapOf()

//    val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expensefor_group)

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)

        groupID = intent.getStringExtra("key").toString()

        fetchParticipantNames()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchParticipantNames() {

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)
        lifecycleScope.launch {
            val participantsNamesMap = mutableMapOf<String, String>()
            try {
                val participantsIDs = FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().await().data?.get("Participants") as? List<String> ?: listOf()

                for (uid in participantsIDs) {
                    val name = FirebaseFirestore.getInstance().collection("Users").document(uid).get().await().data?.get("name") as? String
                    if (name != null && uid!=FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                        participantsNamesMap[name] = uid
                    }
                }



                for(i in participantsNamesMap){
                    namestoAmounts[i.key] = "0"
                }

                namestoAmounts["You"] = "0"

                Log.d(TAG,"Names and Amounts: $namestoAmounts")


                participantsNamesMap["You"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                namestoUID = participantsNamesMap

                Log.d(TAG, "Names and UIDs: $participantsNamesMap")

                //Adding Chips to the Group
                addNamesAsChips(participantsNamesMap)



                //Pop Up Menu for Paid By
                findViewById<TextView>(R.id.paidby).setOnClickListener { view ->
                    view.startAnimation(scaleAnimation)
                    showPopupMenu1(view,participantsNamesMap)
                }

                //Pop Up Menu for Equally or Unequally
                findViewById<TextView>(R.id.equallyorunequally).setOnClickListener { view ->
                    view.startAnimation(scaleAnimation)
                    showPopupMenu2(view,participantsNamesMap)
                }

                findViewById<ImageButton>(R.id.submit).setOnClickListener {
                    Log.d(TAG,"Split: $equallyorunequally")
                    it.startAnimation(scaleAnimation)
                    Log.d(TAG,"Splitting: $namestoAmounts")

                    Toast.makeText(applicationContext,"Paid by: $paidBy",Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext,"EqorUneq: $namestoAmounts",Toast.LENGTH_SHORT).show()

                    var expense:MutableList<HashMap<String,String>> = mutableListOf()

                    val hashi1:HashMap<String,String> //Paid By
                    hashi1 = hashMapOf(
                        "Paid by" to participantsNamesMap[paidBy].toString()
                    )

                    Log.d(TAG,"Hashi 1: ${hashi1}")

                    val hashi2:HashMap<String,String> = hashMapOf() //UID to Amount

                    if(equallyorunequally == "Equally"){
                        val totAmount:Int = findViewById<EditText>(R.id.amount).text.toString().toInt()
                        val totPeople = namestoAmounts.size

                        for(i in namestoAmounts){
                            hashi2[participantsNamesMap[i.key].toString()] = (totAmount/totPeople).toString()
                        }
                    }

                    else{
                        for(i in namestoAmounts){
                            hashi2[participantsNamesMap[i.key].toString()] = i.value
                        }
                    }

                    Log.d(TAG,"Hashi 2: ${hashi2}")

                    val hashi3:HashMap<String,String> //Other Details

                    hashi3 = hashMapOf(
                        "Description" to findViewById<EditText>(R.id.description).text.toString(),
                        "Note" to findViewById<EditText>(R.id.note).text.toString()
                    )

                    Log.d(TAG,"Hashi 3: ${hashi3}")

                    expense.add(hashi1)
                    expense.add(hashi2)
                    expense.add(hashi3)

                    Log.d(TAG,"Expense Details: ${expense}")


                    for(i in participantsNamesMap){
                        FirebaseFirestore.getInstance().collection("Users").document(i.value).get().addOnSuccessListener {
                            val demodata:HashMap<String,MutableList<HashMap<String,String>>> = it.data?.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val currentDateAndTime = dateFormat.format(Date())
                            demodata[currentDateAndTime] = expense
                            FirebaseFirestore.getInstance().collection("Users").document(i.value).update("Expenses",demodata).addOnSuccessListener {
                            }
                        }
                    }

                    FirebaseFirestore.getInstance().collection("Groups").document(groupID).get().addOnSuccessListener {
                        val oldexpenses:HashMap<String,MutableList<HashMap<String,String>>> = it.data?.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val currentDateAndTime = dateFormat.format(Date())

                        oldexpenses[currentDateAndTime] = expense
                        FirebaseFirestore.getInstance().collection("Groups").document(groupID).update("Expenses",oldexpenses).addOnSuccessListener {
                            Toast.makeText(applicationContext,"Added",Toast.LENGTH_SHORT).show()
                        }

                        var Loans:HashMap<String,String> = hashi1
                        Loans = it.data?.get("Loans") as HashMap<String, String>

                        participantsNamesMap[paidBy]
                        var totalSum:Int = 0
                        for(i in hashi2){
                            totalSum += i.value.toInt()
                            if(i.key != participantsNamesMap[paidBy]){
                                val curramount:Int = Loans[i.key]?.toInt()!!
                                Loans[i.key] = (curramount - i.value.toInt()).toString()

                            }
                        }
                        val currAmount:Int = Loans[participantsNamesMap[paidBy].toString()]?.toInt()!!
                        Loans[participantsNamesMap[paidBy].toString()] = (currAmount + totalSum - hashi2[participantsNamesMap[paidBy]]?.toInt()!!).toString()

                        FirebaseFirestore.getInstance().collection("Groups").document(groupID).update("Loans",Loans)

                    }

//                    hashi1["Paid by"]?.let { it1 -> updateLoans(it1,hashi2) }


                }



            } catch (e: Exception) {
                Log.e(TAG, "Error fetching participant names", e)
            }
        }
    }


    fun updateLoans(payerId: String, loansUpdate: HashMap<String, String>) {
        val db = Firebase.firestore

        // Step 1: Update the payer's document
        val payerDocRef = db.collection("Users").document(payerId)
        db.runTransaction { transaction ->
            val payerSnapshot = transaction.get(payerDocRef)
            val payerLoans = payerSnapshot["Loans"] as? HashMap<String, String> ?: hashMapOf()

            loansUpdate.forEach { (uid, amount) ->
                if(uid != payerId){
                    val currentAmount = payerLoans[uid]?.toIntOrNull() ?: 0
                    val incrementAmount = amount.toIntOrNull() ?: 0
                    payerLoans[uid] = (currentAmount + incrementAmount).toString()
                }
            }

            transaction.update(payerDocRef, "Loans", payerLoans)
        }
            .addOnSuccessListener {
                println("Payer loans updated successfully.")
            }
            .addOnFailureListener { e ->
                println("Failed to update payer loans: $e")
            }

        // Step 2: Update each beneficiary's document
        loansUpdate.forEach { (beneficiaryId, amount) ->
            if (beneficiaryId == payerId) return@forEach // Skip if the beneficiary is the payer

            val beneficiaryDocRef = db.collection("Users").document(beneficiaryId)
            db.runTransaction { transaction ->
                val beneficiarySnapshot = transaction.get(beneficiaryDocRef)
                val beneficiaryLoans = beneficiarySnapshot["Loans"] as? HashMap<String, String> ?: hashMapOf()

                // Assuming you want to decrement the payer's amount in beneficiary's loans
                val currentAmount = beneficiaryLoans[payerId]?.toIntOrNull() ?: 0
                val decrementAmount = amount.toIntOrNull() ?: 0
                beneficiaryLoans[payerId] = (currentAmount - decrementAmount).toString()

                transaction.update(beneficiaryDocRef, "Loans", beneficiaryLoans)
            }
                .addOnSuccessListener {
                    println("Beneficiary $beneficiaryId loans updated successfully.")
                }
                .addOnFailureListener { e ->
                    println("Failed to update beneficiary $beneficiaryId loans: $e")
                }
        }
    }



    companion object {
        private const val TAG = "addExpenseforGroup"
    }

    private fun showPopupMenu1(view: View, namestoUIDs: Map<String,String>) {
        // Initialize the PopupMenu
        val popup = PopupMenu(this, view)

        // Inflate the menu from XML resource (if using XML)
        // popup.menuInflater.inflate(R.menu.your_menu_resource, popup.menu)

        // Alternatively, add menu items directly in code
        for(i in namestoUIDs){
            popup.menu.apply {
                add(i.key.toString())
                // Add as many items as you need
            }
        }




        // Set a click listener for menu item clicks
        popup.setOnMenuItemClickListener { menuItem ->
            // Handle menu item click
            Toast.makeText(this, "Clicked: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.paidby).setText(menuItem.title)
            paidBy = menuItem.title.toString()
            Log.d(TAG,"Paid by updated: $paidBy")
            true
        }
        // Show the PopupMenu
        popup.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopupMenu2(view: View, namestoUIDs: Map<String,String>) {
        // Initialize the PopupMenu
        val popup = PopupMenu(this, view)



        // Inflate the menu from XML resource (if using XML)
        // popup.menuInflater.inflate(R.menu.your_menu_resource, popup.menu)

        // Alternatively, add menu items directly in code

        popup.menu.apply {
            add("Equally")
            add("Unequally")
            // Add as many items as you need
        }

        popup.setOnMenuItemClickListener { menuItem ->
            // Handle menu item click
            Toast.makeText(this, "Clicked: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.equallyorunequally).setText(menuItem.title)
            equallyorunequally = menuItem.title.toString()



            if(menuItem.title == "Unequally"){
                for(i in namestoUIDs){
                    names.add(i.key.toString())
                }

                val linearLayoutContainer = findViewById<LinearLayout>(R.id.linearLayoutContainer)
                names.forEachIndexed { index, item ->
                    val textview = TextView(this).apply {
                        text = item
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f) // Set text size to 20dp
                        setTypeface(typeface, Typeface.BOLD) // Set text style to bold
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                            // Convert 20dp margin to pixels
                            val margin20dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
                            it.setMargins(margin20dp, 0, margin20dp, 0) // Set left and right margins to 20dp
                        }
                    }

                    val editText = EditText(this).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        this.setText("0")
                        this.gravity = Gravity.CENTER_HORIZONTAL
                        namestoAmounts[item] = "0"
                        // Customize your EditText
                    }
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            namestoAmounts[item] =  s.toString()// Update the corresponding user input in the list
                            Log.d(ContentValues.TAG,"Amounts: ${namestoAmounts}")
                        }
                    })
                    val container = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        addView(textview)
                        addView(editText)
                    }
                    linearLayoutContainer.addView(container)
                }
            }

            else{
                findViewById<LinearLayout>(R.id.linearLayoutContainer).removeAllViews()
            }

//            findViewById<ImageButton>(R.id.submit).setOnClickListener {
//                Log.d(TAG,"Split: $equallyorunequally")
//                Log.d(TAG,"Splitting: $namestoAmounts")
//
//                Toast.makeText(this,"Paid by: $paidBy",Toast.LENGTH_SHORT).show()
//                Toast.makeText(this,"EqorUneq: $namestoAmounts",Toast.LENGTH_SHORT).show()
//
//                val expense:MutableList<HashMap<String,String>> = mutableListOf()
//
//                val hashi1:HashMap<String,String>
//                if(paidBy == "You"){
//                    hashi1 = hashMapOf(
//                        "Paid by" to FirebaseAuth.getInstance().currentUser?.uid.toString()
//                    )
//                }
//                else{
//                    hashi1 = hashMapOf(
//                        "Paid by" to namestoUIDs[paidBy].toString()
//                    )
//                }
//
//                val hashi2:HashMap<String,String> = hashMapOf()
//
//                for(i in namestoAmounts){
//                    if(i.key == "You"){
//                        hashi2[FirebaseAuth.getInstance().currentUser?.uid.toString()] = i.value.toString()
//                    }
//                    else{
//                        if(equallyorunequally == "Equally"){
//                            hashi2[namestoUIDs[i.key].toString()] = (findViewById<EditText>(R.id.amount).text.toString().toInt()/namestoUIDs.size).toString()
//                        }
//                        else{
//                            hashi2[namestoUIDs[i.key].toString()] = i.value
//                        }
//
//                    }
//                }
//
//                val hashi3:HashMap<String,String>
//
//                hashi3 = hashMapOf(
//                    "Description" to findViewById<EditText>(R.id.description).text.toString(),
//                    "Note" to findViewById<EditText>(R.id.note).text.toString()
//                )
//
//                expense.add(hashi1)
//                expense.add(hashi2)
//                expense.add(hashi3)
//
//                for(i in namestoUIDs){
//                    FirebaseFirestore.getInstance().collection("Users").document(i.value).get().addOnSuccessListener {
//                        val demodata:HashMap<String,MutableList<HashMap<String,String>>> = it.data?.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>
//                        val current = LocalDateTime.now()
//                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")
//                        demodata[current.format(formatter)] = expense
//                        FirebaseFirestore.getInstance().collection("Users").document(i.value).update("Expenses",demodata).addOnSuccessListener {
//                            Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show()
//                        }
//
//                    }
//                }
//            }


            true
        }

        popup.show()
    }


    private fun addNamesAsChips(namesMap: Map<String, String>) {
        val chipGroup = findViewById<ChipGroup>(R.id.chip_group)
        chipGroup.removeAllViews() // Remove all views to prevent duplicates if this method is called multiple times

        namesMap.keys.forEach { name ->
            val chip = Chip(this).apply {
                text = name
                isCloseIconVisible = false // Set true if you want a close icon
                // Any other Chip customization
                setOnClickListener {
                    // Handle chip click, if necessary
                }
            }
            chipGroup.addView(chip)
        }
    }



}

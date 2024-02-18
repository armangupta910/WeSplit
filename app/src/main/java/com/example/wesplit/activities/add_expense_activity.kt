package com.example.wesplit.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import com.example.wesplit.DataCallback
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

class add_expense_activity : AppCompatActivity() {
    private var selectedMenuItemId: Int? = null
    private var friendsList: MutableList<String> = mutableListOf()
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    var paidBy = "You"
    var ref = 0

    private var friendsAdded:MutableList<String> = mutableListOf()
    val friendsNames = mutableListOf<String>()
    val hashi:HashMap<String,String> = hashMapOf() // UID to Name
    var map:HashMap<String,String> = hashMapOf() // Name to Amount (You have to use hashi to convert names into respective UIDs )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        findViewById<ImageButton>(R.id.backAddExpenseFragment).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.equallyorunequally).setOnClickListener {view->
            showPopupMenu(view)
        }

        //Adding Search Functionality

        autoCompleteTextView = findViewById(R.id.searchBox)
        setupAutoCompleteTextView()

        Toast.makeText(this,"Size of Hashi: ${hashi.size}, Size of FriendsName: ${friendsNames.size}",Toast.LENGTH_SHORT).show()

        var addExpense: ImageButton = findViewById(R.id.checkAddExpense)
        addExpense.setOnClickListener {
            findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
            addExpense.visibility = View.GONE
            var name = ""
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
                name = it.get("name").toString()
            }
            hashi[FirebaseAuth.getInstance().currentUser?.uid.toString()] = name
            Toast.makeText(this,"Friends Added: ${friendsAdded.size}",Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"Hashi size: ${hashi.size}",Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"Map size: ${map.size}",Toast.LENGTH_SHORT).show()
            val amount = findViewById<EditText>(R.id.amount).text.toString()
            if(ref == 0){
                val amountinintegertoeachperson = amount.toInt()/(hashi.size)
                //Itte rupye baantne hain.....amount Hashi mein jo UIDs hai unmein
                val data:MutableList<HashMap<String,String>> = mutableListOf()

                val paidby:HashMap<String,String> = hashMapOf()
                if(paidBy == "You"){
                    paidby["Paid by"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                }
                else{
                    paidby["Paid by"] = paidBy
                }

                val paidsforandamounts:HashMap<String,String> = hashMapOf()
                var UIDtoamount:HashMap<String,String> = hashMapOf()
//                for(i in hashi){
//                    UIDtoamount[i.key] = map[i.value].toString()
//                }

                for(i in hashi){
                    paidsforandamounts[i.key] = amountinintegertoeachperson.toString()
                }
                Log.d(TAG,"Paid for and Amount: ${paidsforandamounts}")
                paidsforandamounts[FirebaseAuth.getInstance().currentUser?.uid.toString()] = amountinintegertoeachperson.toString()

                var otherData:HashMap<String,String> = hashMapOf(
                    "Description" to findViewById<EditText>(R.id.description).text.toString(),
                    "Note" to findViewById<EditText>(R.id.note).text.toString()
                )

                data.add(paidby)
                data.add(paidsforandamounts)
                data.add(otherData)

                Log.d(TAG,"Data going to be inserted: ${data}")

                addOrUpdateExpenses(UIDtoamount,data)

                Log.d(TAG,"Final Done")

            }
            else{
                Toast.makeText(this,"Ghusa",Toast.LENGTH_SHORT).show()
                val amountinintegertoeachperson = amount.toInt()/(hashi.size+1)
                //Itte rupye baantne hain.....amount Hashi mein jo UIDs hai unmein
                val data:MutableList<HashMap<String,String>> = mutableListOf()

                val paidby:HashMap<String,String> = hashMapOf()
                if(paidBy == "You"){
                    paidby["Paid by"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                }
                else{
                    paidby["Paid by"] = paidBy
                }

                val paidsforandamounts:HashMap<String,String> = hashMapOf()
                var UIDtoamount:HashMap<String,String> = hashMapOf()
                for(i in hashi){
                    UIDtoamount[i.key] = map[i.value].toString()
                }
                UIDtoamount[FirebaseAuth.getInstance().currentUser?.uid.toString()] = map["You"].toString()

                Log.d(TAG,"UID to Amounts: ${UIDtoamount}")

                for(i in UIDtoamount){
                    paidsforandamounts[i.key] = i.value
                }

                var otherData:HashMap<String,String> = hashMapOf(
                    "Description" to findViewById<EditText>(R.id.description).text.toString(),
                    "Note" to findViewById<EditText>(R.id.note).text.toString()
                )

                data.add(paidby)
                data.add(paidsforandamounts)
                data.add(otherData)

                addOrUpdateExpenses(UIDtoamount,data)
            }
            Toast.makeText(this,"Expenditure added",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    fun addOrUpdateExpenses(UIDtoamount:HashMap<String,String>, expensesList: MutableList<HashMap<String, String>>) {
        for(uid in UIDtoamount) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("Users").document(uid.key)

            // Get the current date and time as a string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDateAndTime = dateFormat.format(Date())

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Check if the Expenses field exists
                    if (document.data?.containsKey("Expenses") == true) {
                        // The Expenses field exists, update it with new data
                        val expenses = document.data?.get("Expenses") as Map<String, Any>?
                        val currentExpensesList =
                            expenses?.get(currentDateAndTime) as MutableList<Map<String, String>>?

                        if (currentExpensesList != null) {
                            // If there's already an entry for the current date, add to it
                            currentExpensesList.addAll(expensesList)
                            userDocRef.update("Expenses.$currentDateAndTime", currentExpensesList)
                                .addOnSuccessListener {
                                    println("Expenses for $currentDateAndTime successfully updated.")
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                }
                        } else {
                            // If there's no entry for the current date, create one
                            userDocRef.update("Expenses.$currentDateAndTime", expensesList)
                                .addOnSuccessListener {
                                    println("New expenses for $currentDateAndTime successfully added.")
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                }
                        }
                    } else {
                        // The Expenses field does not exist, create it
                        val expensesMap = hashMapOf<String, Any>(
                            "Expenses" to hashMapOf(
                                currentDateAndTime to expensesList
                            )
                        )
                        userDocRef.update(expensesMap).addOnSuccessListener {
                            // Successfully updated the document with the Expenses field
                            println("Document successfully updated with new Expenses field.")
                            Log.d(TAG, "Done")
                        }.addOnFailureListener { e ->
                            // Handle the failure
                            Log.d(TAG, "Not Done")
                            e.printStackTrace()
                        }
                    }
                } else {
                    // Handle the case where the document does not exist
                    println("Document does not exist!")
                }
            }.addOnFailureListener { exception ->
                // Handle any failures in getting the document
                exception.printStackTrace()
            }
        }
    }

    fun addExpense(documentId: String, expensesMap: Map<String, String>) {
        // Get an instance of Firestore
        val db = FirebaseFirestore.getInstance()

        // Prepare the outer map which contains the "Expenses" key
        val documentMap = hashMapOf(
            "Expenses" to expensesMap
        )

        // Add the document with the specified ID and the map to the "Expenses" collection
        db.collection("Expenses").document(documentId).set(documentMap)
            .addOnSuccessListener {
                // Handle success
                Log.d("Firestore", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w("Firestore", "Error writing document", e)
            }
    }




    private fun setupAutoCompleteTextView() {
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val queryText = s.toString().trim()
                if (queryText.length >= 1) { // Fetch suggestions if user has typed at least 1 character
                    fetchDataFromFirestoreAndFilterFriendsByName(queryText,autoCompleteTextView)
                }
            }
        })

        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            // Get the selected item
            val selectedItem = adapterView.getItemAtPosition(position).toString()
            // Now you can use the selected item to do whatever you need


            // Example: perform an action with the selected item
//            handleSelectedItem(selectedItem)
        }
    }

    private fun fetchDataFromFirestoreAndFilterFriendsByName(queryText: String,autoCompleteTextView: AutoCompleteTextView) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString() // Make sure to replace this with the actual current user's ID

        // Fetch the current user's friends list
        db.collection("Users").document(userId).get().addOnSuccessListener { document ->
            val friendsList = document.get("friends") as? MutableList<String> ?: return@addOnSuccessListener

            // Track completion of all friend queries
            val countDownLatch = CountDownLatch(friendsList.size)

            // For each friend UID, fetch their user document
            friendsList.forEach { friendUid ->
                db.collection("Users").document(friendUid).get().addOnSuccessListener { friendDoc ->
                    val friendName = friendDoc.getString("name")

                    // Check if the friend's name matches the query and add it to the list
                    friendName?.let {
                        if (it.contains(queryText, ignoreCase = true)) { // Basic partial and case-insensitive matching
                            var fuck = 0
                            for(i in friendsNames){
                                if(i==it){
                                    fuck = 1
                                    break
                                }
                            }
                            if(fuck == 0){
                                friendsNames.add(it)
                                hashi[friendDoc.id.toString()] = it
                            }

                        }
                    }

                    countDownLatch.countDown()

                    Log.d(TAG,"Friends Name: ${friendsNames}")
                    Log.d(TAG,"Hashi: ${hashi}")
                }.addOnFailureListener {
                    // Handle failure
                    countDownLatch.countDown()
                }
//                autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
//                    // Get the selected item
//                    val selectedItem = adapterView.getItemAtPosition(position).toString()
//                    var reqUID:String = ""
//                    for ((key,value) in hashi){
//                        if(value == selectedItem){
//                            reqUID = key
//                            val chip = Chip(this).apply {
//                                text = hashi[reqUID]
//                                isCloseIconVisible = true
//
//                                setOnCloseIconClickListener{
//                                        findViewById<ChipGroup>(R.id.chip_group).removeView(this@apply)
//
//                                }
//                            }
//                            findViewById<ChipGroup>(R.id.chip_group).addView(chip)
//                        }
//                    }
//                    friendsAdded.add(reqUID)
//                    Toast.makeText(this,"${friendsAdded.size} clicked!",Toast.LENGTH_SHORT).show()
//                    // Now you can use the selected item to do whatever you need
//
//
//                    // Example: perform an action with the selected item
////            handleSelectedItem(selectedItem)
//                }

                autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
                    // Get the selected item text
                    val selectedItem = adapterView.getItemAtPosition(position) as String
                    var reqUID = ""
                    // Iterate over the HashMap to find the matching value and get its key
                    for ((key, value) in hashi) {
                        if (value == selectedItem) {
                            reqUID = key
                            break // Exit the loop once the match is found
                        }
                    }

                    // Proceed only if reqUID is not empty

                    if (reqUID.isNotEmpty()) {
                        val chip = Chip(this).apply {
                            text = selectedItem
                            isCloseIconVisible = true
                            // Ensure the correct context is used, especially if this code is inside a Fragment
                            setOnCloseIconClickListener {
                                (it.parent as? ChipGroup)?.removeView(it)
                                val iter = friendsAdded.iterator()

                                while(iter.hasNext()){
                                    val item:String = iter.next().toString()
                                    if(hashi[item] == selectedItem){
                                        iter.remove()
                                        break
                                    }
                                }
                            }
                        }
                        var demoref = 0
                        for(item in friendsAdded){
                            if(item == reqUID){
                                demoref = 1
                                break
                            }
                        }
                        if(demoref == 0){
                            friendsAdded.add(reqUID)
                            findViewById<ChipGroup>(R.id.chip_group)?.addView(chip)
                            Toast.makeText(this, "${friendsAdded.size} clicked!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this,"Already Added",Toast.LENGTH_SHORT).show()
                        }
                        // Use safe call (?)
                    }
                    findViewById<TextView>(R.id.paidby).setOnClickListener {

                        val itemsMap: MutableMap<String, String> = mutableMapOf()
                        for(i in friendsAdded){
                            itemsMap[i] = hashi[i].toString()
                        }
                        showPopupMenuWithMapItems(it, itemsMap)

                    }

                }

            }

            Thread {
                try {
                    countDownLatch.await()

                    runOnUiThread {
                        updateAutoCompleteTextView(friendsNames)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()

        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents: ", exception)
        }


    }

    fun showPopupMenuWithMapItems(view: View, itemsMap: Map<String, String>) {
        val popupMenu = PopupMenu(view.context, view)
        itemsMap.forEach { entry ->
            popupMenu.menu.add(entry.value)
        }
        popupMenu.menu.add("you")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // Handle menu item click events here
            // Find the key (UID) associated with the clicked name
            val selectedKey = itemsMap.filterValues { it == menuItem.title }.keys.firstOrNull()
            if(menuItem.title == "you"){
                paidBy = "You"
                findViewById<TextView>(R.id.paidby).setText("you")
            }
            else{
                paidBy = selectedKey.toString()
                findViewById<TextView>(R.id.paidby).setText(itemsMap[selectedKey])
            }

            Toast.makeText(view.context, "Selected UID: $selectedKey", Toast.LENGTH_SHORT).show()
            true
        }

        popupMenu.show()
    }


    private fun updateAutoCompleteTextView(items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.showDropDown()
    }

    private fun uncheckAllMenuItems(popupMenu: PopupMenu) {
        val menu = popupMenu.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
    }


    val texts:MutableList<TextView> = mutableListOf()
    val edits:MutableList<EditText> = mutableListOf()
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.adding_expense_menu, popupMenu.menu)

        // Set initial check state based on stored value
        selectedMenuItemId?.let {
            popupMenu.menu.findItem(it).isChecked = true
        } ?: run {
            // Default selection if no item was previously selected
            popupMenu.menu.findItem(R.id.equally).isChecked = true
            selectedMenuItemId = R.id.equally // Update this as per your logic
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // This is not necessary as checkableBehavior="single" should handle it
            // but included for explicitness
            uncheckAllMenuItems(popupMenu)
            menuItem.isChecked = true
            selectedMenuItemId = menuItem.itemId // Update the selected item ID

            when (menuItem.itemId) {
                R.id.equally -> {
                    // Handle "Equally" option clicked
                    findViewById<TextView>(R.id.equallyorunequally).setText("Equally")
                    findViewById<LinearLayout>(R.id.linearLayoutContainer).removeAllViews()
                    ref = 0
                    true
                }
                R.id.unequally -> {
                    // Handle "Unequally" option clicked
                    findViewById<TextView>(R.id.equallyorunequally).setText("Unequally")

                    FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener {
                        var name:MutableList<String> = mutableListOf()
                        var hashmap:HashMap<String,String> = hashMapOf() //UID to Name

                        for(i in friendsAdded){
                            for(j in it){
                                if(j.id.toString() == i){
                                    name.add(j.data.get("name").toString())
                                    hashmap[i]=j.data.get("name").toString()
                                }
                            }
                        }
                        val items = name
                        items.add("You")
                        val linearLayoutContainer = findViewById<LinearLayout>(R.id.linearLayoutContainer)
                        items.forEachIndexed { index, item ->
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
                                // Customize your EditText
                            }
                            editText.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                }

                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                }

                                override fun afterTextChanged(s: Editable?) {
                                    map[item] =  s.toString()// Update the corresponding user input in the list
                                    Log.d(TAG,"Amounts: ${map}")
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

                    ref = 1
                    true
                }
                else -> false
            }
        }

        // Attempt to force showing icons next to items, if applicable
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popupMenu.show()
    }




}
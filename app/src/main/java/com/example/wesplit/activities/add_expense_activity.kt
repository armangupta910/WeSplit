package com.example.wesplit.activities

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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.PopupWindow
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
import java.util.concurrent.CountDownLatch

class add_expense_activity : AppCompatActivity() {
    private var selectedMenuItemId: Int? = null
    private var friendsList: MutableList<String> = mutableListOf()
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private var friendsAdded:MutableList<String> = mutableListOf()
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
        val friendsNames = mutableListOf<String>()
        val hashi:HashMap<String,String> = hashMapOf() // UID to Name

        // Fetch the current user's friends list
        db.collection("Users").document(userId).get().addOnSuccessListener { document ->
            val friendsList = document.get("friends") as? List<String> ?: return@addOnSuccessListener

            // Track completion of all friend queries
            val countDownLatch = CountDownLatch(friendsList.size)

            // For each friend UID, fetch their user document
            friendsList.forEach { friendUid ->
                db.collection("Users").document(friendUid).get().addOnSuccessListener { friendDoc ->
                    val friendName = friendDoc.getString("name")

                    // Check if the friend's name matches the query and add it to the list
                    friendName?.let {
                        if (it.contains(queryText, ignoreCase = true)) { // Basic partial and case-insensitive matching
                            friendsNames.add(it)
                            hashi[friendDoc.id.toString()] = it
                        }
                    }

                    countDownLatch.countDown()
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
//                        val listView = LayoutInflater.from(this).inflate(R.layout.popup_list_layout, null) as ListView
//                        var items:MutableList<String> = mutableListOf() // Example items
//                        for (i in friendsAdded){
//                            hashi[i]?.let { items.add(it) }
//                        }
//                        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
//                        listView.adapter = adapter
//
//                        // Initialize the PopupWindow
//                        val popupWindow = PopupWindow(listView, view.width, WindowManager.LayoutParams.WRAP_CONTENT, true)
//
//                        // Handle list item clicks
//                        listView.setOnItemClickListener { _, _, position, _ ->
//                            val selectedItem = hashi[items[position]]
//                            Toast.makeText(this, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
//                            popupWindow.dismiss() // Dismiss the popup after an item is clicked
//                        }
//
//                        // Show the PopupWindow below the TextView
//                        popupWindow.showAsDropDown(view)
                    }





//                    var demoref = 0
//                    for(item in friendsAdded){
//                        if(item == reqUID){
//                            demoref = 1
//                            break
//                        }
//                    }
//                    if(demoref == 0){
//                        friendsAdded.add(reqUID)
//                        Toast.makeText(this, "${friendsAdded.size} clicked!", Toast.LENGTH_SHORT).show()
//                    }
//                    else{
//                        Toast.makeText(this,"Already Added",Toast.LENGTH_SHORT).show()
//                    }

                }

            }

            // Wait for all queries to complete
            Thread {
                try {
                    countDownLatch.await()
                    // This will run on a background thread, switch back to the UI thread to update UI components
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
                findViewById<TextView>(R.id.paidby).setText("you")
            }
            else{
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

    var ref = 0
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
                        var hashmap:HashMap<String,String> = hashMapOf()
                        var map:HashMap<String,String> = hashMapOf()
                        for(i in friendsAdded){
                            for(j in it){
                                if(j.id.toString() == i){
                                    name.add(j.data.get("name").toString())
                                    hashmap[i]=j.data.get("name").toString()
                                }
                            }
                        }
                        val items = name
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
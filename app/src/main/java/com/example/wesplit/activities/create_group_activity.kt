package com.example.wesplit.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class create_group_activity : AppCompatActivity() {

    // Dynamic list to store UIDs of selected friends
    private val selectedFriendsUids = mutableListOf<String>()

    private lateinit var friendsAutoCompleteTextView: AutoCompleteTextView
    private lateinit var selectedFriendsChipGroup: ChipGroup

    // Map to store friend's name as key and UID as value
    private val friendsNameToUidMap = mutableMapOf<String, String>()

    var type = "Home"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        friendsAutoCompleteTextView = findViewById(R.id.friendsAutoCompleteTextView)
        selectedFriendsChipGroup = findViewById(R.id.selectedFriendsChipGroup)

        ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green1))

        fetchFriendsAndSetupAutoComplete()

        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        val homeChip = findViewById<Chip>(R.id.home)
        val tripChip = findViewById<Chip>(R.id.trip)
        val coupleChip = findViewById<Chip>(R.id.couple)
        val othersChip = findViewById<Chip>(R.id.other)
        homeChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.creme))
        tripChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.creme))
        coupleChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.creme))
        othersChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.creme))

        // Set initial state
        homeChip.isChecked = true


        homeChip.setOnClickListener {
            homeChip.isChecked = true
            tripChip.isChecked = false
            coupleChip.isChecked = false
            othersChip.isChecked = false
            type = "Home"
            Log.d(TAG,"Type: ${type}")
        }

        tripChip.setOnClickListener {
            homeChip.isChecked = false
            tripChip.isChecked = true
            coupleChip.isChecked = false
            othersChip.isChecked = false
            type = "Trip"
            Log.d(TAG,"Type: ${type}")
        }

        coupleChip.setOnClickListener {
            homeChip.isChecked = false
            tripChip.isChecked = false
            coupleChip.isChecked = true
            othersChip.isChecked = false
            type = "Couple"
            Log.d(TAG,"Type: ${type}")
        }

        othersChip.setOnClickListener {
            homeChip.isChecked = false
            tripChip.isChecked = false
            coupleChip.isChecked = false
            othersChip.isChecked = true
            type = "Others"
            Log.d(TAG,"Type: ${type}")
        }

        findViewById<ImageButton>(R.id.backAddExpenseFragment).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.checkAddGroup).setOnClickListener {
            Log.d(TAG,"Type: ${type} Friends: ${selectedFriendsUids}")
            selectedFriendsUids.add(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val documentName = generateRandomKey()
            val group = hashMapOf(
                "Name" to findViewById<EditText>(R.id.groupName).text.toString(),
                "Type" to type,
                "Participants" to selectedFriendsUids
            )
            FirebaseFirestore.getInstance().collection("Groups").document(documentName).set(group).addOnSuccessListener {
                Toast.makeText(this,"Group added",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            for(i in selectedFriendsUids){
                FirebaseFirestore.getInstance().collection("Users").document(i).get().addOnSuccessListener {
                    val groups:MutableList<String> = it.data?.get("groups") as MutableList<String>
                    groups.add(documentName)
                    FirebaseFirestore.getInstance().collection("Users").document(i).update("groups",groups).addOnSuccessListener {
                        Toast.makeText(this,"Group added each",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    fun generateRandomKey(): String {
        return UUID.randomUUID().toString()
    }

    private fun fetchFriendsAndSetupAutoComplete() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString() // Get the current user's UID
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(uid).get().addOnSuccessListener { document ->
            val friendsUids = document["friends"] as List<String>? ?: listOf()

            for (friendUid in friendsUids) {
                db.collection("Users").document(friendUid).get().addOnSuccessListener { friendDoc ->
                    val friendName = friendDoc["name"] as String? ?: ""
                    friendsNameToUidMap[friendName] = friendUid

                    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, friendsNameToUidMap.keys.toList())
                    friendsAutoCompleteTextView.setAdapter(adapter)
                }
            }
        }

        setupAutoCompleteTextView()
    }

    private fun setupAutoCompleteTextView() {
        friendsAutoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->

            friendsAutoCompleteTextView.setText("")
            val selectedName = adapterView.getItemAtPosition(position) as String
            val selectedUid = friendsNameToUidMap[selectedName] ?: ""

            if (!selectedFriendsUids.contains(selectedUid)) {
                selectedFriendsUids.add(selectedUid)
                Log.d("SelectedFriends", "Added friend UID: $selectedUid")
                Log.d(TAG,"${selectedFriendsUids}")
                addChipForFriend(selectedName, selectedUid)
            }
        }
    }

    private fun addChipForFriend(name: String, uid: String) {
        val chip = Chip(this).apply {
            text = name
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedFriendsChipGroup.removeView(this)
                selectedFriendsUids.remove(uid)
                Log.d("SelectedFriends", "Removed friend UID: $uid")
                Log.d(TAG,"${selectedFriendsUids}")
            }
        }
        selectedFriendsChipGroup.addView(chip)
    }

}
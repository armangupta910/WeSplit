package com.example.wesplit.activities

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
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
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class create_group_activity : AppCompatActivity() {

    // Dynamic list to store UIDs of selected friends
    private val selectedFriendsUids = mutableListOf<String>()

    private lateinit var friendsAutoCompleteTextView: AutoCompleteTextView
    private lateinit var selectedFriendsChipGroup: ChipGroup

    // Map to store friend's name as key and UID as value
    private val friendsNameToUidMap = mutableMapOf<String, String>()

    var type = "Home"


    private lateinit var groupNameEditText: EditText
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

//    val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)


        groupNameEditText = findViewById(R.id.groupName)

        groupNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove any previously posted runnables to reset the delay
                runnable?.let { handler.removeCallbacks(it) }

                runnable = Runnable {
                    // This block of code will run 1 second after the user stops typing
                    s?.let {
                        val name:String = it.toString()
                        findViewById<CircleImageView>(R.id.groupImage).setImageDrawable(createLetterDrawable(this@create_group_activity,name))
                    }
                }.also {
                    handler.postDelayed(it, 200) // 1000ms = 1 second delay
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })

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
//            it.startAnimation(scaleAnimation)
            homeChip.isChecked = true
            tripChip.isChecked = false
            coupleChip.isChecked = false
            othersChip.isChecked = false
            type = "Home"
            Log.d(TAG,"Type: ${type}")
        }

        tripChip.setOnClickListener {
//            it.startAnimation(scaleAnimation)
            homeChip.isChecked = false
            tripChip.isChecked = true
            coupleChip.isChecked = false
            othersChip.isChecked = false
            type = "Trip"
            Log.d(TAG,"Type: ${type}")
        }

        coupleChip.setOnClickListener {
//            it.startAnimation(scaleAnimation)
            homeChip.isChecked = false
            tripChip.isChecked = false
            coupleChip.isChecked = true
            othersChip.isChecked = false
            type = "Couple"
            Log.d(TAG,"Type: ${type}")
        }

        othersChip.setOnClickListener {
//            it.startAnimation(scaleAnimation)
            homeChip.isChecked = false
            tripChip.isChecked = false
            coupleChip.isChecked = false
            othersChip.isChecked = true
            type = "Others"
            Log.d(TAG,"Type: ${type}")
        }

        findViewById<ImageButton>(R.id.backAddExpenseFragment).setOnClickListener {
//            it.startAnimation(scaleAnimation)
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
            finish()
        }



        findViewById<ImageButton>(R.id.checkAddGroup).setOnClickListener {
//            it.startAnimation(scaleAnimation)
            Log.d(TAG,"Type: ${type} Friends: ${selectedFriendsUids}")
            selectedFriendsUids.add(FirebaseAuth.getInstance().currentUser?.uid.toString())

            val loans:HashMap<String,String> = hashMapOf()
            for(i in selectedFriendsUids){
                loans[i] = "0"
            }

            val documentName = generateRandomKey()
            val group = hashMapOf(
                "Name" to findViewById<EditText>(R.id.groupName).text.toString(),
                "Type" to type,
                "Participants" to selectedFriendsUids,
                "Expenses" to hashMapOf<String,MutableList<HashMap<String,String>>>(),
                "Loans" to loans
            )
            FirebaseFirestore.getInstance().collection("Groups").document(documentName).set(group).addOnSuccessListener {
                Toast.makeText(this,"Group added",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
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
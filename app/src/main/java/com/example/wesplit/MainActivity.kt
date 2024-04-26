//package com.example.wesplit
//
//import android.content.Context
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.FragmentContainerView
//import com.example.wesplit.activities.add_expense_activity
//import com.example.wesplit.activities.create_group_activity
//import com.example.wesplit.activities.sign_in_activity
//import com.example.wesplit.fragments.accountFragment
//import com.example.wesplit.fragments.activityFragment
//import com.example.wesplit.fragments.friendsFragment
//import com.example.wesplit.fragments.groupsFragment
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//
//    val groups = findViewById<LinearLayout>(R.id.navGroups)
//    val friends = findViewById<LinearLayout>(R.id.navFriends)
//    val activity = findViewById<LinearLayout>(R.id.navActivity)
//    val account = findViewById<LinearLayout>(R.id.navAccount)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val token=getSharedPreferences("data", Context.MODE_PRIVATE)
//        val editor=token.edit()
//        editor.putString("data","wesplitisactivenow")
//        editor.commit()
//
//        val fragment:FragmentContainerView = findViewById(R.id.fragment)
//        supportFragmentManager.beginTransaction().add(R.id.fragment,groupsFragment()).commit()
//
//        val sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            putString("LastSelectedScreen", "groups")
//            apply()
//        }
//
//
//
//        groups.setOnClickListener {
//            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.VISIBLE
//            supportFragmentManager.beginTransaction().replace(R.id.fragment,groupsFragment()).commit()
//            with(sharedPref.edit()) {
//                putString("LastSelectedScreen", "groups")
//                apply()
//            }
//        }
//        friends.setOnClickListener {
//            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//            supportFragmentManager.beginTransaction().replace(R.id.fragment,friendsFragment()).commit()
//            with(sharedPref.edit()) {
//                putString("LastSelectedScreen", "friends")
//                apply()
//            }
//        }
//        activity.setOnClickListener {
//            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//            supportFragmentManager.beginTransaction().replace(R.id.fragment,activityFragment()).commit()
//            with(sharedPref.edit()) {
//                putString("LastSelectedScreen", "activity")
//                apply()
//            }
//        }
//        account.setOnClickListener {
//            account.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//            findViewById<Button>(R.id.signout).visibility = View.GONE
//            supportFragmentManager.beginTransaction().replace(R.id.fragment,accountFragment()).commit()
//            with(sharedPref.edit()) {
//                putString("LastSelectedScreen", "account")
//                apply()
//            }
//        }
//
//        val addExpense = findViewById<Button>(R.id.addExpense)
//        addExpense.setOnClickListener {
//            startActivity(Intent(this,add_expense_activity::class.java))
//        }
//
//        val createGroup = findViewById<Button>(R.id.createNewGroup).setOnClickListener {
//            startActivity(Intent(this,create_group_activity::class.java))
//        }
//
//        val button:Button = findViewById(R.id.signout)
//        button.setOnClickListener {
//            signout()
//        }
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
//        val lastSelectedScreen = sharedPref.getString("LastSelectedScreen", "groups")
//
//        // Restore the state or select the fragment based on lastSelectedScreen
//        when(lastSelectedScreen) {
//            "activity" -> {
//                // Code to display the corresponding fragment or highlight the custom Bottom Navigation item
//                activity.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//                account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//                supportFragmentManager.beginTransaction().replace(R.id.fragment,activityFragment()).commit()
//            }
//            // Handle other cases
//            "groups" -> {
//                // Code for default selection
//                groups.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//                activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                findViewById<LinearLayout>(R.id.specialButtons).visibility = View.VISIBLE
//                supportFragmentManager.beginTransaction().replace(R.id.fragment,groupsFragment()).commit()
//
//            }
//            "account" -> {
//                account.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//                activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//                findViewById<Button>(R.id.signout).visibility = View.GONE
//                supportFragmentManager.beginTransaction().replace(R.id.fragment,accountFragment()).commit()
//            }
//            "friends" -> {
//                friends.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
//                activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
//                findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
//                supportFragmentManager.beginTransaction().replace(R.id.fragment,friendsFragment()).commit()
//            }
//        }
//    }
//    private fun signout(){
//        val shared = getSharedPreferences("data",Context.MODE_PRIVATE)
//        val edit = shared.edit()
//        edit.putString("data","")
//        edit.apply()
//        FirebaseAuth.getInstance().signOut()
//        startActivity(Intent(this,sign_in_activity::class.java))
//        finish()
//    }
//}

package com.example.wesplit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.wesplit.activities.add_expense_activity
import com.example.wesplit.activities.add_friend_activity
import com.example.wesplit.activities.create_group_activity
import com.example.wesplit.activities.notifications
import com.example.wesplit.activities.sign_in_activity
import com.example.wesplit.fragments.accountFragment
import com.example.wesplit.fragments.activityFragment
import com.example.wesplit.fragments.friendsFragment
import com.example.wesplit.fragments.groupsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.getInstance
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    fun dpToPx(dp: Int, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun startScanner() {
        // Show an options dialog or bottom sheet here to let the user choose between camera or gallery
        val options = arrayOf("Scan using Camera", "Select from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select QR Code Source")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> startCameraScanner() // User chose to scan using camera
                    1 -> pickImageFromGallery() // User chose to select from gallery
                }
            }.show()
    }

    private fun startCameraScanner() {
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val integrator = IntentIntegrator(this).apply {
            setPrompt("Scan a QR code")
            setOrientationLocked(false)
            setBeepEnabled(true)
        }
        integrator.initiateScan()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, add_friend_activity.GALLERY_REQUEST_CODE)
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 1234 // Define a request code for gallery intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == add_friend_activity.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle gallery selection
            data?.data?.let { uri ->
                // Assuming you have a function to handle the scanning from a URI
                scanQRCodeFromUri(uri, this)
            }
        } else {
            // Handle camera scanner result
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show()

            val qrContent = result.contents.toString()
            Toast.makeText(this,"Group ID is :- " + qrContent,Toast.LENGTH_SHORT).show()

            FirebaseFirestore.getInstance().collection("Groups").document(qrContent).get().addOnSuccessListener {
                val participants:MutableList<String> = it.data?.get("Participants") as MutableList<String>
                if(FirebaseAuth.getInstance().currentUser?.uid.toString() in participants){
                    Toast.makeText(this,"You're already in the Group!",Toast.LENGTH_SHORT).show()
                }
                else{

                    for(i in participants){
                        FirebaseFirestore.getInstance().collection("Users").document(i).get().addOnSuccessListener {it1->
                            var friends:MutableList<String> = it1.data?.get("friends") as MutableList<String>
                            if(FirebaseAuth.getInstance().currentUser?.uid.toString() in friends){

                            }
                            else{
                                friends.add(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                val loans:HashMap<String,String> = it1.data?.get("Loans") as HashMap<String, String>
                                loans[FirebaseAuth.getInstance().currentUser?.uid.toString()] = "0"
                                FirebaseFirestore.getInstance().collection("Users").document(i).update("friends",friends)
                                FirebaseFirestore.getInstance().collection("Users").document(i).update("Loans",loans)

                                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {it2->
                                    var friendscurr = it2.data?.get("friends") as MutableList<String>
                                    friendscurr.add(i)

                                    val loanscurr = it2.data?.get("Loans") as HashMap<String,String>
                                    loanscurr[i] = "0"

                                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("friends",friendscurr)
                                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("Loans",loanscurr)
                                }
                            }
                        }
                    }

                    participants.add(FirebaseAuth.getInstance().currentUser?.uid.toString());
                    val loans:HashMap<String,String> = it.data?.get("Loans") as HashMap<String, String>
                    loans[FirebaseAuth.getInstance().currentUser?.uid.toString()] = "0"

                    FirebaseFirestore.getInstance().collection("Groups").document(qrContent).update("Participants",participants)
                    FirebaseFirestore.getInstance().collection("Groups").document(qrContent).update("Loans",loans)

                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {it4->
                        val groups:MutableList<String> = it4.data?.get("groups") as MutableList<String>
                        groups.add(qrContent)
                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("groups",groups).addOnSuccessListener {
                            Toast.makeText(this,"You've been added to the group!",Toast.LENGTH_SHORT).show()
                            recreate()
                        }
                    }

                }
            }


            // Handle the scanned QR code string
        }
    }

    private fun scanQRCodeFromUri(uri: Uri, context: Context) {
        try {
            // Convert URI to Bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                bitmap.recycle()

                // Create a binary bitmap source from the bitmap
                val source = RGBLuminanceSource(width, height, pixels)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                // Try to decode the QR code
                val result = MultiFormatReader().decode(binaryBitmap)
                if (result != null) {
                    // If QR code is decoded successfully, use the result text
                    val qrContent = result.text
                    Toast.makeText(this,qrContent,Toast.LENGTH_SHORT).show()

                    FirebaseFirestore.getInstance().collection("Groups").document(qrContent).get().addOnSuccessListener {
                        val participants:MutableList<String> = it.data?.get("Participants") as MutableList<String>
                        if(FirebaseAuth.getInstance().currentUser?.uid.toString() in participants){
                            Toast.makeText(this,"You're already in the Group!",Toast.LENGTH_SHORT).show()
                        }
                        else{

                            for(i in participants){
                                FirebaseFirestore.getInstance().collection("Users").document(i).get().addOnSuccessListener {it1->
                                    var friends:MutableList<String> = it1.data?.get("friends") as MutableList<String>
                                    if(FirebaseAuth.getInstance().currentUser?.uid.toString() in friends){

                                    }
                                    else{
                                        friends.add(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                        val loans:HashMap<String,String> = it1.data?.get("Loans") as HashMap<String, String>
                                        loans[FirebaseAuth.getInstance().currentUser?.uid.toString()] = "0"
                                        FirebaseFirestore.getInstance().collection("Users").document(i).update("friends",friends)
                                        FirebaseFirestore.getInstance().collection("Users").document(i).update("Loans",loans)

                                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {it2->
                                            var friendscurr = it2.data?.get("friends") as MutableList<String>
                                            friendscurr.add(i)

                                            val loanscurr = it2.data?.get("Loans") as HashMap<String,String>
                                            loanscurr[i] = "0"

                                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("friends",friendscurr)
                                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("Loans",loanscurr)
                                        }
                                    }
                                }
                            }

                            participants.add(FirebaseAuth.getInstance().currentUser?.uid.toString());
                            val loans:HashMap<String,String> = it.data?.get("Loans") as HashMap<String, String>
                            loans[FirebaseAuth.getInstance().currentUser?.uid.toString()] = "0"

                            FirebaseFirestore.getInstance().collection("Groups").document(result.toString()).update("Participants",participants)
                            FirebaseFirestore.getInstance().collection("Groups").document(result.toString()).update("Loans",loans)

                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {it4->
                                val groups:MutableList<String> = it4.data?.get("groups") as MutableList<String>
                                groups.add(qrContent)
                                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("groups",groups).addOnSuccessListener {
                                    Toast.makeText(this,"You've been added to the group!",Toast.LENGTH_SHORT).show()
                                    recreate()

                                }
                            }

                        }
                    }



                }
            }
        }catch (e: Exception) {
            // Handle exceptions
            runOnUiThread {
                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.notification).setOnClickListener {
            startActivity(Intent(this, notifications::class.java))
        }

        findViewById<ImageView>(R.id.qr).setOnClickListener {
            startScanner()
        }

        val slideOutView = findViewById<LinearLayout>(R.id.specialButtons)
        val triggerView = findViewById<CardView>(R.id.arrowButton)
        val visibleWidthPx = dpToPx(80, this)

        slideOutView.post {
            val viewWidthPx = slideOutView.width.toFloat()
            val initialTranslation = viewWidthPx - visibleWidthPx
            slideOutView.translationX = initialTranslation

            var isPanelShown = false

            triggerView.setOnClickListener {
                if (isPanelShown) {
                    // Slide in (hide)
                    slideOutView.animate()
                        .translationX(initialTranslation)
                        .setDuration(300)
                        .start()
                } else {
                    // Slide out (show)
                    slideOutView.animate()
                        .translationX(0f)
                        .setDuration(300)
                        .start()
                }
                isPanelShown = !isPanelShown
            }
        }


        val token = getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = token.edit()
        editor.putString("data", "wesplitisactivenow")
        editor.commit()

        val fragment: FragmentContainerView = findViewById(R.id.fragment)
        supportFragmentManager.beginTransaction().add(R.id.fragment, groupsFragment()).commit()

        val groups = findViewById<LinearLayout>(R.id.navGroups)
        val friends = findViewById<LinearLayout>(R.id.navFriends)
        val activity = findViewById<LinearLayout>(R.id.navActivity)
        val account = findViewById<LinearLayout>(R.id.navAccount)

        val groupsImage: ImageView = findViewById(R.id.groupsimage)
        val friendsimage: ImageView = findViewById(R.id.friendsimage)
        val activityimage: ImageView = findViewById(R.id.activityimage)
        val accountimage: ImageView = findViewById(R.id.accountimage)

        val groupstext: TextView = findViewById(R.id.groupstext)
        val friendstext: TextView = findViewById(R.id.friendstext)
        val activitytext: TextView = findViewById(R.id.activitytext)
        val accounttext: TextView = findViewById(R.id.accounttext)



        findViewById<ImageView>(R.id.share).setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)
            findViewById<ImageView>(R.id.share).startAnimation(scaleAnimation)
            val message:String = "\uD83C\uDF1F Exciting News! \uD83C\uDF1F I found an app that makes splitting expenses super easy for friends & groups - no more headaches over who owes what! Let’s give it a try for our next outing. Download it and let me know what you think! \uD83D\uDCF1\uD83D\uDCB8"
//            val sendIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, message)
//                type = "text/plain"
//                // Ensure the package is targeted to WhatsApp
//                setPackage("com.whatsapp")
//            }
//
//// Attempt to launch WhatsApp
//            startActivity(sendIntent)

            // Example usage
            val context = this // Or any valid Context reference
            val imageResourceId = R.drawable.logo // Replace with your actual image resource ID
            val shareText = "Check out this cool image!" // The text you want to share

            val imageUri = copyResourceToFile(context, imageResourceId, "logo.png")
            imageUri?.let {
                shareImageWithText(it, message, context)
            }


        }

        groups.setOnClickListener {
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))

            groupstext.setTextColor(ContextCompat.getColor(this, R.color.green1))
            friendstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            activitytext.setTextColor(ContextCompat.getColor(this, R.color.white))
            accounttext.setTextColor(ContextCompat.getColor(this, R.color.white))


            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,groupsFragment()).commit()
        }
        friends.setOnClickListener {
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))

            groupstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            friendstext.setTextColor(ContextCompat.getColor(this, R.color.green1))
            activitytext.setTextColor(ContextCompat.getColor(this, R.color.white))
            accounttext.setTextColor(ContextCompat.getColor(this, R.color.white))


            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,friendsFragment()).commit()
        }
        activity.setOnClickListener {
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))

            groupstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            friendstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            activitytext.setTextColor(ContextCompat.getColor(this, R.color.green1))
            accounttext.setTextColor(ContextCompat.getColor(this, R.color.white))


            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,activityFragment()).commit()
        }
        account.setOnClickListener {
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.green1))

            groupstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            friendstext.setTextColor(ContextCompat.getColor(this, R.color.white))
            activitytext.setTextColor(ContextCompat.getColor(this, R.color.white))
            accounttext.setTextColor(ContextCompat.getColor(this, R.color.green1))


            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            findViewById<Button>(R.id.signout).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,accountFragment()).commit()
        }

        val addExpense = findViewById<Button>(R.id.addExpense)
        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)
        addExpense.setOnClickListener {
            findViewById<Button>(R.id.addExpense).startAnimation(scaleAnimation)
            startActivity(Intent(this,add_expense_activity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)

        }

        val createGroup = findViewById<Button>(R.id.createNewGroup).setOnClickListener {
            findViewById<Button>(R.id.createNewGroup).startAnimation(scaleAnimation)
            startActivity(Intent(this,create_group_activity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        }

        val button:Button = findViewById(R.id.signout)
        button.setOnClickListener {
            signout()
        }
    }

    fun copyResourceToFile(context: Context, resourceId: Int, fileName: String): Uri? {
        val file = File(context.externalCacheDir, fileName)
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            // Return a content URI for the file using FileProvider
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun shareImageWithText(uri: Uri, text: String, context: Context) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri) // Image URI
            putExtra(Intent.EXTRA_TEXT, text) // Text to share
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Application invite"))
    }




    private fun signout(){
        val shared = getSharedPreferences("data",Context.MODE_PRIVATE)
        val edit = shared.edit()
        edit.putString("data","")
        edit.apply()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,sign_in_activity::class.java))
        overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        finish()
    }
}
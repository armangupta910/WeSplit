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

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.example.wesplit.activities.add_expense_activity
import com.example.wesplit.activities.create_group_activity
import com.example.wesplit.activities.sign_in_activity
import com.example.wesplit.fragments.accountFragment
import com.example.wesplit.fragments.activityFragment
import com.example.wesplit.fragments.friendsFragment
import com.example.wesplit.fragments.groupsFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token=getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor=token.edit()
        editor.putString("data","wesplitisactivenow")
        editor.commit()

        val fragment:FragmentContainerView = findViewById(R.id.fragment)
        supportFragmentManager.beginTransaction().add(R.id.fragment,groupsFragment()).commit()

        val groups = findViewById<LinearLayout>(R.id.navGroups)
        val friends = findViewById<LinearLayout>(R.id.navFriends)
        val activity = findViewById<LinearLayout>(R.id.navActivity)
        val account = findViewById<LinearLayout>(R.id.navAccount)

        val groupsImage:ImageView = findViewById(R.id.groupsimage)
        val friendsimage:ImageView = findViewById(R.id.friendsimage)
        val activityimage:ImageView = findViewById(R.id.activityimage)
        val accountimage:ImageView = findViewById(R.id.accountimage)

        val groupstext:TextView = findViewById(R.id.groupstext)
        val friendstext:TextView = findViewById(R.id.friendstext)
        val activitytext:TextView = findViewById(R.id.activitytext)
        val accounttext:TextView = findViewById(R.id.accounttext)

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
        addExpense.setOnClickListener {
            startActivity(Intent(this,add_expense_activity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)

        }

        val createGroup = findViewById<Button>(R.id.createNewGroup).setOnClickListener {
            startActivity(Intent(this,create_group_activity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        }

        val button:Button = findViewById(R.id.signout)
        button.setOnClickListener {
            signout()
        }
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
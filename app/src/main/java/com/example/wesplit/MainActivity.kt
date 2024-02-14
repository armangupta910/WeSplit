package com.example.wesplit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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

        groups.setOnClickListener {
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,groupsFragment()).commit()
        }
        friends.setOnClickListener {
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,friendsFragment()).commit()
        }
        activity.setOnClickListener {
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,activityFragment()).commit()
        }
        account.setOnClickListener {
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            findViewById<LinearLayout>(R.id.specialButtons).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.fragment,accountFragment()).commit()
        }

        val addExpense = findViewById<Button>(R.id.addExpense)
        addExpense.setOnClickListener {
            startActivity(Intent(this,add_expense_activity::class.java))
        }

        val createGroup = findViewById<Button>(R.id.createNewGroup).setOnClickListener {
            startActivity(Intent(this,create_group_activity::class.java))
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
        finish()
    }
}
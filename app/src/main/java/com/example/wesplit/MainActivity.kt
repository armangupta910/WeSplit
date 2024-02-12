package com.example.wesplit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.example.wesplit.activities.add_expense_activity
import com.example.wesplit.fragments.accountFragment
import com.example.wesplit.fragments.activityFragment
import com.example.wesplit.fragments.friendsFragment
import com.example.wesplit.fragments.groupsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment,groupsFragment()).commit()
        }
        friends.setOnClickListener {
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            supportFragmentManager.beginTransaction().replace(R.id.fragment,friendsFragment()).commit()
        }
        activity.setOnClickListener {
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            supportFragmentManager.beginTransaction().replace(R.id.fragment,activityFragment()).commit()
        }
        account.setOnClickListener {
            account.setBackgroundColor(ContextCompat.getColor(this,R.color.creme))
            activity.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            friends.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            groups.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            supportFragmentManager.beginTransaction().replace(R.id.fragment,accountFragment()).commit()
        }

        val addExpense = findViewById<Button>(R.id.addExpense)
        addExpense.setOnClickListener {
            startActivity(Intent(this,add_expense_activity::class.java))
        }
    }
}
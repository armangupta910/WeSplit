package com.example.wesplit.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.wesplit.R
import com.example.wesplit.activities.add_friend_activity
import com.example.wesplit.activities.sign_in_activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class friendsFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_friends, container, false)

        val imagebutt = frag.findViewById<ImageButton>(R.id.addFriend)
        imagebutt.setOnClickListener {
            startActivity(Intent(activity,add_friend_activity::class.java))
        }

        return frag
    }


}
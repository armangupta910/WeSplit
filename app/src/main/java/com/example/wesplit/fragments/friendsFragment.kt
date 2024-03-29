package com.example.wesplit.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.activities.add_friend_activity
import com.example.wesplit.activities.friend_expense_details_activity
import com.example.wesplit.activities.sign_in_activity
import com.example.wesplit.dropdown
import com.example.wesplit.recyclerviews.adaptorForGroupsFragment
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class friendsFragment() : Fragment() {

    private lateinit var autoCompleteFriends: AutoCompleteTextView
    private val friendsNamesToUIDs = HashMap<String, String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_friends, container, false)

        val imagebutt = frag.findViewById<Button>(R.id.addFriendFloatingButton)
        imagebutt.setOnClickListener {
            startActivity(Intent(activity,add_friend_activity::class.java))
        }

        autoCompleteFriends = frag.findViewById<AutoCompleteTextView>(R.id.autocompleteFriends)

        fetchFriendsAndSetupAutocomplete()

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())

        // Variable to hold the friends list
        var friendsList: MutableList<String> = mutableListOf()

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Extract the friends array from the document
                val friends = document.get("friends") as? MutableList<String>
                if (friends != null) {
                    // Store the friends list in the variable
                    friendsList = friends
                    val x = frag.findViewById<RecyclerView>(R.id.recyclerFriends)

                    val y = context?.let { adaptorforfriendslist(it,friendsList) }
                    x.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.VERTICAL,false)
                    x.adapter = y

                    // Do something with the friends list here, such as updating the UI
                } else {
                    // Handle the case where the friends field is missing or not a list
                }
            } else {
                // Handle the case where the document does not exist
            }
            Log.d(TAG,"${friendsList}")
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred during the fetch
        }

        return frag
    }

    private fun fetchFriendsAndSetupAutocomplete() {
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid.toString() // Replace with actual current user UID
        FirebaseFirestore.getInstance().collection("Users").document(currentUserUID)
            .get().addOnSuccessListener { document ->
                val friendsUIDs = document.get("friends") as List<String>? ?: return@addOnSuccessListener

                for (friendUID in friendsUIDs) {
                    FirebaseFirestore.getInstance().collection("Users").document(friendUID)
                        .get().addOnSuccessListener { friendDoc ->
                            val friendName = friendDoc.getString("name") ?: return@addOnSuccessListener
                            friendsNamesToUIDs[friendName] = friendUID

                            // Update autocomplete list
                            val adapter = dropdown(
                                requireContext(),
                                ArrayList(friendsNamesToUIDs.keys)
                            )
                            autoCompleteFriends.setAdapter(adapter)

                            autoCompleteFriends.setOnItemClickListener { parent, view, position, id ->
                                val selectedName = parent.adapter.getItem(position) as String
                                val selectedUID = friendsNamesToUIDs[selectedName]
                                // Use selectedUID for further operations

                                val intent = Intent(requireContext(),
                                    friend_expense_details_activity::class.java)
                                intent.putExtra("friendUID",selectedUID)

                                autoCompleteFriends.setText("")

                                startActivity(intent)
                            }

                        }
                }
            }
    }


}
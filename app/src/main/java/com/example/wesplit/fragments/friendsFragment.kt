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
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
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
    private lateinit var x:RecyclerView
    private lateinit var y:adaptorforfriendslist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_friends, container, false)
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(),R.anim.button_scale)
        val imagebutt = frag.findViewById<Button>(R.id.addFriendFloatingButton)
        imagebutt.setOnClickListener {
            imagebutt.startAnimation(scaleAnimation)
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
                if(friends?.size == 0){
                    frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                    frag.findViewById<LottieAnimationView>(R.id.empty).visibility = View.VISIBLE
                }
                if (friends != null) {
                    // Store the friends list in the variable
                    friendsList = friends
                    x = frag.findViewById<RecyclerView>(R.id.recyclerFriends)

                    y = context?.let { adaptorforfriendslist(it,friendsList) }!!
                    x.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.VERTICAL,false)
                    x.adapter = y

                    frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
                    frag.findViewById<RecyclerView>(R.id.recyclerFriends).visibility = View.VISIBLE

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

        frag.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).setOnRefreshListener {
            friendsList.clear()

            frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.VISIBLE
            frag.findViewById<RecyclerView>(R.id.recyclerFriends).visibility  =View.GONE

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Extract the friends array from the document
                    val friends = document.get("friends") as? MutableList<String>
                    if(friends?.size == 0){
                        frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility = View.VISIBLE
                    }
                    if (friends != null) {
                        // Store the friends list in the variable
                        friendsList = friends

                        y.updateData(friendsList)

                        frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
                        frag.findViewById<RecyclerView>(R.id.recyclerFriends).visibility = View.VISIBLE

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

            frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
            frag.findViewById<RecyclerView>(R.id.recyclerFriends).visibility  =View.VISIBLE
            frag.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).isRefreshing = false
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
package com.example.wesplit.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.example.wesplit.activities.friend_expense_details_activity
import com.example.wesplit.activities.groupDetailsActivity
import com.example.wesplit.dropdown
import com.example.wesplit.recyclerviews.adaptorForGroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class groupsFragment : Fragment() {
    private var selectedMenuItemId: Int? = null

    private lateinit var autoCompleteGroups: AutoCompleteTextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag = inflater.inflate(R.layout.fragment_groups, container, false)

        autoCompleteGroups = frag.findViewById<AutoCompleteTextView>(R.id.autoCompleteGroups)

        fetchGroupsAndSetupAutocomplete()

        val data:MutableList<HashMap<String,Any>> = mutableListOf()

        var loans:HashMap<String,String> = hashMapOf()

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            loans = it.data?.get("Loans") as HashMap<String, String>
            var amount:Int = 0
            for(i in loans){
                amount += i.value.toInt()
            }
            if(amount<0){
                frag.findViewById<TextView>(R.id.totalAmount).setText("Overall, you've borrowed ₹ " + ((-1)*amount).toString())
                frag.findViewById<TextView>(R.id.totalAmount).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            if(amount>0){
                frag.findViewById<TextView>(R.id.totalAmount).setText("Overall, you've lent ₹ " + (amount).toString())
                frag.findViewById<TextView>(R.id.totalAmount).setTextColor(ContextCompat.getColor(requireContext(), R.color.green1))
            }
            if(amount==0){
                frag.findViewById<TextView>(R.id.totalAmount).setText("Everything is settled up")
                frag.findViewById<TextView>(R.id.totalAmount).setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            frag.findViewById<TextView>(R.id.totalAmount).visibility = View.VISIBLE
            frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
        }

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            val groups:MutableList<String> = it.get("groups") as MutableList<String>
            for(i in groups){
                if(i!="") {
                    FirebaseFirestore.getInstance().collection("Groups").document(i).get()
                        .addOnSuccessListener {
                            val demodata = it.data as HashMap<String,Any>
                            demodata["key"] = i.toString()
                            data.add(demodata)

                            val x = frag.findViewById<RecyclerView>(R.id.recyclerGroups)
                            val y = adaptorForGroupsFragment(requireContext(),data)
                            x.layoutManager =
                                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                            x.adapter = y
                        }
                }
            }
        }

//        val x = frag.findViewById<RecyclerView>(R.id.recyclerGroups)
//        val y = adaptorForGroupsFragment()
//        x.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
//        x.adapter = y

        val filter = frag.findViewById<TextView>(R.id.filterFragmentGroups)
        filter.setOnClickListener { view->
            showPopupMenu(view)
        }

        return frag
    }
    private fun showPopupMenu(view: View) {
        context?.let { ctx ->
            val popupMenu = PopupMenu(ctx, view)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

            // Pre-select an item if needed (optional)
            // popupMenu.menu.findItem(R.id.action_option1).isChecked = true

            selectedMenuItemId?.let { id ->
                popupMenu.menu.findItem(id)?.isChecked = true
//                CompoundButtonCompat.setButtonTintList(id, ColorStateList.valueOf(ContextCompat.getColor(this,R.colo.you)))
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                popupMenu.menu.forEach { item ->
                    item.isChecked = false
                }
                menuItem.isChecked = true// Toggle the checked state or simply set to true

                selectedMenuItemId = menuItem.itemId

                when (menuItem.itemId) {
                    R.id.allGroups -> {
                        // Handle option 1 clicked
                        true
                    }
                    R.id.outstanding -> {
                        // Handle option 2 clicked
                        true
                    }
                    R.id.youowe -> {
                        // Handle option 3 clicked
                        true
                    }
                    R.id.oweyou -> {
                        // Handle option 3 clicked
                        true
                    }
                    else -> false
                }
            }

            // This will show icons or check marks next to the menu items if they're set
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

    private fun fetchGroupsAndSetupAutocomplete() {
        val groupsNamesToIDs = HashMap<String, String>() // This will store group names to group IDs

        FirebaseFirestore.getInstance().collection("Groups")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val groupName = document.getString("Name") ?: continue // Extract the group name
                    val groupId = document.id // Get the document ID, which is the group ID

                    groupsNamesToIDs[groupName] = groupId // Map group name to group ID
                }

                // Assuming your dropdown adapter function is ready and named `dropdown`,
                // and your AutoCompleteTextView variable is `autoCompleteGroups`.
                val adapter = dropdown(
                    requireContext(),
                    ArrayList(groupsNamesToIDs.keys)
                )
                autoCompleteGroups.setAdapter(adapter)

                autoCompleteGroups.setOnItemClickListener { parent, view, position, id ->
                    val selectedName = parent.adapter.getItem(position) as String
                    val selectedGroupId = groupsNamesToIDs[selectedName]
                    // Use selectedGroupId for further operations

                    // Assuming you're navigating to a GroupExpenseDetailsActivity and passing the selected group ID
                    val intent = Intent(requireContext(), groupDetailsActivity::class.java)
                    intent.putExtra("key", selectedGroupId)

                    autoCompleteGroups.setText("") // Clear the text after selection

                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }




}
package com.example.wesplit.fragments

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorForGroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class groupsFragment : Fragment() {
    private var selectedMenuItemId: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag = inflater.inflate(R.layout.fragment_groups, container, false)

        val x = frag.findViewById<RecyclerView>(R.id.recyclerGroups)
        val y = adaptorForGroupsFragment()
        x.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        x.adapter = y

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

}
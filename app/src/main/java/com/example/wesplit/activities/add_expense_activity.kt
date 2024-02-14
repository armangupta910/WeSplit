package com.example.wesplit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.forEach
import com.example.wesplit.MainActivity
import com.example.wesplit.R

class add_expense_activity : AppCompatActivity() {
    private var selectedMenuItemId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        findViewById<ImageButton>(R.id.backAddExpenseFragment).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.equallyorunequally).setOnClickListener {view->
            showPopupMenu(view)
        }
    }

    private fun uncheckAllMenuItems(popupMenu: PopupMenu) {
        val menu = popupMenu.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
    }

    var ref = 0


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.adding_expense_menu, popupMenu.menu)

        // Set initial check state based on stored value
        selectedMenuItemId?.let {
            popupMenu.menu.findItem(it).isChecked = true
        } ?: run {
            // Default selection if no item was previously selected
            popupMenu.menu.findItem(R.id.equally).isChecked = true
            selectedMenuItemId = R.id.equally // Update this as per your logic
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // This is not necessary as checkableBehavior="single" should handle it
            // but included for explicitness
            uncheckAllMenuItems(popupMenu)
            menuItem.isChecked = true
            selectedMenuItemId = menuItem.itemId // Update the selected item ID

            when (menuItem.itemId) {
                R.id.equally -> {
                    // Handle "Equally" option clicked
                    findViewById<TextView>(R.id.equallyorunequally).setText("Equally")
                    ref = 0
                    true
                }
                R.id.unequally -> {
                    // Handle "Unequally" option clicked
                    findViewById<TextView>(R.id.equallyorunequally).setText("Unequally")
                    ref = 1
                    true
                }
                else -> false
            }
        }

        // Attempt to force showing icons next to items, if applicable
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
package com.example.wesplit.fragments

import android.app.DatePickerDialog
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.wesplit.R
import com.example.wesplit.newDropdown
import com.example.wesplit.recyclerviews.adaptorforexpenses
import com.example.wesplit.recyclerviews.adaptorforfriendslist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class activityFragment : Fragment() {

    fun isDateMatching(dateTimeStr: String, year: Int, month: Int, dayOfMonth: Int): Boolean {
        // Parse the date-time string
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.parse(dateTimeStr)

        // Extract the year, month, and day from the date-time
        val calendar = Calendar.getInstance().apply {
            time = date ?: return false // Return false if parsing fails
        }
        val yearFromStr = calendar.get(Calendar.YEAR)
        val monthFromStr = calendar.get(Calendar.MONTH) + 1// Convert 0-based month to 1-based
        val dayFromStr = calendar.get(Calendar.DAY_OF_MONTH)

        // Compare the selected date with the extracted date
        return year == yearFromStr && month == monthFromStr && dayOfMonth == dayFromStr
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var data: MutableList<HashMap<String, MutableList<HashMap<String, String>>>> = mutableListOf()
        val frag = inflater.inflate(R.layout.fragment_activity, container, false)
        lateinit var x:RecyclerView
        lateinit var y:adaptorforexpenses
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            userDocumentRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val expensesField: HashMap<String, MutableList<HashMap<String, String>>> =
                        document.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>


                    if (expensesField.size == 0) {
                        frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility = View.VISIBLE
                    }
                    for (i in expensesField) {
                        var demo: HashMap<String, MutableList<HashMap<String, String>>> =
                            hashMapOf()
                        Log.d(TAG, "Demo: $demo")
                        demo[i.key] = i.value
                        data.add(demo)
                    }
                    // Convert and sort the entries by date-time in descending order
                    data.sortByDescending { it.keys.first() }
                    Log.d(TAG, "Data: ${data}")
                    x = frag.findViewById<RecyclerView>(R.id.recyclerActivity)
                    y = context?.let { adaptorforexpenses(it, data) }!!
                    x.layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    x.adapter = y

                    frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
                    frag.findViewById<RecyclerView>(R.id.recyclerActivity).visibility = View.VISIBLE

                    Log.d(TAG, "Data: $data")


                    val showDatePickerButton = frag.findViewById<ImageButton>(R.id.datePicker)

                    frag.findViewById<ImageButton>(R.id.clear).setOnClickListener {
                        frag.findViewById<EditText>(R.id.datetext).setText("")
                        y?.updateData(data)
                        frag.findViewById<ImageButton>(R.id.clear).visibility = View.GONE
                        if (data.size == 0) {
                            frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                            frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                View.VISIBLE
                        } else {
                            frag.findViewById<TextView>(R.id.text).visibility = View.GONE
                            frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                View.GONE
                        }
                    }

                    showDatePickerButton.setOnClickListener {
                        // Get the current date
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        // Create the DatePickerDialog
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                // The month value is 0-based. e.g., 0 for January.

                                // Use the selected year, month, and day of month here
                                val dateString =
                                    "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"

                                val demodata: MutableList<HashMap<String, MutableList<HashMap<String, String>>>> =
                                    mutableListOf()

                                for (i in data) {
                                    val dateTimeStr: String = i.keys.first().toString()
                                    if (isDateMatching(
                                            dateTimeStr,
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDayOfMonth
                                        ) == true
                                    ) {
                                        Log.d(TAG, "Matched")
                                        demodata.add(i)
                                    }
                                }

                                if (y != null) {
                                    Log.d(TAG, "New Data: $data")
                                    frag.findViewById<EditText>(R.id.datetext)
                                        .setText("Looking for $dateString")
                                    y.updateData(demodata)
                                    if (demodata.size == 0) {
                                        frag.findViewById<TextView>(R.id.text).visibility =
                                            View.VISIBLE
                                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                            View.VISIBLE
                                    } else {
                                        frag.findViewById<TextView>(R.id.text).visibility =
                                            View.GONE
                                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                            View.GONE
                                    }
                                    frag.findViewById<ImageButton>(R.id.clear).visibility =
                                        View.VISIBLE
                                }



                                Log.d(TAG, "Demo Data: $demodata")
                                // Do something with the selected date, e.g., display it or use it in your app

                            },
                            year,
                            month,
                            day
                        )

                        // Show the DatePickerDialog
                        datePickerDialog.show()
                    }


                } else {
                    println("Document does not exist")
                }
            }.addOnFailureListener { exception ->
                println("Error fetching document: $exception")
            }

        frag.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).setOnRefreshListener {
            data.clear()
            frag.findViewById<RecyclerView>(R.id.recyclerActivity).visibility = View.GONE
            frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.VISIBLE


            userDocumentRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val expensesField: HashMap<String, MutableList<HashMap<String, String>>> =
                        document.get("Expenses") as HashMap<String, MutableList<HashMap<String, String>>>


                    if (expensesField.size == 0) {
                        frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility = View.VISIBLE
                    }
                    for (i in expensesField) {
                        var demo: HashMap<String, MutableList<HashMap<String, String>>> =
                            hashMapOf()
                        Log.d(TAG, "Demo: $demo")
                        demo[i.key] = i.value
                        data.add(demo)
                    }
                    // Convert and sort the entries by date-time in descending order
                    data.sortByDescending { it.keys.first() }

                    y.updateData(data)

                    frag.findViewById<LottieAnimationView>(R.id.lottieprogi).visibility = View.GONE
                    frag.findViewById<RecyclerView>(R.id.recyclerActivity).visibility = View.VISIBLE

                    frag.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).isRefreshing = false

                    Log.d(TAG, "Data: $data")

                    val showDatePickerButton = frag.findViewById<ImageButton>(R.id.datePicker)

                    frag.findViewById<ImageButton>(R.id.clear).setOnClickListener {
                        frag.findViewById<EditText>(R.id.datetext).setText("")
                        y?.updateData(data)
                        frag.findViewById<ImageButton>(R.id.clear).visibility = View.GONE
                        if (data.size == 0) {
                            frag.findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                            frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                View.VISIBLE
                        } else {
                            frag.findViewById<TextView>(R.id.text).visibility = View.GONE
                            frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                View.GONE
                        }
                    }

                    showDatePickerButton.setOnClickListener {
                        // Get the current date
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        // Create the DatePickerDialog
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                // The month value is 0-based. e.g., 0 for January.

                                // Use the selected year, month, and day of month here
                                val dateString =
                                    "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"

                                val demodata: MutableList<HashMap<String, MutableList<HashMap<String, String>>>> =
                                    mutableListOf()

                                for (i in data) {
                                    val dateTimeStr: String = i.keys.first().toString()
                                    if (isDateMatching(
                                            dateTimeStr,
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDayOfMonth
                                        ) == true
                                    ) {
                                        Log.d(TAG, "Matched")
                                        demodata.add(i)
                                    }
                                }

                                if (y != null) {
                                    Log.d(TAG, "New Data: $data")
                                    frag.findViewById<EditText>(R.id.datetext)
                                        .setText("Looking for $dateString")
                                    y.updateData(demodata)
                                    if (demodata.size == 0) {
                                        frag.findViewById<TextView>(R.id.text).visibility =
                                            View.VISIBLE
                                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                            View.VISIBLE
                                    } else {
                                        frag.findViewById<TextView>(R.id.text).visibility =
                                            View.GONE
                                        frag.findViewById<LottieAnimationView>(R.id.empty).visibility =
                                            View.GONE
                                    }
                                    frag.findViewById<ImageButton>(R.id.clear).visibility =
                                        View.VISIBLE
                                }



                                Log.d(TAG, "Demo Data: $demodata")
                                // Do something with the selected date, e.g., display it or use it in your app

                            },
                            year,
                            month,
                            day
                        )

                        // Show the DatePickerDialog
                        datePickerDialog.show()
                    }


                } else {
                    println("Document does not exist")
                }


            }
        }

            return frag

        }


}
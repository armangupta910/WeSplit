package com.example.wesplit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class getting_details_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getting_details)

        val button1:Button = findViewById(R.id.back)
        val button2:Button = findViewById(R.id.done)
        button1.setOnClickListener {
            startActivity(Intent(this,sign_in_activity::class.java))
            finish()
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        }
        findViewById<EditText>(R.id.email).setText(FirebaseAuth.getInstance().currentUser?.email.toString())
        findViewById<EditText>(R.id.email).isEnabled = false
        button2.setOnClickListener {
            val name:String
            val email:String
            val phone:String

            name = findViewById<EditText>(R.id.name).text.toString()
            email = findViewById<EditText>(R.id.email).text.toString()
            phone = "+91 " + findViewById<EditText>(R.id.phone).text.toString()

            addUserToFirestore(name,email,phone)

            Toast.makeText(this,"You're registered. Happy splitting",Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
            finish()

        }
    }
    private fun addUserToFirestore(name: String, email: String, phoneNumber: String) {
        val user = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        val friends:MutableList<String> = mutableListOf()
        val db = FirebaseFirestore.getInstance()
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone_number" to phoneNumber,
                "friends" to friends,
                "groups" to mutableListOf<String>(),
                "Expenses" to hashMapOf<String,Any>(),
                "Loans" to hashMapOf<String,String>()
            )

            db.collection("Users").document(user)
                .set(userData)
                .addOnSuccessListener {
                    // Handle success, e.g., show a toast message or update UI
                    Log.d("Firestore", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    // Handle failure, e.g., show error message
                    Log.w("Firestore", "Error writing document", e)
                }
    }
}
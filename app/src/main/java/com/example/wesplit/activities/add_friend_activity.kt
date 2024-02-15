package com.example.wesplit.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator

class add_friend_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        val check:Button = findViewById(R.id.check)

        val text:TextView = findViewById(R.id.text)
        val name:TextView = findViewById(R.id.name)
        val email:TextView = findViewById(R.id.email)
        val add:Button = findViewById(R.id.add)
        val phone:TextView = findViewById(R.id.phone)

        check.setOnClickListener {
            val uid = findViewById<EditText>(R.id.uid).text.toString()
            var ref = true
            val db = FirebaseFirestore.getInstance()
            db.collection("Users").get().addOnSuccessListener {
                for(i in it){
                    if(i.id.toString() == uid && i.id.toString()!=FirebaseAuth.getInstance().currentUser!!.uid.toString()){
                        ref = false
                        text.visibility = View.VISIBLE
                        name.setText("Name: " + i.data.get("name"))
                        name.visibility = View.VISIBLE
                        email.setText("Email: " + i.data.get("email"))
                        email.visibility = View.VISIBLE
                        add.visibility = View.VISIBLE
                        phone.setText("Phone: " + i.data.get("phone_number"))
                        phone.visibility = View.VISIBLE

                        findViewById<Button>(R.id.add).setOnClickListener {
                            db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("friends",FieldValue.arrayUnion(uid)).addOnSuccessListener {
                                Toast.makeText(this,"Friend added successfully!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                if(ref == true){
                    Toast.makeText(this,"Sorry, there's no person with this ID",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"Could not search", Toast.LENGTH_SHORT).show()
            }

        }

        findViewById<ImageButton>(R.id.goBack).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        val scan:Button = findViewById(R.id.scan)
        scan.setOnClickListener {
            startScanner()
        }

    }
    private fun startScanner() {
        // Explicitly set your activity's orientation to portrait.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val integrator = IntentIntegrator(this).apply {
            setPrompt("Scan a QR code")
            // It seems you want to lock the orientation, but let's ensure it's portrait.
            // However, since the orientation is already set to portrait for the activity,
            // you might want to experiment with not locking the orientation here
            // or ensure that the library respects this setting correctly.
            setOrientationLocked(false) // Try setting it to false as a test
            setBeepEnabled(true)
            // You can also specify the camera ID if you want to use a specific camera.
            // setCameraId(0) // for the back camera, typically
        }

        // Start the scanner. The activity's orientation is already set to portrait above.
        integrator.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                val check:Button = findViewById(R.id.check)

                val text:TextView = findViewById(R.id.text)
                val name:TextView = findViewById(R.id.name)
                val email:TextView = findViewById(R.id.email)
                val add:Button = findViewById(R.id.add)
                val phone:TextView = findViewById(R.id.phone)
                val uid = result.contents
                var ref = true
                val db = FirebaseFirestore.getInstance()
                db.collection("Users").get().addOnSuccessListener {
                    for(i in it){
                        if(i.id.toString() == uid && i.id.toString()!=FirebaseAuth.getInstance().currentUser!!.uid.toString()){
                            ref = false
                            text.visibility = View.VISIBLE
                            name.setText("Name: " + i.data.get("name"))
                            name.visibility = View.VISIBLE
                            email.setText("Email: " + i.data.get("email"))
                            email.visibility = View.VISIBLE
                            add.visibility = View.VISIBLE
                            phone.setText("Phone: " + i.data.get("phone_number"))
                            phone.visibility = View.VISIBLE

                            findViewById<Button>(R.id.add).setOnClickListener {
                                db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("friends",FieldValue.arrayUnion(uid)).addOnSuccessListener {
                                    Toast.makeText(this,"Friend added successfully!",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    if(ref == true){
                        Toast.makeText(this,"Sorry, there's no person with this ID",Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Could not search", Toast.LENGTH_SHORT).show()
                }
                // Display the scanned result.contents as needed
            }
        }
    }


}
package com.example.wesplit.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
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
                            db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
                                val friends = it.get("friends") as? List<String>?: listOf()
                                if(!friends.contains(uid)){
                                    db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("friends",FieldValue.arrayUnion(uid)).addOnSuccessListener {
                                        Toast.makeText(this,"Friend added successfully!",Toast.LENGTH_SHORT).show()
                                    }
                                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
                                        val loans = it.get("Loans") as? Map<String,String> ?: mapOf()
                                        val updatedLoans = HashMap(loans)

                                        updatedLoans.putAll(mapOf(uid to "0"))
                                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("Loans",updatedLoans).addOnSuccessListener {

                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(this,"Friend already added!",Toast.LENGTH_SHORT).show()
                                }
                            }
                            db.collection("Users").document(uid).get().addOnSuccessListener {
                                val friends = it.get("friends") as? List<String>?: listOf()
                                if(!friends.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())){
                                    db.collection("Users").document(uid).update("friends",FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid.toString())).addOnSuccessListener {
                                        Toast.makeText(this,"Friend added successfully!",Toast.LENGTH_SHORT).show()
                                    }
                                    FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnSuccessListener {
                                        val loans = it.get("Loans") as? Map<String,String> ?: mapOf()
                                        val updatedLoans = HashMap(loans)

                                        updatedLoans.putAll(mapOf(FirebaseAuth.getInstance().currentUser?.uid.toString() to "0"))
                                        FirebaseFirestore.getInstance().collection("Users").document(uid).update("Loans",updatedLoans).addOnSuccessListener {

                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(this,"Friend already added!",Toast.LENGTH_SHORT).show()
                                }
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
        // Show an options dialog or bottom sheet here to let the user choose between camera or gallery
        val options = arrayOf("Scan using Camera", "Select from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select QR Code Source")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> startCameraScanner() // User chose to scan using camera
                    1 -> pickImageFromGallery() // User chose to select from gallery
                }
            }.show()
    }

    private fun startCameraScanner() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val integrator = IntentIntegrator(this).apply {
            setPrompt("Scan a QR code")
            setOrientationLocked(false)
            setBeepEnabled(true)
        }
        integrator.initiateScan()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1234 // Define a request code for gallery intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle gallery selection
            data?.data?.let { uri ->
                // Assuming you have a function to handle the scanning from a URI
                scanQRCodeFromUri(uri,this)
            }
        } else {
            // Handle camera scanner result
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
                                    db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
                                        val friends = it.get("friends") as? List<String> ?: listOf()
                                        if (!friends.contains(uid)) {
                                            db.collection("Users")
                                                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                .update("friends", FieldValue.arrayUnion(uid))
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Friend added successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            FirebaseFirestore.getInstance().collection("Users")
                                                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                .get().addOnSuccessListener {
                                                    val loans =
                                                        it.get("Loans") as? Map<String, String>
                                                            ?: mapOf()
                                                    val updatedLoans = HashMap(loans)

                                                    updatedLoans.putAll(mapOf(uid to "-1"))
                                                    FirebaseFirestore.getInstance()
                                                        .collection("Users")
                                                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                        .update("Loans", updatedLoans)
                                                        .addOnSuccessListener {

                                                        }
                                                }
                                        }
                                    }

                                    db.collection("Users").document(uid).get().addOnSuccessListener {
                                        val friends = it.get("friends") as? List<String> ?: listOf()
                                        if (!friends.contains(uid)) {
                                            db.collection("Users")
                                                .document(uid)
                                                .update("friends", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid.toString()))
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Friend added successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            FirebaseFirestore.getInstance().collection("Users")
                                                .document(uid)
                                                .get().addOnSuccessListener {
                                                    val loans =
                                                        it.get("Loans") as? Map<String, String>
                                                            ?: mapOf()
                                                    val updatedLoans = HashMap(loans)

                                                    updatedLoans.putAll(mapOf(FirebaseAuth.getInstance().currentUser?.uid.toString() to "-1"))
                                                    FirebaseFirestore.getInstance()
                                                        .collection("Users")
                                                        .document(uid)
                                                        .update("Loans", updatedLoans)
                                                        .addOnSuccessListener {

                                                        }
                                                }
                                        }
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
                    // Handle the scanned QR code string
                }
            }
        }
    }

    private fun scanQRCodeFromUri(uri: Uri, context: Context) {
        try {
            // Convert URI to Bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                bitmap.recycle()

                // Create a binary bitmap source from the bitmap
                val source = RGBLuminanceSource(width, height, pixels)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                // Try to decode the QR code
                val result = MultiFormatReader().decode(binaryBitmap)
                if (result != null) {
                    // If QR code is decoded successfully, use the result text
                    val qrContent = result.text
                    // Handle the decoded QR code text as needed, e.g., showing in a Toast or processing further
                    runOnUiThread {
                        Toast.makeText(context, "QR Code content: $qrContent", Toast.LENGTH_LONG).show()
                        val check:Button = findViewById(R.id.check)

                        val text:TextView = findViewById(R.id.text)
                        val name:TextView = findViewById(R.id.name)
                        val email:TextView = findViewById(R.id.email)
                        val add:Button = findViewById(R.id.add)
                        val phone:TextView = findViewById(R.id.phone)
                        val uid = qrContent.toString()
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

                        // Process the QR code content as needed
                    }
                } else {
                    // Handle failure to decode QR code
                    runOnUiThread {
                        Toast.makeText(context, "Could not decode QR Code", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Handle failure to load image
                runOnUiThread {
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
            runOnUiThread {
                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_LONG).show()
            }
        }
    }



}
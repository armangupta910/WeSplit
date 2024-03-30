package com.example.wesplit.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class splashscreen : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    var ref = true
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var token: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        token=getSharedPreferences("data", Context.MODE_PRIVATE)
        if(token.getString("data","")!=""){
            val userUid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

            val db = FirebaseFirestore.getInstance()
            db.collection("Users").get().addOnSuccessListener {
                for(i in it){
                    if(i.id.toString() == userUid){
                        ref = false
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
                        finish()
                    }
                }
                if(ref == true){
                    startActivity(Intent(this,getting_details_activity::class.java))
                    overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
                }
            }.addOnFailureListener {
                Toast.makeText(this,"Could not search", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            startActivity(Intent(this,sign_in_activity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
        }

    }
}
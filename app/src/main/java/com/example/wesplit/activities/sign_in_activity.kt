package com.example.wesplit.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.MultiLayoutAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class sign_in_activity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    var ref = true
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var token: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = MultiLayoutAdapter()

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> "Home"
//                1 -> "Profile"
//                2 -> "Settings"
//                else -> null
//            }
            tab.icon = when (position) {
                0 -> ContextCompat.getDrawable(this, R.drawable.point)
                1 -> ContextCompat.getDrawable(this, R.drawable.point)
                2 -> ContextCompat.getDrawable(this, R.drawable.point)
                else -> null
            }
        }.attach()

        val scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.button_scale)
//        token=getSharedPreferences("data", Context.MODE_PRIVATE)
//        if(token.getString("data","")!=""){
//            val userUid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
//
//            val db = FirebaseFirestore.getInstance()
//            db.collection("Users").get().addOnSuccessListener {
//                for(i in it){
//                    if(i.id.toString() == userUid){
//                        ref = false
//                        startActivity(Intent(this,MainActivity::class.java))
//                        finish()
//                    }
//                }
//                if(ref == true){
//                    startActivity(Intent(this,getting_details_activity::class.java))
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this,"Could not search",Toast.LENGTH_SHORT).show()
//            }
//        }
        val butt:Button = findViewById(R.id.googleSignIn)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.revokeAccess()

        firebaseAuth = FirebaseAuth.getInstance()

        butt.setOnClickListener { view: View? ->
            findViewById<ImageView>(R.id.google).visibility = View.GONE
            findViewById<ProgressBar>(R.id.progi).visibility = View.VISIBLE
            dimBackground()
            view?.startAnimation(scaleAnimation)
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }
    }

    fun dimBackground() {
        val window = window
        val layoutParams = window.attributes
        layoutParams.dimAmount = 0.5f // Adjust this value to control the dim amount
        window.attributes = layoutParams
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign in Failed!", Toast.LENGTH_SHORT).show()
            findViewById<ImageView>(R.id.google).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progi).visibility = View.GONE
        }
    }
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val userUid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

                val db = FirebaseFirestore.getInstance()
                db.collection("Users").get().addOnSuccessListener {
                    for(i in it){
                        if(i.id.toString() == userUid){
                            ref = false
                            startActivity(Intent(this,MainActivity::class.java))
                            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
                            finish()
                        }
                    }
                    if(ref == true){
                        startActivity(Intent(this,getting_details_activity::class.java))
                        overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Could not search",Toast.LENGTH_SHORT).show()
                    findViewById<ImageView>(R.id.google).visibility = View.VISIBLE
                    findViewById<ProgressBar>(R.id.progi).visibility = View.GONE
                }
            }
        }
    }
    private fun checkIfUserExists(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").get().addOnSuccessListener {
            for(i in it){
                if(i.id.toString() == uid){
                    ref=false
                    break
                }
            }
        }
    }
}
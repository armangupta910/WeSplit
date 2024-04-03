package com.example.wesplit.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
                        if(isInternetAvailable(this) == true) {
                            startActivity(Intent(this, MainActivity::class.java))
                            overridePendingTransition(
                                androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
                                androidx.appcompat.R.anim.abc_fade_out
                            )
                            finish()
                        }
                        else{
                            showNoInternetDialog()
                        }
                    }
                }
                if(ref == true){
                    if(isInternetAvailable(this) == true) {
                        startActivity(Intent(this, getting_details_activity::class.java))
                        overridePendingTransition(
                            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
                            androidx.appcompat.R.anim.abc_fade_out
                        )
                    }
                    else{
                        showNoInternetDialog()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this,"Could not search", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            if(isInternetAvailable(this) == true){
                startActivity(Intent(this,sign_in_activity::class.java))
                overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_fade_out)
            }
            else{
                showNoInternetDialog()
            }

        }

    }

    fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Internet Connection")
        builder.setMessage("Internet connection not found.")
        builder.setPositiveButton("Try Again") { dialog, which ->
            // Code to reload the activity
            finish()
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false) // Prevent the user from canceling the dialog with back button
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
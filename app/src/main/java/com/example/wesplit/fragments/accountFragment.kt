package com.example.wesplit.fragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wesplit.R
import com.google.android.datatransport.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import android.provider.Settings
import com.example.wesplit.activities.sign_in_activity
import java.io.IOException
import java.io.OutputStream


class accountFragment : Fragment() {

    private lateinit var qrCodeBitmap:Bitmap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_account, container, false)

        val getQR:LinearLayout = frag.findViewById(R.id.getQR)
        getQR.setOnClickListener {
            showCustomDialog()
        }

        val profileImage:ImageView = frag.findViewById<ImageView>(R.id.prfileImage)
        val imageURL = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        Glide.with(requireContext())
            .load(imageURL)
            .apply(RequestOptions().placeholder(R.drawable.person_image).error(R.drawable.person_image))
            .into(profileImage)
        frag.findViewById<TextView>(R.id.email).setText(FirebaseAuth.getInstance().currentUser?.email.toString())
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            frag.findViewById<TextView>(R.id.name).setText(it.get("name").toString())
        }
        val noti = frag.findViewById<LinearLayout>(R.id.notificationsettings)
        noti.setOnClickListener {
            // Create an Intent that opens the notification settings for your app
            val intent = Intent().apply {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        putExtra("app_package", requireActivity().packageName)
                        putExtra("app_uid", requireActivity().applicationInfo.uid)
                    }
                    else -> {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        addCategory(Intent.CATEGORY_DEFAULT)
                        data = Uri.parse("package:${requireActivity().packageName}")
                    }
                }
            }

            // Start the activity with the intent
            startActivity(intent)
        }

        val contact = frag.findViewById<LinearLayout>(R.id.contact)
        // Assuming this code is inside an Activity or a Fragment. If it's in a Fragment, use requireContext() instead of this for the context.
        contact.setOnClickListener {
            val recipientEmail = "wesplit2226@gmail.com" // The email address you want to send to
            val subject = "Contact Support"
            val body = ""

            val mailto = "mailto:$recipientEmail" +
                    "?cc=" + // CC addresses, if needed. Separate multiple addresses with commas
                    "&subject=" + Uri.encode(subject) +
                    "&body=" + Uri.encode(body)

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(mailto)

                // To specifically open the Gmail app, if desired. Comment this line to show chooser dialog
                setPackage("com.google.android.gm")
            }

            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                // Handle case when no email app is installed
                Toast.makeText(requireContext(), "No email client installed.", Toast.LENGTH_SHORT).show()
            }
        }

        frag.findViewById<TextView>(R.id.signout).setOnClickListener {
            val shared = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
            val edit = shared.edit()
            edit.putString("data", "")
            edit.apply()

            // Sign out the Firebase user
            FirebaseAuth.getInstance().signOut()

            // Start sign-in activity. Make sure the context is correct.
            // You might need to adjust this depending on your navigation setup.
            // For example, if using NavController for navigation component.
            val intent = Intent(requireActivity(), sign_in_activity::class.java)
            startActivity(intent)

            // Close the current activity if this fragment is tied to it
            // Note: Be cautious with this call, as it will close the entire activity hosting this fragment.
            // If you're using a single-activity architecture with multiple fragments, consider using
            // the navigation component to navigate back instead.
            requireActivity().finish()
        }



        return frag
    }
    private fun showCustomDialog() {
        // Context is required, hence use requireContext() to get the context
        val dialog = Dialog(requireContext())

        // Set the custom layout
        dialog.setContentView(R.layout.custom_dialog)

        // Initialize the views of the custom dialog
        val qr = dialog.findViewById<ImageView>(R.id.QRCode)

        val qrCodeBitmap = generateQRCode(FirebaseAuth.getInstance().currentUser?.uid.toString())
        qr.setImageBitmap(qrCodeBitmap)



        // Set button click listener or other logic
        dialog.findViewById<Button>(R.id.share).setOnClickListener {
            val qrCodeBitmap = generateQRCode(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val fileName = "qrCode.png" // You can use any name, just ensure it's unique
            val fileUri = saveBitmapToFile(requireContext(), qrCodeBitmap!!, fileName)
            Toast.makeText(requireContext(), "File generated succesfully for sharing", Toast.LENGTH_SHORT).show()

            fileUri?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, it)
                    putExtra(Intent.EXTRA_TEXT,"Hey there, I just started using the WeSplit App and it is just Fabulous. I highly encourage you to use the App and be my friend there. Happy Splitting.\n Scan the QR code from the App to add me as a friend.\n My UID: ${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
                    type = "image/png"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val shareChooser = Intent.createChooser(shareIntent, null)
                startActivity(shareChooser)
            } ?: run {
                // Handle the error when fileUri is null
                Toast.makeText(requireContext(), "Error sharing QR code", Toast.LENGTH_SHORT).show()
            }
//            dialog.dismiss()
        }

        // Display the custom dialog
        dialog.show()
    }


    fun generateQRCode(text: String): Bitmap? {
        val width = 500
        val height = 500
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val file = File(context.cacheDir, fileName)
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }


    private fun shareImageUri(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }




}
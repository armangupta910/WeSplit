package com.example.wesplit.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.wesplit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class accountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag =  inflater.inflate(R.layout.fragment_account, container, false)

        val qrCodeImageView: ImageView = frag.findViewById(R.id.QRCode)
        val qrCodeBitmap = generateQRCode(FirebaseAuth.getInstance().currentUser?.uid.toString())
        qrCodeImageView.setImageBitmap(qrCodeBitmap)

        return frag
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


}
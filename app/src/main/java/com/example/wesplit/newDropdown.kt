package com.example.wesplit

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class newDropdown(
    context: Context, data: MutableList<HashMap<String, MutableList<HashMap<String, String>>>>
) : ArrayAdapter<String>(context, R.layout.dropdown, data.map { it.values.first()[2]["Description"].toString() }) {

//    private val descriptions = mutableListOf<String>()
//
//    init {
//        for (entry in data) {
//            val expenseList = entry.values.first()
//
//            val hashi:HashMap<String,String> = expenseList[2]
//            descriptions.add(hashi["Description"].toString())
//
//        }
//
//        // Add all descriptions to the adapter
//        addAll(descriptions)
//    }



    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val descriptions = data.map { it.values.first()[2]["Description"].toString() }
    private val amounts = data.map {it.values.first()[2]["Amount"].toString()}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.dropdown, parent, false)



        val descriptionText = view.findViewById<TextView>(R.id.name)
        val letterDrawable = view.findViewById<ImageView>(R.id.drawableStart)



        val description = descriptions[position]
        descriptionText.text = description
        letterDrawable.setImageDrawable(createAmountDrawableWithColors(context, amounts[position],20,20,ContextCompat.getColor(context,R.color.white),ContextCompat.getColor(context,R.color.black)))

        Log.d(TAG,"Description: ${descriptions} ")

        return view
    }

    fun createAmountDrawableWithColors(context: Context, amount: String, widthInDp: Int, heightInDp: Int, textColor: Int, bgColor: Int): Drawable? {
        val metrics = context.resources.displayMetrics

        // Convert dp dimensions to pixels
        val widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp.toFloat(), metrics).toInt()
        val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp.toFloat(), metrics).toInt()

        // Initialize a Bitmap and Canvas to draw
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set the background color
        bitmap.eraseColor(bgColor) // This will fill the bitmap with the background color

        // Initialize a Paint object for drawing the text
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            // Start with a default text size; adjust below
            textSize = heightPx.toFloat() * 0.4f
        }

        // Measure text and adjust text size to fit within the specified dimensions
        val textBounds = Rect()
        paint.getTextBounds(amount, 0, amount.length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

        // Adjust text size based on the actual width
        if (textWidth > widthPx || textHeight > heightPx) {
            val widthRatio = widthPx.toFloat() / textWidth.toFloat()
            val heightRatio = heightPx.toFloat() / textHeight.toFloat()
            val ratio = minOf(widthRatio, heightRatio)

            paint.textSize = paint.textSize * ratio * 0.9f // Scale down a bit more for padding
        }

        // Re-measure with new size
        paint.getTextBounds(amount, 0, amount.length, textBounds)

        // Calculate the position to center the text
        val x = widthPx / 2f - textBounds.width() / 2f - textBounds.left
        val y = heightPx / 2f + textBounds.height() / 2f - textBounds.bottom

        // Draw the text on the canvas
        canvas.drawText(amount, x, y, paint)

        // Return the bitmap as a Drawable
        return BitmapDrawable(context.resources, bitmap)
    }

}

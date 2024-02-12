package com.example.wesplit.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.wesplit.MainActivity
import com.example.wesplit.R
import com.google.android.material.chip.Chip

class create_group_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        val home:Chip = findViewById(R.id.home)
        val trip:Chip = findViewById(R.id.trip)
        val couple:Chip = findViewById(R.id.couple)
        val others:Chip = findViewById(R.id.other)

        home.setOnClickListener {
            home.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green1))
            trip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            couple.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            others.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
        }
        trip.setOnClickListener {
            home.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            trip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green1))
            couple.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            others.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
        }
        couple.setOnClickListener {
            home.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            trip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            couple.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green1))
            others.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
        }
        others.setOnClickListener {
            home.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            trip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            couple.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white))
            others.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green1))
        }

        findViewById<ImageButton>(R.id.backAddExpenseFragment).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}
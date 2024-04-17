package com.example.wesplit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.MultiLayoutAdapter

class carousel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = MultiLayoutAdapter()

    }
}
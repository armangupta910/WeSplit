package com.example.wesplit.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R

class MultiLayoutAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LAYOUT_ONE = 0
        private const val LAYOUT_TWO = 1
        private const val LAYOUT_THREE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> LAYOUT_ONE
            1 -> LAYOUT_TWO
            2 -> LAYOUT_THREE
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LAYOUT_ONE -> ViewHolderOne(inflater.inflate(R.layout.carousel_layout_1, parent, false))
            LAYOUT_TWO -> ViewHolderTwo(inflater.inflate(R.layout.carousel_layout_2, parent, false))
            LAYOUT_THREE -> ViewHolderThree(inflater.inflate(R.layout.carousel_layout_3, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Handle your binding logic here
        // You can use 'when (holder)' to differentiate between your ViewHolders if necessary
    }

    override fun getItemCount(): Int = 3 // Since you have three layouts/pages

    // Define ViewHolder for each layout
    class ViewHolderOne(itemView: View) : RecyclerView.ViewHolder(itemView)
    class ViewHolderTwo(itemView: View) : RecyclerView.ViewHolder(itemView)
    class ViewHolderThree(itemView: View) : RecyclerView.ViewHolder(itemView)
}

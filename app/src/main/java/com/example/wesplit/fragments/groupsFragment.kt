package com.example.wesplit.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wesplit.R
import com.example.wesplit.recyclerviews.adaptorForGroupsFragment


class groupsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val frag = inflater.inflate(R.layout.fragment_groups, container, false)

        val x = frag.findViewById<RecyclerView>(R.id.recyclerGroups)
        val y = adaptorForGroupsFragment()
        x.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        x.adapter = y

        return frag
    }
}
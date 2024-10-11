package com.example.virnandaelsa_3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class fragProfile : Fragment() {

    lateinit var thisParent : DashboardActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DashboardActivity
        return inflater.inflate(R.layout.frag_edprofile, container, false)
    }
}
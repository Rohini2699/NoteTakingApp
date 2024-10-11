package com.example.notetakingapp.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.notetakingapp.R


class thirdscreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_thirdscreen, container, false)

        val next = view.findViewById<TextView>(R.id.textView3)
        //val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)
        next.setOnClickListener{
          findNavController().navigate(R.id.onboard)
        }

        return view
    }
    }



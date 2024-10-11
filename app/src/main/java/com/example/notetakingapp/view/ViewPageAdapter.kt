package com.example.notetakingapp.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter (list: ArrayList<Fragment> ,fragmentManager: FragmentManager ,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    private val fragmentList=list
    override fun getItemCount(): Int {

        TODO("Not yet implemented")
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
        return fragmentList[position]
    }
}
package com.example.notetakingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.databinding.FragmentOnBoardingFragmetBinding
import com.example.notetakingapp.view.ViewPageAdapter
import com.example.notetakingapp.view.screens.FirstScreen
import com.example.notetakingapp.view.screens.SecondScreen
import com.example.notetakingapp.view.screens.ThirdScreen

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardingFragment : Fragment() {

    private var binding: FragmentOnBoardingFragmetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingFragmetBinding.inflate(inflater)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPageAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding?.run {
            viewPager.adapter = adapter
            dotindicator.attachTo(viewPager)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            tvNext.setOnClickListener {
                if (viewPager.currentItem == 2) {
                    val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPref?.edit {
                        putBoolean("onboarding_complete", true)
                        apply()
                    }
                    findNavController().navigate(R.id.action_onboardingFragment_to_homeScreenFragment)
                } else {
                    viewPager.currentItem += 1
                }
            }
        }
    }
}
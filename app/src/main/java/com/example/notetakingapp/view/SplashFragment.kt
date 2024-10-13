package com.example.notetakingapp.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.util.Constants.PREF_KEY_ONBOARDING
import com.example.notetakingapp.util.Constants.PREF_NAME


class SplashFragment : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        // Check if onboarding is complete using Shared Preferences
        val sharedPref = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isOnboardingComplete = sharedPref.getBoolean(PREF_KEY_ONBOARDING, false)
        Handler(Looper.getMainLooper()) .postDelayed({
            if (!isOnboardingComplete) {
                // Navigate to the onboarding fragment to start the onboarding process
                findNavController().navigate(
                    R.id.action_splashFragment_to_onBoardingFragmet
                )
            }
            else
            {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)

            }
        } , 3000)

        return view
    }


    }

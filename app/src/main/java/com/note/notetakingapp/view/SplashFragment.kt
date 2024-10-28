package com.note.notetakingapp.view
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.note.notetakingapp.R
import com.note.notetakingapp.util.Constants.PREF_KEY_ONBOARDING
import com.note.notetakingapp.util.Constants.PREF_NAME


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()

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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity) .supportActionBar?.show()
    }


    }

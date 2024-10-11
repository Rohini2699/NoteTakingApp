package com.example.notetakingapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.notetakingapp.view.ViewPageAdapter
import com.example.notetakingapp.view.screens.firstscreen
import com.example.notetakingapp.view.screens.secondscreen
import com.example.notetakingapp.view.screens.thirdscreen
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [onBoardingFragmet.newInstance] factory method to
 * create an instance of this fragment.
 */
class onBoardingFragmet : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_boarding_fragmet, container, false)
              val fragmentList = arrayListOf<Fragment>(
                  firstscreen(),
                  secondscreen(),
                  thirdscreen()

              )

               val adapter = ViewPageAdapter(fragmentList ,requireActivity().supportFragmentManager,lifecycle)
                  val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.adapter =adapter
        val indicator = view.findViewById<DotsIndicator>(R.id.dotindicator)
        indicator.attachTo(viewPager)

         if (onBoardingFragmentFinished())
         {
             findNavController().navigate(R.id.homeFragment)
         }
        else
         {
             findNavController().navigate(R.id.onBoardingFragmet)

         }
        return view

    }
    private fun onBoardingFragmentFinished():Boolean{
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding" , Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("finished " , false)
    }


}
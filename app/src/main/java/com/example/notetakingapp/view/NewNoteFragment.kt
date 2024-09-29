package com.example.notetakingapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentSecondaryfragmentBinding
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.room.Priority
import com.example.notetakingapp.viewmodel.NoteViewModel


class NewNoteFragment : Fragment(R.layout.fragment_secondaryfragment) ,MenuProvider {
   private  var _addNoteBinding: FragmentSecondaryfragmentBinding?=null
    private val binding get() = _addNoteBinding!! // Assertion operator
    private lateinit var myViewModel: NoteViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _addNoteBinding = FragmentSecondaryfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel = (activity as MainActivity).myViewmodel
        mView = view
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.RESUMED)
        binding.lottieAnimationView.apply {
            setAnimation("animation1.json") // Set the animation file
            View.VISIBLE
            playAnimation() // Start the animation

            // Pause the animation after 3 seconds
            postDelayed({
                if (isAnimating) {
                    pauseAnimation() // Pause the animation
                    visibility = View.GONE // Hide the view if needed
                }
            }, 2000) // 3000 milliseconds = 3 seconds
        }

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
    }
    private fun savenote(view:View) {
        val noteTitle = binding.title.text.toString().trim()
        val notedescription = binding.description.text.toString().trim()
        if(noteTitle.isNotEmpty())
        {
            val notes = Notes(0, noteTitle , notedescription ,isSelected = false , false, priority = null )
            myViewModel.addNote(notes)
            Toast.makeText(mView.context,"Notes saved Successfully " , Toast.LENGTH_SHORT).show()

            view.findNavController().popBackStack(R.id.homeFragment , false)
        }
        else{
            Toast.makeText(mView.context,"Please enter note title and note description " , Toast.LENGTH_SHORT ).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
       menu.clear()
        menuInflater.inflate(R.menu.menu_new_note , menu )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.menu_save -> {
                savenote(mView)

                true
            }
            else->false
        }
        }
    override fun onDestroy() {
        super.onDestroy()
        _addNoteBinding=null
    }
}


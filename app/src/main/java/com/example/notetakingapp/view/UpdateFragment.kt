package com.example.notetakingapp.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentUpdateBinding
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.viewmodel.NoteViewModel
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateFragment : Fragment(R.layout.fragment_update) ,MenuProvider {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!! // Assertion operator
    private lateinit var myViewModel: NoteViewModel
    private lateinit var currentNote: Notes
    //Since the update note fragment contains arguments in nav _graph
    private val args: UpdateFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)
        myViewModel = (activity as MainActivity).myViewmodel
        Log.d("UpdateFragment", "Arguments: ${args.notes}")
        currentNote = args.notes!!
        binding.editNoteTitle.setText(currentNote.title)
        binding.editNoteDesc.setText(currentNote.description)

        //if the user update the note
        binding.editNoteFab.setOnClickListener {
            val title = binding.editNoteTitle.text.toString().trim()
            val body = binding.editNoteDesc.text.toString().trim()
            if (title.isNotEmpty()) {
                val note = Notes(currentNote.id, title, body ,false)
                myViewModel.updateNotes(note)
                view.findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Unable to update ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun deleteNote() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete Note ")
            setMessage("you want to delete it ")
            setPositiveButton("Delete") { _, _ ->
                myViewModel.deleteNotes(currentNote)
                view?.findNavController()?.navigate(R.id.action_updateFragment_to_homeFragment)
            }
            setNegativeButton("cancel", null)
        }.create().show()
    }
    private fun voiceInput() {
        //language e.g. "en" for "English", "ur" for "Urdu", "hi" for "Hindi" etc.
        // val language = "en"

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            voiceInputArl.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val voiceInputArl = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val result = activityResult.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            val text = result!![0]

            binding.editNoteDesc.append(text)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_update_note , menu )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId)
        {
            R.id.menu_delete ->{
                deleteNote()
                true
            }
            R.id.menu_mic->{
                voiceInput()
                true
            }
            else->false
        }
    }

}




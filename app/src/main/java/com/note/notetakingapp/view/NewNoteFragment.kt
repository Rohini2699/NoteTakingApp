package com.note.notetakingapp.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.note.notetakingapp.R
import com.note.notetakingapp.databinding.FragmentSecondaryfragmentBinding
import com.note.notetakingapp.room.Note
import com.note.notetakingapp.viewmodel.NoteViewModel


class NewNoteFragment : Fragment(R.layout.fragment_secondaryfragment), MenuProvider {
    private var _addNoteBinding: FragmentSecondaryfragmentBinding? = null
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
    ): View {
        _addNoteBinding = FragmentSecondaryfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel = (activity as MainActivity).myViewmodel
        mView = view
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)


    }

    private fun savenote(view: View) {
        val noteTitle = binding.title.text.toString().trim()
        val notedescription = binding.description.text.toString().trim()
        if (noteTitle.isNotEmpty()) {
            val notes =
                Note(
                    id = 0,
                    title = noteTitle,
                    description = notedescription,
                    isSelected = false,
                    isPinned = false,
                    date = (System.currentTimeMillis()).toString()
                )
            myViewModel.addNote(notes)
            Toast.makeText(mView.context, "Notes saved Successfully ", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(
                mView.context,
                "Please enter note title and note description ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_new_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_save -> {
                savenote(mView)
                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _addNoteBinding = null
    }
}


package com.example.notetakingapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notetak.CustomAdapter
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentHomeBinding
import com.example.notetakingapp.room.Note
import com.example.notetakingapp.room.Priority
import com.example.notetakingapp.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider,
    CustomAdapter.NoteClickListener {
    //here we are using the searchview
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // Assertion operator
    private lateinit var viewModel: NoteViewModel
    private lateinit var noteAdapter: CustomAdapter
    private var ispinned: Boolean = false
    private var isAlreadyPinned: Boolean = false
//    private lateinit var currentNote: Notes


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        viewModel = (activity as MainActivity).myViewmodel // Ensure MainActivity has myViewModel
        // Initialize your adapter and other UI elements here
        binding.homefragment = viewModel
        binding.lifecycleOwner = this


        // myViewModel.selectedlist.observe(viewLifecycleOwner){selectedNotes-> selectedNotes.forEach()}

        setUpRecyclerView()
        setupObserver()

        binding.fbutton.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_SecondaryFragment)
        }
        //myViewModel.selectedlist.observe(viewLifecycleOwner){notes->noteAdapter .
        //differ.submitList(notes)}
        binding.canceltext.setOnClickListener {
            binding.constraintSelectDeselect.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
            binding.fbutton.visibility = View.VISIBLE
            viewModel.removeSelection()

        }
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_pin -> {

                    viewModel.pinUnpinNotes(isAlreadyPinned)

//                  myViewModel.pinSelectedNotes(!ispinned)
//                    menuItem.setIcon(if (!ispinned) R.drawable.baseline_push_pin_24 else R.drawable.unpin)
//                    ispinned = !ispinned
                    true
                }

                R.id.action_deleteall -> {
                    // Handle Search click here
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete Note ")
                        setMessage("You want to delete the selected notes ")
                        setPositiveButton("Delete") { _, _ ->
                            viewModel.deleteNotesById()

                        }
                        setNegativeButton("cancel", null)
                    }.create().show()
                    true

                }

                R.id.action_lowpriority -> {
                    // Handles low priority  clicks here
                    viewModel.filterNotesByPriority(Priority.LOW)
                    viewModel.setPriorityForSelectedNotes(Priority.LOW)
                    true
                }

                R.id.action_highpriority -> {
                    // Handle priority clicks here
                    viewModel.filterNotesByPriority(Priority.HIGH)
                    viewModel.setPriorityForSelectedNotes(Priority.HIGH)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupObserver() {
        activity?.let {
            viewModel.users.observe(viewLifecycleOwner) { notes ->
                Log.d("NotesFragment", "Notes received: $notes")
                notes?.let {
                    viewModel.setNotesList(notes)
                }
            }
        }

        activity?.let {
            viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
                noteAdapter.differ.submitList(notes)
                updateUI(notes)
            }
        }

        viewModel.selectedNotesList.observe(viewLifecycleOwner) { notes ->
            Log.d("selectednotes", "HomeFragment : ${notes.size}")
            if (notes != null) {
                if (notes.isNotEmpty()) {
                    binding.constraintSelectDeselect.isVisible = true
                    binding.bottomNavigationView.isVisible = true
                    binding.fbutton.isVisible = false
                } else {
                    binding.constraintSelectDeselect.isVisible = false
                    binding.bottomNavigationView.isVisible = false
                    binding.fbutton.isVisible = true
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        noteAdapter = CustomAdapter(mutableListOf(), this)
        //Here, requireContext() ensures you're providing a non-null Context when creating your CustomAdapter.
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
        }
    }


    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if (note.isEmpty()) {
                binding.emptynotes.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.constraintSelectDeselect.isVisible = false
                binding.bottomNavigationView.isVisible = false
                binding.fbutton.isVisible = true
            } else {
                binding.emptynotes.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchNote(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery =
            "%$query" // it is a wild card entry that it can have 0 or more number of position .
        viewModel.searchnotes(searchQuery)
            .observe(this) { list -> noteAdapter.differ.submitList(list) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)
        val menuSearch = menu.findItem(R.id.title).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun onItemClick(notes: Note, view: View, isLongClick: Boolean) {
        if (isLongClick) {
            Log.d("ItemClick", "Long click detected")
        } else {
            view.post {
                viewModel.removeSelection()
                val directions = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(notes)
                view.findNavController().navigate(directions)
            }
        }
    }

    override fun onLongClicked(position: Int, notes: Note) {
        viewModel.selectNoteAtIndex(position)
        isAlreadyPinned = notes.isPinned
        binding.bottomNavigationView.menu.findItem(R.id.action_pin).setIcon(
            if (notes.isPinned) R.drawable.unpin
            else R.drawable.baseline_push_pin_24
        )
    }
}

/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)
        val mMenuSearch = menu.findItem(R.id.title).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)

    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        searchNote(query)
        return false
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNote(newText)
        }
        return true
    }
    private fun searchNote(query: String?) {
        val searchQuery ="%$query"
        myViewModel.searchnotes(searchQuery).observe(this ,{list->noteAdapter.differ.submitList(list)})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }



 */
package com.note.notetakingapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.note.notetakingapp.databinding.ActivityMainBinding
import com.note.notetakingapp.repository.NotesRepository
import com.note.notetakingapp.room.NotesDatabase
import com.note.notetakingapp.viewmodel.NoteViewModel
import com.note.notetakingapp.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var myViewmodel: NoteViewModel
    private lateinit var database: NotesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intilialize the database
        database = NotesDatabase.getInstance(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Get a reference to the NavHostFragment
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.findNavController
        setUpViewModel()
    }

    private fun setUpViewModel() {
        // establishing the connection between the database and the repository
        val dao = NotesDatabase.getInstance(applicationContext).db
        val notesRepository = NotesRepository(dao)
        // establishing the connection between the viewmodel factory and the repository.
        val viewmodelFactory = ViewModelFactory(notesRepository)
        //establishing the connection between the viewmodel factory and the view model.
        myViewmodel = ViewModelProvider(this, viewmodelFactory).get(NoteViewModel::class.java)
    }
}

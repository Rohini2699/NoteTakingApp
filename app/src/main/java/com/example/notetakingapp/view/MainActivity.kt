package com.example.notetakingapp.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.ActivityMainBinding
import com.example.notetakingapp.repository.NotesRepository
import com.example.notetakingapp.room.NotesDatabase
import com.example.notetakingapp.util.Constants.PREF_KEY_ONBOARDING
import com.example.notetakingapp.util.Constants.PREF_NAME
import com.example.notetakingapp.viewmodel.NoteViewModel
import com.example.notetakingapp.viewmodel.ViewModelFactory


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

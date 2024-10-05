package com.example.notetakingapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.notetakingapp.databinding.ActivityMainBinding
import com.example.notetakingapp.repository.NotesRepository
import com.example.notetakingapp.room.NotesDao
import com.example.notetakingapp.room.NotesDatabase
import com.example.notetakingapp.viewmodel.NoteViewModel
import com.example.notetakingapp.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public lateinit var myViewmodel:NoteViewModel
    private lateinit var database :NotesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

             // Intilialize the database
        database=NotesDatabase.getInstance(this)


        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        }

    private fun setUpViewModel() {
        
        // establishing the connection between the database and the repository
        val dao = NotesDatabase.getInstance(applicationContext).db
        val  notesRepository = NotesRepository(dao)
        // establishing the connection between the viewmodel factory and the repository.
        val viewmodelFactory = ViewModelFactory(notesRepository)
        //establishing the connection between the viewmodel factory and the view model.
        myViewmodel=ViewModelProvider(this ,viewmodelFactory).get(NoteViewModel::class.java)

    }
}

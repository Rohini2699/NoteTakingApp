package com.note.notetakingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.note.notetakingapp.repository.NotesRepository

class ViewModelFactory (private val repo: NotesRepository) : ViewModelProvider.Factory{
    // this is a class which is used to instantiate and
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteViewModel::class.java)){
            return NoteViewModel(repo) as  T
            // here we are creating a contact viewmodel in  create fun
        }
        throw IllegalArgumentException("Unknown view model class")


    }

}





package com.example.notetakingapp.viewmodel


import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetakingapp.repository.NotesRepository
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.room.Priority
import kotlinx.coroutines.launch


class NoteViewModel(private val repo: NotesRepository) : ViewModel(), Observable {

    // LiveData from the repository
    val users: LiveData<List<Notes>> = repo.allnotes

    private val _allNotesMutableList = MutableLiveData<List<Notes>>(emptyList())
    val allNotes: LiveData<List<Notes>> = _allNotesMutableList

    // MutableLiveData for internal use
    private val _mutableNotes = MutableLiveData<List<Notes>>()

    // Function to initialize the MutableLiveData
    private fun initializeMutableNotes() {
        val currentNotes = repo.allnotes.value?.toMutableList() ?: emptyList()
        _mutableNotes.value = currentNotes
        _allNotesMutableList.value = currentNotes
    }

    private var _selectedNotesList = MutableLiveData<MutableList<Notes>>(mutableListOf())
    var selectedNotesList: LiveData<MutableList<Notes>> = _selectedNotesList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    @Bindable
    val selectalltext = MutableLiveData<String>()

    @Bindable
    val selectedcounttext = MutableLiveData<String>()
    var hasSelectedAll = false

    init {
        selectalltext.value = "SelectAll"
        selectedcounttext.value = "No items are selected "
        initializeMutableNotes()
    }

    fun addNote(notes: Notes) = viewModelScope.launch {
        // this ensured that the coroutines will be cancelled when the association viewmodel is destroyed or cancelled.
        repo.insertNote(notes)
    }

    fun updateNotes(notes: Notes) = viewModelScope.launch {
        repo.update(notes)
    }

    fun deleteNotes(notes: Notes) = viewModelScope.launch {
        repo.delete(notes)
    }

    fun deselect() = viewModelScope.launch {
        repo.deselectall()
        _selectedNotesList.value = mutableListOf()
        val currentList = getCurrentList()
        val newList = currentList.map { note -> note.copy(isSelected = true) }
        _allNotesMutableList.value = newList
    }

    fun selectall() = viewModelScope.launch {

        if (selectalltext.value == "SelectAll") {

            // Perform update operation
            updatecount()
//            resetSelectedList()
            updateAllNotesList()
            // pinSelectedNotes()
            // Change the value to "DeleteAll"
            selectalltext.value = "DeselectAll"
        } else if (selectalltext.value == "DeselectAll") {
            // Perform delete operation
            deselect()
            selectedcounttext.value = "No items are selected"
            // Change the value to "SelectAll"
            selectalltext.value = "SelectAll"
        } else {
            // Handle unexpected state if needed
            Log.e("MyViewModel", "Unexpected state: ${selectalltext.value}")
        }
//         else
//         {
//             deleteallnotes()
//             selectalltext.value="SelectAll"
//         }
    }

    private fun updateAllNotesList() {
        val currentList = getCurrentList()
        val newList = currentList.map { note -> note.copy(isSelected = true) }
        _allNotesMutableList.value = newList
    }

    fun updatecount() = viewModelScope.launch {

        val rowscount = repo.updateall()
        selectedcounttext.value = "$rowscount  items(s) selected"
        _selectedNotesList.value = users.value?.toMutableList() ?: mutableListOf()
    }

    fun updateselectedcount() {
        /* val count = selectctedNotesList.value?.let { currentList ->
             val updatedList = currentList.toMutableList() // Copy the list
             updatedList.count() // Return the count of the list
         } ?: 0  // Return 0 if selectedlist.value is null
         */

        val selectedNoteCount = selectedNotesList.value?.count() ?: 0
        selectedcounttext.value = "$selectedNoteCount item(s)selected"
    }

    // Assuming selectedlist is MutableLiveData<List<T>>
    fun resetSelectedList() {
        _selectedNotesList.value = mutableListOf() // Assign an empty list to reset
    }

    private fun addToSelectedList(note: Notes) {
        //        _selectedNotesList.value?.add(note)
        val currentList = selectedNotesList.value ?: mutableListOf()
        currentList.add(note)
        _selectedNotesList.value = currentList
        Log.d("CurrentListSize", "AddToList: ${selectedNotesList.value?.size}")
    }

    private fun removeFromSelectedList(note: Notes) {
//        _selectedNotesList.value?.remove(note)
        val currentList = selectedNotesList.value ?: mutableListOf()
        currentList.removeIf { it.id == note.id }
        _selectedNotesList.value = currentList
        Log.d("CurrentListSize", "RemoveFromList: ${selectedNotesList.value?.size}")
    }

    fun setNotesList(notesList: List<Notes>) {
        _allNotesMutableList.value = notesList
    }

    fun selectNoteAtIndex(index: Int) {
        val currentList = getCurrentList()
        if (currentList.isNotEmpty()) {
            val note = currentList[index]
            val newList = currentList.map {
                if (it.id == note.id) {
                    val isSelected = !note.isSelected
                    if (isSelected) {
                        addToSelectedList(note)
                        updateselectedcount()
                    } else {
                        removeFromSelectedList(note)
                        updateselectedcount()
                    }
                    note.copy(isSelected = isSelected)
                } else it
            }
            _allNotesMutableList.value = newList
        }

        /*      val currNotes = users.value?.toMutableList() ?: return
                val note = currNotes[index]
                note.isSelected = !note.isSelected // Toggle selection state

                if (selectctedNotesList.value == null) {
                    _selectctedNotesList.value = mutableListOf()
                }
                if (note.isSelected) {
                    addToSelectedList(note)
                    updateselectedcount()
                } else {
                    removeFromSelectedList(note)
                    updateselectedcount()
                }*/
    }

    fun pinSelectedNotes() {
        val currentList = getCurrentList()
        val newList = currentList.map { note ->
            if (note.isSelected) {
                if(note.isPinned) {
                    Log.d("SelectedNotes", "IF Block: ")
                    note.copy(isPinned = false)
                }
                else
                {
                    note.copy(isPinned = true)
                }

            } else {
                Log.d("SelectedNotes", "Else Block: ")
                note
            }
        }

        _allNotesMutableList.value = newList
        Log.d("CurrentListSize", "Pinned List Size: ${newList}")

//        val pinnotes = _selectedNotesList.value ?: return
//            pinnotes.forEach{ notes -> notes.isPinned = true
//             Log.d("pinnotes" ,"pinnotes: ${pinnotes.size}")
//            }
//        pinnotes.onEach { it.isPinned = true }
//        _selectedNotesList.value = pinnotes
//        val currNotes = users.value?.toMutableList() ?: return
//        for (note in currNotes) {
//            if (note.isSelected) {
//                println("Selected Note: ${note.title}")
//            } else {
//                println("Unselected Note: ${note.title}")
//            }
//        }
    }
    fun deleteNotesById(){
        viewModelScope.launch {
            val selectedNotes = selectedNotesList.value
            selectedNotes?.map {
                    notes-> repo.deleteNotesById(notes.id)
                val currentList = getCurrentList().toMutableList()
                currentList.removeIf { it.id == notes.id }
                _allNotesMutableList .value=currentList

            }
            updatecount()


        }
    }

    fun setPriorityForSelectedNotes(priority: Priority) {
        val currentList = getCurrentList()
        if (currentList.isNotEmpty()) {
            val newList = currentList.map {
                if (it.isSelected) {
                    it.copy(priority = priority)
                } else {
                    it
                }
            }
            _allNotesMutableList.value = newList
        }


        /*// Update the priority of selected notes in _selectedNotesList
        val selectedNotes = _selectedNotesList.value ?: return
        val updatedSelectedNotes = selectedNotes.map { note ->
            if (note.isSelected) {
                note.copy(priority = priority)  // Update priority
            } else {
                note  // Keep the note as it is if not selected
            }
        }
        _selectedNotesList.value = updatedSelectedNotes.toMutableList()
        // Update the priority of selected notes in the main users list
        val currNotes = users.value?.toMutableList() ?: return
        val updatedNotes = currNotes.map { note ->
            if (note.isSelected) {
                note.copy(priority = priority)  // Update priority for selected notes
            } else {
                note  // Keep the other notes unchanged
            }
        }

        // Update the main list of notes
        _mutableNotes.value = updatedNotes*/
    }

    // This method will filter notes based on the selected priority
    fun filterNotesByPriority(priority: Priority) {
        val currNotes = getCurrentList()
        val filteredNotes = currNotes.filter { it.priority == priority }

        // Update the filtered list in LiveData so the UI can show only the filtered notes
        _mutableNotes.value = filteredNotes.toMutableList()

    }
//
//    fun setPriorityForSelectedNotes(priority:Priority){
//        val currNotes = users.value?.toMutableList() ?: return
//         val updatednotes=currNotes.map { notes: Notes ->
//             if (notes.isSelected)
//             {
//                 notes.copy(priority=priority)
//             }
//             else
//             {
//                 notes
//             }
//
//
//         }
//        updatednotes
//
//
//    }
//


    // updateNotes(note)
    // Update LiveData
    // selected notes is not needed to persist in the db . becoz whenever user close and opens the app its not needed to persist.


//    fun updatecount() =viewModelScope.launch {
//        val count = repo.updateall()
//        selectedcounttext.value = "$count item(s)selected"
//    }
//fun markSelectedItem(index: Int): Boolean {
//    // Launch in background thread (Dispatchers.IO)
//    viewModelScope.launch(Dispatchers.IO) {
//        try {
//            users.value?.let { currentList ->
//                val updatedList = currentList.toMutableList() // Copy the list
//                val selectedItem = updatedList[index]
//                // Toggle the selection state
//                selectedItem.isSelected = !selectedItem.isSelected
//
//                // Update the item in the database (persist changes)
//                updateNotes(selectedItem)
//                Log.d("ViewModel", "Item updated successfully")
//            }
//        } catch (e: Exception) {
//            Log.e("ViewModel", "Error updating item: ${e.message}")
//        }
//    }
//    return true // Returning a boolean value (can be adjusted based on use case)


//            val selectedItem = differ.currentList[index]
//            selectedItem.isSelected=!selectedItem.isSelected
//        notifyDataSetChanged()
//        return true
//        }


    fun searchnotes(query: String?) = repo.searchNote(query)

    // Observable property registry
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    fun removeSelection() {
        _selectedNotesList.value = mutableListOf()
        val newList = getCurrentList().map {
            it.copy(isSelected = false)
        }
        _allNotesMutableList.value = newList
    }

    private fun getCurrentList() = allNotes.value?.toMutableList() ?: mutableListOf()
}




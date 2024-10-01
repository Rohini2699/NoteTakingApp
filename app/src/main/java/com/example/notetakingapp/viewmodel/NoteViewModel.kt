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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NoteViewModel( private val repo: NotesRepository) : ViewModel(), Observable {

    // LiveData from the repository
    val users: LiveData<List<Notes>> = repo.allnotes

    // MutableLiveData for internal use
    private val _mutableNotes = MutableLiveData<List<Notes>>()

    // Function to initialize the MutableLiveData
    fun initializeMutableNotes() {
        val currentNotes = users.value?.toMutableList() ?: mutableListOf()
        _mutableNotes.value = currentNotes
    }

    private var _selectctedNotesList:MutableLiveData<MutableList<Notes>> = MutableLiveData<MutableList<Notes>>()
    var selectctedNotesList:LiveData<MutableList<Notes>> = _selectctedNotesList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage
       @Bindable
       val selectalltext = MutableLiveData<String>()
        @Bindable
        val selectedcounttext = MutableLiveData<String>()
    var hasSelectedAll = false
    init {
        selectalltext.value="SelectAll"
        selectedcounttext.value="No items are selected "
        initializeMutableNotes()
    }
    fun addNote(notes: Notes) = viewModelScope.launch {
        // this ensured that the coroutines will be cancelled when the association viewmodel is destroyed or cancelled.
       repo.insertNote(notes)
    }
    fun updateNotes(notes: Notes)=viewModelScope.launch {
        repo.update(notes)
    }
    fun deleteNotes(notes: Notes)=viewModelScope.launch {
        repo.delete(notes)
    }
    fun deselect() =viewModelScope.launch {
           repo.deselectall()
        _selectctedNotesList.value = mutableListOf()
    }
     fun selectall() = viewModelScope.launch{

         if (selectalltext.value == "SelectAll") {

             // Perform update operation
                 updatecount()
                 resetSelectedList()
                // pinSelectedNotes()
             // Change the value to "DeleteAll"
             selectalltext.value = "DeselectAll"
         } else if (selectalltext.value == "DeselectAll") {
             // Perform delete operation
                  deselect()
             selectedcounttext.value="No items are selected"
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
    fun updatecount()= viewModelScope.launch {

        val rowscount = repo.updateall()
        selectedcounttext.value = "$rowscount  items(s) selected"
        _selectctedNotesList.value = users.value?.toMutableList() ?: mutableListOf()
    }
    fun updateselectedcount() {

        val count =selectctedNotesList.value?.let { currentList ->
            val updatedList = currentList.toMutableList() // Copy the list
            updatedList.count() // Return the count of the list

        } ?: 0  // Return 0 if selectedlist.value is null
        selectedcounttext.value = "$count item(s)selected"

    }
    // Assuming selectedlist is MutableLiveData<List<T>>
    fun resetSelectedList() {
        _selectctedNotesList.value= mutableListOf() // Assign an empty list to reset
    }

    fun addToSelectedList(note: Notes) {
        _selectctedNotesList.value?.add(note)
        _selectctedNotesList.value = _selectctedNotesList.value // Trigger LiveData update
    }

    fun removeFromSelectedList(note: Notes) {
        _selectctedNotesList.value?.remove(note)
        _selectctedNotesList.value = _selectctedNotesList.value // Trigger LiveData update
    }

    fun selectNoteAtIndex(index: Int) {
       val currNotes = users.value?.toMutableList() ?: return
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
        }

    }
        fun pinSelectedNotes(){
            Log.d("IsPinned", "Selected note Size: ${selectctedNotesList.value?.size}")
            val pinnotes = _selectctedNotesList.value ?:return
//            pinnotes.forEach{ notes -> notes.isPinned = true
//             Log.d("pinnotes" ,"pinnotes: ${pinnotes.size}")
//            }
            pinnotes.onEach { it.isPinned = true }
            _selectctedNotesList.value = pinnotes
            val currNotes = users.value?.toMutableList() ?: return
            for (note in currNotes)
            {
                if (note.isSelected)
                {
                    println("Selected Note: ${note.title}")
                }
                else
                {
                    println("Unselected Note: ${note.title}")
                }
            }


        }

    fun setPriorityForSelectedNotes(priority: Priority) {
        // Update the priority of selected notes in _selectedNotesList
        val selectedNotes = _selectctedNotesList.value ?: return
        val updatedSelectedNotes = selectedNotes.map { note ->
            if (note.isSelected) {
                note.copy(priority = priority)  // Update priority
            } else {
                note  // Keep the note as it is if not selected
            }
        }
        _selectctedNotesList.value = updatedSelectedNotes.toMutableList()
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
        _mutableNotes.value=updatedNotes
    }
    // This method will filter notes based on the selected priority
    fun filterNotesByPriority(priority: Priority) {
        val currNotes = users.value?.toMutableList() ?: return
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



    fun searchnotes(query:String?)  = repo.searchNote(query)
    // Observable property registry
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}




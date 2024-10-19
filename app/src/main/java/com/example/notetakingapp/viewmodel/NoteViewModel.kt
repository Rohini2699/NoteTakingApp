package com.example.notetakingapp.viewmodel


import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetakingapp.repository.NotesRepository
import com.example.notetakingapp.room.Note
import com.example.notetakingapp.room.Priority
import kotlinx.coroutines.launch


class NoteViewModel(private val repo: NotesRepository) : ViewModel(), Observable {

    // LiveData from the repository
    val users: LiveData<List<Note>> = repo.allnotes
    val selectedListOfNotes: LiveData<List<Note>> = repo.selectednotes

    private val _allNotesMutableList = MutableLiveData<List<Note>>(emptyList())
    val allNotes: LiveData<List<Note>> = _allNotesMutableList

    private var _selectedNotesList = MutableLiveData<List<Note>>(emptyList())
    var selectedNotesList: LiveData<List<Note>> = _selectedNotesList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    @Bindable
    val selectAllText = MutableLiveData<String>()

    @Bindable
    val selectedCountText = MutableLiveData<String>()

    // MutableLiveData for internal use
    private val _mutableNotes = MutableLiveData<List<Note>>()

    // Function to initialize the MutableLiveData
    private fun initializeMutableNotes() {
        val currentNotes = repo.allnotes.value?.toMutableList() ?: emptyList()
        _mutableNotes.value = currentNotes
        _allNotesMutableList.value = currentNotes
    }

    init {
        selectAllText.value = "SelectAll"
        selectedCountText.value = "No items are selected "
        initializeMutableNotes()
    }

    fun addNote(notes: Note) = viewModelScope.launch {
        // this ensured that the coroutines will be cancelled when the association viewmodel is destroyed or cancelled.
        repo.insertNote(notes)
    }

    fun updateNotes(notes: Note) = viewModelScope.launch {
        repo.update(notes)
    }

    fun deleteNotes(notes: Note) = viewModelScope.launch {
        repo.delete(notes)
    }

    fun selectAllNotes() = viewModelScope.launch {
        //Here changed to "when" block instead of "if-else" statement
        when (selectAllText.value) {
            "SelectAll" -> {
//                repo.updateAll(1)
                updateSelectedCount()
                updateAllNotesList(isSelectAll = true)
                selectAllText.value = "DeselectAll"
            }

            "DeselectAll" -> {
//                repo.updateAll(0)
                updateAllNotesList(isSelectAll = false)
                selectedCountText.value = "No items are selected"
                selectAllText.value = "SelectAll"
            }

            else -> {
                Log.e("MyViewModel", "Unexpected state: ${selectAllText.value}")
            }
        }
    }

    private fun updateAllNotesList(isSelectAll: Boolean) {
        val currentList = getCurrentList()
        val newList = currentList.map { note -> note.copy(isSelected = isSelectAll) }
        _allNotesMutableList.value = newList
        if (isSelectAll) {
            // Add all notes in selectedNotesList variable when user click on selectAll button
            _selectedNotesList.value = newList
        } else {
            // Set empty list when user click on deselect button
            _selectedNotesList.value = emptyList()
        }
    }

    private fun removeFromSelectedList(note: Note) {
        val mutableList = getSelectedNoteList()
        Log.d("CurrentListSize", "Size before removing element: ${mutableList.size}")
        mutableList.removeIf { it.id == note.id }
        Log.d("CurrentListSize", "Size after removing element: ${selectedNotesList.value?.size}")
        _selectedNotesList.value = mutableList
    }

    fun setNotesList(notesList: List<Note>) {
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
                        updateSelectedCount()
                    } else {
                        removeFromSelectedList(note)
                        updateSelectedCount()
                    }
                    note.copy(isSelected = isSelected)
                } else it
            }
            // Set "DeselectAll" text if there is only one note in the adapter and if it is selected
            // Or else all the notes in the adapter which are selected
            if (
                getSelectedNoteList().count() == (allNotes.value?.count() ?: 0)
            ) {
                selectAllText.value = "DeselectAll"
            } else {
                selectAllText.value = "SelectAll"
            }
            _allNotesMutableList.value = newList
        }
    }

    private fun addToSelectedList(note: Note) {
        val mutableList = getSelectedNoteList()
        Log.d("CurrentListSize", "Size before adding element: ${mutableList.size}")
        if (mutableList.contains(note).not()) { // Only add if the note is not in the list already
            mutableList.add(note)
            Log.d("CurrentListSize", "Size after adding element: ${selectedNotesList.value?.size}")
            _selectedNotesList.value = mutableList
        }
    }

    private fun updateSelectedCount() {
        val selectedNoteCount = getSelectedNoteList().count()
        selectedCountText.value = "$selectedNoteCount items selected"
    }


    //    fun pinSelectedNotes(isPinned: Boolean) {
//        viewModelScope.launch {
//            val selectedNotes = selectedNotesList.value
//            selectedNotes?.map { notes ->
//                // Save pin status in the repository
//                repo.savePinStatus(notes.id, isPinned)
//                Log.d("pinselectednotes", "pin notes $notes")
//                // Get the current list or initialize it as an empty list if null
//                val currentList = getCurrentList()?.toMutableList() ?: mutableListOf()
//                    currentList.add(notes)
//                // Update the LiveData with the new list
//                _allNotesMutableList.value = currentList
//                Log.d("currentlist", "currentlist $currentList")
//            }
//        }
//    }
    fun pinSelectedNotes(isPinned: Boolean) {
        viewModelScope.launch {
            val currentList = getCurrentList()
            val newList = currentList.map { note -> note.copy(isSelected = true) }
            Log.d("selectednotes", "$selectedNotesList")
            // val currentList = getCurrentList().toMutableList()
            newList?.map { notes ->
                repo.savePinStatus(notes.id, isPinned)
                if (notes.isPinned) {
                    notes.copy(isPinned = false)
                    Log.d("Selectednotes", "IF block")
                } else {
                    notes.copy(isPinned = true)
                    Log.d("Selectednotes", "else block")
                }
                _allNotesMutableList.value = newList
                Log.d("currentList", "currentList${selectedNotesList.value?.count()}")
            }
        }
    }


    fun deleteNotesById() {
        viewModelScope.launch {
            val selectedNotes = getSelectedNoteList()
            val currentList = getCurrentList()
            selectedNotes.map { notes ->
                repo.deleteNotesById(notes.id)
                currentList.removeIf { it.id == notes.id }
            }
            _allNotesMutableList.value = currentList
            updateSelectedCount()
        }
    }

    fun setPriorityForSelectedNotes(priority: Priority) {
        val currentList = getCurrentList()
        if (currentList.isNotEmpty()) {
            val newList = currentList.map {
                if (it.isSelected) {
                    if (it.priority == priority) {
                        Log.d("priotity", "$priority")
                        it.copy(priority = priority, isHighPriorityVisible = true)
                    } else {
                        it.copy(
                            priority = priority, isHighPriorityVisible = !it.isHighPriorityVisible
                        )
                    }
                } else {
                    it.copy(priority = null, isHighPriorityVisible = false)
                }
            }
            _allNotesMutableList.value = newList
        }

    }

    // This method will filter notes based on the selected priority
    fun filterNotesByPriority(priority: Priority) {
        val currNotes = getCurrentList()
        val filteredNotes = currNotes.filter { it.priority == priority }
        // Update the filtered list in LiveData so the UI can show only the filtered notes
        _mutableNotes.value = filteredNotes.toMutableList()

    }

    fun getCurrentPinStatus(noteId: Int): Boolean {
        return _allNotesMutableList.value?.find { it.id == noteId }?.isPinned ?: false
    }


    fun searchnotes(query: String?) = repo.searchNote(query)

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
    private fun getSelectedNoteList() = selectedNotesList.value?.toMutableList() ?: mutableListOf()

    //Pin and Unpin notes based on note selection
    fun pinUnpinNotes(isAlreadyPinned: Boolean) {
        val selectedNotes = getSelectedNoteList()
        if (isAlreadyPinned) {
            //Unpin notes
            if (selectedNotes.isNotEmpty()) {
                viewModelScope.launch {
                    val noteIds = selectedNotes.map { it.id }
                    repo.updateIsPinnedColumn(
                        noteIds = noteIds, // Ids list count must not be greater than 999
                        isPinned = 0
                    )
                    // Update AllNotes List with isPinned = false
                    _allNotesMutableList.value = allNotes.value?.map {
                        if (noteIds.contains(it.id)) {
                            it.copy(isPinned = false)
                        } else it
                    }?.toList() ?: emptyList()
                    // Set empty list when user click on deselect button
                    _selectedNotesList.value = emptyList()
                }
            }
        } else {
            //Pin notes
            viewModelScope.launch {
                val noteIds = selectedNotes.map { it.id }
                repo.updateIsPinnedColumn(
                    noteIds = noteIds, // Ids list count must not be greater than 999
                    isPinned = 1
                )
                // Update AllNotes List with isPinned = true
                _allNotesMutableList.value = allNotes.value?.map {
                    if (noteIds.contains(it.id)) {
                        it.copy(isPinned = true)
                    } else it
                }?.toList() ?: emptyList()
                // Set empty list when user click on deselect button
                _selectedNotesList.value = emptyList()
            }
        }
    }
}




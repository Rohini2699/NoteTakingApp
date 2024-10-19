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
import java.io.File


class NoteViewModel(private val repo: NotesRepository) : ViewModel(), Observable {
    // LiveData from the repository
    val users: LiveData<List<Note>> = repo.allnotes

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
                updateSelectedCount()
                updateAllNotesList(isSelectAll = true)
                selectAllText.value = "DeselectAll"
            }

            "DeselectAll" -> {
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
                        addNoteToSelectedList(note)
                        updateSelectedCount()
                    } else {
                        removeNoteFromSelectedList(note)
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

    private fun addNoteToSelectedList(note: Note) {
        val selectedNotes = getSelectedNoteList()
        if (selectedNotes.contains(note).not()) { // Add note if it is not already in the list
            selectedNotes.add(note)
            _selectedNotesList.value = selectedNotes
        }
    }

    private fun removeNoteFromSelectedList(note: Note) {
        val selectedNotes = getSelectedNoteList()
        selectedNotes.removeIf { it.id == note.id }
        _selectedNotesList.value = selectedNotes
    }

    private fun updateSelectedCount() {
        val selectedNoteCount = getSelectedNoteList().count()
        selectedCountText.value = "$selectedNoteCount items selected"
    }

    fun deleteNotesById() {
        viewModelScope.launch {
            val selectedNotes = getSelectedNoteList()
            val currentList = getCurrentList()
            selectedNotes.map { note ->
                repo.deleteNotesById(note.id)
                currentList.removeIf { it.id == note.id }
                // Delete image file from the internal storage
                if (note.imagePath.isNullOrEmpty().not()) {
                    val file = File(note.imagePath!!)
                    file.delete()
                }
            }
            _allNotesMutableList.value = currentList
            _selectedNotesList.value = emptyList()
            updateSelectedCount()
        }
    }

    fun setLowPriorityForSelectedNotes() {
        val selectedNotes = getSelectedNoteList()
        viewModelScope.launch {
            val noteIds = selectedNotes.map { it.id }
            repo.updatePriorityColumnInDB(
                noteIds = noteIds,
                priority = if (selectedNotes.first().priority == Priority.LOW) Priority.NONE else Priority.LOW,
                isHighPriorityVisible = 1
            )

            // Update AllNotes List with priority and isHighPriorityVisible
            _allNotesMutableList.value = allNotes.value?.map {
                if (noteIds.contains(it.id)) {
                    it.copy(priority = Priority.LOW, isHighPriorityVisible = true)
                } else it
            }?.toList() ?: emptyList()
            // Set empty list when user click on deselect button
            _selectedNotesList.value = emptyList()
        }
    }

    fun setHighPriorityForSelectedNotes() {
        val selectedNotes = getSelectedNoteList()
        viewModelScope.launch {
            val noteIds = selectedNotes.map { it.id }
            repo.updatePriorityColumnInDB(
                noteIds = noteIds,
                priority = if (selectedNotes.first().priority == Priority.HIGH) Priority.NONE else Priority.HIGH,
                isHighPriorityVisible = 1
            )

            // Update AllNotes List with priority and isHighPriorityVisible
            _allNotesMutableList.value = allNotes.value?.map {
                if (noteIds.contains(it.id)) {
                    it.copy(priority = Priority.HIGH, isHighPriorityVisible = true)
                } else it
            }?.toList() ?: emptyList()
            // Set empty list when user click on deselect button
            _selectedNotesList.value = emptyList()
        }
    }

    fun searchNotes(query: String?) = repo.searchNote(query)

    fun removeSelection() {
        _selectedNotesList.value = mutableListOf()
        val newList = getCurrentList().map {
            it.copy(isSelected = false)
        }
        _allNotesMutableList.value = newList
    }

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

    private fun getCurrentList() = allNotes.value?.toMutableList() ?: mutableListOf()
    private fun getSelectedNoteList() = selectedNotesList.value?.toMutableList() ?: mutableListOf()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}




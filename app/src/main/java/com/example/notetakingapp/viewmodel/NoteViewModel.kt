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
    val selectedListOfNotes:LiveData<List<Notes>> = repo.selectednotes
    private val _allNotesMutableList = MutableLiveData<List<Notes>>(emptyList())
    val allNotes: LiveData<List<Notes>> = _allNotesMutableList

    private val _allNotesSelectedMutableList = MutableLiveData<List<Notes>>(emptyList())
    val selectedNotes: LiveData<List<Notes>> = _allNotesSelectedMutableList

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
        _selectedNotesList.value = mutableListOf()
        val currentList = getCurrentList()
        val newList = currentList.map { note -> note.copy(isSelected = true) }
        _allNotesMutableList.value = newList
    }

    fun selectall() = viewModelScope.launch {
        if (selectalltext.value == "SelectAll") {
            repo.updateall(1)
            updatecount()
            updateAllNotesList()
            selectalltext.value = "DeselectAll"
        } else if (selectalltext.value == "DeselectAll") {
            repo.updateall(0)
            deselect()
            selectedcounttext.value = "No items are selected"
            selectalltext.value = "SelectAll"
        } else {
            Log.e("MyViewModel", "Unexpected state: ${selectalltext.value}")
        }
    }

    private fun updateAllNotesList() {
        val currentList = getCurrentList()
        val newList = currentList.map { note -> note.copy(isSelected = true) }
        _allNotesMutableList.value = newList
    }

    fun updatecount() = viewModelScope.launch {
        selectedcounttext.value = "${selectedNotes.value?.count()?:0}  items(s) selected"
        _selectedNotesList.value = users.value?.toMutableList() ?: mutableListOf()
    }

    fun updateselectedcount() {

        val selectedNoteCount = selectedNotesList.value?.count() ?: 0
        selectedcounttext.value = "$selectedNoteCount item(s)selected"
    }
    private fun addToSelectedList(note: Notes) {

        val currentList = selectedNotesList.value ?: mutableListOf()
        currentList.add(note)
        _selectedNotesList.value = currentList
        Log.d("CurrentListSize", "AddToList: ${selectedNotesList.value?.size}")
    }

    private fun removeFromSelectedList(note: Notes) {
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
            if (newList.count() == 1) {
                selectalltext.value = "DeselectAll"
            }
        }
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
    fun pinSelectedNotes(isPinned:Boolean){
        viewModelScope.launch {
            val currentList = getCurrentList()
            val newList = currentList.map { note -> note.copy(isSelected = true) }
            Log.d("selectednotes" ,"$selectedNotes")
           // val currentList = getCurrentList().toMutableList()
            newList?.map { notes ->
                repo.savePinStatus(notes.id, isPinned)
                    if(notes.isPinned) {
                        notes.copy(isPinned = false)
                        Log.d("Selectednotes", "IF block")
                    } else {
                        notes.copy(isPinned = true)
                        Log.d("Selectednotes", "else block")
                    }
                    _allNotesSelectedMutableList.value = newList
                    Log.d("currentList", "currentList$selectedNotes")
            }
        }
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
                    if (it.priority==priority) {
                        Log.d("priotity", "$priority")
                        it.copy(priority = priority, isHighPriorityVisible = true)
                    } else {
                        it.copy(
                            priority = priority,
                            isHighPriorityVisible = !it.isHighPriorityVisible
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

        fun getCurrentPinStatus(noteId:Int): Boolean {
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
    private fun getSelectedList() = selectedNotes.value?.toMutableList()?: mutableListOf()
}




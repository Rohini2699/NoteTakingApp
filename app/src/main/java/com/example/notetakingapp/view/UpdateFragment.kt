package com.example.notetakingapp.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentUpdateBinding
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.util.Utils.convertMillisToLocalDateTime
import com.example.notetakingapp.util.Utils.formatLocalDateTimeWithZoneId
import com.example.notetakingapp.viewmodel.NoteViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.ZoneId
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateFragment : Fragment(R.layout.fragment_update), MenuProvider {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!! // Assertion operator
    private lateinit var myViewModel: NoteViewModel
    private lateinit var currentNote: Notes

    //Since the update note fragment contains arguments in nav _graph
    private val args: UpdateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        myViewModel = (activity as MainActivity).myViewmodel
        currentNote = args.notes!!

        val noteDate = currentNote.date.orEmpty()
        if (noteDate.isNotEmpty()) {
            val localDateTime = convertMillisToLocalDateTime(noteDate.toLong())
            println(localDateTime)
            val formattedDate = formatLocalDateTimeWithZoneId(
                dateTime = localDateTime,
                zoneId = ZoneId.systemDefault()
            )
            binding.datetext.text = formattedDate
        }
        binding.editNoteTitle.setText(currentNote.title)
        binding.editNoteDesc.setText(currentNote.description)
        if (currentNote.imageArray != null) {
            val byteArray = currentNote.imageArray?.toByteArray()
            byteArray?.let { array ->
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                binding.image.setImageBitmap(bitmap)
            }
        }

        //if the user update the note
        binding.editNoteFab.setOnClickListener {
            val title = binding.editNoteTitle.text.toString().trim()
            val body = binding.editNoteDesc.text.toString().trim()
            if (title.isNotEmpty()) {
                val note = Notes(
                    currentNote.id,
                    title,
                    body,
                    currentNote.isSelected,
                    currentNote.isPinned,
                    (System.currentTimeMillis()).toString()
                )
                myViewModel.updateNotes(note)
                view.findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Unable to update ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete Note ")
            setMessage("you want to delete it ")
            setPositiveButton("Delete") { _, _ ->
                myViewModel.deleteNotes(currentNote)
                view?.findNavController()?.navigate(R.id.action_updateFragment_to_homeFragment)
            }
            setNegativeButton("cancel", null)
        }.create().show()
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
        if (galleryUri != null) {
            binding.image.setImageURI(galleryUri)
           val bitmap =  localImageUriToBitmap(requireContext(),galleryUri)
           val array = compressBitmapToByteArray(bitmap)
            myViewModel.saveImage(currentNote.id, array.toString())
        } else {
            binding.image.isVisible = false
        }
    }

    private fun voiceInput() {
        //language e.g. "en" for "English", "ur" for "Urdu", "hi" for "Hindi" etc.
        // val language = "en"

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            voiceInputArl.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val voiceInputArl = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val result =
                activityResult.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            val text = result!![0]

            binding.editNoteDesc.append(text)
        }
    }

    fun localImageUriToBitmap(context: Context, localImageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, localImageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, localImageUri)    }
    }
    fun compressBitmapToByteArray(originalBitmap: Bitmap, maxBytes: Long = 1_000_000): ByteArray {
        // Initialize the quality variable and the output stream
        var quality = 100
        var byteArrayOutputStream: ByteArrayOutputStream

        do {
            // Reset the stream for each attempt at a different quality level
            byteArrayOutputStream = ByteArrayOutputStream()

            // Compress the bitmap into the ByteArrayOutputStream at the current quality
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            // Reduce the quality for the next iteration if the current size exceeds maxBytes
            quality -= 5

        } while (byteArrayOutputStream.size() > maxBytes && quality > 0)

        // Return the byte array representing the compressed bitmap
        return byteArrayOutputStream.toByteArray()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_update_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_delete -> {
                deleteNote()
                true
            }

            R.id.menu_mic -> {
                voiceInput()
                true
            }

            R.id.menu_image -> {
               checkPermissionAndRequest()
                true
            }

            else -> false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, you can access the images now
            galleryLauncher.launch("image/*")
        } else {
            // Permission is denied, handle the case
            Toast.makeText(requireContext(), "Please give read images permission.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission is granted
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    galleryLauncher.launch("image/*")
                }

                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            // Handle permissions for Android versions below 13
            checkPermissionBelow13()
        }
    }

    private fun checkPermissionBelow13() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // For API levels below 13, request READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                }

                else -> {
                    // Request permission for API < 13
                    val permissionsToRequest = mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                    // If API level is 28 or below, request WRITE_EXTERNAL_STORAGE as well
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }

                    // Launch permission request
                    requestPermissionLauncherBelow13.launch(permissionsToRequest.toTypedArray())
                }
            }
        }
    }

    // Register the permissions request callback for multiple permissions
    private val requestPermissionLauncherBelow13 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the permission result
        when {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> {
                // READ_EXTERNAL_STORAGE granted
                galleryLauncher.launch("image/*")
            }

            else -> {
                // Permission denied, handle accordingly
                Toast.makeText(requireContext(), "Please give read images permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




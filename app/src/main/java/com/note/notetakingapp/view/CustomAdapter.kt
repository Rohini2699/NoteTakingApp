package com.note.notetakingapp.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.note.notetakingapp.R
import com.note.notetakingapp.databinding.NoteLayoutBinding
import com.note.notetakingapp.room.Note
import com.note.notetakingapp.room.Priority
import com.note.notetakingapp.util.Utils.convertMillisToLocalDateTime
import com.note.notetakingapp.util.Utils.formatLocalDateTimeWithZoneId
import java.time.ZoneId
import java.util.Random

class CustomAdapter(private val listener: NoteClickListener) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    interface NoteClickListener {
        fun onItemClick(notes: Note, v: View, isLongClick: Boolean)
        fun onLongClicked(position: Int, notes: Note)
    }

    inner class MyViewHolder(val binding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun bind(notes: Note, position: Int) {
            binding.notetitle.text = notes.title
            binding.description.text = notes.description
            binding.button.isChecked = notes.isSelected
            if (notes.date?.isNotEmpty() == true) {
                val localDateTime = convertMillisToLocalDateTime(notes.date.toLong())
                val formattedDate =
                    formatLocalDateTimeWithZoneId(localDateTime, zoneId = ZoneId.systemDefault())
                binding.datetext.text = formattedDate
            } else {
                binding.datetext.isVisible = false
            }

            binding.cardview.setOnClickListener {
                listener.onItemClick(notes, binding.cardview, false)
            }
            binding.cardview.setOnLongClickListener {
                listener.onLongClicked(position, notes)
                true
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
            // it is used to update the contents without modifying the entire dataset .
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: NoteLayoutBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.note_layout, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNotes = differ.currentList[position]

        holder.bind(currentNotes, position)
        if (currentNotes.isSelected) {
            holder.binding.button.visibility = View.VISIBLE
        } else {
            holder.binding.button.visibility = View.GONE
        }

        val random = Random()
        val alpha = (128..255).random()
        val colors = Color.argb(
            alpha,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )

        holder.binding.card.setBackgroundColor(colors)

        if (currentNotes.isPinned) {
            holder.binding.pin.visibility = View.VISIBLE

        } else {
            holder.binding.pin.visibility = View.GONE
        }

        if (currentNotes.priority != Priority.NONE) {
            if (currentNotes.priority ==  Priority.LOW) {
                holder.binding.lowpriority.isVisible = currentNotes.isHighPriorityVisible
                holder.binding.cardview.setBackgroundColor(Color.GRAY)
                holder.binding.highpriority.isVisible = false
            } else {
                holder.binding.highpriority.isVisible = currentNotes.isHighPriorityVisible
                holder.binding.cardview.setBackgroundColor(colors)
                holder.binding.lowpriority.isVisible = false
            }
        } else {
            holder.binding.lowpriority.isVisible = false
            holder.binding.highpriority.isVisible = false
            holder.binding.cardview.setBackgroundColor(Color.WHITE)
        }
    }
}








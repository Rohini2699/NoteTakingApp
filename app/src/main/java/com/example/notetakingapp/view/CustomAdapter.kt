package com.example.notetak


import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.NoteLayoutBinding
import com.example.notetakingapp.room.Notes
import com.example.notetakingapp.room.Priority
import com.example.notetakingapp.util.Utils.convertMillisToLocalDateTime
import com.example.notetakingapp.util.Utils.formatLocalDateTimeWithZoneId
import java.time.ZoneId
import java.util.Random

class CustomAdapter(private val notes: List<Notes>, private val listener: NoteClickListener) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    //private var isHighPriorityVisible = false

    interface NoteClickListener {
        fun onItemClick(notes: Notes, v: View, isLongClick: Boolean)
        fun onLongClicked(position: Int, notes: Notes)
       // fun onPinClick(position: Int ,)

    }

    inner class MyViewHolder(val binding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        private varprivate lateinit var currentNotes: Notes
//         isLongClickTriggered = false
        /**
        init {

        itemView.setOnLongClickListener(this)
        itemView.setOnClickListener(this)
        }
         **/
//        @SuppressLint("SuspiciousIndentation")
//        override fun onLongClick(v: View?): Boolean {
//            isLongClickTriggered=true
//          val position=bindingAdapterPosition
//            if (position !=RecyclerView.NO_POSITION)
//            {
//                listener.onLongClicked(position)
//                return true
//            }
//            return false
//        }
        @SuppressLint("SuspiciousIndentation")
        fun bind(notes: Notes, position: Int) {
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
//        override fun onClick(v: View?) {
//            v?.let {
//                listener.onItemClick(currentNotes, it , true)
//            }
//
//        }
    }

    // viewholder class is basically used for attatching the layout xml files
    //var currentSelectedIndex = -1
    //private var isLongClickTriggered = false
    private val differCallback = object : DiffUtil.ItemCallback<Notes>() {
        override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            Log.d("DiffUtil", "Comparing items: ${oldItem.id} with ${newItem.id}")
            return oldItem.id == newItem.id
            // it is used to update the contents without modifying the entire dataset .
        }

        override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            Log.d("DiffUtil", "Comparing contents: ${oldItem} with ${newItem}")
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // val layoutInflater = LayoutInflater.from(parent.context)
        //   DataBindingUtil.inflate(layoutInflater, R.layout.card_item, parent, false)
        val layoutinflator = LayoutInflater.from(parent.context)
        val binding: NoteLayoutBinding =
            DataBindingUtil.inflate(layoutinflator, R.layout.note_layout, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNotes = differ.currentList[position]
//        Log.d("NoteAdapter", "Binding note: ${currentNotes.title}, position: $position")
//        Log.d("NoteAdapter", "Binding note is pin : ${currentNotes.isPinned}, position: $position")
        Log.d(
            "NoteAdapter",
            "Binding note is selected : ${currentNotes.isSelected}, position: $position"
        )
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

        if (currentNotes.priority != null) {
            if (currentNotes.priority ==  Priority.LOW) {
                holder.binding.lowpriority.isVisible = currentNotes.isHighPriorityVisible
                holder.binding.highpriority.isVisible = false
            } else {
                holder.binding.highpriority.isVisible = currentNotes.isHighPriorityVisible
                holder.binding.lowpriority.isVisible = false
            }
        } else {
            holder.binding.lowpriority.isVisible = false
            holder.binding.highpriority.isVisible = false
        }
    }

//    fun markselecteditem(index: Int): Boolean {
//            val selectedItem = differ.currentList[index]
//            selectedItem.isSelected=!selectedItem.isSelected
//        notifyDataSetChanged()
//        return true
//        }
//        fun deselectedItem(index: Int) {
//
//           if(currentSelectedIndex == index){
//               currentSelectedIndex=-1
//               differ.currentList.get(index).isSelected=false
//               notifyDataSetChanged()
//           }
//        }

}








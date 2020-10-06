package lv.db.mobile.rtu_android.keep.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.keep_item.view.*
import lv.db.mobile.rtu_android.keep.AdapterClickListener
import lv.db.mobile.rtu_android.keep.R
import lv.db.mobile.rtu_android.keep.database.KeepNote
import java.io.File

class KeepNotesRecyclerAdapter(
    private val listener: AdapterClickListener,
    private val notes: MutableList<KeepNote>
) :
    RecyclerView.Adapter<KeepNotesRecyclerAdapter.KeepNotesViewHolder>() {

    class KeepNotesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeepNotesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.keep_item, parent, false)
        return KeepNotesViewHolder(
            view
        )
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: KeepNotesViewHolder, position: Int) {
        val note = notes[position]
        holder.itemView.keepTitle.text = note.title
        holder.itemView.keepText.text = note.text
        holder.itemView.setBackgroundColor(
            when (note.color_id) {
                1 -> Color.GREEN
                2 -> Color.YELLOW
                3 -> Color.parseColor("#FF7F00")
                4 -> Color.GRAY
                else -> Color.parseColor("#E3F2FD")
            }
        )

        if (!note.image_path.isNullOrEmpty()) {
            val imageFile = File(note.image_path)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                holder.itemView.keepPicture.setImageBitmap(bitmap)
            }
        } else {
            holder.itemView.keepPicture.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            listener.itemClicked(notes[position])
        }

    }
}
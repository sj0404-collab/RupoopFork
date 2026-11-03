package com.santiago43rus.rupoop.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.santiago43rus.rupoop.data.SearchResult

class VideoAdapter(private val onClick: (SearchResult) -> Unit) :
    RecyclerView.Adapter<VideoAdapter.VH>() {

    private val items = mutableListOf<SearchResult>()

    fun submit(list: List<SearchResult>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.author.text = item.author?.name ?: ""
        item.thumbnailUrl?.let { holder.thumb.load(it) }
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
        val thumb: ImageView = v.findViewById(R.id.thumb)
    }
}

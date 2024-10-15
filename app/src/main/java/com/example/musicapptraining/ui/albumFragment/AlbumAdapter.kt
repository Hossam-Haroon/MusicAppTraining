package com.example.musicapptraining.ui.albumFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.databinding.AlbumsRvItemBinding

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    class AlbumViewHolder(val binding : AlbumsRvItemBinding): ViewHolder(binding.root) {

    }

    private val diffUtil = object : ItemCallback<Album>(){
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.albumID == newItem.albumID
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

    }

    var asyncListDiffer = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = AlbumsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlbumViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = asyncListDiffer.currentList[position]

        holder.binding.albumName.text = album.albumName
        holder.binding.albumImage.setImageResource(album.albumID.toInt())
        holder.binding.albumArtist.text = album.albumCreator
        holder.itemView.setOnClickListener {
            onClickListener?.let { it(album) }
        }
    }

    private var onClickListener :((Album)->Unit)? = null
    fun setOnItemClickListener(listener:(Album)->Unit){
        onClickListener = listener
    }
}
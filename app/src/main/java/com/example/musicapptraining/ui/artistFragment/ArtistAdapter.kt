package com.example.musicapptraining.ui.artistFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.databinding.ArtistsRvItemBinding

class ArtistAdapter: RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {
    class ArtistViewHolder(val binding: ArtistsRvItemBinding): ViewHolder(binding.root) {

    }

    val diffUtil = object: ItemCallback<Artist>(){
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.artistName == newItem.artistName
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }

    }

    var asyncListDiffer = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ArtistsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtistViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = asyncListDiffer.currentList[position]

        holder.binding.playlistSongsCount.text = artist.artistName
        holder.itemView.setOnClickListener {
            onClickListener?.let {
                it(artist)
            }
        }
    }

    var onClickListener : ((Artist)->Unit)? = null

    fun setOnItemClickListener(listener:(Artist)->Unit){
        onClickListener = listener
    }
}
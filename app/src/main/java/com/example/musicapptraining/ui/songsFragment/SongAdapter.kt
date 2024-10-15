package com.example.musicapptraining.ui.songsFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.SongsRvItemBinding

class SongAdapter(

): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    class SongViewHolder(val binding : SongsRvItemBinding): ViewHolder(binding.root) {

    }


    private val diffUtil = object :ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songId == newItem.songId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }

    val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = asyncListDiffer.currentList[position]

        holder.binding.nonPlayedSongNameTv.text = song.songName
        holder.binding.nonPlayedSongArtistNameTv.text = song.songArtist
        holder.itemView.setOnClickListener {
            onClickListener?.let{
                it(song)
            }
        }
        holder.binding.nonPlayedMoreImage.setOnClickListener {
            onMoreClickListener?.let{
                it(song)
            }
        }


    }

    var onClickListener : ((Song)-> Unit)? = null

    fun setOnItemClickListener(listener:(Song)-> Unit){
        onClickListener = listener
    }

    var onMoreClickListener : ((Song)-> Unit)? = null

    fun setOnMoreButtonClickListener(listener: (Song) -> Unit){
        onMoreClickListener = listener
    }

}
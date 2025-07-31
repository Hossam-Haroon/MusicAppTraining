package com.example.musicapptraining.ui.fragments.songsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.musicapptraining.ui.baseAdapter.BaseAdapter
import com.example.musicapptraining.databinding.SongsRvItemBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.ui.baseAdapter.BaseViewHolder
import com.example.musicapptraining.utilities.BaseDiffCallback

class SongAdapter: BaseAdapter<Song, SongAdapter.SongViewHolder, SongsRvItemBinding>(
    BaseDiffCallback(
        itemsTheSame = {oldItem,newItem-> oldItem.songId == newItem.songId },
        contentsTheSame = {oldItem, newItem -> oldItem == newItem }
    )
){
    class SongViewHolder(binding : SongsRvItemBinding): BaseViewHolder<SongsRvItemBinding>(binding)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongsRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }
    override fun bind(item: Song, position: Int, binding: SongsRvItemBinding) {
        binding.apply {
            nonPlayedSongNameTv.text = item.songName
            nonPlayedSongArtistNameTv.text = item.songArtist
            root.setOnClickListener {
                onClickListener?.let{
                    it(item)
                }
            }
            nonPlayedMoreImage.setOnClickListener {
                onMoreClickListener?.let{
                    it(item)
                }
            }
        }
    }
    private var onClickListener : ((Song)-> Unit)? = null
    fun setOnItemClickListener(listener:(Song)-> Unit){
        onClickListener = listener
    }
    private var onMoreClickListener : ((Song)-> Unit)? = null
    fun setOnMoreButtonClickListener(listener: (Song) -> Unit){
        onMoreClickListener = listener
    }
}

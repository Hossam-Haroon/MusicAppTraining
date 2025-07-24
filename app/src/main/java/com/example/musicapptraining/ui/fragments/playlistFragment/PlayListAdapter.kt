package com.example.musicapptraining.ui.fragments.playlistFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.databinding.AddPlaylistRvItemBinding
import com.example.musicapptraining.databinding.PlaylistsRvItemBinding
import com.example.musicapptraining.utilities.BaseDiffCallback
import com.example.musicapptraining.utilities.PlaylistItem

class PlayListAdapter:
    ListAdapter<PlaylistItem, ViewHolder>(BaseDiffCallback<PlaylistItem>(
        itemsTheSame = {oldItem, newItem ->
            return@BaseDiffCallback when {
                oldItem is PlaylistItem.PlaylistContent
                        && newItem is PlaylistItem.PlaylistContent ->
                    oldItem.playlist.playlistName == newItem.playlist.playlistName
                oldItem is PlaylistItem.AddPlaylistButton
                        && newItem is PlaylistItem.AddPlaylistButton ->
                    true
                else -> false
            }
        },
        contentsTheSame = {oldItem, newItem ->
            return@BaseDiffCallback when {
                oldItem is PlaylistItem.PlaylistContent
                        && newItem is PlaylistItem.PlaylistContent ->
                    oldItem.playlist == newItem.playlist
                oldItem is PlaylistItem.AddPlaylistButton
                        && newItem is PlaylistItem.AddPlaylistButton ->
                    true
                else -> false
            }
        }
    )
    ){
    class PlayListViewHolder(val binding : PlaylistsRvItemBinding): ViewHolder(binding.root)
    class AddPlayListViewHolder(val binding : AddPlaylistRvItemBinding): ViewHolder(binding.root)
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PlaylistItem.PlaylistContent -> VIEW_TYPE_NORMAL
            is PlaylistItem.AddPlaylistButton -> VIEW_TYPE_SPECIAL
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == VIEW_TYPE_SPECIAL){
            val binding = AddPlaylistRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            AddPlayListViewHolder(binding)
        }else{
            val binding = PlaylistsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PlayListViewHolder(binding)
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PlaylistItem.PlaylistContent -> {
                val playlistHolder = holder as PlayListViewHolder
                playlistHolder.binding.apply {
                    playlistName.text = item.playlist.playlistName
                    playlistSongsCount.text = item.playlist.playlistSongs.size.toString()
                    root.setOnClickListener { onClickListener?.let {
                        it(item.playlist)
                    } }
                }
            }
            is PlaylistItem.AddPlaylistButton -> {
                val addPlaylistHolder = holder as AddPlayListViewHolder // Safe cast
                addPlaylistHolder.binding.root.setOnClickListener {
                    onNewPlayListClickListener?.invoke()
                }
            }
        }
    }
    private var onClickListener : ((PlayList)-> Unit)? = null
    fun setOnItemClickListener(listener:(PlayList)-> Unit){
        onClickListener = listener
    }
    private var onNewPlayListClickListener : (()-> Unit)? = null
    fun setOnNewPlaListClickListener(listener:()-> Unit){
        onNewPlayListClickListener = listener
    }
    companion object{
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_SPECIAL = 1
    }
}

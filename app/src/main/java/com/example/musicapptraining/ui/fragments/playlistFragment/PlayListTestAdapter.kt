package com.example.musicapptraining.ui.fragments.playlistFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.databinding.AddPlaylistRvItemBinding
import com.example.musicapptraining.databinding.PlaylistsRvItemBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class PlayListTestAdapter: RecyclerView.Adapter<ViewHolder>() {
    class PlayListViewHolder(val binding : PlaylistsRvItemBinding): ViewHolder(binding.root)
    class AddPlayListViewHolder(val binding : AddPlaylistRvItemBinding): ViewHolder(binding.root)
    val VIEW_TYPE_NORMAL = 0
    val VIEW_TYPE_SPECIAL = 1
    private val diffUtil = object: ItemCallback<PlayList>(){
        override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem.playlistName == newItem.playlistName
        }
        override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem == newItem
        }
    }
    var asyncListDiff = AsyncListDiffer(this,diffUtil)
    override fun getItemViewType(position: Int): Int {
        return if (position == asyncListDiff.currentList.size){
            VIEW_TYPE_SPECIAL
        }else{
            VIEW_TYPE_NORMAL
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
    override fun getItemCount(): Int {
        return asyncListDiff.currentList.size+1
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is PlayListViewHolder && position < asyncListDiff.currentList.size){
            val playLists = asyncListDiff.currentList[position]
            holder.binding.playlistName.text = playLists.playlistName
            holder.binding.playlistSongsCount.text = playLists.playlistSongs.size.toString()
            holder.itemView.setOnClickListener {
                onClickListener?.let {
                    it(playLists)
                }
            }
        }else if (holder is AddPlayListViewHolder){
            holder.itemView.setOnClickListener {
                onNewPlayListClickListener?.invoke()
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
}
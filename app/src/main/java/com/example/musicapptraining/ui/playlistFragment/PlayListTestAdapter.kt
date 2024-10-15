package com.example.musicapptraining.ui.playlistFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.databinding.PlaylistsRvItemBinding
import com.example.musicapptraining.ui.playlistFragment.PlayListAdapter.PlayListViewHolder

class PlayListTestAdapter: RecyclerView.Adapter<PlayListTestAdapter.PlayListTestViewHolder>() {
    class PlayListTestViewHolder (val binding : PlaylistsRvItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    private val diffUtil = object: ItemCallback<PlayList>(){
        override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem.playlistName == newItem.playlistName
        }

        override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem == newItem
        }

    }

    var asyncListDiff = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListTestViewHolder {
        val binding = PlaylistsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlayListTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayListTestViewHolder, position: Int) {
        val playLists = asyncListDiff.currentList[position]

        holder.binding.playlistName.text = playLists.playlistName
        holder.binding.playlistSongsCount.text = playLists.playlistSongs.size.toString()
        holder.itemView.setOnClickListener {
            onClickListener?.let {
                it(playLists)
            }
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiff.currentList.size
    }

    var onClickListener : ((PlayList)-> Unit)? = null

    fun setOnItemClickListener(listener:(PlayList)-> Unit){
        onClickListener = listener
    }


}
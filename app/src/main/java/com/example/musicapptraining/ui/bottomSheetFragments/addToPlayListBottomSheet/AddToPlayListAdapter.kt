package com.example.musicapptraining.ui.bottomSheetFragments.addToPlayListBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.musicapptraining.databinding.AddToPlaylistRvItemBinding
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.ui.baseAdapter.BaseAdapter
import com.example.musicapptraining.ui.baseAdapter.BaseViewHolder
import com.example.musicapptraining.utilities.BaseDiffCallback

class AddToPlayListAdapter :
    BaseAdapter<
            Playlist,
            AddToPlayListAdapter.AddToPlaylistViewHolder,
            AddToPlaylistRvItemBinding
            >(
    BaseDiffCallback(
        itemsTheSame = {oldItem, newItem -> oldItem.playlistName == newItem.playlistName },
        contentsTheSame = {oldItem, newItem -> oldItem == newItem }
    )
){
    class AddToPlaylistViewHolder(binding : AddToPlaylistRvItemBinding):
        BaseViewHolder<AddToPlaylistRvItemBinding>(binding)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToPlaylistViewHolder {
            val binding = AddToPlaylistRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return AddToPlaylistViewHolder(binding)
        }
    override fun bind(item: Playlist, position: Int, binding: AddToPlaylistRvItemBinding) {
        binding.apply {
            playListNameTv.text = item.playlistName
            root.setOnClickListener {
                onClickListener?.let {
                    it(item)
                }
            }
        }
    }
    private var onClickListener : ((Playlist)-> Unit)? = null
    fun setOnItemClickListener(listener:(Playlist)-> Unit){
        onClickListener = listener
    }
}

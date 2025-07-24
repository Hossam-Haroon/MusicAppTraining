package com.example.musicapptraining.ui.fragments.artistFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.databinding.ArtistsRvItemBinding
import com.example.musicapptraining.ui.baseAdapter.BaseAdapter
import com.example.musicapptraining.ui.baseAdapter.BaseViewHolder
import com.example.musicapptraining.utilities.BaseDiffCallback

class ArtistAdapter: BaseAdapter<Artist, ArtistAdapter.ArtistViewHolder,ArtistsRvItemBinding>(
    BaseDiffCallback(
        itemsTheSame = {oldItem, newItem -> oldItem.artistName == newItem.artistName },
        contentsTheSame = {oldItem, newItem -> oldItem == newItem }
    )
) {
    class ArtistViewHolder(binding: ArtistsRvItemBinding):
        BaseViewHolder<ArtistsRvItemBinding>(binding)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ArtistsRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArtistViewHolder(binding)
    }
    override fun bind(item: Artist, position: Int, binding: ArtistsRvItemBinding) {
        binding.apply {
            playlistSongsCount.text = item.artistName
            root.setOnClickListener{
                onClickListener?.let {
                    it(item)
                }
            }
        }
    }
    private var onClickListener : ((Artist)->Unit)? = null
    fun setOnItemClickListener(listener:(Artist)->Unit){
        onClickListener = listener
    }
}
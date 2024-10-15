package com.example.musicapptraining.ui.addToPlayListBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.AddToPlaylistRvItemBinding
import com.example.musicapptraining.databinding.SongsRvItemBinding

class AddToPlayListAdapter : RecyclerView.Adapter<AddToPlayListAdapter.AddToPlaylistViewHolder>() {
        class AddToPlaylistViewHolder(val binding : AddToPlaylistRvItemBinding): ViewHolder(binding.root) {

        }


        private val diffUtil = object : ItemCallback<PlayList>(){
            override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
               return oldItem.playlistName == newItem.playlistName
            }

            override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
                return oldItem == newItem
            }
        }

        val asyncListDiffer = AsyncListDiffer(this, diffUtil)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToPlaylistViewHolder {
            val binding = AddToPlaylistRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return AddToPlaylistViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return asyncListDiffer.currentList.size
        }

        override fun onBindViewHolder(holder: AddToPlaylistViewHolder, position: Int) {
            val playList = asyncListDiffer.currentList[position]

            holder.binding.playListNameTv.text = playList.playlistName

            holder.itemView.setOnClickListener {
                onClickListener?.let {
                    it(playList)
                }
            }
        }

        var onClickListener : ((PlayList)-> Unit)? = null

        fun setOnItemClickListener(listener:(PlayList)-> Unit){
            onClickListener = listener
        }
}

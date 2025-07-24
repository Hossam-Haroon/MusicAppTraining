package com.example.musicapptraining.ui.baseAdapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<V:ViewBinding>(val binding:V):ViewHolder(binding.root)

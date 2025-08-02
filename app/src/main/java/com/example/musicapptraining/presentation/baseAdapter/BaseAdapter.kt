package com.example.musicapptraining.presentation.baseAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T,VH: BaseViewHolder<VB>,VB:ViewBinding>(
    diffCallback:ItemCallback<T>
):ListAdapter<T,VH>(diffCallback){
    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun bind(item:T,position: Int,binding: VB)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        bind(item,position, holder.binding)
    }
}




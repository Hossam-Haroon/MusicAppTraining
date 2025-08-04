package com.example.musicapptraining.utilities

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

inline fun <reified T:Parcelable> Bundle.getParcelableCompat(key:String): T?{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        getParcelable(key,T::class.java)
    }else{
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}
fun RecyclerView.setAdapterData(
    adapter : RecyclerView.Adapter<*>,
    hasFixedSize : Boolean = true
)= apply{
    this.adapter = adapter
    layoutManager = LinearLayoutManager(context)
    setHasFixedSize(hasFixedSize)
}
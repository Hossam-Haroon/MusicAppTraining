package com.example.musicapptraining.presentation.fragments.homeFragment

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.NavController
import com.example.musicapptraining.R

class MenuClickHandler(
    private val context: Context,
    private val navController: NavController
) {
    fun showMenuForMoreOptions(view : View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_items, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {item->
            setCorrectOrderForEveryItemId(item)
        }
        popupMenu.show()
    }
    private fun setCorrectOrderForEveryItemId(item: MenuItem):Boolean{
        return when(item.itemId){
            R.id.find_local_songs -> {
                navController.navigate(
                    R.id.action_homeFragment_to_scanLocalAudiosFromDeviceFragment
                )
                true
            }
            R.id.settings ->{
                true
            }
            else -> false
        }
    }
}
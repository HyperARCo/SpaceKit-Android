package com.dentreality.spacekit.sample

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SpaceKitViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "SpaceKitViewModel"
    }

    private val context: Context get() = getApplication<Application>().applicationContext
    private val productDatabase: ProductDatabase by lazy { ProductDatabase(context) }
    private val shoppingList: ShoppingList by lazy { ShoppingList(productDatabase) }

    fun loadData() {
        Log.i(TAG, "loadData()")
        viewModelScope.launch {
            Log.i(TAG, "original list:$shoppingList")
        }
    }

    fun onItemsOrdered(list: List<Product>) {
        Log.i(TAG, "onItemsOrdered($list)")
    }
}
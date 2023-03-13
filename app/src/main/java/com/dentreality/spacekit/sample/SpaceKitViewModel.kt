package com.dentreality.spacekit.sample

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpaceKitViewModel @Inject constructor(
    private val shoppingList: ShoppingList
) : ViewModel() {

    companion object {
        private const val TAG = "SpaceKitViewModel"
    }

    val listPool: MutableLiveData<ListPool> get() = shoppingList.poolListLiveData

    fun loadData() {
        Log.v(TAG, "loadData()")
        viewModelScope.launch {
            Log.i(TAG, "original list:$shoppingList")
        }
    }

    fun onItemsOrdered(list: List<Product>) {
        Log.v(TAG, "onItemsOrdered($list)")
        shoppingList.setOrderedShoppingList(list)
    }

    fun onListItemClicked(product: Product) {
        Log.v(TAG, "onListItemClicked(product:$product)")
        shoppingList.removeFromShoppingList(product)
    }

    fun onPoolItemClicked(product: Product) {
        Log.v(TAG, "onPoolItemClicked(product:$product)")
        shoppingList.addToShoppingList(product)
    }
}
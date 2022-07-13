package com.dentreality.spacekit.sample

import androidx.lifecycle.MutableLiveData

class SettableLiveData<T>(private val setAction: (T) -> T) : MutableLiveData<T>() {
    override fun postValue(value: T) {
        super.postValue(setAction(value))
    }
}

class ShoppingList(productDatabase: ProductDatabase) {

    val shoppingList: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val productPoolList: SettableLiveData<List<Product>> = SettableLiveData { unorderedList ->
        unorderedList.sortedBy { it.itemName }
    }

    init {
        productPoolList.value = productDatabase.productList
    }

    fun addToShoppingList(product: Product) {
        moveProduct(product, productPoolList, shoppingList)
    }

    fun removeFromShoppingList(product: Product) {
        moveProduct(product, shoppingList, productPoolList)
    }

    private fun moveProduct(
        product: Product,
        fromList: MutableLiveData<List<Product>>,
        toList: MutableLiveData<List<Product>>
    ) {
        val newFromList = mutableListOf<Product>()
        newFromList.addAll(fromList.value!!)
        newFromList.remove(product)

        val newToList = mutableListOf(product)
        newToList.addAll(toList.value ?: emptyList())

        toList.value = newToList
        fromList.value = newFromList
    }

    override fun toString(): String {
        return "ShoppingList(shoppingList=${shoppingList.value}, productPool=${productPoolList.value})"
    }
}
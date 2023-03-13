package com.dentreality.spacekit.sample

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class SettableLiveData<T>(private val setAction: (T) -> T) : MutableLiveData<T>() {
    override fun postValue(value: T) {
        super.postValue(setAction(value))
    }
}

data class ListPool(
    val list: List<Product> = emptyList(),
    val pool: List<Product> = emptyList()
)

class ShoppingList @Inject constructor(private val productDatabase: ProductDatabase) {

    val poolListLiveData: SettableLiveData<ListPool> = SettableLiveData { unordered ->
        ListPool(
            unordered.list.sortedBy { it.itemName },
            unordered.pool.sortedBy { it.itemName }
        )
    }
    private val shoppingList = mutableListOf<Product>()
    private val productPool = mutableListOf<Product>()

    init {
        productPool.addAll(productDatabase.productList.sortedBy { it.itemName })
        updateLiveData()
    }

    fun setOrderedShoppingList(list: List<Product>) {
        shoppingList.clear()
        shoppingList.addAll(list)
        updateLiveData()
    }

    fun addToShoppingList(product: Product) {
        moveProduct(product, productPool, shoppingList)
    }

    fun removeFromShoppingList(product: Product) {
        moveProduct(product, shoppingList, productPool)
    }

    private fun moveProduct(
        product: Product,
        fromList: MutableList<Product>,
        toList: MutableList<Product>
    ) {
        fromList.remove(product)
        toList.add(product)
        updateLiveData()
    }

    private fun updateLiveData() {
        poolListLiveData.value = ListPool(shoppingList, productPool)
    }

    override fun toString(): String {
        return "ShoppingList(shoppingList=${shoppingList}, productPool=${productPool})"
    }
}
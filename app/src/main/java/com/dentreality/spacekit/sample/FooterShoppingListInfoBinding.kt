package com.dentreality.spacekit.sample

import androidx.core.view.isVisible
import com.dentreality.spacekit.sample.databinding.ViewFooterShoppingListInfoBinding

fun ViewFooterShoppingListInfoBinding.handleListOfProductUpdate(sizeListOfProducts: Int) {
    val imageResource = if (sizeListOfProducts == 0) {
        R.drawable.img_add_new
    } else {
        R.drawable.img_product_list
    }

    val message = if (sizeListOfProducts == 0) {
        root.resources.getString(R.string.add_new)
    } else {
        root.resources.getQuantityString(
            R.plurals.list_of_products,
            sizeListOfProducts,
            sizeListOfProducts
        )
    }

    listOfProductsIcon.setImageResource(imageResource)
    listOfProductsTV.text = message
}

fun ViewFooterShoppingListInfoBinding.handleTargetDestinationChanged(target: Product) {
    nextItemCard.isVisible = true
    nextItemName.text = target.itemName
    nextItemName.isSelected = true

    val iconDrawable = target.icon.toDrawable(root.context)
    nextItemImage.setImageDrawable(iconDrawable)
}

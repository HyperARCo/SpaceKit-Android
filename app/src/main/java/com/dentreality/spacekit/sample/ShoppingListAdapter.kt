package com.dentreality.spacekit.sample

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.dentreality.spacekit.sample.databinding.*

class ShoppingListAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        private const val TAG = "ShoppingListAdapter"

        const val EMPTY_TYPE = 0
        const val LIST_PRODUCT_TYPE = 1
        const val POOL_HEADER_TYPE = 2
        const val POOL_PRODUCT_TYPE = 3
    }

    var onListClick: (product: Product) -> Unit = {}
    var onPoolClick: (product: Product) -> Unit = {}

    abstract class BaseEntry
    class EmptyEntry : BaseEntry()
    class PoolHeaderEntry : BaseEntry()
    data class ListEntry(val product: Product) : BaseEntry()
    data class PoolEntry(val product: Product) : BaseEntry()

    private val entries = ArrayList<BaseEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            EMPTY_TYPE -> EmptyViewHolder.newInstance(parent)
            LIST_PRODUCT_TYPE -> ListItemViewHolder.newInstance(parent, onListClick)
            POOL_HEADER_TYPE -> PoolHeaderViewHolder.newInstance(parent)
            POOL_PRODUCT_TYPE -> PoolItemViewHolder.newInstance(parent, onPoolClick)
            else -> throw Exception("Don't recognise type:$viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder(holder:$holder, position:$position)")
        val entry: BaseEntry = entries[position]
        when (val viewType = getItemViewType(position)) {
            LIST_PRODUCT_TYPE -> (holder as ListItemViewHolder).bind(entry as ListEntry)
            POOL_PRODUCT_TYPE -> (holder as PoolItemViewHolder).bind(entry as PoolEntry)
            EMPTY_TYPE, POOL_HEADER_TYPE -> Log.v(TAG, "No binding needed")
            else -> throw Exception("Don't recognise type:$viewType")
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (val entry: BaseEntry = entries[position]) {
            is EmptyEntry -> EMPTY_TYPE
            is ListEntry -> LIST_PRODUCT_TYPE
            is PoolHeaderEntry -> POOL_HEADER_TYPE
            is PoolEntry -> POOL_PRODUCT_TYPE
            else -> throw Exception("Don't recognise type:$entry")
        }

    override fun getItemCount(): Int = entries.size

    abstract class BaseViewHolder<Binding : ViewBinding, Entry : BaseEntry>(protected val binding: Binding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(entry: Entry)
    }

    private class EmptyViewHolder(binding: ItemListEmptyBinding) :
        BaseViewHolder<ItemListEmptyBinding, EmptyEntry>(binding) {
        override fun bind(entry: EmptyEntry) {}

        companion object {
            fun newInstance(parent: ViewGroup): EmptyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return EmptyViewHolder(ItemListEmptyBinding.inflate(inflater, parent, false))
            }
        }
    }

    private class ListItemViewHolder(
        binding: ItemListProductBinding,
        private val clickAction: (product: Product) -> Unit
    ) :
        BaseViewHolder<ItemListProductBinding, ListEntry>(binding) {
        override fun bind(entry: ListEntry) {
            binding.productName.text = entry.product.itemName
            binding.productIcon.setImageDrawable(entry.product.icon.toDrawable(binding.productIcon.context))
            binding.root.setOnClickListener { clickAction(entry.product) }
        }

        companion object {
            fun newInstance(
                parent: ViewGroup,
                clickAction: (product: Product) -> Unit
            ): ListItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return ListItemViewHolder(
                    ItemListProductBinding.inflate(inflater, parent, false),
                    clickAction
                )
            }
        }
    }

    private class PoolHeaderViewHolder(binding: ItemPoolHeaderBinding) :
        BaseViewHolder<ItemPoolHeaderBinding, PoolHeaderEntry>(binding) {
        override fun bind(entry: PoolHeaderEntry) {}

        companion object {
            fun newInstance(parent: ViewGroup): PoolHeaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return PoolHeaderViewHolder(ItemPoolHeaderBinding.inflate(inflater, parent, false))
            }
        }
    }

    private class PoolItemViewHolder(
        binding: ItemPoolProductBinding,
        private val clickAction: (product: Product) -> Unit
    ) :
        BaseViewHolder<ItemPoolProductBinding, PoolEntry>(binding) {
        override fun bind(entry: PoolEntry) {
            binding.productName.text = entry.product.itemName
            binding.productIcon.setImageDrawable(entry.product.icon.toDrawable(binding.productIcon.context))
            binding.root.setOnClickListener { clickAction(entry.product) }
        }

        companion object {
            fun newInstance(
                parent: ViewGroup,
                clickAction: (product: Product) -> Unit
            ): PoolItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return PoolItemViewHolder(
                    ItemPoolProductBinding.inflate(inflater, parent, false),
                    clickAction
                )
            }
        }
    }

    fun updateList(shoppingList: ListPool) {
        entries.clear()

        if (shoppingList.list.isEmpty())
            entries.add(EmptyEntry())
        else {
            entries.addAll(shoppingList.list.map { ListEntry(it) })
        }

        entries.add(PoolHeaderEntry())

        if (shoppingList.pool.isEmpty())
            entries.add(EmptyEntry())
        else {
            entries.addAll(shoppingList.pool.map { PoolEntry(it) })
        }
        notifyDataSetChanged()
    }
}

val diffCallback = object : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.identifier == newItem.identifier

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}
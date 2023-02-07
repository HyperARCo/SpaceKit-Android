package com.dentreality.spacekit.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentreality.spacekit.SpaceKitUIFragment
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.ListListener
import com.dentreality.spacekit.sample.databinding.FragmentSpaceKitViewBinding
import com.dentreality.spacekit.sample.databinding.ViewFooterShoppingListInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.*


/**
 * This fragment shows the SpaceKit UI along with a shopping list as bottom sheet
 */
class SpaceKitViewFragment : Fragment() {

    companion object {
        private const val TAG = "SpaceKitViewFragment"
    }

    private val viewModel: SpaceKitViewModel by viewModels()
    private var binding: FragmentSpaceKitViewBinding? = null
    private lateinit var spaceKitUiFragment: SpaceKitUIFragment
    private lateinit var listAdapter: ShoppingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpaceKitViewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUI()

        viewModel.loadData()
    }

    private fun configureUI() {
        binding?.apply {
            spaceKitUiFragment = childFragmentManager
                .findFragmentById(R.id.spaceKitFragment) as SpaceKitUIFragment

            //load data
            spaceKitUiFragment.initialise()

            //add listener for ordered items
            spaceKitUiFragment.addListListener(object : ListListener<Destination> {
                override fun onUpdateOrderedDestinations(destinations: List<Destination>) {
                    viewModel.onItemsOrdered(destinations.map { it as Product })
                }
            })

            //create shopping list info footer
            val footerBinding =
                ViewFooterShoppingListInfoBinding.inflate(LayoutInflater.from(requireContext()))
                    .apply {
                        listOfProductsTV.text =
                            resources.getQuantityString(R.plurals.list_of_products, 0, 0)
                        listOfProductsSection.setOnClickListener {
                            toggleShoppingList()
                        }

                        //notify SpaceKitFragment that user found the product
                        gotItButton.setOnClickListener {
                            spaceKitUiFragment.markItemAsFound()
                        }
                    }
            //add footer into SpaceKitFragment
            spaceKitUiFragment.addFooter(footerBinding.root)

            //add listener for destination updates
            spaceKitUiFragment.updateTargetDestinationListener { target, iconDrawable, description ->
                footerBinding.apply {
                    nextItemCard.isInvisible = target == null

                    nextItemName.text = target?.itemName
                    nextItemName.isSelected = true

                    iconDrawable?.let {
                        nextItemImage.setImageDrawable(it)
                    }

                    nextItemDescription.isVisible = if (description != null) {
                        nextItemDescription.text = description
                        true
                    } else {
                        false
                    }
                }
            }

            //add listener for shopping list size updates
            spaceKitUiFragment.updateShoppingListSizeListener { size ->
                footerBinding.listOfProductsIcon.setImageResource(
                    if (size == 0) {
                        R.drawable.img_add_new
                    } else {
                        R.drawable.img_product_list
                    }
                )
                footerBinding.listOfProductsTV.text = if (size == 0) {
                    resources.getString(R.string.add_new)
                } else {
                    resources.getQuantityString(R.plurals.list_of_products, size, size)
                }
            }

            listAdapter = ShoppingListAdapter()
            listAdapter.onListClick = { viewModel.onListItemClicked(it) }
            listAdapter.onPoolClick = { viewModel.onPoolItemClicked(it) }
            listInclude.shoppingListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listAdapter
            }

            viewModel.listPool.observe(requireActivity()) { listPool ->
                listAdapter.updateList(listPool)
                val destinations: Array<Destination> = listPool.list.toTypedArray()
                spaceKitUiFragment.setDestinations(*destinations)
            }
        }
    }

    private fun toggleShoppingList() {
        binding?.apply {
            val sheetBehavior = from(listInclude.shoppingListSheet)
            val newState = if (sheetBehavior.state != STATE_EXPANDED) {
                STATE_EXPANDED
            } else {
                STATE_COLLAPSED
            }
            sheetBehavior.setState(newState)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
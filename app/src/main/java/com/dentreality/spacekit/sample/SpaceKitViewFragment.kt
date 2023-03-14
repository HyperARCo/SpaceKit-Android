package com.dentreality.spacekit.sample

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentreality.spacekit.SpaceKitUIFragment
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.ListListener
import com.dentreality.spacekit.ext.SpaceKitContextFactory
import com.dentreality.spacekit.ext.SpaceKitVenue
import com.dentreality.spacekit.sample.common.CachedAssetFile
import com.dentreality.spacekit.sample.databinding.FragmentSpaceKitViewBinding
import com.dentreality.spacekit.sample.databinding.ViewFooterShoppingListInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * This fragment shows the SpaceKit UI along with a shopping list as bottom sheet
 */
@AndroidEntryPoint
class SpaceKitViewFragment : Fragment(R.layout.fragment_space_kit_view) {
    private val binding by viewBinding(FragmentSpaceKitViewBinding::bind)

    private val viewModel: SpaceKitViewModel by viewModels()
    private lateinit var spaceKitUiFragment: SpaceKitUIFragment
    private lateinit var listAdapter: ShoppingListAdapter
    private var destinations: MutableList<Destination> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()

        viewModel.loadData()
    }

    private fun configureUI() {
        spaceKitUiFragment = childFragmentManager
            .findFragmentById(R.id.spaceKitFragment) as SpaceKitUIFragment

        val cached = CachedAssetFile(requireContext(), "sampleData.zip")
        val spaceKitVenue = SpaceKitVenue(cached.getFile().absolutePath)

        val spaceKitContext = SpaceKitContextFactory.create(spaceKitVenue)

        //load data
        spaceKitUiFragment.initialise(spaceKitContext)

        val footerBinding = ViewFooterShoppingListInfoBinding.inflate(layoutInflater)

        //add listener for ordered items
        spaceKitContext.addListListener(object : ListListener<Destination> {
            override fun onUpdateOrderedDestinations(destinations: List<Destination>) {
                this@SpaceKitViewFragment.destinations = destinations.toMutableList()
                viewModel.onItemsOrdered(destinations.map { it as Product })

                footerBinding.handleListOfProductUpdate(destinations.size)

                if (destinations.isNotEmpty()) {
                    footerBinding.handleTargetDestinationChanged(destinations.first() as Product)
                } else {
                    footerBinding.nextItemCard.isVisible = false
                }
            }
        })

        //create shopping list info footer
        footerBinding.apply {
            listOfProductsTV.text =
                resources.getQuantityString(R.plurals.list_of_products, 0, 0)

            listOfProductsSection.setOnClickListener {
                toggleShoppingList()
            }

            //notify SpaceKitFragment that user found the product
            gotItButton.setOnClickListener {
                if (destinations.isEmpty()) return@setOnClickListener
                destinations.removeFirst()
                spaceKitContext.setDestinations(destinations)
            }
        }

        //add footer into SpaceKitFragment
        spaceKitUiFragment.addFooter(footerBinding.root)

        listAdapter = ShoppingListAdapter()
        listAdapter.onListClick = { viewModel.onListItemClicked(it) }
        listAdapter.onPoolClick = { viewModel.onPoolItemClicked(it) }
        binding.listInclude.shoppingListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }

        viewModel.listPool.observe(requireActivity()) { listPool ->
            listAdapter.updateList(listPool)
            spaceKitContext.setDestinations(listPool.list)
        }
    }

    private fun toggleShoppingList() {
        binding.apply {
            val sheetBehavior = from(listInclude.shoppingListSheet)
            val newState = if (sheetBehavior.state != STATE_EXPANDED) {
                STATE_EXPANDED
            } else {
                STATE_COLLAPSED
            }
            sheetBehavior.setState(newState)
        }
    }
}
package com.dentreality.spacekit.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentreality.spacekit.SpaceKitUIFragment
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.ListListener
import com.dentreality.spacekit.sample.databinding.FragmentSpaceKitViewBinding
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
        binding = FragmentSpaceKitViewBinding.inflate(inflater, container, false).apply {

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

            shoppingBasketFab.setOnClickListener {
                toggleShoppingList()
            }

            val sheetBehavior = from(listInclude.shoppingListSheet)
            sheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        STATE_EXPANDED ->
                            Log.i(TAG, "Shopping list drawer is open")
                        STATE_COLLAPSED, STATE_HIDDEN ->
                            Log.i(TAG, "Shopping list drawer is closed")
                        else -> //other possibilities: STATE_DRAGGING, STATE_SETTLING, STATE_HALF_EXPANDED
                            Log.v(TAG, "Shopping list drawer state:$newState")
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            listAdapter = ShoppingListAdapter()
            listAdapter.onListClick = { viewModel.onListItemClicked(it) }
            listAdapter.onPoolClick = { viewModel.onPoolItemClicked(it) }
            listInclude.shoppingListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listAdapter
            }

            viewModel.listPool.observe(requireActivity()) { listPool ->
                listAdapter.updateList(listPool)
                val destinations:Array<Destination> = listPool.list.toTypedArray()
                spaceKitUiFragment.setDestinations(*destinations)
            }
        }
        return binding!!.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
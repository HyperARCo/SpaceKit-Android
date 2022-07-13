package com.dentreality.spacekit.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dentreality.spacekit.SpaceKitUIFragment
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.ListListener
import com.dentreality.spacekit.sample.databinding.FragmentSpaceKitViewBinding

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

            //enable press-to-locate
            spaceKitUiFragment.isLocationOverrideEnabled = true

            shoppingBasketFab.setOnClickListener {
                Toast.makeText(requireContext(), "Shopping basket pressed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return binding!!.root
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
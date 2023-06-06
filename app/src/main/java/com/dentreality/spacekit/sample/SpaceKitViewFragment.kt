package com.dentreality.spacekit.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dentreality.spacekit.SpaceKitUIFragment
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.ListListener
import com.dentreality.spacekit.ext.SpaceKitContextFactory
import com.dentreality.spacekit.ext.SpaceKitVenue
import com.dentreality.spacekit.sample.common.CachedAssetFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * This fragment shows the SpaceKit UI along with a shopping list as bottom sheet
 */
@AndroidEntryPoint
class SpaceKitViewFragment : Fragment(R.layout.fragment_space_kit_view) {
    private val viewModel: SpaceKitViewModel by viewModels()
    private lateinit var spaceKitUiFragment: SpaceKitUIFragment

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

        lifecycleScope.launch {
            val spaceKitContext = SpaceKitContextFactory.create(spaceKitVenue)

            withContext(Dispatchers.Main) {
                /*
                Custom UI injection:
                    spaceKitUiFragment.changeLevelSwitcher(levelSwitcherConfig: LevelSwitcherConfig)
                    spaceKitUiFragment.changeRecenterButton(recenterButtonConfig: RecenterButtonConfig)
                    spaceKitUiFragment.changeInfoView(infoViewConfig: InfoViewConfig)
                 */

                //load data
                spaceKitUiFragment.initialise(spaceKitContext)

                //add listener for ordered items
                spaceKitContext.addListListener(object : ListListener<Destination> {
                    override fun onUpdateOrderedDestinations(destinations: List<Destination>) {

                    }
                })

                /*
                Changing destinations:
                    spaceKitContext.setDestinations(destinations: List<Destination>)
                */
            }
        }
    }
}
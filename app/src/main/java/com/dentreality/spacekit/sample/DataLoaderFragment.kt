package com.dentreality.spacekit.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dentreality.spacekit.android.ext.SpaceKitKtx
import com.dentreality.spacekit.android.requestWifiEnable
import com.dentreality.spacekit.ext.Requisite
import com.dentreality.spacekit.sample.databinding.FragmentDataLoaderBinding

/**
 * A fragment that handles all data loading and permissions required for showing the SpaceKit UI
 */
class DataLoaderFragment : Fragment() {

    companion object {
        private const val TAG = "DataLoaderFragment"
    }

    private var binding: FragmentDataLoaderBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataLoaderBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onStart() {
        super.onStart()
        loadVenueData()
        continueWithRequisites()
    }

    private fun loadVenueData() {
        Log.w(TAG, "loadVenueData not yet implemented")
    }

    private fun continueToSpaceKitView() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    @Suppress("DEPRECATION")
    private fun continueWithRequisites() {
        val requisites = SpaceKitKtx.getUnfulfilledPrerequisites(requireActivity())
        if (requisites.isEmpty()) {
            continueToSpaceKitView()
        } else {
            Log.i(TAG, "Found unfulfilled requisites:$requisites")
            requisites.forEach {
                when (it) {
                    Requisite.CAMERA_PERMISSION -> requestCameraPermissions()
                    Requisite.FINE_LOCATION_PERMISSION -> requestLocationPermissions()
                    Requisite.LOCATION_ENABLED -> requestLocationServices()
                    Requisite.WIFI_ENABLED -> requestWifi()
                    else -> Log.w(TAG, "(NOT) asking for $it")
                }
            }
        }
    }

    private fun requestLocationServices() {
        show(AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_service_title)
            .setMessage(R.string.location_service_body)
            .setPositiveButton(R.string.location_service_yes) { _, _ ->
                requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.location_service_no) { _, _ ->
                confirmDeniedRequisites()
            }
            .create()
        )
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            continueWithRequisites()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestCameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                continueWithRequisites()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                show(AlertDialog.Builder(requireContext())
                    .setTitle(R.string.camera_permission_rationale_title)
                    .setMessage(R.string.camera_permission_rationale_body)
                    .setCancelable(false)
                    .setPositiveButton(R.string.camera_permission_rationale_yes) { _, _ ->
                        //request again
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton(R.string.camera_permission_rationale_no) { _, _ ->
                        confirmDeniedRequisites()
                    }
                    .create()
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            continueWithRequisites()
        } else {
            requestCameraPermissions()
        }
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                continueWithRequisites()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                show(AlertDialog.Builder(requireContext())
                    .setTitle(R.string.location_permission_rationale_title)
                    .setMessage(R.string.location_permission_rationale_body)
                    .setCancelable(false)
                    .setPositiveButton(R.string.location_permission_rationale_yes) { _, _ ->
                        //request again
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton(R.string.location_permission_rationale_no) { _, _ ->
                        confirmDeniedRequisites()
                    }
                    .create()
                )
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestWifi() {
        show(AlertDialog.Builder(requireContext())
            .setTitle(R.string.wifi_enable_title)
            .setMessage(R.string.wifi_enable_body)
            .setCancelable(false)
            .setPositiveButton(R.string.wifi_enable_yes) { _, _ ->
                //enable wifi
                requireActivity().requestWifiEnable()
            }
            .setNegativeButton(R.string.wifi_enable_no) { _, _ ->
                //quit the app
                confirmDeniedRequisites()
            }
            .create()
        )
    }

    private fun confirmDeniedRequisites() {
        show(AlertDialog.Builder(requireContext())
            .setTitle(R.string.requisites_denied_title)
            .setMessage(R.string.requisites_denied_body)
            .setCancelable(false)
            .setPositiveButton(R.string.requisites_denied_try) { _, _ ->
                //ask again
                continueWithRequisites()
            }
            .setNegativeButton(R.string.requisites_denied_quit) { _, _ ->
                //quit the app
                requireActivity().finish()
            }
            .create()
        )
    }

    private var _dialog: AlertDialog? = null
    private fun show(dialog: AlertDialog) {
        _dialog?.dismiss()
        _dialog = dialog.apply { show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
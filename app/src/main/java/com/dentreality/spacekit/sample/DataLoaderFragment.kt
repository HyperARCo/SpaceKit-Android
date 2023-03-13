package com.dentreality.spacekit.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dentreality.spacekit.android.ext.requestWifiEnable
import com.dentreality.spacekit.ext.Requisite
import com.dentreality.spacekit.ext.Requisites
import com.dentreality.spacekit.sample.databinding.FragmentDataLoaderBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A fragment that handles all data loading and permissions required for showing the SpaceKit UI
 */
@AndroidEntryPoint
class DataLoaderFragment : Fragment(R.layout.fragment_data_loader) {
    private val binding by viewBinding(FragmentDataLoaderBinding::bind)

    companion object {
        private const val TAG = "DataLoaderFragment"
    }

    override fun onStart() {
        super.onStart()
        checkRequisites()
    }

    private fun checkRequisites() {
        val requisites = Requisites.requestUnfulfilledRequisites(requireContext())
        if (requisites.isEmpty()) {
            findNavController().navigate(R.id.actionContinueToSpaceKitViewer)
        } else {
            onRequisitesNeeded(requisites)
        }
    }

    private fun onRequisitesNeeded(requisites: List<Requisite>) {
        Log.i(TAG, "Found unfulfilled requisites:$requisites")
        when (val nextRequisite = requisites.first()) {
            Requisite.CAMERA_PERMISSION -> requestCameraPermissions()
            Requisite.FINE_LOCATION_PERMISSION -> requestLocationPermissions()
            Requisite.LOCATION_ENABLED -> requestLocationServices()
            Requisite.WIFI_ENABLED -> requestWifi()
            else -> Log.w(TAG, "(NOT) asking for $nextRequisite")
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkRequisites()
        } else {
            requestCameraPermissions()//ask again
        }
    }

    private fun requestCameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                checkRequisites()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
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
                    .create().show()
            }
            else -> {
                // Directly ask for the permission.
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkRequisites()
        } else {
            requestLocationPermissions()//ask again
        }
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                checkRequisites()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
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
                    .create().show()
            }
            else -> {
                // Directly ask for the permission.
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestLocationServices() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_service_title)
            .setMessage(R.string.location_service_body)
            .setCancelable(false)
            .setPositiveButton(R.string.location_service_yes) { _, _ ->
                requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.location_service_no) { _, _ ->
                confirmDeniedRequisites()
            }
            .create().show()
    }

    private fun requestWifi() {
        AlertDialog.Builder(requireContext())
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
            .create().show()
    }

    private fun confirmDeniedRequisites() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.requisites_denied_title)
            .setMessage(R.string.requisites_denied_body)
            .setCancelable(false)
            .setPositiveButton(R.string.requisites_denied_try) { _, _ ->
                //ask again
                checkRequisites()
            }
            .setNegativeButton(R.string.requisites_denied_quit) { _, _ ->
                //quit the app
                requireActivity().finish()
            }
            .create().show()
    }
}
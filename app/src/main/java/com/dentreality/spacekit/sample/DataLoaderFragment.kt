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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dentreality.spacekit.android.ext.SpaceKit
import com.dentreality.spacekit.android.ext.SpaceKitAssetData
import com.dentreality.spacekit.android.ext.requestWifiEnable
import com.dentreality.spacekit.ext.Requisite
import com.dentreality.spacekit.ext.SpaceKitStatusListener
import com.dentreality.spacekit.sample.databinding.FragmentDataLoaderBinding

/**
 * A fragment that handles all data loading and permissions required for showing the SpaceKit UI
 */
class DataLoaderFragment : Fragment(), SpaceKitStatusListener {

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
        SpaceKit.setStatusListener(this)
        SpaceKit.initialise(SpaceKitAssetData(requireContext(), "sampleData.zip"))
    }

    override fun onStop() {
        super.onStop()
        SpaceKit.setStatusListener(null)
    }

    override fun onSpaceKitReady() {
        findNavController().navigate(R.id.actionContinueToSpaceKitViewer)
    }

    override fun onRequisitesNeeded(requisites: List<Requisite>) {
        Log.i(TAG, "Found unfulfilled requisites:$requisites")
        when (val nextRequisite = requisites.first()) {
            Requisite.CAMERA_PERMISSION -> requestCameraPermissions()
            Requisite.FINE_LOCATION_PERMISSION -> requestLocationPermissions()
            Requisite.LOCATION_ENABLED -> requestLocationServices()
            Requisite.WIFI_ENABLED -> requestWifi()
            else -> Log.w(TAG, "(NOT) asking for $nextRequisite")
        }
    }

    override fun onError(exception: Exception) {
        val error = "An error occurred during SpaceKit initialisation"
        Log.w(TAG, error, exception)
        activity?.let { Toast.makeText(it, error, Toast.LENGTH_LONG).show() }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            SpaceKit.checkRequisites()
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
                SpaceKit.checkRequisites()
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
            SpaceKit.checkRequisites()
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
                SpaceKit.checkRequisites()
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
                SpaceKit.checkRequisites()
            }
            .setNegativeButton(R.string.requisites_denied_quit) { _, _ ->
                //quit the app
                requireActivity().finish()
            }
            .create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
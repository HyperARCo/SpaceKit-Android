package com.dentreality.spacekit.sample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpaceKitViewModel @Inject constructor(
) : ViewModel() {

    companion object {
        private const val TAG = "SpaceKitViewModel"
    }

    fun loadData() {
        Log.v(TAG, "loadData()")
        viewModelScope.launch {
        }
    }
}
package com.dentreality.spacekit.sample

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dentreality.spacekit.ThemeManagerFactory
import com.dentreality.spacekit.sample.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    companion object {
        private const val TAG = "SecondFragment"
    }

    private var binding: FragmentSecondBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false).apply {
            buttonSecond.setOnClickListener {
                Log.w(TAG, "No destination to go to")
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val venueId = "b81ab9ae-8bad-4750-ad3c-c49dfbee6503"//M&S Waterside
        val themeManagerFactory: ThemeManagerFactory = ThemeManagerFactory
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
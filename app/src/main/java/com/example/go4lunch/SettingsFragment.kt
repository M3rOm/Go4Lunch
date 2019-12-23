package com.example.go4lunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    //Here user can toggle notification.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onStart() {
        notification_switch.setOnClickListener {
            //TODO: Switch notifications state
        }
        super.onStart()
    }
}
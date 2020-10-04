package com.mrvijay.runningapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)



        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {

            /*NO-CODE*/
        }
        navHostFragment.findNavController().addOnDestinationChangedListener {
                _, destination, _ ->

            when(destination.id)
            {
                R.id.settingsFragment4, R.id.runFragment4, R.id.statisticsFragment4 ->
                    bottomNavigationView.visibility= View.VISIBLE

                else -> bottomNavigationView.visibility=View.GONE


            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    fun navigateToTrackingFragmentIfNeeded(intent:Intent?)
    {
        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT)
        {
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }
}
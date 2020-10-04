package com.mrvijay.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.others.Constants.KEY_FIRST_TIME_TOOGLE
import com.mrvijay.runningapp.others.Constants.KEY_NAME
import com.mrvijay.runningapp.others.Constants.KEY_WEIGHT
import com.mrvijay.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.setup_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.setup_fragment) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen=true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen)
        {
            val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment3,true)
                .build()

            findNavController().navigate(
                R.id.action_setupFragment3_to_runFragment4,
                savedInstanceState,
                navOptions
            )
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()

            if (success) {
                findNavController().navigate(R.id.action_setupFragment3_to_runFragment4)
            } else {

                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT)
                    .show()
            }


        }


    }

    private fun writePersonalDataToSharedPref():Boolean
    {
        val name=etName.text.toString()
        val weight=etWeight.text.toString()

        if(name.isEmpty() || weight.isEmpty())
        {
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOOGLE,false)
            .apply()

        val toolbarText="Let's go, $name"
        requireActivity().tvToolbarTitle.text=toolbarText
        return true





    }
}

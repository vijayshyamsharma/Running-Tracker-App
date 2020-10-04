package com.mrvijay.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.others.Constants.KEY_NAME
import com.mrvijay.runningapp.others.Constants.KEY_WEIGHT
import com.mrvijay.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()

        btnApplyChanges.setOnClickListener {
            val success=applyChangesToSharedPref()

            if(success)
            {
                Snackbar.make(view,"Saved changes",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Please fill out all the details",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFieldsFromSharedPref()
    {
        val name=sharedPreferences.getString(KEY_NAME,"")
        val weight=sharedPreferences.getFloat(KEY_WEIGHT,80f)

        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref():Boolean
    {
        val nameText=etName.text.toString()
        val weightText=etWeight.text.toString()

        if(nameText.isEmpty() || weightText.isEmpty())
        {
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT,weightText.toFloat())
            .apply()

        val toolbarText="Let's go $nameText"
        requireActivity().tvToolbarTitle.text=toolbarText

        return true


    }


}
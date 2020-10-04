package com.mrvijay.runningapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.adapters.RunAdapter
import com.mrvijay.runningapp.others.Constants.PERMISSION_REQUEST_CODE
import com.mrvijay.runningapp.others.SortType
import com.mrvijay.runningapp.others.TrackingUtility
import com.mrvijay.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.run_fragment.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class RunFragment : Fragment(R.layout.run_fragment),EasyPermissions.PermissionCallbacks {

    private lateinit var runAdapter: RunAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()

        setupRecyclerView()

        when(viewModel.sortType)
        {
           SortType.DATE->spFilter.setSelection(0)
            SortType.RUNNING_TIME->spFilter.setSelection(1)
            SortType.DISTANCE->spFilter.setSelection(2)
            SortType.AVG_SPEED->spFilter.setSelection(3)
            SortType.CALORIES_BURNED->spFilter.setSelection(4)
        }




        spFilter.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {

                when(pos)
                {
                    0->viewModel.sortRuns(SortType.DATE)
                    1->viewModel.sortRuns(SortType.RUNNING_TIME)
                    2->viewModel.sortRuns(SortType.DISTANCE)
                    3->viewModel.sortRuns(SortType.AVG_SPEED)
                    4->viewModel.sortRuns(SortType.CALORIES_BURNED)
                }

            }
        }


        viewModel.runs.observe(viewLifecycleOwner, Observer {

            runAdapter.submitList(it)
        })

        fab.setOnClickListener { findNavController().navigate(R.id.action_runFragment4_to_trackingFragment3) }
    }


    fun requestPermissions()
    {
        if(TrackingUtility.hasLocationPermissions(requireContext()))
        {
            return
        }

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q)
        {
            EasyPermissions.requestPermissions(this,"you need to give permission to use this app."
            ,PERMISSION_REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        else
        {
            EasyPermissions.requestPermissions(this,"you need to give permission to use this app."
                ,PERMISSION_REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder(this).build().show()
        }
        else
        {
            requestPermissions()

        }


    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    private fun setupRecyclerView()=rvRuns.apply {
        runAdapter= RunAdapter()
        adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())


    }
}
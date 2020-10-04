package com.mrvijay.runningapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.db.Run
import com.mrvijay.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.mrvijay.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.mrvijay.runningapp.others.Constants.ACTION_STOP_SERVICE
import com.mrvijay.runningapp.others.Constants.MAP_ZOOM
import com.mrvijay.runningapp.others.Constants.POLYLINE_COLOR
import com.mrvijay.runningapp.others.Constants.POLYLINE_WIDTH
import com.mrvijay.runningapp.others.TrackingUtility
import com.mrvijay.runningapp.services.Polyline
import com.mrvijay.runningapp.services.TrackingService
import com.mrvijay.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.tracking_fragment.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.tracking_fragment) {

    private var isTracking=false

    private var pathPoints= mutableListOf<Polyline>()

    private val viewModel: MainViewModel by viewModels()

    private var map:GoogleMap?=null

    private var curTimeInMillis=0L

    private var menu: Menu? =null

    @set:Inject
    private var weight=80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        btnFinishRun.setOnClickListener {

            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }

        mapView.getMapAsync {
            map=it
            addAllPolylines()
        }

        subscribeToObservers()


    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    fun sendCommandToService(action:String)=
        Intent(requireContext(),TrackingService::class.java).also {

            it.action=action
            requireContext().startService(it)
        }

    private fun moveCameraToUser()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
        {
            map?.animateCamera(

               CameraUpdateFactory.newLatLngZoom(

                   pathPoints.last().last(),
                   MAP_ZOOM
               )

            )
        }
    }

    private fun zoomToSeeWholeTrack()
    {
        val bounds=LatLngBounds.Builder()

        for(polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(

            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()

            )
        )
    }

    private fun endRunAndSaveToDb()
    {
        map?.snapshot {bmp->

            var distanceInMeters=0

            for(polyline in pathPoints)
            {
                distanceInMeters+=TrackingUtility.calculatePolylineLength(polyline).toInt()
            }

            val avgSpeed= round((distanceInMeters/1000f) / (curTimeInMillis/1000f/60/60) * 10 ) /10f
            val dateTimeStamp=Calendar.getInstance().timeInMillis
            val caloriesBurned=((distanceInMeters/1000f)*weight).toInt()

            val run=Run(bmp,dateTimeStamp,avgSpeed,distanceInMeters,curTimeInMillis,caloriesBurned)

            viewModel.insertRun(run)

            Snackbar.make(

                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()

            stopRun()




        }
    }

    private fun updateTracking(isTracking:Boolean)
    {
        this.isTracking=isTracking

        if(!isTracking && curTimeInMillis>0L)
        {

            btnToggleRun.text="START"
            btnFinishRun.visibility=View.VISIBLE
        }
        else if(isTracking)
        {
            btnToggleRun.text="STOP"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.GONE
        }


    }


    private fun addAllPolylines()
    {
        for(polyline in pathPoints)
        {
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().size>1)
        {
            val preLastLatLng=pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng=pathPoints.last().last()

            val polyLineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polyLineOptions)
        }
    }

    private fun toggleRun()
    {
        if(isTracking)
        {
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        else
        {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObservers()
    {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {

            updateTracking(it)

        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {

            pathPoints=it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {

            curTimeInMillis=it
            val formattedTime=TrackingUtility.getFormattedStopWatchTime(curTimeInMillis,true)
            tvTimer.text=formattedTime
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_tracking_menu,menu)

        this.menu=menu

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if(curTimeInMillis>0)
        {
            this.menu?.getItem(0)?.isVisible=true

        }
    }
    private fun showCancelTrackingDialog()
    {
        val dialog=MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_ ->

                stopRun()

            }
            .setNegativeButton("No"){dialogInterface, _->

                dialogInterface.cancel()

            }
            .create()

        dialog.show()
    }

    private fun stopRun()
    {
        tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment3_to_runFragment4)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.miCancelTracking->
            {
                showCancelTrackingDialog()
            }
        }

        return super.onOptionsItemSelected(item)


    }

}

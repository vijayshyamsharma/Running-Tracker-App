package com.mrvijay.runningapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mrvijay.runningapp.R
import com.mrvijay.runningapp.others.CustomMarkerView
import com.mrvijay.runningapp.others.TrackingUtility
import com.mrvijay.runningapp.ui.viewmodels.MainViewModel
import com.mrvijay.runningapp.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.statistics_fragment.*
import kotlin.math.round


@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.statistics_fragment) {

    private val viewModel: StatisticsViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        setupBarChart()
    }

    private fun subscribeToObservers()
    {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {

            it?.let {

                val totalTimeRun=TrackingUtility.getFormattedStopWatchTime(it)

                tvTotalTime.text=totalTimeRun
            }

        })

            viewModel.totalDistance.observe(viewLifecycleOwner, Observer {

                it?.let {

                    val km=it/1000f

                    val totalDistance= round(km*10f)/10f

                    val totalDistanceString="${totalDistance}km"

                    tvTotalDistance.text=totalDistanceString
                }
            })

            viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {

                it?.let {

                    val avgSpeed=round(it*10f)/10f

                    val avgSpeedString="${avgSpeed}km/h"

                    tvAverageSpeed.text=avgSpeedString
                }
            })

            viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {

                it?.let {

                    val totalCalories="${it}kcal"

                    tvTotalCalories.text=totalCalories
                }
            })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {

            it?.let {

                val allAvgSpeeds=it.indices.map { i-> BarEntry(i.toFloat(),it[i].avgSpeedInKMH) }

                val bardataSet=BarDataSet(allAvgSpeeds,"Avg Speed Over Time").apply {

                    valueTextColor=Color.WHITE
                    color=ContextCompat.getColor(requireContext(),R.color.colorAccent)

                }

                barChart.data= BarData(bardataSet)
                barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()
            }


        })




    }

    private fun setupBarChart()
    {
        barChart.xAxis.apply {

            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor= Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)


        }

        barChart.axisLeft.apply {

            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisRight.apply {

            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }

        barChart.apply {

            description.text="Avg Speed Over Time"
            legend.isEnabled=false
        }
    }

}
package com.example.hridoy.map_stack_demo_project.ui.main.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.example.hridoy.map_stack_demo_project.databinding.ActivityMainBinding
import com.example.hridoy.map_stack_demo_project.databinding.ListItemBinding
import com.example.hridoy.map_stack_demo_project.ui.base.BaseActivity
import com.example.hridoy.map_stack_demo_project.ui.main.adapter.LocationAdapter
import com.example.hridoy.map_stack_demo_project.ui.main.viewmodel.LocationViewModel

/**
 * MainActivity class is the home page of this project
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listItemBinding: ListItemBinding
    private lateinit var adapter: LocationAdapter
    private var locationRecordsList: ArrayList<LocationInfoDB> = ArrayList()
    private var twoPane: Boolean = false
    private val locationViewModel: LocationViewModel by viewModels()
    private var styleName: String = "Street"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        listItemBinding = DataBindingUtil.setContentView(this, R.layout.list_item)

        if (listItemBinding.itemDetailContainer != null){
            twoPane = true
        }

        setUpUI(twoPane)
        setUpObserver(locationViewModel)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.style_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }*/



    /**
     * setupUI function sets up the UI which means the RecyclerView for this Activity.
     *
     *
     **/

    private fun setUpUI(twoPane: Boolean) {
        listItemBinding.locationRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocationAdapter(locationRecordsList, twoPane, styleName)
        listItemBinding.locationRecyclerView.addItemDecoration(
            DividerItemDecoration(listItemBinding.locationRecyclerView.context,
                    (listItemBinding.locationRecyclerView.layoutManager as LinearLayoutManager).orientation)
        )
        listItemBinding.locationRecyclerView.adapter = adapter
    }

    /**
     * setupObserver method sets observer for the UI state from ViewModel and checks if data is available or not.
     *
     *
     **/

    private fun setUpObserver(locationViewModel: LocationViewModel) {
        var flag = 0
        value.observe(this, { bool ->
            flag = if (bool){
                presentToast(this.getString(R.string.network_connected))
                locationViewModel.fetchLocations()
                1
            }else{
                locationViewModel.getAllLocationsFromDB()
                0
            }
        })

        locationViewModel.locationModel.observe(this, { locationModel ->
            locationRecordsList.clear()
            locationRecordsList.addAll(locationModel.locationInfoDB)
            if (locationRecordsList.isEmpty() && flag==0){
                presentToast(this.getString(R.string.network_not_connected_cannot_parse_all_data))
                listItemBinding.locationProgressBar.visibility = View.GONE
            }else if (locationRecordsList.isNotEmpty() && flag==0){
                presentToast(this.getString(R.string.network_not_connected_data_from_local_db))
                adapter.notifyDataSetChanged()
                listItemBinding.locationProgressBar.visibility = View.GONE
            }else{
                presentToast(locationModel.status)
                adapter.notifyDataSetChanged()
                listItemBinding.locationProgressBar.visibility = View.GONE
            }
        })
    }
}
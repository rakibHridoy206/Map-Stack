package com.example.hridoy.map_stack_demo_project.data.repository

import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.data.api.LocationApiService
import com.example.hridoy.map_stack_demo_project.data.api.LocationRetroInstance
import com.example.hridoy.map_stack_demo_project.data.database.DatabaseManager
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.example.hridoy.map_stack_demo_project.data.model.Records
import com.example.hridoy.map_stack_demo_project.utils.App

/**
 * LocationRepository class initiates the interface with the object from LocationRetroInstance class and performs storing data to local database.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class LocationRepository {
    private var service: LocationApiService = LocationRetroInstance.getRetroService().create(LocationApiService::class.java)


    /**
     * getLocations function gathers data from the interface
     *
     *
     * @return An observable Single for LocationInfo model class
     **/

    fun getLocations() = service.getLocationInfo()

    /**
     * setLocationRecordsData function used to set  data from api to the local database
     *
     *
     * @param  recordsList
     * @return A String message
     **/
    fun setLocationRecordsData(recordsList: List<Records>): String{
        val del = DatabaseManager(App.instance).clearAllData()
        if(del < 0){
            return App.instance.getString(R.string.cannot_delete_database)
        }

        var flag = 0

        recordsList.forEach { records ->
            flag += DatabaseManager(App.instance).insertDatabaseItems(LocationInfoDB(records.id, records.locName, records.lat, records.long))
        }

        return when (flag) {
            recordsList.size -> {
                App.instance.getString(R.string.parse_all_data, flag)
            }
            0 -> {
                App.instance.getString(R.string.cannot_parse_any_data)
            }
            else -> {
                App.instance.getString(R.string.cannot_parse_all_data)
            }
        }
    }
}
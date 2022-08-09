package com.example.hridoy.map_stack_demo_project.data.database

import android.content.Context
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import java.sql.SQLException

/**
 * DatabaseManager class performs the necessary queries to perform CRUD operations
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class DatabaseManager(context: Context) {
    private var databaseHelper: DatabaseHelper? = null
    private var locationInfoDAO: Dao<LocationInfoDB, Long>? = null

    init {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper::class.java)
        try {
            locationInfoDAO = (databaseHelper as DatabaseHelper).getLocationRecordsDAO()
        }catch (e: SQLException){
            e.printStackTrace()
        }
    }

    /**
     * insertDatabaseItems function inserts values to the local database.
     *
     *
     * @param  locationInfoDB
     * @return Int: returns an integer of success or failure
     **/
    fun insertDatabaseItems(locationInfoDB: LocationInfoDB): Int{
         try {
             locationInfoDAO?.create(locationInfoDB)?.let {
                 return it
             }
        }catch (e: SQLException){
            e.printStackTrace()
             return -1
        }

        return -1
    }

    /**
     * clearAllData function deletes all data from the local database.
     *
     *
     * @return Int: returns an integer of number of rows
     **/
    fun clearAllData(): Int{
        try {
            databaseHelper?.clearTable()?.let {
                return it
            }

        }catch (e: SQLException){
            e.printStackTrace()
            return -1
        }
        return -1
    }

    /**
     * getLocationRecords function retrieves all data from the local database.
     *
     *
     * @return List: returns a list of LocationInfoDB
     **/
    fun getLocationRecords(): List<LocationInfoDB>{
        try {
            locationInfoDAO?.queryForAll()?.let {
                return it
            }
        }catch (e: SQLException){
            e.printStackTrace()
            return emptyList()
        }
        return emptyList()
    }
}
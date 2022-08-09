package com.example.hridoy.map_stack_demo_project.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.example.hridoy.map_stack_demo_project.utils.Constants
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.lang.Exception
import java.sql.SQLException
import kotlin.Throws

/**
 * DatabaseHelper class extends OrmLiteSqliteOpenHelper to perform as a data layer between OOP and Database
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/
class   DatabaseHelper(context: Context):
        OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        const val DATABASE_NAME = Constants.databaseName
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = Constants.tableName
    }

    private var locationInfoDBDAO: Dao<LocationInfoDB, Long>? = null

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        try {
            TableUtils.createTable(connectionSource, LocationInfoDB::class.java)
        }catch (e: SQLException){
            e.printStackTrace()
        }
    }

    override fun onUpgrade(
        database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {
        try {
            if (checkTableExist(database, TABLE_NAME)){
                TableUtils.dropTable<LocationInfoDB, Any>(connectionSource, LocationInfoDB::class.java, false)
            }
        }catch (e: SQLException){
            e.printStackTrace()
        }
    }

    override fun close() {
        locationInfoDBDAO = null
        super.close()
    }

    /**
     * checkTableExist function checks if a table exist or not.
     *
     *
     * @param  database, String of table name
     * @return Boolean: returns a boolean of true or false
     **/

    private fun checkTableExist(database: SQLiteDatabase?, tableName: String): Boolean {
        var tableExixt = false
        try {
            val cursor = database?.query(tableName, null, null, null
            ,null, null, null)
            tableExixt = true
            cursor?.close()
        }catch (e: Exception){
        }
        return tableExixt
    }

    /**
     * getLocationRecordsDAO function gets the DAO for the database.
     *
     *
     * @return Dao: returns a DAO for LocationInfoDB
     **/
    @Throws(SQLException::class)
    fun getLocationRecordsDAO(): Dao<LocationInfoDB, Long>?{
        if (locationInfoDBDAO == null){
            locationInfoDBDAO = getDao(LocationInfoDB::class.java)
        }
        return locationInfoDBDAO
    }

    /**
     * clearTable function clears the table.
     *
     *
     * @return Int: Integer value of rows numbers
     **/
    @Throws(SQLException::class)
    fun clearTable(): Int{
        return TableUtils.clearTable(getConnectionSource(), LocationInfoDB::class.java)
    }
}
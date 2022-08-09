package com.example.hridoy.map_stack_demo_project.data.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

/**
 * LocationInfoDB class declares some variables for storing data to the local database and the variable names will set as column names.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

@DatabaseTable(tableName = "location_info")
class LocationInfoDB: Serializable {
    
    @DatabaseField
    var id: Int? = null

    @DatabaseField
    var locationName: String? = null

    @DatabaseField
    var lat: Double? = null

    @DatabaseField(columnName = "long")
    var longitude: Double? = null

    constructor(id: Int?, locationName: String?, lat: Double?, longitude: Double?) {
        this.id = id
        this.locationName = locationName
        this.lat = lat
        this.longitude = longitude
    }

    constructor()
}
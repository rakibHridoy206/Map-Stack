package com.example.hridoy.map_stack_demo_project.data.model

import com.google.gson.annotations.SerializedName

/**
 * LocationInfo is a data class and declares a List for Records class to get data from api.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

data class LocationInfo(@SerializedName("record")
                        val records: List<Records> = emptyList())

/**
 * Records is a data class and declares some variables for storing data from api.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2020
 * @apiNote Please do not change any parameters without designer consent
 **/

data class Records(val id: Int,
                   @SerializedName("loc_name")
                   val locName: String,
                   val lat: Double,
                   val long: Double)

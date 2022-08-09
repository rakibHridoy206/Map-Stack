package com.example.hridoy.map_stack_demo_project.data.api

import com.example.hridoy.map_stack_demo_project.data.model.LocationInfo
import io.reactivex.Single
import retrofit2.http.GET

/**
 * LocationApiService is an interface and declares a function which will fetch data from an api
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

interface LocationApiService {

    /**
     * getLocationInfo function is declared with a get annotations
     *
     *
     * @return An obserable Single for LocationInfo model class
     **/

    @GET(".")
    fun getLocationInfo(): Single<LocationInfo>
}
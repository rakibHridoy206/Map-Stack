package com.example.hridoy.map_stack_demo_project.data.api

import com.example.hridoy.map_stack_demo_project.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * LocationRetroInstance class gets data from web server through api call using Retrofit2 and RxJava
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class LocationRetroInstance {
    companion object{

        /**
         * getRetroService function parses json data and returns a Retrofit object.
         *
         *
         * @return Retrofit: returns an object of Retrofit after parsing data
         **/

        fun getRetroService(): Retrofit{
            val builder = OkHttpClient.Builder()

            builder.addInterceptor(Interceptor{ interceptor ->
                val interceptorBuilder = interceptor.request().newBuilder()
                interceptorBuilder.addHeader(Constants.headerName, Constants.headerKey)
                return@Interceptor interceptor.proceed(interceptorBuilder.build())
            })

            val client: OkHttpClient = builder.build()

            return Retrofit.Builder().baseUrl(Constants.apiURL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        }
    }
}
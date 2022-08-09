package com.example.hridoy.map_stack_demo_project.utils

import android.app.Application

/**
 * App class extends Application and creates instance to use in the application.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class App: Application() {
    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }
}
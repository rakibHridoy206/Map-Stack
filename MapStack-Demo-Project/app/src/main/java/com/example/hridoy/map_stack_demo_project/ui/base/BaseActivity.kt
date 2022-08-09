package com.example.hridoy.map_stack_demo_project.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.utils.Constants
import java.lang.Exception
import java.net.InetAddress

/**
 * BaseActivity class is the base class for all the activity and it will provide facilities
 * which will be needed in more than one activity.
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

open class BaseActivity : AppCompatActivity() {
    val value = MutableLiveData<Boolean>()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        connectivityManager  = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        value.value = false
        checkVersion()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * checkVersion function checks the SDK version of the mobile/emulator device
     *
     *
     **/

    private fun checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallBack())
        }else {
            //this.getSystemService(Context.CONNECTIVITY_SERVICE)
            lessNougatNetworkRegister()
        }
    }

    /**
     * lessNougatNetworkRegister function registers to get notified about all network.
     *
     *
     **/

    private fun lessNougatNetworkRegister(){
        val requestBuilder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivityManager.registerNetworkCallback(requestBuilder.build(), connectivityManagerCallBack())
    }

    /**
     * connectivityManagerCallBack function sets and returns a NetworkCallBack value
     *
     *
     * @return ConnectivityManager.NetworkCallback: returns a NetworkCallBack
     **/

    private fun connectivityManagerCallBack(): ConnectivityManager.NetworkCallback{
        networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onLost(network: Network) {
                super.onLost(network)
                value.postValue(false)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isConnected()){
                    value.postValue(true)
                }else{
                    value.postValue(false)
                }
            }
        }
        return networkCallback
    }

    /**
     * isConnected function returns a boolean if the network is available when its connected.
     *
     *
     * @return Boolean: returns an integer of success or failure
     **/
    private fun isConnected(): Boolean{
        return try {
            val ip: InetAddress = InetAddress.getByName(Constants.hostName)
            !ip.equals("")
        }catch (e: Exception){
            false
        }
    }

    /**
     * presentToast function creates a Toast with different message
     *
     *
     * @param message
     **/
    protected fun presentToast(message: String?){
        if (message != null){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}
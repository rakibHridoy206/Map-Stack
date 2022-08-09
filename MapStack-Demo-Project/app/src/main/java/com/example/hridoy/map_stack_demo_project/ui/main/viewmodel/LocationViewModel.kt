package com.example.hridoy.map_stack_demo_project.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.data.database.DatabaseManager
import com.example.hridoy.map_stack_demo_project.data.model.LocationModel
import com.example.hridoy.map_stack_demo_project.data.repository.LocationRepository
import com.example.hridoy.map_stack_demo_project.utils.App.Companion.instance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * LocationViewModel class extends ViewModel lifecycle to perform lifecycle operations
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class LocationViewModel: ViewModel() {
    private val repository = LocationRepository()
    val locationModel = MutableLiveData<LocationModel>()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    /**
     * fetchLocations function gets info from api, sets it to the local DB.
     *
     *
     **/
    fun fetchLocations(){
        compositeDisposable
                .add(repository.getLocations()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { locationInfo ->
                                    locationInfo.records.let { recordsList ->
                                        val status = repository.setLocationRecordsData(recordsList)
                                        getAllLocationsFromDB(status)
                                    }
                                },
                                {
                                    locationModel.postValue(LocationModel(instance.getString(R.string.error_status), emptyList()))
                                })
                )
    }

    /**
     * getAllLocationsFromDB function retrieves and sets data to LocationModel class from local database.
     *
     *
     * @param status
     **/

    fun getAllLocationsFromDB(status: String? = null) {
        locationModel.value = LocationModel(status, DatabaseManager(instance).getLocationRecords())
    }
}
package com.example.hridoy.map_stack_demo_project.ui.main.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.example.hridoy.map_stack_demo_project.databinding.BottomSheetMapBinding
import com.example.hridoy.map_stack_demo_project.utils.Constants
import com.example.hridoy.map_stack_demo_project.utils.Constants.bundleTagString
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.bottom_sheet_map.*
import retrofit2.Call
import retrofit2.Response

/**
 * BottomSheetMapFragment class shows map on a bottom sheet by extending BottomSheetDialogFragment
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class BottomSheetMapFragment: BottomSheetDialogFragment(), OnMapReadyCallback {
    private var locationInfoDB = LocationInfoDB()
    private var styleName: String = "Light"

    private lateinit var mapView: MapView
    private lateinit var bottomSheetBinding: BottomSheetMapBinding
    private lateinit var mapBoxMap: MapboxMap
    private var lat :Double = 0.0
    private var longitude :Double = 0.0
    private lateinit var latLng: LatLng

    private val icon = "marker"
    private val routeLayerID = "route-layer-id"
    private val routeSourceID = "route-source-id"

    companion object {
        const val TAG = "Showing Position"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        activity?.let {
            Mapbox.getInstance(it.applicationContext, getString(R.string.mapbox_access_token))
        }
        bottomSheetBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_map, container, false)
        val view: View = bottomSheetBinding.root
        mapView = bottomSheetBinding.mapViewPosition
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationInfoDB = arguments?.getSerializable(Constants.bundleTagObject) as LocationInfoDB
        val twoPane = arguments?.getBoolean(Constants.bundleTagBoolean)
        styleName = arguments?.getString(bundleTagString).toString()

        lat = locationInfoDB.lat ?: 0.0
        longitude = locationInfoDB.longitude ?: 0.0
        latLng = LatLng(lat, longitude)

        if(twoPane == true){
            bottomSheetBinding.layout.visibility = View.GONE
            setHasOptionsMenu(true)
        }else{
            bottomSheetBinding.tvLocation.text = locationInfoDB.locationName
            dialog?.setOnShowListener {
                val da = it as BottomSheetDialog
                val bottom = da.bottomSheet
                bottom.let { sheet->
                    da.behavior.peekHeight = 2500
                    sheet.parent.parent.requestLayout()
                }
            }
        }
        bottomSheetBinding.imageViewMenu.setOnClickListener { viewPop->
            showPopup(viewPop)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.style_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_street){
            mapBoxMap.setStyle(Style.LIGHT){ style->
                setLocationPoints(style, latLng)
            }
        }else if (id == R.id.menu_dark){
            mapBoxMap.setStyle(Style.DARK){ style ->
                setLocationPoints(style, latLng)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapBoxMap = mapboxMap

        mapboxMap.setStyle(Style.MAPBOX_STREETS){ style->
            setLocationPoints(style, latLng)
        }

        val position = CameraPosition.Builder().target(latLng).zoom(9.0).tilt(10.0).build()
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    private fun setLocationPoints(style: Style, latLng: LatLng) {
        val desLatLng = LatLng(23.622641, 90.499794)
        val symbolManager = SymbolManager(mapView, mapBoxMap, style)
        symbolManager.iconAllowOverlap = true
        symbolManager.iconIgnorePlacement = true
        addIcon(style)

        symbolManager.create(SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(icon)
                .withIconSize(2.0f)
        )

        symbolManager.create(SymbolOptions()
                .withLatLng(LatLng(desLatLng))
                .withIconImage(icon)
                .withIconSize(2.0f))

        val origin: Point = Point.fromLngLat(latLng.longitude, latLng.latitude)
        val destination: Point = Point.fromLngLat(desLatLng.longitude, desLatLng.latitude)
        initSource(style, origin, destination)

        initLayers(style)

        getRoute(mapBoxMap, origin, destination)
    }

    /**
     * initSource function initiates the origin and destination of two points and adds them to the style.
     *
     *
     * @param style: A Style type parameter of MapBox, origin
     * @param origin: A Point for origin in the map
     * @param destination: A Point for destination in the map
     **/

    private fun initSource(style: Style, origin: Point, destination: Point) {
        style.addSource(GeoJsonSource(routeSourceID))
        val iconGeoJsonSource =
            GeoJsonSource(icon, FeatureCollection.fromFeatures(arrayOf(
                    Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                    Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))
            ))
            )
        style.addSource(iconGeoJsonSource)
    }

    /**
     * initLayers function initiates and adds a LineLayer to the map.
     *
     *
     * @param  style: A Style type parameter of MapBox
     **/

    private fun initLayers(style: Style) {
        val routeLayer = LineLayer(routeLayerID, routeSourceID)
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.BLUE)
        )
        style.addLayer(routeLayer)
    }

    /**
     * getRoute function creates the route from origin to destination.
     *
     *
     * @param mapBoxMap: A instance of MapBoxMap
     * @param origin: A Point for origin in the map
     * @param destination: A Point for destination in the map
     **/

    private fun getRoute(mapBoxMap: MapboxMap, origin: Point, destination: Point) {
        val mapBoxDirections: MapboxDirections = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build()

        mapBoxDirections.enqueueCall(
                object : retrofit2.Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        Log.wtf("rakib", "on response")
                        if (response.body() == null) {
                            return
                        } else if (response.body()!!.routes().size < 1) {
                            return
                        }

                        val currentRoute: DirectionsRoute = response.body()!!.routes()[0]

                        mapBoxMap.getStyle { style ->
                            val source: GeoJsonSource? = style.getSourceAs(routeSourceID)
                            source?.setGeoJson(currentRoute.geometry()?.let { string ->
                                LineString.fromPolyline(string, com.mapbox.core.constants.Constants.PRECISION_6)
                            })
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {

                    }
                }
        )
    }

    /**
     * addIcon function sets the marker's icon for the map into a variable
     *
     *
     * @param style: A Style type parameter of MapBox
     **/

    private fun addIcon(style: Style) {
        style.addImage(icon, BitmapUtils.getBitmapFromDrawable(activity?.let {
            ContextCompat.getDrawable(
                    it.applicationContext, R.drawable.ic_baseline_location_on)
        })!!)
    }

    /**
     * showPopup function works on setting the popup menu on BottomSheetDialog
     *
     *
     * @param  view: A View type parameter for setting up the menu
     * @return none
     **/

    private fun showPopup(view: View) {
        val popUp = PopupMenu(activity, view)
        popUp.inflate(R.menu.style_menu)
        popUp.setOnMenuItemClickListener{ item->
            when(item.itemId){
                R.id.menu_street -> {
                    mapBoxMap.setStyle(Style.MAPBOX_STREETS){ style->
                        setLocationPoints(style, latLng)
                    }
                }
                R.id.menu_dark ->{
                    mapBoxMap.setStyle(Style.DARK){style->
                        setLocationPoints(style, latLng)
                    }
                }
            }
            true
        }
        popUp.show()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}


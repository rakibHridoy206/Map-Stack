package com.example.hridoy.map_stack_demo_project.ui.main.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.example.hridoy.map_stack_demo_project.R
import com.example.hridoy.map_stack_demo_project.data.model.LocationInfoDB
import com.example.hridoy.map_stack_demo_project.databinding.ItemLocationNameBinding
import com.example.hridoy.map_stack_demo_project.ui.main.view.BottomSheetMapFragment
import com.example.hridoy.map_stack_demo_project.ui.main.view.MainActivity
import com.example.hridoy.map_stack_demo_project.utils.Constants
import com.example.hridoy.map_stack_demo_project.utils.Constants.bundleTagBoolean
import com.example.hridoy.map_stack_demo_project.utils.Constants.bundleTagString
import kotlinx.android.synthetic.main.item_location_name.view.*

/**
 * LocationAdapter class is an adapter class and used to set value in the RecyclerView
 *
 * @owner   Brotecs Technologies, LLC.
 * @version 1.0.0
 * @author  Md. Rakib Mahmud Hridoy
 * @since   03/01/2021
 * @apiNote Please do not change any parameters without designer consent
 **/

class LocationAdapter(private val locationInfoDBList: ArrayList<LocationInfoDB>,
                      private val twoPane: Boolean,
                      private val styleName: String):
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    private lateinit var binding: ItemLocationNameBinding
    private var selectedPos : Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =DataBindingUtil.inflate(
                layoutInflater, R.layout.item_location_name, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationInfoDB: LocationInfoDB = locationInfoDBList[position]
        holder.bind(locationInfoDB)

        holder.itemView.locationNameCardView.setOnClickListener {
            holder.itemView.locationNameCardView.setBackgroundColor(Color.GRAY)
            notifyItemChanged(selectedPos)
            selectedPos = position

            val arg = Bundle()
            arg.putSerializable(Constants.bundleTagObject, locationInfoDB)
            arg.putBoolean(bundleTagBoolean, twoPane)
            arg.putString(bundleTagString, styleName)
            val fm = (binding.root.context as MainActivity).supportFragmentManager
            val fragment = BottomSheetMapFragment()
            fragment.arguments = arg

            if (twoPane){
                fm.beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()

            }else{
                fragment.show(fm, BottomSheetMapFragment.TAG)
            }
        }

        if (selectedPos == position){
            holder.itemView.locationNameCardView.setBackgroundColor(Color.GRAY)
        }
        else{
            holder.itemView.locationNameCardView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int {
        return locationInfoDBList.size
    }

    /**
     * ViewHolder class sets value to the ui by sending the LocationInfoDB class object to the layout
     *
     * @owner   Brotecs Technologies, LLC.
     * @version 1.0.0
     * @author  Md. Rakib Mahmud Hridoy
     * @since   03/01/2021
     * @apiNote Please do not change any parameters without designer consent
     **/

    class ViewHolder(private val binding: ItemLocationNameBinding):
            RecyclerView.ViewHolder(binding.root)  {
        /**
         * bind method gets an object type parameter from LocationInfoDB local database class and
         * sets value to the layout and also performs click events.
         *
         *
         * @param locationInfoDB
         **/

        fun bind(locationInfoDB: LocationInfoDB){
            binding.setVariable(BR.locationDB, locationInfoDB)
        }
    }
}
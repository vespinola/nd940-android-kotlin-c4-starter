package com.udacity.project4.locationreminders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import timber.log.Timber

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var reminderDataItem: ReminderDataItem
    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        reminderDataItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
//        TODO: Add the implementation of the reminder details
        binding.reminderDataItem = reminderDataItem

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.map_style
                    )
            )
            if (!success) {
                Timber.e("Style parsing failed.")
            }

        } catch (e: Resources.NotFoundException) {
            Timber.e("Can't find style. Error: $e")
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            map = googleMap

            setMapStyle(map)

            val latLng = LatLng(reminderDataItem.latitude!!, reminderDataItem.longitude!!)

            map.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .title(reminderDataItem.title)
            )

            val zoomLevel = 20f
            val homeLatLng = LatLng(latLng.latitude, latLng.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        }
    }
}

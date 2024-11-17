package com.example.virnandaelsa_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.example.virnandaelsa_3.databinding.ActivitymapsBinding
import com.google.android.gms.maps.model.LatLngBounds

class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivitymapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivitymapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup FAB buttons for map types
        binding.fabMap1.setOnClickListener {
            gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        binding.fabMap2.setOnClickListener {
            gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        binding.fabMap3.setOnClickListener {
            gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // Set default map type
        gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        // Add a marker at the default location
        val defaultLocation = LatLng(-7.831065736194604, 112.03295680717933) // Jakarta location as an example
        gMap.addMarker(MarkerOptions().position(defaultLocation).title("Wisma Rias"))

        // Draw the polygon
        drawPolygon()

        // Move the camera to a position that fits the polygon
        val polygonBounds = listOf(
            LatLng(-7.830991649238685, 112.03290642612218), // Point 1
            LatLng(-7.831037661139179, 112.03303946360121), // Point 2
            LatLng(-7.83115620024925, 112.03298829534003), // Point 3
            LatLng(-7.831096150835864, 112.03284738582083)  // Point 4
        )

        // Set the camera to fit the polygon bounds
        val builder = LatLngBounds.Builder()
        polygonBounds.forEach { builder.include(it) }
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        gMap.moveCamera(cameraUpdate)
    }

    // Function to draw the polygon on the map
    private fun drawPolygon() {
        // Define coordinates for the polygon
        val polygonPoints = listOf(
            LatLng(-7.830991649238685, 112.03290642612218), // Point 1
            LatLng(-7.831037661139179, 112.03303946360121), // Point 2
            LatLng(-7.831153080799424, 112.03298672093202), // Point 3
            LatLng(-7.831100050148678, 112.03286234269717) // Point 4
        )

        // Create the polygon options
        val polygonOptions = PolygonOptions().apply {
            addAll(polygonPoints)
            strokeColor(android.graphics.Color.RED)  // Correct color format
            fillColor(0x7F00FF00)  // Green with transparency
            strokeWidth(5f)
        }

        // Add the polygon to the map
        gMap.addPolygon(polygonOptions)
    }
}

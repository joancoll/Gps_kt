package cat.dam.andy.gps_kt

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartLocation: Button
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private val context: Context = this
    private var permissionManager = PermissionManager(context)
    private var requestingLocationUpdates = false
    private lateinit var locationManager: LocationManager
    private var locationEnabled = false // Variable de control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initPermissions()
        initListeners()
        initLocationManager()
        updateButtonScreen()
    }

    private fun initViews() {
        btnStartLocation = findViewById(R.id.btn_startLocation)
        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)
    }

    private fun initPermissions() {
        permissionManager.addPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            getString(R.string.locationPermissionInfo),
            getString(R.string.locationPermissionNeeded),
            getString(R.string.locationPermissionDenied),
            getString(R.string.locationPermissionThanks),
            getString(R.string.locationPermissionSettings)
        )
    }

    private fun initListeners() {
        btnStartLocation.setOnClickListener {
            if (!permissionManager.hasAllNeededPermissions()) {
                permissionManager.askForPermissions(permissionManager.getRejectedPermissions())
            } else {
                if (!locationManager.hasConnection()) {
                    locationManager.showEnableLocationDialog()
                } else {
                    toggleLocationUpdates()
                }
            }
        }
    }

    private fun initLocationManager() {
        val locUpdateTimeInterval: Long = 3000 // milliseconds
        val locUpdateMinimalDistance = 2.0f // meters
        locationManager = LocationManager(
            context,
            locUpdateTimeInterval,
            locUpdateMinimalDistance,
            locationListener
        )
    }

    private fun toggleLocationUpdates() {
        // Activa o desactiva el tracking de la localització segons clica l'usuari i estat actual
        if (locationEnabled) {
            locationEnabled = false
            locationManager.stopLocationTracking()
            updateButtonScreen()
        } else {
            // no canviem l'estat fins que rebem la primera localització
            requestingLocationUpdates = true
            locationManager.startLocationTracking()
        }
    }

    private fun updateButtonScreen() {
        // Actualitza el text i el botó segons l'estat de la localització
        if (!locationEnabled) {
            btnStartLocation.text = getString(R.string.start_location_updates)
            btnStartLocation.setBackgroundColor(getColor(R.color.btn_on))
            tvLatitude.text = "-----"
            tvLongitude.text = "-----"
        } else {
            btnStartLocation.text = getString(R.string.stop_location_updates)
            btnStartLocation.setBackgroundColor(getColor(R.color.btn_off))
        }
    }

    private fun updateLocationInfo(latitude: Double, longitude: Double) {
        tvLatitude.text = String.format(Locale.getDefault(), "%.4f", latitude)
        tvLongitude.text = String.format(Locale.getDefault(), "%.4f", longitude)
    }

    // LocationListener per actualitzar la informació de la localització
    // quan es produeix un canvi.

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateLocationInfo(location.latitude, location.longitude)
            locationEnabled = true
            requestingLocationUpdates = false
            updateButtonScreen()
        }

        override fun onProviderDisabled(provider: String) {
            locationEnabled = false
            requestingLocationUpdates = false
            updateButtonScreen()
            locationManager.stopLocationTracking()
            locationManager.showEnableLocationDialog()
        }

        override fun onProviderEnabled(provider: String) {
//            locationEnabled = true
//            updateButtonScreen()
//            locationManager.startLocationTracking()
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // No es requereix implementació específica aquí
        }
    }
}

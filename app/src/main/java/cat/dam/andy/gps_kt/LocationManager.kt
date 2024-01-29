package cat.dam.andy.gps_kt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class LocationManager(
    private val activityContext: Context,
    private var timeInterval: Long,
    private var minimalDistance: Float,
    private val locationListener: LocationListener
) {
    private var locationManager: LocationManager? = null

    init {
        initLocationManager()
    }

    private fun initLocationManager() {
        locationManager = activityContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun startLocationTracking() {
        if (locationManager != null) {
            // Demana actualitzacions de la ubicació mitjançant el proveïdor de xarxa i GPS
            if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(
                        activityContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activityContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    timeInterval,
                    minimalDistance,
                    locationListener
                )
            }
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    timeInterval,
                    minimalDistance,
                    locationListener
                )
            }
        }
    }

    fun hasConnection(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun stopLocationTracking() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }
    }
    fun showEnableLocationDialog() {
        val alertDialog = AlertDialog.Builder(activityContext)
        alertDialog.setTitle(activityContext.getString(R.string.location_configuration))
        alertDialog.setMessage(activityContext.getString(R.string.location_configuration_message))
        alertDialog.setPositiveButton(activityContext.getString(R.string.ok)) { _, _ ->
            // Obre la configuració de ubicació per permetre a l'usuari habilitar el GPS
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activityContext.startActivity(intent)
        }
        alertDialog.setNegativeButton(activityContext.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}

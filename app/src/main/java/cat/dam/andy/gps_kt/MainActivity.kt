package cat.dam.andy.gps_kt

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private var btnGps: Button? = null
    private var tvLatitude: TextView? = null
    private var tvLongitude: TextView? = null
    private var gpsTracker: GpsTracker? = null
    private var permissionManager: PermissionManager? = null

    private val permissionsRequired: ArrayList<PermissionData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initPermissions()
        initListeners(this@MainActivity)
        showGPSInfo(this@MainActivity)
    }

    private fun initViews() {
        btnGps = findViewById(R.id.btn_gps)
        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)
    }

    private fun initPermissions() {
        permissionsRequired.add(
            PermissionData(
                Manifest.permission.ACCESS_FINE_LOCATION,
                getString(R.string.locationPermissionNeeded),
                "",
                getString(R.string.locationPermissionThanks),
                getString(R.string.locationPermissionSettings)
            )
        )
    }

    private fun initListeners(context:Context) {
        permissionManager = PermissionManager(context, permissionsRequired)
        btnGps?.setOnClickListener {
            if (!permissionManager?.hasAllNeededPermissions(context, permissionsRequired)!!) {
                permissionManager?.askForPermissions(
                    context,
                    permissionManager!!.getRejectedPermissions(context, permissionsRequired)
                )
            } else {
                showGPSInfo(context)
            }
        }
    }

    private fun showGPSInfo(context:Context) {
        if (permissionManager?.hasAllNeededPermissions(context, permissionsRequired) == true) {
            gpsTracker = GpsTracker(context)
            if (gpsTracker!!.canGetLocation()) {
                val latitude: Double = gpsTracker!!.getLatitude()
                val longitude: Double = gpsTracker!!.getLongitude()
                tvLatitude?.text = String.format(Locale.getDefault(), "%.4f", latitude)
                tvLongitude?.text = String.format(Locale.getDefault(), "%.4f", longitude)
                Toast.makeText(
                    context, R.string.locationReady,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                gpsTracker!!.showSettingsAlert()
            }
        }
    }
}

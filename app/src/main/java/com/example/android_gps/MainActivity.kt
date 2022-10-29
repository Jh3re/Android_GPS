package com.example.android_gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_gps.Constantes.INTERVAL_TIME
import com.example.android_gps.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    //OJITO: Esta forma no es necesariamente obligatoria de usar este dato, solo es referencial
    // companion object se usa para definir constantes que seran globales en tu clase que sus
    // valores son accedidos por cualquiera instancia
    companion object{
        val PERMISSION_GRANTED = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private lateinit var binding: ActivityMainBinding

    // Nosotros usaremos la herramienta de los servicios de Google para localizacion
    private lateinit var fusedLocation: FusedLocationProviderClient
    private val PERMISSION_ID = 42
    private var isGPSEnabled = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var distance = 0.0
    private var velocity = 0.0
    private var c = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabGPS.setOnClickListener{
            enableGPSService()
        }
        binding.fabCoordinates.setOnClickListener{
            manageLocation()
        }
    }

    /**
     * Situacion: Configurar la habilitacion
     *               de GPS en el celular.
     */

    private fun enableGPSService() {
        if (!hasGPSEnabled()){
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_text_title)
                .setMessage(R.string.dialog_text_description)
                .setPositiveButton(R.string.dialog_button_accept,
                    DialogInterface.OnClickListener {
                            dialog, wich -> goToEnableGPS()
                    })
                .setNegativeButton(R.string.dialog_button_deny) {
                        dialog, wich -> isGPSEnabled = false
                }
                .setCancelable(true)
                .show()
        }else
            Toast.makeText(this,"GPS activado",Toast.LENGTH_SHORT).show()

    }

    private fun goToEnableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun hasGPSEnabled(): Boolean {

        // Manager: Es como el director de la orquesta
        //          es el que lleva la batuta.
        //          organiza y gestiona lo referido al manejo
        //          de cierto servicio o recurso

        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Situacion: Configuracion y solicitud de permisos
     * en la APP para poder usar GPS
     */
    // checkSelfPermission: Evalua el valor que tiene en tu app cierto permiso, no verifica si tienes
    // o no permiso solo ve que valor numerico tiene asignado ese permiso en tu app
    // PERMISSION_GRANTED: Es un valor numerico general en Android que representa el valor que
    // significa un permiso otorgado, los permisos que estan revisando en este metodo
    private fun allPermissionsGranted(): Boolean = PERMISSION_GRANTED.all {
        ActivityCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Formato tradicional del de arriba
    private fun checkPermission(): Boolean {
        return ActivityCompat.
        checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionUser(){
        // Lanzar la ventana al usuario para solcitar permisos o lo deniegue
        ActivityCompat.requestPermissions(this, PERMISSION_GRANTED,PERMISSION_ID)
    }

    /**
     * Situación: Obtencion de coordenadas
     * configuracion de objeto que trabaja con el sensor
     * y obtiene Localizaciones llamado FusedLocation
     */

    @SuppressLint("MissingPermission")
    private fun manageLocation(){
        if (hasGPSEnabled()){
            if (allPermissionsGranted()) {
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                fusedLocation.lastLocation.addOnSuccessListener {
                    location -> getCoordinates()
                }
            } else
                requestPermissionUser()
        }else
            goToEnableGPS()
    }

    @SuppressLint("MissingPermission")
    private fun getCoordinates() {
        // Para la version de Google gms location 21 y superiores
        var locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,INTERVAL_TIME
        ).setMaxUpdates(100).build() // Tarea 200 300

        /*val locationRequest = LocationRequest.create().apply {

            // Para versiones de Google gms location 20 e inferiores
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }*/
        fusedLocation.requestLocationUpdates(locationRequest,myLocationCallback,Looper.myLooper())
    }

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var myLastLocation: Location? = locationResult.lastLocation
            if (myLastLocation != null){
                var lastLatitude = myLastLocation.latitude
                var lastLongitude = myLastLocation.longitude

                if (c>0){
                    distance = calculateDistance(lastLatitude,lastLongitude)
                    velocity = calculateVelocity()
                }

                binding.apply {
                    txtLatitude.text = "Latitude: "+ lastLatitude.toString()
                    txtLength.text = "Longitud: "+ lastLongitude.toString()
                    txtDistance.text = "$distance mts."
                    txtVelocity.text = "$velocity Km/h "
                }
                latitude = lastLatitude//myLastLocation.latitude
                longitude = lastLongitude//myLastLocation.longitude
                c++
                getAddressInfo()

            }
        }
    }

    private fun getAddressInfo() {
        // La clase para obtener direcciones a partir
        // de coordenadas se llama Geocoder
        // Pueden obtener de 1 a n direcciones
        // Siempre en formatode Array
        val geocoder = Geocoder(this)
        try {
            //TODO en la version KT 1.9 esta deprecado
            var addresses = geocoder.getFromLocation(latitude,longitude,1)
            binding.txtDirection.text = addresses.get(0).getAddressLine(0)
        }catch (e: Exception){
            binding.txtDirection.text = "No se pudo obtener la direccion"
        }

    }
    private fun calculateDistance(lastLatitude: Double, lastLongitude: Double):Double {
        val earthRadious = 6371.0 // Kilometros
        val diffLatitude = Math.toRadians(lastLatitude - latitude)
        val diffLongitude = Math.toRadians(lastLongitude - longitude)
        val sinLatitude = sin(diffLatitude / 2)
        val sinLongitude = sin(diffLongitude / 2)
        val resul_1 = Math.pow(sinLatitude, 2.0) +
                (Math.pow(sinLongitude, 2.0) * cos(Math.toRadians(latitude)) * cos(Math.toRadians(lastLatitude)))
        val resul_2 = 2 * atan2(sqrt(resul_1), sqrt(1-resul_1))
        val distance = (earthRadious * resul_2) * 1000.0
        return distance
    }

    // Condicionales ideales
    private fun calculateVelocity():Double = (distance / INTERVAL_TIME) * 3.6
}
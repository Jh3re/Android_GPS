package com.example.android_gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_gps.databinding.ActivityMainBinding
import com.google.android.gms.location.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabGPS.setOnClickListener{
            enableGPSService()
        }
        binding.fabCoordinates.setOnClickListener{

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
            }
        }else
            goToEnableGPS()
    }

    @SuppressLint("MissingPermission")
    private fun getCoordinates() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        fusedLocation.requestLocationUpdates(locationRequest,myLocationCallback,Looper.myLooper())
    }

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult != null){
                var myLastLocation = locationResult.lastLocation
                binding.txtLatitude.text = myLastLocation.latitude.toString()
                binding.txtLength.text = myLastLocation.longitude.toString()

            }
        }
    }
}
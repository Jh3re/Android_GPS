package com.example.android_gps

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.android_gps.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient

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
}
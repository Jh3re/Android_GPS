package com.example.android_gps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.android_gps.Coordenates.casaJhere
import com.example.android_gps.Coordenates.puno
import com.example.android_gps.Coordenates.salchiSalvaje
import com.example.android_gps.Coordenates.stadium
import com.example.android_gps.Coordenates.univalle
import com.example.android_gps.Coordenates.valleLuna

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.android_gps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var c = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        // El mapa se carga asincronamente
        // No satura tu proceso principal o de la UI
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    // Respuesta cuando el mapa esta listo para trabajar el parametro que tienen devuelve el mapa de
    // Google listo y configurado
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Usted puede delimitar el rango de Zoom de la camarar para evitar que el usuario haga un
        // zoom in o out de la camara
        mMap.apply {
            setMinZoomPreference(15f)
            setMaxZoomPreference(19f)
        }

        // define coordenadas en un objeto LatLng que conjunciona latitud y lengitud
        val bgtColombia = LatLng(4.707138568742354, -74.06524803384755)
        // Marcadores  ....
        mMap.addMarker(MarkerOptions().position(bgtColombia).title("Lugar feliz").snippet("${bgtColombia.latitude},${bgtColombia.longitude}"))

        // Posicionar la camara en la ubicacion de preferencia
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bgtColombia,20f))

        /**
         * Configuracion de su camara personalizada
         */
        val customCamera = CameraPosition.Builder()
            .target(univalle) // donde apunta la camara
            .zoom(17f) // 15 y 18 calles 20 edificios
            .tilt(90f) // angulo de inclinacion de la camara, no deberia ser agresivos
            .bearing(195f) // cambio de orientacion de 0 a 360
            .build()
        // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(customCamera))

        /**
         * Movimiento de la camara (animacion de la camara)
         * Plus--- uso standar de corrutinas
         */

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(univalle, 17f))
        // Corrutinas para apreciar mejor el movimiento
        lifecycleScope.launch {
            delay(5000)
            // Para mover la camara entre puntos puntos en el mapa se recomienda una animacion
            // se usa animateCamera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(casaJhere,17f))
            mMap.addMarker(MarkerOptions().position(casaJhere).title("Mi casa").snippet("${casaJhere.latitude},${casaJhere.longitude}"))
            delay(10000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stadium,17f))
            mMap.addMarker(MarkerOptions().position(stadium).title("Stadium").snippet("${stadium.latitude},${stadium.longitude}"))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(valleLuna,17f))
            mMap.addMarker(MarkerOptions().position(valleLuna).title("Valle de la luna").snippet("${valleLuna.latitude},${valleLuna.longitude}"))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(salchiSalvaje,17f))
            mMap.addMarker(MarkerOptions().position(salchiSalvaje).title("Salchichas").snippet("${salchiSalvaje.latitude},${salchiSalvaje.longitude}"))
            delay(10000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puno,17f))
            mMap.addMarker(MarkerOptions().position(puno).title("Meta... PUNO-PERU").snippet("${puno.latitude},${puno.longitude}"))
        }

        mMap.setOnMapClickListener {

            if(c<4){
                mMap.addMarker(MarkerOptions().position(it).title("Mi nueva posicion").snippet("${it.latitude}, ${it.longitude}").draggable(true))
            }else{
                Toast.makeText(this,"Ya completo el numero de marcas",Toast.LENGTH_SHORT).show()
            }
            c++

            /*
            * Configuracion de controles de Ui y Gestures del mapa
            * */
            mMap.uiSettings.apply {
                isZoomControlsEnabled=true // Controles de zoom
                isCompassEnabled=true// Habilita el compas de la orientacion
                isMapToolbarEnabled=true// Botones complementarios del mapa

            }
        }

    }

}
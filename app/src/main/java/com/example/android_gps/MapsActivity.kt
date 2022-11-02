package com.example.android_gps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.android_gps.databinding.ActivityMapsBinding

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bgtColombia,20f))

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
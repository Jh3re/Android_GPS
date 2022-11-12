package com.example.android_gps

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.android_gps.Coordenates.casaJhere
import com.example.android_gps.Coordenates.cementerioJudios
import com.example.android_gps.Coordenates.lapaz
import com.example.android_gps.Coordenates.plazaAbaroa
import com.example.android_gps.Coordenates.puno
import com.example.android_gps.Coordenates.salchiSalvaje
import com.example.android_gps.Coordenates.stadium
import com.example.android_gps.Coordenates.univalle
import com.example.android_gps.Coordenates.valleLuna

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.android_gps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var polyline: Polyline;
    private lateinit var handler : Handler;

    private var c = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.binding = binding

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        // El mapa se carga asincronamente
        // No satura tu proceso principal o de la UI
        mapFragment.getMapAsync(this)
        // Activar evento listener de conjunto de botones
        setupToggleButttons()
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
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var ptlist = ArrayList<LatLng>()

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

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(univalle, 17f))
        // Corrutinas para apreciar mejor el movimiento
        handler = Handler(Looper.getMainLooper());

        polyline = mMap.addPolyline(PolylineOptions().addAll(ptlist));

        var runnableCode = object: Runnable {
            override fun run() {
                var w = polyline.width;
                w = w + 0.5f;
                if (w > 25.0) {
                    w = 1.0f;
                }
                polyline.setWidth(w);
                handler.postDelayed(this, 50);
            }
        }
        /**
        lifecycleScope.launch {
            delay(5000)
            // Para mover la camara entre puntos puntos en el mapa se recomienda una animacion
            // se usa animateCamera
            ptlist.add(LatLng(casaJhere.latitude,casaJhere.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(casaJhere,17f))
            mMap.addMarker(MarkerOptions().position(casaJhere).title("Mi casa").snippet("${casaJhere.latitude},${casaJhere.longitude}"))
            handler.postDelayed(runnableCode, 50);

            ptlist.add(LatLng(stadium.latitude, stadium.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stadium,17f))
            mMap.addMarker(MarkerOptions().position(stadium).title("Stadium").snippet("${stadium.latitude},${stadium.longitude}"))
            handler.postDelayed(runnableCode, 50);

            ptlist.add(LatLng(valleLuna.latitude, valleLuna.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(valleLuna,17f))
            mMap.addMarker(MarkerOptions().position(valleLuna).title("Valle de la luna").snippet("${valleLuna.latitude},${valleLuna.longitude}"))
            handler.postDelayed(runnableCode, 50);

            ptlist.add(LatLng(salchiSalvaje.latitude, salchiSalvaje.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(salchiSalvaje,17f))
            mMap.addMarker(MarkerOptions().position(salchiSalvaje).title("Salchichas").snippet("${salchiSalvaje.latitude},${salchiSalvaje.longitude}"))
            handler.postDelayed(runnableCode, 50);

            ptlist.add(LatLng(puno.latitude,puno.longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puno,17f))
            mMap.addMarker(MarkerOptions().position(puno).title("Meta... PUNO-PERU").snippet("${puno.latitude},${puno.longitude}"))
        }*/

        /**
         * Movimiento de cámara por pixeles puede ser horizontal, vertical o combinado
         */
        /*lifecycleScope.launch {
            delay(5_000)
            for (i in 0..100){
                mMap.animateCamera(CameraUpdateFactory.scrollBy(0f,140f))
                delay(500)
            }
        }*/

        /**
         * Bounds para delimitar areas de accion en el mapa, armar sesgos
         */
        val univalleBounds = LatLngBounds(plazaAbaroa, cementerioJudios)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lapaz,15f))
        lifecycleScope.launch {
            delay(3_500)
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(univalleBounds,Utils.dp(32)))
            // Punto central del cuadrante definido
            delay(2000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(univalleBounds.center,18f))

        }
        // Como delimitar al area
        mMap.setLatLngBoundsForCameraTarget(univalleBounds)

        // Activar la posicion actual en el mapa
        // Evaluar permisos de GPS
        mMap.isMyLocationEnabled = true

        mMap.setOnMapClickListener {

            if(c<4){
                mMap.addMarker(MarkerOptions().position(it).title("Mi nueva posicion").snippet("${it.latitude}, ${it.longitude}").draggable(true))
            }else{
                Toast.makeText(this,"Ya completo el numero de marcas",Toast.LENGTH_SHORT).show()
            }
            c++

        }
        /**
         * Configuracion de controles de Ui y Gestures del mapa
         */
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
            isZoomControlsEnabled=true // Controles de zoom
            isCompassEnabled=true// Habilita el compas de la orientacion
            isMapToolbarEnabled=true// Botones complementarios del mapa
            isRotateGesturesEnabled = false // Ya no pueden rotar  el mapa
            isTiltGesturesEnabled = false // Ya no pueden inclinar la camara
            isZoomGesturesEnabled = true // habilitar o deshabilitar gesture de zoom
        }

        /**
         * Configuracion, personalizacion, estilos de mapa
         */
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style))

        /**
         * Configuracion y personalizacion de marcadores
         * Estilos, formas y eventos
         */
        val univalleMarker = mMap.addMarker(MarkerOptions().title("La U").position(univalle))
        univalleMarker?.run {
            // Tonos definidos por android
            // setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            // color hue
            // setIcon(BitmapDescriptorFactory.defaultMarker(74f))
            // Marcador personalizado desde imagenes de vectores
            Utils.getBitmapFromVector(this@MapsActivity, R.drawable.ic_baseline_fireplace_24)?.let {
                setIcon(BitmapDescriptorFactory.fromBitmap(it))
            }
            //setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant))
            rotation = 145f
            setAnchor(0.5f,0.5f)
            isFlat = true
            isDraggable = true
            snippet = "Texto alternativo"
        }

        // Eventos en markers
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)

        /**
         * Trazado de rutas
         * partiendo de dibujar lineas en el mapa
         * entre puntos de coordenadas
         * (Polyline)
         */
        setupPolyline()

    }

    // Dibujar una linea en el mapa
    private fun setupPolyline() {
        // Tener una ruta en formato de array o lista
        val miRuta = mutableListOf(univalle,stadium, cementerioJudios)
        // se configura y crea la linea
        val polyline = mMap.addPolyline(PolylineOptions()
            .color(Color.WHITE) // color de la linea
            .width(12f) // Ancho de la linea
            .clickable(true) // Permite que a la linea le puedes hacer click
            .geodesic(true) // Define la linea respetando la curvatura de la tierra


        )
        // Tienes que indicar los puntos sobre los que se vam a dibujar
        // polyline.points = miRuta
        lifecycleScope.launch {
            val miRutaPersonalizada = mutableListOf<LatLng>(univalle,stadium, cementerioJudios)
            for (punto in miRuta){
                miRutaPersonalizada.add(punto)
                polyline.points = miRutaPersonalizada
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punto,15f))
                delay(3_500)
            }
            // Configurar la interseccion o union de lineas
            // polyline.width = 10f
            // polyline.jointType = JointType.ROUND
            // polyline.jointType = JointType.BEVEL

            // Personalizar el patron de la linea
            // 1) Linea continua por defecto
            // 2) Que la linea sea dibujada por puntos separados por un espacio
            // 3) Segmentos de linea separados por espacio o guiones
            polyline.pattern = listOf(Dot(), Gap(32f), Dash(16f), Gap(32f))
        }


    }

    private fun setupToggleButttons(){
        binding.toggleGroup.addOnButtonCheckedListener {
                group, checkedId, isChecked ->
            if (isChecked){
                mMap.mapType = when(checkedId) {
                    R.id.btnNormal -> GoogleMap.MAP_TYPE_NORMAL
                    R.id.btnHybrid -> GoogleMap.MAP_TYPE_HYBRID
                    R.id.btnSatellite -> GoogleMap.MAP_TYPE_SATELLITE
                    R.id.btnTerrain -> GoogleMap.MAP_TYPE_TERRAIN
                    else -> {GoogleMap.MAP_TYPE_NONE}
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // marker: es el marcador al que se hace click
        Toast.makeText(this,"${marker.position.latitude}, ${marker.position.longitude}",Toast.LENGTH_SHORT).show()
        return false
    }

    // Cuando el marcador esta siendo arrastrado por el mapa
    override fun onMarkerDrag(marker: Marker) {
        // marker: el marcador que estas arrastrando
        binding.toggleGroup.visibility = View.INVISIBLE
        marker.alpha = 0.4f

    }

    // Cuando se suelta el marcador luego de haberlo arrastrado
    override fun onMarkerDragEnd(marker: Marker) {
        binding.toggleGroup.visibility = View.VISIBLE
        marker.alpha = 1.0f
        // Sirve para desplegar la ventana de informacion
        // llamada infoWindow
        marker.showInfoWindow()
    }

    // Cuando esta empezando a ser arrastrado el marcador
    override fun onMarkerDragStart(marker: Marker) {
        // Ocultar la ventana de informacion
        marker.hideInfoWindow()
    }

}

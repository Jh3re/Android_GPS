package com.example.android_gps

import androidx.viewbinding.ViewBinding

object Utils {
    // Pantalla necesitan referencia de la pantalla representada en codigo....
    var binding: ViewBinding? = null

    // Funcion para estimar los pixeles en base a la densidad de pixeles por pantalla

    fun dp(pixeles: Int): Int {
        if (binding == null) return 0
        val escala = binding!!.root.resources.displayMetrics.density
        return (escala * pixeles + 0.5f).toInt()
    }
}
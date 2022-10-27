package com.example.android_gps

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_gps.Constantes.KEY_HOBBY
import com.example.android_gps.Constantes.KEY_VALORATION
import com.example.android_gps.databinding.ActivityPersistenciaBinding

class PersistenciaActivity : AppCompatActivity() {
    private lateinit var sharedPreference : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: ActivityPersistenciaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeSharedPreference()
        binding.btnProcesar.setOnClickListener {
            saveData()
            loadData()
        }
    }

    private fun initializeSharedPreference() {
        //Primero busca el dispositivo un archivo con ese nombre
        // si no existe el archivo lo va crear
        // pero si existe va a traer el archivo
        // asignando el archivo a una variable para manejar el archivo por codigo
        sharedPreference = getSharedPreferences("persistencia", MODE_PRIVATE)
        // considerar tener una variable donde gestionen el archivo en modo de escritura
        editor = sharedPreference.edit() // editor es el archivo en modo escritura
    }

    // Guardar datos en el archivo
    private fun saveData(){
        // En los Shared Preference la informacion se guarda en formato de registros
        // cada registro se guarda en formato LLAVE - VALOR
        val myHobby = binding.txtHobby.text.toString()
        // Cuando guarda, primero barre el archivo y busca si existe ya esta llave
        // si ya existe en ese registro va a reemplazar el valor
        // si no existe recien va a crear el registro
        editor.apply{
            putString(KEY_HOBBY, myHobby)
            putInt(KEY_VALORATION,100)
        }.apply()
        // 1) usar apply(): es un guardado asincrono
        // 1) usar commit(): es un guardado sincrono

    }

    private fun loadData(){
        val myHobby = sharedPreference.getString(KEY_HOBBY,"vacio")
        val myValor = sharedPreference.getInt(KEY_VALORATION, 0)
        binding.txtResultado.text = "Mis datos son: $myHobby, $myValor ..."
    }
}
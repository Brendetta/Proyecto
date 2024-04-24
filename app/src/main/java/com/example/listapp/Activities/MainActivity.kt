package com.example.listapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.listapp.DB.BaseDatos
import com.example.listapp.Data.UserData
import com.example.listapp.R
import com.example.listapp.UserStore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvNombre : EditText = findViewById(R.id.nombre_usuario)
        val tvPass : EditText = findViewById(R.id.clave)
        val cbRecordar = findViewById<CheckBox>(R.id.recordar_usuario)
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)

        val nombrePrevio = sharedPreferences.getString("Username", "")
        val clavePrevio = sharedPreferences.getString("Password", "")
        val recordarPrevio = sharedPreferences.getBoolean("RecordarUsuario", false)

        tvNombre.setText(nombrePrevio)
        tvPass.setText(clavePrevio)
        cbRecordar.isChecked = recordarPrevio

        val btnLogin = findViewById<Button>(R.id.login)

        btnLogin.setOnClickListener {
            val nombre = tvNombre.text.toString()
            val clave = tvPass.text.toString()

            if(cbRecordar.isChecked) {
                sharedPreferences.edit().putString("Username", nombre).apply()
                sharedPreferences.edit().putString("Password", clave).apply()
                sharedPreferences.edit().putBoolean("RecordarUsuario", true).apply()
            } else {
                sharedPreferences.edit().remove("Username").apply()
                sharedPreferences.edit().remove("Password").apply()
                sharedPreferences.edit().remove("RecordarUsuario").apply()
            }

            val dbHelper = BaseDatos(this)

            var login: UserData? = null

            if(nombre.isNotBlank() && clave.isNotBlank()){
                login = dbHelper.comprobarUsuario(nombre, clave)
            }

            if(login != null){
                val userStore = UserStore.getInstance()
                userStore.setUser(login)
                val intent = Intent(this, ListaCompra::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        val crearCuenta = findViewById<TextView>(R.id.crear_cuenta)

        crearCuenta.setOnClickListener{
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }
}

package com.example.listapp.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.listapp.DB.BaseDatos
import com.example.listapp.R


class Registro : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //regresar al inicio de sesión
        val volverInicio = findViewById<TextView>(R.id.volver_inicio)

        volverInicio.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        }
        //insertar en la BBDD
        val btnAceptar = findViewById<Button>(R.id.inicio_sesion)

        btnAceptar.setOnClickListener{
            val tvNombre : TextView = findViewById(R.id.Usuario)
            val tvCorreo_electronico: TextView = findViewById(R.id.Correo)
            val tvPassword : TextView = findViewById(R.id.Password)
            val tvConfirmar: TextView = findViewById(R.id.Repetir)

            val nombre = tvNombre.text.toString()
            val correo = tvCorreo_electronico.text.toString()
            val pass = tvPassword.text.toString()
            val confirm = tvConfirmar.text.toString()

            if (pass.equals(confirm)){
                val dbHelper = BaseDatos(this)
                val nuevoUsuario = dbHelper.insertarUsuario(nombre, correo, pass)
                if(nuevoUsuario){
                    Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show()
                tvConfirmar.setText("")
            }
        }

    }


}
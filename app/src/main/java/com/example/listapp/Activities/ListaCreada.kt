package com.example.listapp.Activities

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.listapp.DB.BaseDatos
import com.example.listapp.Data.ListaCompraData
import com.example.listapp.Data.ProductoData
import com.example.listapp.R


class ListaCreada : AppCompatActivity() {
    var productos: MutableList<ProductoData> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_creada)

        val listaCompra = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("listaCompra", ListaCompraData::class.java)
        }else{
            intent.getParcelableExtra<ListaCompraData>("listaCompra")
        }
        val tvTitulo : TextView = findViewById(R.id.titulo_lista)
        tvTitulo.setText(listaCompra?.titulo)
        val dbHelper = BaseDatos(this)
        productos = dbHelper.recuperarProductos(listaCompra?.id)

        val listView = findViewById<ListView>(R.id.listado_productos)

        //Creamos un ArrayAdapter para manejar los elementos de la lista
        val listaAdapter = ArrayAdapter<ProductoData>(this, android.R.layout.simple_list_item_checked, productos)

        // Asignamos el adaptador a la ListView
        listView.adapter = listaAdapter

        this.actualizaChecked()

        listView.setOnItemClickListener{ _, view, position, _ ->
            val producto = productos.get(position)

            if(producto.comprado == 1) {
                producto.comprado = 0
            } else {
                producto.comprado = 1
            }

            this.actualizaChecked()
            if (listaCompra != null) {
                dbHelper.insertarProducto(producto.id, producto.nombre, producto.comprado, listaCompra.id)
            }
        }
    }

    fun actualizaChecked() {
        val listView = findViewById<ListView>(R.id.listado_productos)
        listView.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE)
        for(i in 0..productos.size - 1) {
            listView.setItemChecked (i, productos.get(i).comprado == 1);
        }
    }
}

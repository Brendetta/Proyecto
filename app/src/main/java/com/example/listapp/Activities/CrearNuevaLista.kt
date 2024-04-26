package com.example.listapp.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listapp.DB.BaseDatos
import com.example.listapp.Data.ListaCompraData
import com.example.listapp.Data.ProductoData
import com.example.listapp.Data.UserData
import com.example.listapp.R
import com.example.listapp.UserStore


class CrearNuevaLista : AppCompatActivity() {

    var productosEliminados : MutableList<ProductoData> = mutableListOf()
    var productos: MutableList<ProductoData> = mutableListOf()
    var productoSeleccionado: ProductoData? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nueva_lista)

        val listaCompra = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("listaCompra", ListaCompraData::class.java)
        }else{
            intent.getParcelableExtra<ListaCompraData>("listaCompra")
        }
        //Dar un título a la nueva lista
        val tvTitulo : TextView = findViewById(R.id.titulo)
        tvTitulo.text = listaCompra?.titulo
        // Recuperar productos si estamos en editar
        if(listaCompra != null) {
            val dbHelper = BaseDatos(this)
            productos = dbHelper.recuperarProductos(listaCompra.id)
        } else {
            productos = mutableListOf()
        }
        this.actualizaLVProductos()
        productosEliminados = mutableListOf()

        //insertar en la BBDD
        val btnAceptar = findViewById<Button>(R.id.guardar_lista)
        btnAceptar.setOnClickListener{
            val userStore = UserStore.getInstance()
            var user: UserData? = userStore.getUser()
            val userId = user?.id

            val id = listaCompra?.id
            val titulo = tvTitulo.text.toString()

            val dbHelper = BaseDatos(this)
            val listaResult = dbHelper.guardarLista(id, titulo, userId, this.productos, this.productosEliminados)
            if(listaResult){
                if(id == null) {
                    Toast.makeText(this, "Lista creada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lista actualizada correctamente", Toast.LENGTH_SHORT).show()
                }
                finish()
            }else{
                Toast.makeText(this, "No se pudo crear la lista", Toast.LENGTH_SHORT).show()
            }
        }

        val btnAddNuevoProducto = findViewById<ImageButton>(R.id.addNuevoProducto)
        btnAddNuevoProducto.setOnClickListener{
            val etNuevoProducto = findViewById<EditText>(R.id.nuevoProducto)
            val nuevoProducto = etNuevoProducto.text.toString()
            etNuevoProducto.setText("")

            productos.add(ProductoData(null, nuevoProducto, 0))
            this.actualizaLVProductos()
        }

        val listView = findViewById<ListView>(R.id.lista_productos)
        listView.setOnItemClickListener { parent, view, position, id ->
            productoSeleccionado = listView.adapter.getItem(position) as ProductoData?
            this.editarProducto()
        }
        listView.setOnItemLongClickListener {parent, view, position, id ->
            productoSeleccionado = listView.adapter.getItem(position) as ProductoData?
            this.borrarProducto()
            return@setOnItemLongClickListener(true)
        }

    }

    fun actualizaLVProductos() {
        val listView = findViewById<ListView>(R.id.lista_productos)

        //Creamos un ArrayAdapter para manejar los elementos de la lista
        val listaAdapter = ArrayAdapter<ProductoData>(this, android.R.layout.simple_list_item_1, productos)

        // Asignamos el adaptador a la ListView
        listView.adapter = listaAdapter
    }


    fun editarProducto() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Editar producto")

        val viewInflated: View = LayoutInflater.from(this)
            .inflate(R.layout.input_simple_layout, null, false)
        val input = viewInflated.findViewById<View>(R.id.input_simple) as EditText
        input.setText(this.productoSeleccionado?.nombre)
        builder.setView(viewInflated)

        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
            dialog.dismiss()
            productoSeleccionado?.nombre = input.getText().toString()
            this.actualizaLVProductos()
        }
        builder.setNegativeButton(
            "Cancelar"
        ) { dialog, which -> dialog.cancel() }


        builder.show()
    }
    fun borrarProducto() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("¿Quieres eliminar este producto?")

        builder.setPositiveButton(
            "Si"
        ) { dialog, _ ->
            dialog.dismiss()
            productos?.remove(productoSeleccionado)
            productoSeleccionado?.let { productosEliminados.add(it) }
            this.actualizaLVProductos()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.cancel() }


        builder.show()
    }

}


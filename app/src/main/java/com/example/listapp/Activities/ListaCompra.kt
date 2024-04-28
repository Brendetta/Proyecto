package com.example.listapp.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.listapp.DB.BaseDatos
import com.example.listapp.Data.ListaCompraData
import com.example.listapp.Data.ProductoData
import com.example.listapp.Data.UserData
import com.example.listapp.R
import com.example.listapp.UserStore


class ListaCompra : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_compra)

        // evento boton crear nueva lista
        val btnAdd = findViewById<ImageButton>(R.id.add_new)
        btnAdd.setOnClickListener {
            val intent = Intent(this, CrearNuevaLista::class.java)
            startActivity(intent)
        }

        //evento pulsación larga de la lista
        val listView = findViewById<ListView>(R.id.lista)
        registerForContextMenu(listView)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.contextual_menu,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val listView = findViewById<ListView>(R.id.lista)
        val listaCompraData: ListaCompraData = listView.adapter.getItem(pos) as ListaCompraData

        return when(item.itemId) {
            R.id.mModificar -> {
                val intent = Intent(this, CrearNuevaLista::class.java)
                intent.putExtra("listaCompra", listaCompraData)
                startActivity(intent)
                return true
            }
            R.id.mEliminar -> {
                val dbHelper = BaseDatos(this)
                dbHelper.eliminarLista(listaCompraData?.id)
                this.actualizarLvLista()
                return true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
        this.actualizarLvLista()
    }
    fun actualizarLvLista(){
        val userStore = UserStore.getInstance()
        var user: UserData? = userStore.getUser()
        val userId = user?.id

        val dbHelper = BaseDatos(this)
        val listaResult: MutableList<ListaCompraData> = dbHelper.recuperarListas(userId)

        val listView = findViewById<ListView>(R.id.lista)

        //Creamos un ArrayAdapter para manejar los elementos de la lista
        val listaAdapter = ArrayAdapter<ListaCompraData>(this, android.R.layout.simple_list_item_1, listaResult)

        // Asignamos el adaptador a la ListView
        listView.adapter = listaAdapter

        listView.setOnItemClickListener{ _, _, position, _ ->
            val listaCompra = listaResult[position]
            val intent = Intent(this, ListaCreada::class.java)
            intent.putExtra("listaCompra", listaCompra)
            startActivity(intent)
        }
    }
}


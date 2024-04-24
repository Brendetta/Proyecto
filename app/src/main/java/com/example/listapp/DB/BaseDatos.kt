package com.example.listapp.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listapp.Data.ListaCompraData
import com.example.listapp.Data.ProductoData
import com.example.listapp.Data.UserData

class BaseDatos(context: AppCompatActivity) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        private const val DATABASE_NAME = "listapp.db"
        private const val DATABASE_VERSION = 1

    }

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(CrearTablaUsuario())
        db.execSQL(CrearTablaProducto())
        db.execSQL(CrearTablaLista())
    }
    private fun CrearTablaUsuario(): String{
        return  ("CREATE TABLE IF NOT EXISTS Usuarios( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT, " +
                "correo_electronico TEXT UNIQUE," +
                "password TEXT)")
    }

    private fun CrearTablaLista(): String{
        return ("CREATE TABLE IF NOT EXISTS Lista_compra(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titulo TEXT," +
                "id_usuario INTEGER," +
                "FOREIGN KEY(id_usuario) REFERENCES Usuarios(id) ON DELETE CASCADE)")
    }
    private fun CrearTablaProducto(): String{
        return ("CREATE TABLE IF NOT EXISTS Productos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "id_lista INTEGER," +
                "FOREIGN KEY(id_lista) REFERENCES Lista_compra(id))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Prod_lista")
        db.execSQL("DROP TABLE IF EXISTS Lista_compra")
        db.execSQL("DROP TABLE IF EXISTS Productos")
        db.execSQL("DROP TABLE IF EXISTS Usuarios")
        onCreate(db)
    }

    fun insertarUsuario (nombre: String, correo_electronico:String, password: String): Boolean{
        val db: SQLiteDatabase = writableDatabase
        var result = false;
        val values: ContentValues = ContentValues().apply{
            put("nombre", nombre)
            put("correo_electronico", correo_electronico)
            put("password", password)
        }
        try{
            db.insertOrThrow("usuarios", null, values)
            result = true
        }catch(error: Exception){
            result = false
        }
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun comprobarUsuario (usuario: String, password: String): UserData? {
        var user: UserData? = null
        val db: SQLiteDatabase = writableDatabase
        val query = "SELECT * FROM usuarios WHERE correo_electronico = ? AND password = ?"
        val cursor: Cursor = db.rawQuery(query,arrayOf(usuario, password))

        if (cursor.count > 0) {
            cursor.moveToFirst()
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            user = UserData(id, nombre, null, null)
        }
        cursor.close()

        return user
    }

    fun guardarLista (id: Int?, titulo: String, userId: Int?, productos: MutableList<ProductoData>, productosEliminados: MutableList<ProductoData>): Boolean{
        val db: SQLiteDatabase = writableDatabase
        var result = false;
        val values: ContentValues = ContentValues().apply{
            put("id", id)
            put("titulo", titulo)
            put("id_usuario", userId)
        }
        try{
            val id_lista = db.insertWithOnConflict("Lista_compra", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            if(id_lista == -1L) {
                result = false
            } else {
                result = true

                for (producto in productos) {
                    this.insertarProducto(producto.id, producto.nombre, id_lista.toInt())
                }
                for (productoEliminado in productosEliminados) {
                    this.eliminarProducto(productoEliminado.id)
                }
            }
        }catch(error: Exception){
            result = false
        }
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun recuperarListas(id: Int?): MutableList<ListaCompraData>{
        val listaCompra = mutableListOf<ListaCompraData>()
        val db: SQLiteDatabase = writableDatabase
        val query = "SELECT * FROM Lista_compra WHERE id_usuario = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(id.toString()))

        try {
            if(cursor.moveToFirst()){
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val titulo = cursor.getString(cursor.getColumnIndex("titulo"))
                    val lista = ListaCompraData(id,titulo)
                    listaCompra.add(lista)
                }while (cursor.moveToNext())
            }
        }catch(error: Exception){
        }finally {
            cursor.close()
        }

        return listaCompra
    }
    fun insertarProducto (id: Int?, nombre: String, id_lista: Int): Boolean{
        val db: SQLiteDatabase = writableDatabase
        val contentValues = ContentValues().apply {
            put("id",id)
            put("nombre", nombre)
            put("id_lista", id_lista)
        }
        val result= db.insertWithOnConflict("Productos", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    @SuppressLint("Range")
    fun recuperarProductos(id: Int?): MutableList<ProductoData>{
        val listaProductos = mutableListOf<ProductoData>()
        val db: SQLiteDatabase = writableDatabase
        val query = "SELECT * FROM Productos WHERE id_lista = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(id.toString()))

        try {
            if(cursor.moveToFirst()){
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                    listaProductos.add(ProductoData(id, nombre))
                }while (cursor.moveToNext())
            }
        }catch(error: Exception){
        }finally {
            cursor.close()
        }

        return listaProductos
    }

    fun eliminarProducto (id: Int?): Boolean{
        val db : SQLiteDatabase = writableDatabase
        db.delete("Productos", "id=?",  arrayOf(id.toString()))
        db.close()
        return true
    }

    fun eliminarLista(id: Int?): Int {
        val productos = recuperarProductos(id)
        for(producto in productos) {
            this.eliminarProducto(producto.id)
        }
        val db : SQLiteDatabase = writableDatabase
        val idLista = id.toString()
        val resultado = db.delete("Lista_compra", "id=?", arrayOf(idLista))
        db.close()
        return resultado
    }

}


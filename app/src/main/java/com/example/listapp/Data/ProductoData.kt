package com.example.listapp.Data

import android.os.Parcel
import android.os.Parcelable

data class ProductoData(val id: Int?, var nombre: String): Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductoData> {
        override fun createFromParcel(parcel: Parcel): ProductoData {
            return ProductoData(parcel)
        }

        override fun newArray(size: Int): Array<ProductoData?> {
            return arrayOfNulls(size)
        }

    }
    override fun toString(): String{
        return nombre
    }

    fun remove(position: Any) {

    }

}
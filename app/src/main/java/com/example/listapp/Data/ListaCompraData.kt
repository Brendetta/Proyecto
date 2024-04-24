package com.example.listapp.Data
import android.os.Parcel
import android.os.Parcelable

data class ListaCompraData (var id: Int, var titulo: String) : Parcelable {
    constructor(titulo: String) : this(0, titulo)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString()
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titulo)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<ListaCompraData> {

        override fun createFromParcel(parcel: Parcel): ListaCompraData {
            return ListaCompraData(parcel)
        }
        override fun newArray(size: Int): Array<ListaCompraData?> {
            return arrayOfNulls(size)
        }

    }
        //Para que se muestre en la lista el título que le he puesto
        override fun toString(): String{
            return titulo
        }
}
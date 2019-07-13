package dmitriy.deomin.aimpradioplalist.custom

import android.os.Parcel
import android.os.Parcelable



data class Radio(val name: String,
                 val kategory: String = "",
                 val kbps: String = "",
                 val url: String,
                 val user_name: String = "",
                 val id_user: String = "",
                 val id:String="")

data class Link(val kbps: String, val url: String)

data class RadioPop(val name: String,
                    val ava_url: String,
                    val link1: Link,
                    val link2: Link,
                    val link3: Link,
                    val link4: Link,
                    val link5: Link)

data class Theme(val name: String, val fon: Int, val item: Int, val text: Int, val text_context: Int)

data class History(val name: String, val url: String, val data_time: String)

data class Koment(val user_name:String="",val user_id:String="",val text:String="",val data:String="",val kom_id:String=""):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_name)
        parcel.writeString(user_id)
        parcel.writeString(text)
        parcel.writeString(data)
        parcel.writeString(kom_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Koment> {
        override fun createFromParcel(parcel: Parcel): Koment {
            return Koment(parcel)
        }

        override fun newArray(size: Int): Array<Koment?> {
            return arrayOfNulls(size)
        }
    }
}


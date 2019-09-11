package com.msproject.myhome.dailycloset

import java.io.Serializable

class MyLocation(latitude:Double, longitude:Double, locality:String):Serializable{
    private var locality = locality
    private var latitude = latitude
    private var longitude = longitude
    fun getLocality():String{
        return locality;
    }
    fun getLatitude():Double{
        return latitude
    }
    fun getLongitude():Double{
        return longitude
    }
}
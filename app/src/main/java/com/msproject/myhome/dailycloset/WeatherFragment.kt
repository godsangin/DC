package com.msproject.myhome.dailycloset

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import androidx.core.content.ContextCompat
import android.os.Build
import android.widget.Toast
import java.nio.file.Files.size
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import android.view.*
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.joda.time.LocalDate
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.lang.IndexOutOfBoundsException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

private const val ARG_PARAM1 = "location"


class WeatherFragment() : Fragment() {

    lateinit var backgroundImageView:ImageView
    lateinit var weatherImageView: ImageView
    lateinit var weatherTextView: TextView
    lateinit var dateTextView: TextView
    lateinit var windTextView: TextView
    lateinit var rainTextView: TextView
    lateinit var cloudTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var minTempTextView: TextView
    lateinit var maxTempTextView: TextView
    lateinit var loadingImageView:ImageView
    private lateinit var date:LocalDate
    private lateinit var myBottomNavigationInteractionListener: BottomNavigationInteractionListener
    lateinit var mAdView : AdView
    var myLocation:MyLocation? = null

    fun setBottomNavigationInteractionListener(myBottomNavigationInteractionListener: BottomNavigationInteractionListener){
        this.myBottomNavigationInteractionListener = myBottomNavigationInteractionListener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myLocation = it.getSerializable(ARG_PARAM1) as MyLocation?
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        backgroundImageView = view.findViewById(R.id.background_image)
        weatherImageView = view.findViewById(R.id.weather_image)
        weatherTextView = view.findViewById(R.id.weather_text)
        dateTextView = view.findViewById(R.id.date_text)
        windTextView = view.findViewById(R.id.wind_text)
        rainTextView = view.findViewById(R.id.rain_text)
        cloudTextView = view.findViewById(R.id.cloud_text)
        locationTextView = view.findViewById(R.id.location_text)
        minTempTextView = view.findViewById(R.id.min_temp)
        maxTempTextView = view.findViewById(R.id.max_temp)
        loadingImageView = view.findViewById(R.id.loading_image)
        date = LocalDate()

        Glide.with(this).load(R.raw.loading).into(loadingImageView)
        setLanguage()
        setWeatherView(this.myLocation)
        getWeatherData(myLocation?.getLatitude(), myLocation?.getLongitude())
        MobileAds.initialize(context, "ca-app-pub-3136625326865731~3192081537")
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        return view
    }

    fun setWeatherView(myLocation: MyLocation?){
        if(myLocation == null){

        }
        locationTextView.setText(myLocation?.getLocality())
        dateTextView.setText(date.toString("yyyy/MM/dd"))
        //위도경도로 api콜 ㄱㄱ
    }

    fun getWeatherData(latitude: Double?, longitude: Double?){
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + getString(R.string.openweatherAPIKEY)
        Log.d("url==", url)
        val receiveUseTask = ReceiveWeatherTask();
        receiveUseTask.execute(url)
    }

    inner class ReceiveWeatherTask: AsyncTask<String, Void, JSONObject>() {
        override fun doInBackground(vararg datas: String?): JSONObject? {
            Log.d("url2==", datas[0])
            try {
                var conn:HttpURLConnection = URL(datas[0]).openConnection() as HttpURLConnection;
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.connect()

                if(conn.responseCode == HttpURLConnection.HTTP_OK){
                    val inputStream = conn.inputStream
                    val reader = InputStreamReader(inputStream)
                    val br = BufferedReader(reader as Reader?)
                    var readed:String = br.readLine()
                    Log.d("readed==", readed)
                    if(readed != null){
                        return JSONObject(readed)
                    }
//                    while(readed != null){
//                        val JObject = JSONObject(readed)
//                        readed = br.readLine()
//                        return JObject
//                    }
                }else{
                    return null
                }
            return null
            }catch(e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: JSONObject?) {
            Log.d("result==", result.toString())
            if(result != null){
                var iconName = ""
                var nowTemp = ""
                var maxTemp = ""
                var minTemp = ""
                var humidity = ""
                var speed = ""
                var main = ""
                var description = ""
                var cloud = ""
                try{
                    iconName = result.getJSONArray("weather").getJSONObject(0).getString("icon")
                    nowTemp = result.getJSONObject("main").getString("temp")
                    humidity = result.getJSONObject("main").getString("humidity")
                    minTemp = result.getJSONObject("main").getString("temp_min")
                    maxTemp = result.getJSONObject("main").getString("temp_max")
                    speed = result.getJSONObject("wind").getString("speed")
                    main = result.getJSONArray("weather").getJSONObject(0).getString("main")
                    val weather = result.getJSONArray("weather")
                    if(weather != null){
                        description = weather.getJSONObject(0).getString("description")
                    }else{
                        description = "sunny"
                    }

                    cloud = result.getJSONObject("clouds").getString("all")

                }catch (e:JSONException){
                    e.printStackTrace()
                }
                finally {
                    windTextView.setText(speed+"m/s")
                    rainTextView.setText(humidity+"%")//이미지변경필요(습도)
                    cloudTextView.setText(cloud+"%")
                    weatherTextView.setText(description)

                    minTempTextView.setText(minTempTextView.text.toString() + minTemp + "℃")
                    maxTempTextView.setText(maxTempTextView.text.toString() + maxTemp + "℃")
                    setIconImage(description)
                    loadingImageView.visibility = View.GONE
                    weatherImageView.visibility = View.VISIBLE
                }
            }
        }

        fun setIconImage(description:String){
            try{
                when(description.toLowerCase()) {
                    "sky is clear" -> {
                        weatherImageView.setImageResource(R.drawable.ic_sun)
                        backgroundImageView.setImageResource(R.drawable.sunny1)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_sun)
                    }
                    "clear sky" ->{
                        weatherImageView.setImageResource(R.drawable.ic_sun)
                        backgroundImageView.setImageResource(R.drawable.sunny1)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_sun)
                    }
                    "few clouds" -> {
                        weatherImageView.setImageResource(R.drawable.ic_sun)
                        backgroundImageView.setImageResource(R.drawable.sunny4)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_sun)
                    }
                    "scattered clouds" -> {
                        weatherImageView.setImageResource(R.drawable.ic_cloud_sun)
                        backgroundImageView.setImageResource(R.drawable.sunny2)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_cloud_sun)

                    }
                    "broken clouds" -> {
                        weatherImageView.setImageResource(R.drawable.ic_cloud)
                        backgroundImageView.setImageResource(R.drawable.sunny3_cloud)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_cloud)
                    }
                    "overcast clouds" -> {
                        weatherImageView.setImageResource(R.drawable.ic_clouds)
                        backgroundImageView.setImageResource(R.drawable.sunny3_cloud)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_clouds)
                    }
                    "shower rain" -> {
                        weatherImageView.setImageResource(R.drawable.ic_umbrella_drizzle)
                        backgroundImageView.setImageResource(R.drawable.rain4)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_umbrella_drizzle)
                    }
                    "light rain" -> {
                        weatherImageView.setImageResource(R.drawable.ic_umbrella_drizzle)
                        backgroundImageView.setImageResource(R.drawable.rain2)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_umbrella_drizzle)
                    }
                    "moderate rain" -> {
                        weatherImageView.setImageResource(R.drawable.ic_umbrella_drizzle)
                        backgroundImageView.setImageResource(R.drawable.rain3)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_umbrella_drizzle)
                    }
                    "rain" ->{
                        weatherImageView.setImageResource(R.drawable.ic_umbrella_drizzle)
                        backgroundImageView.setImageResource(R.drawable.rain1)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_umbrella_drizzle)
                    }
                    "thunderstorm" -> {
                        weatherImageView.setImageResource(R.drawable.ic_cloud_lightning)
                        backgroundImageView.setImageResource(R.drawable.rain1)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_cloud_lightning)
                    }
                    "snow" -> {
                        weatherImageView.setImageResource(R.drawable.ic_wi_snow_wind)
                        backgroundImageView.setImageResource(R.drawable.snow)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_wi_snow_wind)
                    }
                    "mist" -> {
                        weatherImageView.setImageResource(R.drawable.ic_cloud_sun)
                        backgroundImageView.setImageResource(R.drawable.sunny2)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_cloud_sun)
                    }
                    "fog" -> {
                        weatherImageView.setImageResource(R.drawable.ic_cloud)
                        backgroundImageView.setImageResource(R.drawable.sunny3_cloud)
                        myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_cloud)
                    }
                    else -> {
                        if(description.toLowerCase().contains("rain")){
                            weatherImageView.setImageResource(R.drawable.ic_umbrella_drizzle)
                            backgroundImageView.setImageResource(R.drawable.rain1)
                            myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_umbrella_drizzle)
                        }
                        else{
                            weatherImageView.setImageResource(R.drawable.ic_sun)
                            backgroundImageView.setImageResource(R.drawable.sunny1)
                            myBottomNavigationInteractionListener.setNavigationIcon(R.drawable.ic_sun)
                        }
                    }
                }
            }catch(e:Exception){
                e.printStackTrace()
            }catch(e:Error){
                e.printStackTrace()
            }

        }
    }

    private fun setLanguage(){
        val sharedPreferences = context?.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getInt("language", 0)
        when(language){
            0 -> {
                minTempTextView.text = getString(R.string.weather_fragment_min_temp_EN)
                maxTempTextView.text = getString(R.string.weather_fragment_max_temp_EN)
            }
            1 -> {
                minTempTextView.text = getString(R.string.weather_fragment_min_temp_KR)
                maxTempTextView.text = getString(R.string.weather_fragment_max_temp_KR)
            }
            2 -> {
                minTempTextView.text = getString(R.string.weather_fragment_min_temp_JP)
                maxTempTextView.text = getString(R.string.weather_fragment_max_temp_JP)
            }
        }
    }


    companion object{
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment sampleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(myLocation:MyLocation?) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, myLocation)
                }
            }

    }

}

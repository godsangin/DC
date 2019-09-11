package com.msproject.myhome.dailycloset

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.joda.time.LocalDate
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.util.*


class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var fragmentManager:FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var mContext: Context
    lateinit var localDate: LocalDate
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var settingSharedPreferences: SharedPreferences
    lateinit var navigationView: LinearLayout
    lateinit var menu:Menu
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private var backKeyPressedTime: Long = 0
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private var toast: Toast? = null
    private var gps: GPSInfo? = null
    private var myLocation:MyLocation? = null

    private val PERMISSIONS_ACCESS_FINE_LOCATION = 1000
    private val PERMISSIONS_ACCESS_COARSE_LOCATION = 1001
    private var isPermission = false
    private lateinit var myBottomNavigationInteractionListener:BottomNavigationInteractionListener
    private val REQUEST_INTRO_CODE = 200
    private val calendarFragment = CalendarFragment()

    val PICK_FROM_ALBUM = 2000
    private var tempFile: File? = null

    var language:Int = 0
    val settingLanguageInteractionListener = object: SettingLanguageInteractionListener{
        override fun notifyDateSetChanged() {
            setLanguage()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        sharedPreferences = getSharedPreferences("Event", Context.MODE_PRIVATE)
        settingSharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        navigationView = findViewById(R.id.navigation_view)
        localDate = LocalDate()
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        navigationView.findViewById<TextView>(R.id.navigation_text).setText("Weather")
        menu = bottomNavigationView.menu
        myBottomNavigationInteractionListener = object : BottomNavigationInteractionListener {
            override fun setNavigationIcon(imageResourceId: Int) {
                menu.findItem(R.id.navigation_weather).setIcon(imageResourceId)
            }
        }

        getPermission()

//        callPermission()  // 권한 요청을 해야 함
        try {
            if(Build.VERSION.SDK_INT != Build.VERSION_CODES.O){
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } catch (ignore: IllegalStateException) {

        }

    }
    protected fun getPermission(){
        var permissionListener: PermissionListener = object : PermissionListener{
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }

            override fun onPermissionGranted() {
                myLocation = getGps()
                setIntro()
                setLanguage()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .check()

    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.navigation_date ->{
                fragmentManager.popBackStack()
                fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, calendarFragment)
                fragmentTransaction.commit()
                navigationView.findViewById<TextView>(R.id.navigation_text).setText("Calendar")
            }
            R.id.navigation_picture -> {
                fragmentManager.popBackStack()
                fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, GalleryFragment())
                fragmentTransaction.commit()
                navigationView.findViewById<TextView>(R.id.navigation_text).setText("Gallery")
            }
            R.id.navigation_weather -> {
                fragmentManager.popBackStack()
                fragmentTransaction = fragmentManager.beginTransaction()
                val weatherFragment = WeatherFragment.newInstance(myLocation)
                weatherFragment.setBottomNavigationInteractionListener(myBottomNavigationInteractionListener)
                fragmentTransaction.replace(R.id.container, weatherFragment)
                fragmentTransaction.commit()
                navigationView.findViewById<TextView>(R.id.navigation_text).setText("Weather")
            }
            R.id.navigation_setting -> {
                //settingfragment needs
                fragmentManager.popBackStack()
                fragmentTransaction = fragmentManager.beginTransaction()
                val settingFragment = SettingFragment()
                settingFragment.setLanguageListener(settingLanguageInteractionListener)
                fragmentTransaction.replace(R.id.container, settingFragment)
                fragmentTransaction.commit()
                navigationView.findViewById<TextView>(R.id.navigation_text).setText("Setting")
            }
        }
        return true
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            when(language){
                0 -> {
                    Toast.makeText(this, getString(R.string.back_button_pressed_EN), Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    Toast.makeText(this, getString(R.string.back_button_pressed_KR), Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(this, getString(R.string.back_button_pressed_JP), Toast.LENGTH_SHORT).show()
                }
            }


            return
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast?.cancel();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestcode==", requestCode.toString())
        if(requestCode == REQUEST_INTRO_CODE){
            setIntro()
        }
        if (requestCode === PICK_FROM_ALBUM) {
            val photoUri = data?.getData()
            var cursor: Cursor? = null
            try {
                /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
                if(photoUri != null) {
                    val proj = arrayOf(MediaStore.Images.Media.DATA)
                    assert(photoUri != null)
                    cursor = applicationContext.getContentResolver()?.query(photoUri, proj, null, null, null)
                    assert(cursor != null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor!!.moveToFirst()
                    tempFile = File(cursor!!.getString(column_index))
                    Log.d("filePath==", tempFile?.absolutePath)
                    calendarFragment.saveGalleryImage(tempFile?.absolutePath.toString())
                }
            }finally {
                if (cursor != null) {
                    cursor!!.close()
                }
            }
        }
    }

    fun setIntro() {
        val pref = getSharedPreferences("intro", MODE_PRIVATE)
        val setting = getSharedPreferences("setting", MODE_PRIVATE)
        var isEnded = pref.getString("isEndedMain", "")
        if (isEnded == "") {
            val settingEditor = settingSharedPreferences.edit()
            settingEditor.putInt("language",language)
            settingEditor.commit()
            val editors = setting.edit()
            editors.putBoolean("push", true)
            editors.commit()
            val intent = Intent(this@MainActivity, IntroMainActivity::class.java)
            startActivityForResult(intent, REQUEST_INTRO_CODE)
            val editor = pref.edit()
            editor.putString("isEndedMain", "true")
            editor.commit()
        }
        else{
            fragmentTransaction = fragmentManager.beginTransaction()
            val weatherFragment = WeatherFragment.newInstance(myLocation)
            weatherFragment.setBottomNavigationInteractionListener(myBottomNavigationInteractionListener)
            fragmentTransaction.replace(R.id.container, weatherFragment)
            fragmentTransaction.commit()
        }

    }

    // 전화번호 권한 요청
    protected fun callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_ACCESS_FINE_LOCATION
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_ACCESS_COARSE_LOCATION
            )
        } else {
            isPermission = true
        }
    }

    private fun getGps(): MyLocation?{
        gps = GPSInfo(applicationContext)
        var myLocation: MyLocation? = null;
        // GPS 사용유무 가져오기
        if (gps!!.isGetLocation()) {
            //GPSInfo를 통해 알아낸 위도값과 경도값
            val latitude = gps!!.latitude
            val longitude = gps!!.longitude
//            val latitude = 35.046140
//            val longitude = 135.111857

            //Geocoder
            val gCoder = Geocoder(applicationContext, Locale.getDefault())
            var addr: List<Address>? = null
            try {
                addr = gCoder.getFromLocation(latitude, longitude, 10)
                if(addr == null){
                    Log.v("알림", "AddressLine(null)" + "\n")
                    Toast.makeText(applicationContext, getString(R.string.weather_fragment_location_not_found_EN), Toast.LENGTH_LONG).show()
                    return null
                }
                var locationString:String = "Location not found"
                for(a: Address in addr){
                    if(a.locality != null && a.locality.length > 0){
                        locationString = a.locality
                        Log.d("country==", a.countryCode.toString())
                        when(a.countryCode.toString()){
                            "KR" -> {
                                language = 1
                            }
                            "JP" -> {
                                language = 2
                            }
                            else -> {
                                language = 0
                            }
                        }
                        return MyLocation(latitude, longitude, locationString)
                    }
                }
                myLocation = MyLocation(latitude, longitude, locationString)
            } catch (e: IOException) {
                e.printStackTrace()
            }catch (e: IndexOutOfBoundsException){
                e.printStackTrace()
            }
            if (addr != null) {
                if (addr.size == 0) {
                    Toast.makeText(applicationContext, getString(R.string.weather_fragment_location_not_found_EN), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // GPS 를 사용할수 없으므로
            gps!!.showSettingsAlert()
        }
        return myLocation
    }

    private fun setLanguage(){
        val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        val language = sharedPreferences.getInt("language", 0)
        when(language){
            0 -> {
                menu.getItem(0).setTitle(getString(R.string.navigation_weather_EN))
                menu.getItem(1).setTitle(getString(R.string.navigation_calendar_EN))
                menu.getItem(2).setTitle(getString(R.string.navigation_gallery_EN))
                menu.getItem(3).setTitle(getString(R.string.navigation_setting_EN))
            }
            1 -> {
                menu.getItem(0).setTitle(getString(R.string.navigation_weather_KR))
                menu.getItem(1).setTitle(getString(R.string.navigation_calendar_KR))
                menu.getItem(2).setTitle(getString(R.string.navigation_gallery_KR))
                menu.getItem(3).setTitle(getString(R.string.navigation_setting_KR))
            }
            2 -> {
                menu.getItem(0).setTitle(getString(R.string.navigation_weather_JP))
                menu.getItem(1).setTitle(getString(R.string.navigation_calendar_JP))
                menu.getItem(2).setTitle(getString(R.string.navigation_gallery_JP))
                menu.getItem(3).setTitle(getString(R.string.navigation_setting_JP))
            }
        }


    }
}

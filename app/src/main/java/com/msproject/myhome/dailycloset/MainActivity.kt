package com.msproject.myhome.dailycloset

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.util.ArrayList


class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var fragmentManager:FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var mContext: Context
    lateinit var localDate: LocalDate
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var navigationView: LinearLayout
    lateinit var menu:Menu
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private var backKeyPressedTime: Long = 0
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private var toast: Toast? = null

    private val PERMISSIONS_ACCESS_FINE_LOCATION = 1000
    private val PERMISSIONS_ACCESS_COARSE_LOCATION = 1001
    private var isPermission = false
    private lateinit var myBottomNavigationInteractionListener:BottomNavigationInteractionListener
    private val REQUEST_INTRO_CODE = 200


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        sharedPreferences = getSharedPreferences("Event", Context.MODE_PRIVATE)
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
            override fun onPermissionGranted() {
                setIntro()

            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                finish();
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
                fragmentTransaction.replace(R.id.container, CalendarFragment())
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
                val weatherFragment = WeatherFragment.newInstance()
                weatherFragment.setBottomNavigationInteractionListener(myBottomNavigationInteractionListener)
                fragmentTransaction.replace(R.id.container, weatherFragment)
                fragmentTransaction.commit()
                navigationView.findViewById<TextView>(R.id.navigation_text).setText("Weather")
            }
            R.id.navigation_setting -> {
                //settingfragment needs
                fragmentManager.popBackStack()
                fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, SettingFragment())
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
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast?.show();
            return;
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
        if(requestCode == REQUEST_INTRO_CODE){
            setIntro()
        }
    }

    fun setIntro() {
        val pref = getSharedPreferences("intro", MODE_PRIVATE)
        val setting = getSharedPreferences("setting", MODE_PRIVATE)
        var isEnded = pref.getString("isEndedMain", "")
        if (isEnded == "") {
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
            val weatherFragment = WeatherFragment.newInstance()
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

}

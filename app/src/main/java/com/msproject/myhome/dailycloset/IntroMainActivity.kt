package com.msproject.myhome.dailycloset

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.joda.time.LocalDate

class IntroMainActivity : AppCompatActivity() {
    lateinit var imageFirst:ImageView
    lateinit var imageSecond:ImageView
    lateinit var imageThird:ImageView
    lateinit var imageFourth:ImageView
    lateinit var textExplain:TextView
    lateinit var fragmentManager: FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var navigationView: LinearLayout
    private lateinit var myBottomNavigationInteractionListener:BottomNavigationInteractionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val introView = findViewById<LinearLayout>(R.id.mouse_view_container)
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        navigationView = findViewById(R.id.navigation_view)
        introView.bringToFront()
        imageFirst = findViewById(R.id.first_image)
        imageSecond = findViewById(R.id.second_image)
        imageThird = findViewById(R.id.third_image)
        imageFourth = findViewById(R.id.fourth_image)
        textExplain = findViewById(R.id.explain_text)
        textExplain.bringToFront()
        val animation =AnimationUtils.loadAnimation(applicationContext, R.anim.mouse_focus)
        imageFirst.bringToFront()
        imageFirst.startAnimation(animation)
        navigationView.findViewById<TextView>(R.id.navigation_text).setText("Weather")
        val menu = bottomNavigationView.menu
        myBottomNavigationInteractionListener = object : BottomNavigationInteractionListener {
            override fun setNavigationIcon(imageResourceId: Int) {
                menu.findItem(R.id.navigation_weather).setIcon(imageResourceId)
            }
        }

        imageFirst.setOnClickListener(View.OnClickListener {
            imageFirst.clearAnimation()
            imageFirst.visibility = View.INVISIBLE
            imageSecond.visibility = View.VISIBLE
            imageSecond.bringToFront()
            imageSecond.startAnimation(animation)
            textExplain.setText("새로운 옷을 기록하거나 옷을 기록한 날짜를 알아볼 수 있는 달력을 확인할 수 있습니다. ")
            fragmentManager.popBackStack()
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, CalendarFragment())
            fragmentTransaction.commit()
            navigationView.findViewById<TextView>(R.id.navigation_text).setText("Gallery")
        })
        imageSecond.setOnClickListener(View.OnClickListener {
            imageSecond.clearAnimation()
            imageSecond.visibility = View.INVISIBLE
            imageThird.visibility = View.VISIBLE
            imageThird.bringToFront()
            imageThird.startAnimation(animation)
            textExplain.setText("가장 최근 찍은 사진을 기준으로 사진별로 모아볼 수 있습니다.")
            fragmentManager.popBackStack()
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, GalleryFragment())
            fragmentTransaction.commit()
        })
        imageThird.setOnClickListener(View.OnClickListener {
            imageThird.clearAnimation()
            imageThird.visibility = View.INVISIBLE
            imageFourth.visibility = View.VISIBLE
            imageFourth.bringToFront()
            imageFourth.startAnimation(animation)
            textExplain.setText("기능에 관한 설정을 변경할 수 있습니다.")
            fragmentManager.popBackStack()
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, SettingFragment())
            fragmentTransaction.commit()
        })
        imageFourth.setOnClickListener(View.OnClickListener {
            imageFourth.clearAnimation()
            finish();
        })


        fragmentTransaction = fragmentManager.beginTransaction()
        val weatherFragment = WeatherFragment.newInstance()
        weatherFragment.setBottomNavigationInteractionListener(myBottomNavigationInteractionListener)
        fragmentTransaction.replace(R.id.container, weatherFragment)
        fragmentTransaction.commit()
    }
}

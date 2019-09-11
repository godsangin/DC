package com.msproject.myhome.dailycloset

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
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
        val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        navigationView = findViewById(R.id.navigation_view)
        introView.bringToFront()
        imageFirst = findViewById(R.id.first_image)
        imageSecond = findViewById(R.id.second_image)
        imageThird = findViewById(R.id.third_image)
        imageFourth = findViewById(R.id.fourth_image)
        textExplain = findViewById(R.id.explain_text)
        val fragmentContainer:FrameLayout = findViewById(R.id.container)
        fragmentContainer.isClickable = false
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
        val language = sharedPreferences.getInt("language", 0)
        setTextFirst(language)

        imageFirst.setOnClickListener(View.OnClickListener {
            imageFirst.clearAnimation()
            imageFirst.visibility = View.INVISIBLE
            imageSecond.visibility = View.VISIBLE
            imageSecond.bringToFront()
            imageSecond.startAnimation(animation)
            setTextSecond(language)
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
            setTextThird(language)
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
            setTextFourth(language)
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
        //에러 확인하기
        val weatherFragment = WeatherFragment.newInstance(null)
        weatherFragment.setBottomNavigationInteractionListener(myBottomNavigationInteractionListener)
        fragmentTransaction.replace(R.id.container, weatherFragment)
        fragmentTransaction.commit()
    }

    private fun setTextFirst(language:Int){
        when(language){
            0 -> {
                textExplain.setText(getString(R.string.intro_main_first_text_EN))
            }
            1 -> {
                textExplain.setText(getString(R.string.intro_main_first_text_KR))
            }
            2 -> {
                textExplain.setText(getString(R.string.intro_main_first_text_JP))
            }
        }
    }

    private fun setTextSecond(language:Int){
        when(language){
            0 -> {
                textExplain.setText(getString(R.string.intro_main_second_text_EN))
            }
            1 -> {
                textExplain.setText(getString(R.string.intro_main_second_text_KR))
            }
            2 -> {
                textExplain.setText(getString(R.string.intro_main_second_text_JP))
            }
        }
    }

    private fun setTextThird(language:Int){
        when(language){
            0 -> {
                textExplain.setText(getString(R.string.intro_main_third_text_EN))
            }
            1 -> {
                textExplain.setText(getString(R.string.intro_main_third_text_KR))
            }
            2 -> {
                textExplain.setText(getString(R.string.intro_main_third_text_JP))
            }
        }
    }

    private fun setTextFourth(language:Int){
        when(language){
            0 -> {
                textExplain.setText(getString(R.string.intro_main_fourth_text_EN))
            }
            1 -> {
                textExplain.setText(getString(R.string.intro_main_fourth_text_KR))
            }
            2 -> {
                textExplain.setText(getString(R.string.intro_main_fourth_text_JP))
            }
        }
    }
}

package com.msproject.myhome.dailycloset

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes

class SetLanguageActivity : AppCompatActivity() {
    lateinit var radioGroup: RadioGroup
    lateinit var submitText:TextView


    var language:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)
        val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        radioGroup = findViewById(R.id.radio_group)
        submitText = findViewById(R.id.submit_text)
        language = sharedPreferences.getInt("language", 0)
        setLanguageRaidoFocus()
        setLanguage()

        radioGroup.setOnCheckedChangeListener(object:RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, @IdRes position: Int) {

                when(position){
                    R.id.english_button -> {
                        Toast.makeText(applicationContext, "english", Toast.LENGTH_SHORT).show()
                        language = 0
                    }
                    R.id.korean_button -> {
                        Toast.makeText(applicationContext, "한국어", Toast.LENGTH_SHORT).show()
                        language = 1
                    }
                    R.id.japanese_button -> {
                        Toast.makeText(applicationContext, "日本語", Toast.LENGTH_SHORT).show()
                        language = 2
                    }
                }
            }
        })
        submitText.setOnClickListener(View.OnClickListener {
            //sharedpreference조작
            val editor = sharedPreferences.edit()
            editor.putInt("language", language)
            editor.commit()
            finish()
        })
    }

    fun setLanguage(){
        when(language){
            0 -> {
                submitText.text = getString(R.string.setting_submit_EN)
            }
            1 -> {
                submitText.text = getString(R.string.setting_submit_KR)
            }
            2 -> {
                submitText.text = getString(R.string.setting_submit_JP)
            }
        }
    }

    fun setLanguageRaidoFocus(){
        when(language){
            0 -> {
                val englishRadioButton = findViewById<RadioButton>(R.id.english_button)
                englishRadioButton.isChecked = true
            }
            1 -> {
                val koreanRadioButton = findViewById<RadioButton>(R.id.korean_button)
                koreanRadioButton.isChecked = true
            }
            2 -> {
                val japaneseRadioButton = findViewById<RadioButton>(R.id.japanese_button)
                japaneseRadioButton.isChecked = true
            }
        }
    }
}

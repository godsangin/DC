package com.msproject.myhome.dailycloset

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList


class SettingFragment : Fragment() {
    lateinit var settingListView:ListView
    lateinit var fsListViewAdapter:FSListViewAdapter
    var settingLanguageInteractionListener: SettingLanguageInteractionListener? = null
    val REQUESTCODE_SETTING = 2000
    val items = ArrayList<SettingItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        settingListView = view.findViewById(R.id.setting_listview)

        setItems()



        settingListView.setOnItemClickListener(object:AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position){
                    0 -> {

                    }
                    1 -> {
                        val intent = Intent(context, SetLanguageActivity::class.java)
                        startActivityForResult(intent, REQUESTCODE_SETTING)
                    }
                }
            }
        })
        return view
    }

    fun setLanguageListener(settingLanguageInteractionListener: SettingLanguageInteractionListener){
        this.settingLanguageInteractionListener = settingLanguageInteractionListener
    }

    fun setItems(){
        val sharedPreferences = context?.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getInt("language", 0)
        items.clear()

        when(language){
            0 -> {
                val pushItem = SettingItem(getString(R.string.setting_fragment_push_title_EN), getString(R.string.setting_fragment_push_content_EN))
                pushItem.checkboxVisibility = true
                items.add(pushItem)
                val languageItem = SettingItem(getString(R.string.setting_fragment_language_title_EN), getString(R.string.setting_fragment_language_content_EN))
                languageItem.checkboxVisibility = false
                items.add(languageItem)
            }
            1 -> {
                val pushItem = SettingItem(getString(R.string.setting_fragment_push_title_KR), getString(R.string.setting_fragment_push_content_KR))
                pushItem.checkboxVisibility = true
                items.add(pushItem)
                val languageItem = SettingItem(getString(R.string.setting_fragment_language_title_KR), getString(R.string.setting_fragment_language_content_KR))
                languageItem.checkboxVisibility = false
                items.add(languageItem)
            }
            2 -> {
                val pushItem = SettingItem(getString(R.string.setting_fragment_push_title_JP), getString(R.string.setting_fragment_push_content_JP))
                pushItem.checkboxVisibility = true
                items.add(pushItem)
                val languageItem = SettingItem(getString(R.string.setting_fragment_language_title_JP), getString(R.string.setting_fragment_language_content_JP))
                languageItem.checkboxVisibility = false
                items.add(languageItem)
            }
        }
        fsListViewAdapter = FSListViewAdapter(items, context)
        settingListView.setAdapter(fsListViewAdapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUESTCODE_SETTING){
            if(settingLanguageInteractionListener != null){
                settingLanguageInteractionListener?.notifyDateSetChanged()
            }
            setItems()
        }
    }
}

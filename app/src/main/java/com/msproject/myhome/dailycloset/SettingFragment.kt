package com.msproject.myhome.dailycloset

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        settingListView = view.findViewById(R.id.setting_listview)
        val items = ArrayList<SettingItem>()
        items.add(SettingItem("푸시알림 허용", "체크 시 푸시 알림 기능을 활성화하고 이벤트를 수신합니다."))
//        items.add(SettingItem("백그라운드 작업 허용", "체크 시 수면시간측정, 일정 등록 알림 등의 작업을 어플이 꺼져있을 경우에도 진행합니다."))
        fsListViewAdapter = FSListViewAdapter(items, context)
        settingListView.setAdapter(fsListViewAdapter)
        return view
    }


}

package com.msproject.myhome.dailycloset

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import java.io.File


class GalleryFragment(context:Context) : Fragment() {
    val pictureList= ArrayList<Picture>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_gallery, container, false)
        var mRecyclerView:RecyclerView = view.findViewById(R.id.gallery_recyclerview)
        var today:LocalDate = LocalDate();
        var file: File = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures/DailyCloset")
        Log.d("fileNum==", file.listFiles().size.toString())
        pictureList.clear()
        for(file in file.listFiles()){
            pictureList.add(Picture(file.name, file.absolutePath))
        }

        val mAdapter = context?.let { GalleryAdapter(it, pictureList) }
        mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(context)
        lm.isItemPrefetchEnabled = true
        mRecyclerView.layoutManager = lm
        mRecyclerView.setHasFixedSize(true)
        return view
    }
    fun convertFileName(date: LocalDate): String {
        return date.toString("yyyyMMdd")
    }

}

package com.msproject.myhome.dailycloset

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate


class GalleryFragment(context:Context) : Fragment() {
    val pictureList= ArrayList<Picture>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_gallery, container, false)
        var mRecyclerView:RecyclerView = view.findViewById(R.id.gallery_recyclerview)
        pictureList.add(Picture(LocalDate(), "1"))
        pictureList.add(Picture(LocalDate(), "1"))
        pictureList.add(Picture(LocalDate(), "1"))
        val mAdapter = context?.let { GalleryAdapter(it, pictureList) }
        mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(context)
        mRecyclerView.layoutManager = lm
        mRecyclerView.setHasFixedSize(true)
        return view
    }


}

package com.msproject.myhome.dailycloset

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class GalleryFragment : Fragment() {
    var pictureList= ArrayList<Picture>()
    lateinit var mRecyclerView: RecyclerView
    lateinit var favoriteButton: ImageView
    lateinit var searchButton:Button
    lateinit var searchText:EditText
    lateinit var ImageNotFoundView:ConstraintLayout
    var viewCode:Int = 0;





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_gallery, container, false)
        mRecyclerView = view.findViewById(R.id.gallery_recyclerview)
        favoriteButton = view.findViewById(R.id.favorite_bt)
        searchButton = view.findViewById(R.id.search_bt)
        searchText = view.findViewById(R.id.search_text)
        ImageNotFoundView = view.findViewById(R.id.image_not_found_view)
        setFavoriteClickListener()
        setSearchListener()
        setRecyclerView()

        return view
    }
    fun convertFileName(date: LocalDate): String {
        return date.toString("yyyyMMdd")
    }
    fun setRecyclerView(){
        var file: File = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures/DailyCloset")
        pictureList.clear()
        var fileList: Array<File>? = file.listFiles()

        if (fileList != null) {
            fileList = fileList?.sortedArray()
            for(file in fileList){
                pictureList.add(Picture(file.name, file.absolutePath))
            }
        }
        pictureList = pictureList.asReversed().toList() as ArrayList<Picture>
        val mAdapter = context?.let { GalleryAdapter(it, pictureList) }
        mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(context)
        lm.isItemPrefetchEnabled = true
        mRecyclerView.layoutManager = lm
        mRecyclerView.setHasFixedSize(true)
        if(pictureList.size == 0){
            ImageNotFoundView.visibility = View.VISIBLE
        }
        else{
            ImageNotFoundView.visibility = View.GONE
        }
    }

    fun setFavoriteClickListener(){
        favoriteButton.setOnClickListener(View.OnClickListener {
            if(viewCode == 0){
                pictureList.clear()
                favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp)
                val sp = context?.getSharedPreferences("favorite", Context.MODE_PRIVATE)
                val wholeString = sp?.getString("favorite", "")
                val favorites = wholeString?.split(" ")
                for(favorite in favorites!!){
                    if(favorite.equals("")) continue
                    pictureList.add(Picture(favorite + ".jpg", Environment.getExternalStorageDirectory().toString() + "/Pictures/DailyCloset/" + favorite + ".jpg"))
                }
                pictureList = ArrayList(pictureList.asReversed().toList())
                val mAdapter = context?.let { GalleryAdapter(it, pictureList) }
                mRecyclerView.adapter = mAdapter

                val lm = LinearLayoutManager(context)
                lm.isItemPrefetchEnabled = true
                mRecyclerView.layoutManager = lm
                mRecyclerView.setHasFixedSize(true)
                viewCode = 1
            }
            else{
                favoriteButton.setImageResource(R.drawable.ic_star_black_24dp)
                setRecyclerView()
                viewCode = 0
            }
            if(pictureList.size == 0){
                ImageNotFoundView.visibility = View.VISIBLE
            }
            else{
                ImageNotFoundView.visibility = View.GONE
            }
        })
    }

    fun setSearchListener(){
        searchButton.setOnClickListener(View.OnClickListener {
            pictureList.clear()
            pictureList.add(Picture(searchText.text.toString() + ".jpg", Environment.getExternalStorageDirectory().toString() + "/Pictures/DailyCloset/" + searchText.text.toString() + ".jpg"))
            val file = File(pictureList.get(0).imgURL)
            if(!file.canRead()){
                //못읽을경우
                pictureList.clear()
                ImageNotFoundView.visibility = View.VISIBLE
                return@OnClickListener;
            }
            else{
                ImageNotFoundView.visibility = View.GONE
            }
            val mAdapter = context?.let { GalleryAdapter(it, pictureList) }
            mRecyclerView.adapter = mAdapter

            val lm = LinearLayoutManager(context)
            lm.isItemPrefetchEnabled = true
            mRecyclerView.layoutManager = lm
            mRecyclerView.setHasFixedSize(true)
            viewCode = 0
        })
    }

}

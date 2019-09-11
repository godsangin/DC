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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakao.adfit.ads.AdListener
import com.kakao.adfit.ads.ba.BannerAdView
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
    lateinit var componentLinearButton:ImageView
    lateinit var componentGridButton:ImageView
    lateinit var adView:BannerAdView
    var viewCode:Int = 0
    var isGridLayout:Boolean = false
    lateinit var mAdapter:GalleryAdapter
    lateinit var mAdView : AdView


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
        componentLinearButton = view.findViewById(R.id.component_linear_bt)
        componentGridButton = view.findViewById(R.id.component_grid_bt)
        mAdView = view.findViewById(R.id.admob_adView)
        adView = view.findViewById(R.id.adfit_adview)

        componentLinearButton.setImageResource(R.drawable.ic_view_day_accent_24dp)
        setFavoriteClickListener()
        setSearchListener()
        setRecyclerView()
        setLanguege()
        setComponentButton()



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
        if(pictureList.size == 0){
            ImageNotFoundView.visibility = View.VISIBLE
        }
        else{
//            pictureList = pictureList.asReversed().toList() as ArrayList<Picture>
            pictureList = ArrayList<Picture>(pictureList.asReversed().toList())
            ImageNotFoundView.visibility = View.GONE
        }
        mAdapter = GalleryAdapter(context!!, pictureList, isGridLayout)
        mRecyclerView.adapter = mAdapter
        setComponent()
    }

    fun setFavoriteClickListener(){//Date라는애 추가하지 못하게 !
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
                setComponent()
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
            val mAdapter = context?.let { GalleryAdapter(it, pictureList, isGridLayout) }
            mRecyclerView.adapter = mAdapter


            viewCode = 0
        })
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    fun setLanguege(){
        val sharedPreferences = context?.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getInt("language", 0)
        when(language){
            0 -> {
                searchText.setHint(getString(R.string.gallery_fragment_search_text_EN))
                searchButton.text = getString(R.string.gallery_fragment_search_button_EN)
                MobileAds.initialize(context, "ca-app-pub-3136625326865731~3192081537")
                val adRequest = AdRequest.Builder().build()
                mAdView.visibility = View.VISIBLE
                mAdView.loadAd(adRequest)
                adView.visibility = View.GONE

            }
            1 -> {
                searchText.setHint(getString(R.string.gallery_fragment_search_text_KR))
                searchButton.text = getString(R.string.gallery_fragment_search_button_KR)
                adView.visibility = View.VISIBLE
                adView.setClientId("DAN-tonyi5sd4vnd")
                adView.setAdListener(object: AdListener{
                    override fun onAdFailed(p0: Int) {

                    }

                    override fun onAdClicked() {

                    }

                    override fun onAdLoaded() {

                    }
                })
                adView.loadAd()
                mAdView.visibility = View.GONE
            }
            2 -> {
                searchText.setHint(getString(R.string.gallery_fragment_search_text_JP))
                searchButton.text = getString(R.string.gallery_fragment_search_button_JP)
                MobileAds.initialize(context, "ca-app-pub-3136625326865731~3192081537")
                val adRequest = AdRequest.Builder().build()
                mAdView.visibility = View.VISIBLE
                mAdView.loadAd(adRequest)
                adView.visibility = View.GONE
            }
        }
    }

    fun setComponentButton(){
        componentGridButton.setOnClickListener(View.OnClickListener {
            if(!isGridLayout){
                isGridLayout = true
                mAdapter = GalleryAdapter(context!!, pictureList, isGridLayout)
                componentGridButton.setImageResource(R.drawable.ic_view_module_accent_24dp)
                componentLinearButton.setImageResource(R.drawable.ic_view_day_gray_24dp)
                setComponent()
            }
        })
        componentLinearButton.setOnClickListener(View.OnClickListener {
            if(isGridLayout){
                isGridLayout = false
                mAdapter = GalleryAdapter(context!!, pictureList, isGridLayout)
                componentGridButton.setImageResource(R.drawable.ic_view_module_gray_24dp)
                componentLinearButton.setImageResource(R.drawable.ic_view_day_accent_24dp)
                setComponent()
            }
        })
    }

    fun setComponent(){
        if(isGridLayout){
            val gl = GridLayoutManager(context,2)
            mRecyclerView.layoutManager = gl
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.adapter = mAdapter
        }
        else{
            val lm = LinearLayoutManager(context)
            lm.isItemPrefetchEnabled = true
            mRecyclerView.layoutManager = lm
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.adapter = mAdapter
        }
    }
}

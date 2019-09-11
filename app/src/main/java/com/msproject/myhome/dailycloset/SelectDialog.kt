package com.msproject.myhome.dailycloset

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class SelectDialog(context:Context, fileDestinationListener: FileDestinationListener, language: Int):Dialog(context){
    lateinit var titleTextView:TextView
    lateinit var selectGalleryTextView:TextView
    lateinit var selectTakeTextView:TextView
    val fileDestinationListener = fileDestinationListener
    val language = language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        setContentView(R.layout.dialog_select)

        titleTextView = findViewById(R.id.title_tv)
        selectGalleryTextView = findViewById(R.id.gallery_tv)
        selectTakeTextView = findViewById(R.id.takepicture_tv)

        setLanguage(language)

        selectGalleryTextView.setOnClickListener {
            fileDestinationListener.doGallery()
            dismiss()
        }

        selectTakeTextView.setOnClickListener {
            fileDestinationListener.doCamera()
            dismiss()
        }
    }

    fun setLanguage(language:Int){
        when(language){
            0 -> {
                titleTextView.text = context.getString(R.string.dialog_select_title_EN)
                selectGalleryTextView.text = context.getString(R.string.dialog_select_gallery_EN)
                selectTakeTextView.text = context.getString(R.string.dialog_select_camera_EN)
            }
            1 -> {
                titleTextView.text = context.getString(R.string.dialog_select_title_KR)
                selectGalleryTextView.text = context.getString(R.string.dialog_select_gallery_KR)
                selectTakeTextView.text = context.getString(R.string.dialog_select_camera_KR)
            }
            2 -> {
                titleTextView.text = context.getString(R.string.dialog_select_title_JP)
                selectGalleryTextView.text = context.getString(R.string.dialog_select_gallery_JP)
                selectTakeTextView.text = context.getString(R.string.dialog_select_camera_JP)
            }
        }
    }

}
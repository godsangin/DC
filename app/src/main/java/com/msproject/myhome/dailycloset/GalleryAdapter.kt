package com.msproject.myhome.dailycloset

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(val context:Context, val pictureList:ArrayList<Picture>):
        RecyclerView.Adapter<GalleryAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return pictureList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(pictureList[position], context)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val myImageView = itemView?.findViewById<ImageView>(R.id.myImage)
        val myTextView = itemView?.findViewById<TextView>(R.id.myText)

        fun bind (picture: Picture, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            if (picture.getImgURL() != "") {
//                val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
//                dogPhoto?.setImageResource(resourceId)

            } else {
                myImageView?.setImageResource(R.mipmap.ic_launcher)
            }
            /* 나머지 TextView와 String 데이터를 연결한다. */
            myTextView?.text = picture.getLocalDate().toString()
        }
    }
}

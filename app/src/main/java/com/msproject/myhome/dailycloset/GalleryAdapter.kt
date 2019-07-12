package com.msproject.myhome.dailycloset

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.media.ExifInterface
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GalleryAdapter(val context:Context, val pictureList:ArrayList<Picture>):
        RecyclerView.Adapter<GalleryAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        resetGlide()
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
        val favoriteButton = itemView?.findViewById<ImageView>(R.id.favorite_bt)


        fun bind(picture: Picture, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            if (picture.getImgURL() != "") {
//                val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
//                dogPhoto?.setImageResource(resourceId)
                val start = System.currentTimeMillis()
                myImageView?.let { Glide.with(context).load(picture.imgURL).into(it) }
//                val bm: Bitmap = decodeSampleBitmapFromResource(picture.getImgURL(), 250, 250)
//                myImageView?.setImageBitmap(bm)
                Log.d("loadTime==", (System.currentTimeMillis() - start).toString())
            } else {
                myImageView?.setImageResource(R.mipmap.ic_launcher)
            }
            /* 나머지 TextView와 String 데이터를 연결한다. */
            myTextView?.text = picture.getFileName().replace(".jpg", "")
            val sp = context.getSharedPreferences("favorite", Context.MODE_PRIVATE)
            val pastString = sp.getString("favorite", "")
            Log.d("filereplace==", picture.fileName.replace("jpg",""))
            Log.d("pastString==", pastString)

            if (pastString?.contains(picture.fileName.replace(".jpg",""))!!){
                picture.favorite = true
            }
            if(picture.favorite){
                favoriteButton?.setImageResource(R.drawable.ic_star_yellow_24dp)
            }
            else{
                favoriteButton?.setImageResource(R.drawable.ic_star_black_24dp)
            }
            favoriteButton?.setOnClickListener(View.OnClickListener {

                val editor = sp.edit()
                if(!picture.favorite && !pastString?.contains(picture.fileName.replace("jpg",""))!!){
                    editor.putString("favorite", pastString + myTextView?.text.toString() + " ")
                    editor.commit()
                    favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp)
                    picture.favorite = true
                    Toast.makeText(context, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    editor.putString("favorite", pastString?.replace(myTextView?.text.toString(), ""))
                    editor.commit()
                    favoriteButton.setImageResource(R.drawable.ic_star_black_24dp)
                    picture.favorite = false
                    Toast.makeText(context, "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun resetGlide(){
        Glide.get(context).clearMemory()
        MyAsyncTask().execute(1,2,3,4,5)
    }

    inner class MyAsyncTask: AsyncTask<Int, Void, Void>(){
        override fun doInBackground(vararg p0: Int?): Void? {
            Glide.get(context).clearDiskCache()
            return null;
        }
    }
}

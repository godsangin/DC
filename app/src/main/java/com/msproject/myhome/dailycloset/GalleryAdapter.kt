package com.msproject.myhome.dailycloset

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import android.util.DisplayMetrics
import android.widget.GridLayout
import androidx.core.view.setPadding


class GalleryAdapter(val context:Context, val pictureList:ArrayList<Picture>, val isGridLayout: Boolean):
        RecyclerView.Adapter<GalleryAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        if(isGridLayout){
            view = LayoutInflater.from(context).inflate(R.layout.gallery_item_small, parent, false)
        }

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return pictureList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        if(isGridLayout){
//            val displaymetrics = DisplayMetrics()
//            (context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
//            //if you need three fix imageview in width
//            val devicewidth = displaymetrics.widthPixels * 4 / 9
//            //if you need 4-5-6 anything fix imageview in height
//            val deviceheight = displaymetrics.heightPixels / 4
//            holder.itemView.layoutParams.width = devicewidth
//            holder.itemView.layoutParams.height = deviceheight
//            holder.itemView.findViewById<TextView>(R.id.myText).setTextSize(15F)
//            val favoriteButton = holder.itemView.findViewById<ImageView>(R.id.favorite_bt)
//            val removeButton = holder.itemView.findViewById<ImageView>(R.id.remove_bt)
//            favoriteButton.setPadding(0)
//            removeButton.setPadding(0)
//            Log.d("isGrid==", "true")
//        }

        holder?.bind(pictureList[position], context)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val myImageView = itemView?.findViewById<ImageView>(R.id.myImage)
        val myTextView = itemView?.findViewById<TextView>(R.id.myText)
        val favoriteButton = itemView?.findViewById<ImageView>(R.id.favorite_bt)
        val removeButton = itemView?.findViewById<ImageView>(R.id.remove_bt)


        fun bind(picture: Picture, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            if (picture.getImgURL()!= null && !picture.getImgURL().equals("")) {
                val imageDownloader = ImageDownloader(context)
                imageDownloader.download(picture.getImgURL(), myImageView)
            } else {
                myImageView?.setImageResource(R.mipmap.ic_launcher)
            }
            /* 나머지 TextView와 String 데이터를 연결한다. */
            myTextView?.text = "Date: " + picture.getFileName().replace(".jpg", "")
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

            removeButton?.setOnClickListener(View.OnClickListener {
                val fileDeleteInteractionListener = object: FileDeleteInteractionListener {
                    override fun deleteSucess(): Boolean {
                        val file = File(picture.getImgURL())
                        if(file.delete()){
                            pictureList.remove(picture)
                            notifyDataSetChanged()
                            val editor = sp.edit()
                            editor.putString("favorite", pastString?.replace(myTextView?.text.toString(), ""))
                            editor.commit()
                            return true
                        }
                        return false
                    }
                }
                val dialog = DeletePictureDialog(context, fileDeleteInteractionListener)
                dialog.show()
                val display:Display = (context as Activity).windowManager.defaultDisplay
                var size:Point = Point()
                display.getSize(size)

                val window = dialog.window as Window
                val x = (size.x * 0.8f).toInt()
                val y = (size.y * 0.3f).toInt()
                window.setLayout(x, y)
            })
        }
    }

}

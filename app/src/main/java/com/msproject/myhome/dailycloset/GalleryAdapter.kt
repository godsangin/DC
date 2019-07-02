package com.msproject.myhome.dailycloset

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(val context:Context, val pictureList:ArrayList<Picture>):
        RecyclerView.Adapter<GalleryAdapter.Holder>() {
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

        fun bind(picture: Picture, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            if (picture.getImgURL() != "") {
//                val resourceId = context.resources.getIdentifier(dog.photo, "drawable", context.packageName)
//                dogPhoto?.setImageResource(resourceId)
                val start = System.currentTimeMillis()
                val bm: Bitmap = decodeSampleBitmapFromResource(picture.getImgURL(), 250, 250)
                myImageView?.setImageBitmap(bm)
                Log.d("loadTime==", (System.currentTimeMillis() - start).toString())
            } else {
                myImageView?.setImageResource(R.mipmap.ic_launcher)
            }
            /* 나머지 TextView와 String 데이터를 연결한다. */
            myTextView?.text = picture.getFileName().replace(".jpg", "")
        }
        fun decodeSampleBitmapFromResource(res: String, reqWidth: Int, reqHeight: Int): Bitmap {
            var options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true;  // 이미지 로드하기 전에  이미지의 크기를 알기위해 true 로 설정 해둔다.
            BitmapFactory.decodeFile(res, options)
            //이미지를 로드하기 전에 해상도 값을 얻을 수 있다.
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {//업로드할 이미지와 실제 필요한 이미지 비교를 통해 이미지 사이즈값을 얻는다.
                val heightRatio = Math.round((height / reqHeight).toDouble())
                val widthRatio = Math.round((width / reqWidth).toDouble())
                inSampleSize = (if (heightRatio < widthRatio) heightRatio else widthRatio).toInt();
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false; // 이미지 로드할 때는  다시 false로 설정한다.
            return BitmapFactory.decodeFile(res, options) //해당 이미지 반환

        }
    }


}

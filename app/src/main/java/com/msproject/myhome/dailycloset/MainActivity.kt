package com.msproject.myhome.dailycloset

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.joda.time.LocalDate
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var fragmentManager:FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var mContext: Context
    lateinit var calendarFragment: CalendarFragment
    lateinit var galleryFragment: GalleryFragment
    lateinit var localDate: LocalDate
    lateinit var calendarBt: Button
    lateinit var weeklyBt: Button
    lateinit var sharedPreferences: SharedPreferences
    final val PICK_FROM_ALBUM = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
        mContext = this
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        sharedPreferences = getSharedPreferences("Event", Context.MODE_PRIVATE)
        calendarBt = findViewById(R.id.calendar_bt)
        weeklyBt = findViewById(R.id.weekly_bt)
        localDate = LocalDate()
        calendarFragment = CalendarFragment()
        galleryFragment = GalleryFragment(this)
        calendarBt.setOnClickListener(View.OnClickListener {
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, calendarFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        weeklyBt.setOnClickListener(View.OnClickListener {
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, galleryFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, calendarFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
    fun getPermission(){
        var permissionListener: PermissionListener = object : PermissionListener{
            override fun onPermissionGranted() {


            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {

            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_2))
            .setDeniedMessage(resources.getString(R.string.permission_1))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()

    }

    private fun goToAlbum(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_FROM_ALBUM){
            var photoUri: Uri? = data!!.data
            lateinit var cursor: Cursor

        }
    }
}

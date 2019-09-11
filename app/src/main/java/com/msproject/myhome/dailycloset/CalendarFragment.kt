package com.msproject.myhome.dailycloset

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.joda.time.LocalDate
import android.provider.MediaStore
import android.R.attr.data
import android.database.Cursor
import android.util.Log
import android.widget.Toast
import java.io.File
import java.lang.NullPointerException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CalendarFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var plannerView: PlannerView

    fun newInstance():CalendarFragment{
        return CalendarFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view : View = inflater.inflate(R.layout.fragment_calendar, container, false)
        plannerView = view.findViewById(R.id.planner)
        plannerView.initView(LocalDate(), this)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("activityresult==","calendar")
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun saveGalleryImage(filePath:String){
        plannerView.saveGalleryImage(filePath)
        plannerView.initView(LocalDate(), this)
        Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
    }
}

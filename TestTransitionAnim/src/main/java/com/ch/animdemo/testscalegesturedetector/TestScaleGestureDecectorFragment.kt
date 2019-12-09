package com.ch.animdemo.testscalegesturedetector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ch.animdemo.R
import kotlinx.android.synthetic.main.activity_alpha.*

class TestScaleGestureDecectorFragment : Fragment() {

    companion object {
        const val TAG = "TestScaleGestureDecectorFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_scale_gesture_detector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val dector = ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                Log.d(TAG, "onScaleBegin")
                return false
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                Log.d(TAG, "onScaleEnd")
            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                Log.d(TAG, "onScale:${detector?.scaleFactor}")
                return true
            }


        })


        val mutiTouchDetector = MutiTouchDetector()

        tv_demo.setOnTouchListener { v, event ->
            dector.onTouchEvent(event)
            mutiTouchDetector.onTouch(event)

            true
        }
    }



}
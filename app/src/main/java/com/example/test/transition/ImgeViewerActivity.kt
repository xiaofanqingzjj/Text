package com.example.test.transition

import android.app.Activity
import android.app.SharedElementCallback
import android.os.Bundle
import android.transition.*
import android.transition.Transition.TransitionListener
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R


class ImgeViewerActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ImgeViewerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_image_viewer)

        val enterTransition = window.enterTransition

        val sharedElementTransition = window.sharedElementEnterTransition

        sharedElementTransition?.addListener(object : TransitionListener{
            override fun onTransitionEnd(transition: Transition?) {
                Log.d(TAG, "onTransitionEnd:$transition")
            }

            override fun onTransitionResume(transition: Transition?) {
                Log.d(TAG, "onTransitionResume")
            }

            override fun onTransitionPause(transition: Transition?) {
                Log.d(TAG, "onTransitionPause")
            }

            override fun onTransitionCancel(transition: Transition?) {
                Log.d(TAG, "onTransitionCancel")
            }

            override fun onTransitionStart(transition: Transition?) {
                Log.d(TAG, "onTransitionStart:$transition")
            }

        })


        setEnterSharedElementCallback(object: SharedElementCallback() {

        })

        setExitSharedElementCallback(object : SharedElementCallback() {

        })





        Log.d(TAG, "enterTransition:$enterTransition, sharedTranstion:$sharedElementTransition")

    }

    override fun finishAfterTransition() {
        setResult(Activity.RESULT_OK, intent)


        super.finishAfterTransition()
        setResult(Activity.RESULT_OK, intent)

    }

    override fun supportFinishAfterTransition() {
        super.supportFinishAfterTransition()
    }



    private fun setupWindowAnimations() {
        window.enterTransition = Fade()
    }

}
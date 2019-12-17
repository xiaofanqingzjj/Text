package com.example.test.transition

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import android.transition.Scene
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_test_transition.*
import java.util.*


class TestTransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_transition)

        btn_test.setOnClickListener {

            val scene = Scene.getSceneForLayout(root, R.layout.test_transition2, this@TestTransitionActivity)
//            scene.enter()

            TransitionManager.go(scene, Slide().apply {
                startDelay = 1000
                duration = 2000

                slideEdge = Gravity.RIGHT

            })
        }


        val context = this
        image_view?.setOnClickListener {

            // 转场放大动画
            val intent = Intent(context, ImgeViewerActivity::class.java)
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(context as Activity, it, context.getString(R.string.imageTransitionName)).toBundle())
        }

        setupWindowAnimations()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            ActivityCompat.setExitSharedElementCallback(this, object : SharedElementCallback() {
                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {

                    names?.clear()
                    sharedElements?.clear()

                    val transitionName = ViewCompat.getTransitionName(image_view2)
                    names?.add(transitionName!!)
                    sharedElements?.put(transitionName!!, image_view2)

//                names.add(ViewCompat.getTransitionName(exitView))
//                sharedElements[Objects.requireNonNull(ViewCompat.getTransitionName(exitView))] = exitView
//
                    setExitSharedElementCallback(object : SharedElementCallback() {

                    })
                }
            })
        }
    }

    private fun setupWindowAnimations() {
        window.enterTransition = Fade().apply {
            duration = 500
        }
        window.exitTransition = Fade()
    }

}
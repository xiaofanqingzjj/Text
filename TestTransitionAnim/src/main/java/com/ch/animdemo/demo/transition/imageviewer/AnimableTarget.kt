package com.ch.animdemo.demo.transition.imageviewer

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

open class AnimableTarget(var imageView: ImageView) : SimpleTarget<Drawable>() {

    companion object {
        const val TAG = "AnimableTarget"
    }

    private var animatable: Animatable? = null


    override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        setResourceInternal(null)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        setResourceInternal(null)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        super.onLoadCleared(placeholder)

        stopAnim()
        setResourceInternal(null)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        setResourceInternal(resource)
    }

    override fun onStart() {
        startAnim()
    }

    override fun onStop() {
        stopAnim()
    }

    private fun setResourceInternal(resource: Drawable?) {
        setResource(resource)
        maybeUpdateAnimatable(resource)
    }

    private fun maybeUpdateAnimatable( resource: Drawable?) {
        if (resource is Animatable) {
            animatable = resource
            startAnim()
        } else {
            animatable?.run {
                stopAnim()
                Log.w(TAG, "image:$imageView, $this: clearAnim")
            }
            animatable = null
        }
    }

    private fun startAnim() {
        animatable?.run {
            Log.d(TAG, "image:$imageView, $this: startAnim")
            start()
        }

    }

    private fun stopAnim() {
        animatable?.run {
            stop()
            Log.e(TAG, "image:$imageView, $this: stopAnim")
        }
    }

    protected fun setResource( resource: Drawable?) {
        imageView?.setImageDrawable(resource)
    }
}

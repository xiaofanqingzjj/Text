package com.example.test

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fortunexiao.tktx.platforms.dp2px
import kotlinx.android.synthetic.main.fragmetn_tesg_btn_bg.*

class TestDownloadProgressButtom : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmetn_tesg_btn_bg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dpbb.mNormalText = SpannableStringBuilder().apply {
            append("下载")
            append("(100m)", TextAppearanceSpan(null, 0, 10.dp2px(context), null, null ), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        dpbb.setOnClickListener {

            val anim = ObjectAnimator.ofFloat(dpbb, DownloadProgressBarButton.PROGRESS, 0f, 100f)
            anim.duration = 3000
            anim.start()



            dpbb.mState = DownloadProgressBarButton.STATE_DOWNLOADING
        }
    }

}
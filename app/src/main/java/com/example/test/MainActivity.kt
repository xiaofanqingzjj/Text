package com.example.test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import com.bedrock.module_base.MenuActivity
import com.bedrock.permissionrequestor.PermissionsRequestor
import com.example.test.alarm.TestAlarm
import com.example.test.bookranklist.BookRankingFragment
import com.example.test.transition.TestTransitionActivity
import com.example.test.card.CardViewActivity
import com.example.test.expendtextview.TextExpendTextView
import com.example.test.install.InstallApkSessionApi
import com.example.test.layout.TestLayoutFragment
import com.example.test.movementmethod.TestMoveMethod
import com.example.test.record.TestAudioPlay
import com.example.test.record.TestRecord
import com.example.test.slidedrawer.TestSlideMenu
import com.example.test.testattrs.MyThemeActivity
import com.example.test.testattrs.TestAttributes
import com.example.test.testconstraint.TestConstraintLayout
import com.example.test.testlottie.TestLottieFragment
import dalvik.system.DexFile


class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionsRequestor(this).request(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.INSTALL_PACKAGES))

        addMenu("过度动画", TestTransitionActivity::class.java)

        addMenu("CardVIEW", CardViewActivity::class.java)

        addMenuByFragment("Lottie", TestLottieFragment::class.java)

        addMenuByFragment("Test MovementMethod", TestMoveMethod::class.java)

        addMenu("Install") {
            startActivity(Intent(this@MainActivity, InstallApkSessionApi::class.java).apply {

            })
        }

        addMenuByFragment("DownloadProgressButtom", TestDownloadProgressButtom::class.java)

        addMenuByFragment("Alarm", TestAlarm::class.java)

        addMenuByFragment("ExpendTextView", TextExpendTextView::class.java)

        addMenuByFragment("TestViewPager", TestViewPager::class.java)

        addMenuByFragment("TestAttrs", TestAttributes::class.java)

        addMenu("TestTheme", MyThemeActivity::class.java)


        addMenuByFragment("TestSlideMenu", TestSlideMenu::class.java)

        addMenuByFragment("TestConstraintLayout", TestConstraintLayout::class.java)

        addMenuByFragment("TextPagingList", TestPagingList::class.java)

        addMenuByFragment("TestSimpleRoundedImageVIEW", TestSimpleRoundedImageView::class.java)

        setupWindowAnimations()

        addMenuByFragment("TestHtmlParser", TestHtmlParser::class.java)
        addMenuByFragment("TestItem", TestViewTypeAdapter::class.java)

        addMenu("NetInfo") {
            NetTools.getInstance().init(this.applicationContext)
            NetTools.getInstance().currentNetWorkType
        }

        addMenuByFragment("Test Focus change", TestFocusableInput::class.java)

        addMenuByFragment("Test Text Layout", TestLayoutFragment::class.java)

        addMenuByFragment("Test Book Ranking", BookRankingFragment::class.java)

        addMenuByFragment("Test Drawer Layout", TestDrawerLayout::class.java)

        addMenuByFragment("Test Center Drawable", TestCenterDrawable::class.java)

        addMenuByFragment("Test StickLayout", TestStickLayout::class.java)

        addMenuByFragment("Test MP3", TestAudioPlay::class.java)

        addMenuByFragment("Test Record", TestRecord::class.java)

        addMenuByFragment("Test Gif", TestGif::class.java)

        addMenuByFragment("Test ViewPager2", TestViewPager2::class.java)
    }


    private fun setupWindowAnimations() {

        window.enterTransition = Fade()
        window.exitTransition = Fade()
    }
}

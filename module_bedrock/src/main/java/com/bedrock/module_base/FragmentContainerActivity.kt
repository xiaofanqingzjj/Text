package com.bedrock.module_base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * 一个Fragment的Activity容器
 * Created by fortunexiao on 2018/9/10.
 */
class FragmentContainerActivity : AppCompatActivity() {

    companion object {

        const val TITLE = "title"
        const val FRAGMENT_CLASS = "fragmentClass"

        /**
         * 使用泛型参数形式传递Class，感觉B格更高
         */
        inline fun <reified T : Fragment> show(context: Context, title: String?, initIntent: (Intent) -> Unit = {}) {
            context.startActivity(Intent(context, FragmentContainerActivity::class.java).apply {
                putExtra(TITLE, title)
                putExtra(FRAGMENT_CLASS, T::class.java)
                initIntent(this)
            })
        }

//        @Deprecated("使用泛型参数更好点")
        @JvmOverloads
        fun show(context: Context, title: String?, fragmentClass: Class<out Fragment>, initIntent: (Intent) -> Unit = { it }) {
            context.startActivity(Intent(context, FragmentContainerActivity::class.java).apply {
                putExtra(TITLE, title)
                putExtra(FRAGMENT_CLASS, fragmentClass)
                initIntent(this)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fragment_container)

        intent.apply {
            val title = getStringExtra(TITLE)
            val fragmentClazz = getSerializableExtra(FRAGMENT_CLASS) as Class<out Fragment>

            supportActionBar?.title = title

            supportFragmentManager.beginTransaction().add(R.id.root,
                    Fragment.instantiate(this@FragmentContainerActivity,
                            fragmentClazz.canonicalName)).commit()
        }

    }
}

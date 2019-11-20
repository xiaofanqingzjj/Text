package com.bedrock.module_base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedrock.module_base.adapter.QuickAdapter
import com.bedrock.module_base.adapter.quickAdapter
import kotlinx.android.synthetic.main.activity_menu.*


/**
 * A simple menu Activity
 *
 * @author fortune
 */
open class MenuActivity: AppCompatActivity() {

    private var menus = mutableListOf<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        recycler_view.quickAdapter(
                data = menus,
                itemLayoutId = android.R.layout.simple_expandable_list_item_1,
                bindData = {data, itemView ->
                    itemView?.run {
                        (data as? Menu)?.run {
                            val textView = findViewById<TextView>(android.R.id.text1)
                            textView.text = name
                            setOnClickListener {
                                click?.invoke()
                            }
                        }
                    }
                }
        )
    }

    /**
     * add menu
     *
     * @param name menu title
     * @param click menu click listener
     */
    open fun addMenu(name: String?, click:(()->Unit)? = null) {
        menus.add(Menu(name, click))
        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Add menu
     *
     * @param name menu title
     * @param targetClazz menu jump activity class
     */
    fun addMenu(name: String?, targetClazz: Class<out Activity>) {
        menus.add(Menu(name) {
          startActivity(Intent(this@MenuActivity, targetClazz))
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Add menu by fragment
     *
     * @param name menu title
     * @param targetClazz menu jump fragment class
     */
    fun addMenuByFragment(name: String?, targetClazz: Class<out Fragment>) {
        menus.add(Menu(name) {
            FragmentContainerActivity.show(this, name, targetClazz)
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Menu
     */
    data class Menu(var name: String? = null, var click: (()->Unit)? = null)

}
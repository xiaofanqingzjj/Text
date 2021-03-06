package com.bedrock.module_base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bedrock.module_base.recycleradapter.quickAdapter
import kotlinx.android.synthetic.main.activity_menu.*


/**
 * A simple menu Activity
 *
 * @author fortune
 */
open class MenuFragment: SimpleFragment() {

    private var menus = mutableListOf<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recycler_view.quickAdapter(
                data = menus,
                itemLayoutId = android.R.layout.simple_expandable_list_item_1,
                bindData = {_, data, itemView ->
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
          startActivity(Intent(context, targetClazz))
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
            FragmentContainerActivity.show(context!!, name, targetClazz)
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Menu
     */
    data class Menu(var name: String? = null, var click: (()->Unit)? = null)

}
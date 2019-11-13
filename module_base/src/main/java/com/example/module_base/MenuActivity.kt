package com.example.module_base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_menu.*


/**
 * 一个简单到菜单Activity
 *
 * @param fortune
 */
open class MenuActivity: AppCompatActivity() {


    var menus = mutableListOf<Menu>()
    var mAdapter: MenuAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        mAdapter = MenuAdapter(menus)

        recycler_view.run {
            this.adapter = mAdapter
            layoutManager = object : LinearLayoutManager(this@MenuActivity) {
                override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                    return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }

    }


    open fun addMenu(name: String?, click:(()->Unit)? = null) {
        menus.add(Menu(name, click))
        mAdapter?.notifyDataSetChanged()
    }

    fun addMenu(name: String?, targetClazz: Class<out Activity>) {
        menus.add(Menu(name) {
          startActivity(Intent(this@MenuActivity, targetClazz))
        })

        mAdapter?.notifyDataSetChanged()
    }


    class Menu(var name: String? = null, var click: (()->Unit)? = null)

    class MenuAdapter(var menus: MutableList<Menu>): RecyclerView.Adapter<MenuAdapter.VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val itemView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(itemView)
        }

        override fun getItemCount(): Int {
            return menus.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val menu = menus[position]
            holder?.text?.text = menu.name
            holder?.itemView?.setOnClickListener {
                menu.click?.invoke()
            }
        }


        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var text: TextView = itemView.findViewById(android.R.id.text1)


        }
    }
}
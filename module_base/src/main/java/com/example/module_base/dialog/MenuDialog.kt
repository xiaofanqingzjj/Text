package com.example.module_base.dialog

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.module_base.R


/**
 * 一个简单的从底部弹出的菜单对话框
 * @author fortunexiao
 */
class MenuDialog : BaseStyleDialog() {

    companion object {

        const val TAG = "MenuDialog"

        fun show(manager: FragmentManager, menus: List<Menu>, withCancel: Boolean) {
            MenuDialog().apply {
                setMenus(menus, withCancel)
            }.show(manager, "menuDialog")
        }

        fun show(manager: FragmentManager, vararg menus: Menu, withCancel: Boolean) {
            show(manager, menus.asList(), withCancel)
        }

        fun show(manager: FragmentManager, vararg menus: Menu) {
            show(manager, menus.asList(), true)
        }
    }

    private lateinit var mMenuContainer: ViewGroup
    private lateinit var btnCancel: View

    private var mMenus: List<Menu>? = null
    private var withCancel: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_menu_buttom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMenuContainer = view.findViewById(R.id.menu_container)
        btnCancel = view.findViewById(R.id.btn_cancel)

        btnCancel.setOnClickListener {
            dismiss()
        }

        setupMenuUI()
    }

    private fun setupMenuUI() {
        mMenuContainer.removeAllViews()

        mMenus?.apply {
            forEach { menu ->
                val tvMenu = layoutInflater.inflate(R.layout.dialog_menu_buttom_item_menu, mMenuContainer, false) as TextView
                tvMenu.apply {
                    tvMenu.text = menu.text
                    if (menu.textColor != 0) {
                        tvMenu.setTextColor(menu.textColor)
                    }
                    setOnClickListener {
                        menu.onClick(it)
                        dismiss()
                    }
                }

                mMenuContainer.addView(tvMenu)
            }
        }

        btnCancel.visibility = if (withCancel) View.VISIBLE else View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 设置窗口样式
        dialog?.window?.run {

            // 窗口显示屏幕的底部
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)

            // 动画为从底部弹出
            attributes.windowAnimations = android.R.style.Animation_InputMethod
        }
    }

    fun setMenus(vararg menus: Menu) {
        setMenus(menus.asList(), withCancel = true)
    }

    fun setMenus(vararg menus: Menu, withCancel: Boolean) {
        setMenus(menus.asList(), withCancel)
    }

    fun setMenus(menus: List<Menu>) {
        setMenus(menus, true)
    }

    fun setMenus(menus: List<Menu>, withCancel: Boolean) {
        mMenus = menus
        this.withCancel = withCancel
    }


    data class Menu(
        var text: String? = null,
        var textColor: Int = 0,
        var onClick: (v: View) -> Unit = {},

        // 内部使用，是否是取消按钮
        internal var isCancel: Boolean = false
    )
}
package com.example.test.test_float_comment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bedrock.module_base.util.DensityUtil;


/**
 * Created by Jacksgong on 15/7/6.
 * 
 * https://github.com/Jacksgong/JKeyboardPanelSwitch
 */
public class KeyboardUtil {

    public static void showKeybord(final View view) {
        if (view == null) {
            return;
        }
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (inputManager == null) {
            return;
        }
        inputManager.showSoftInput(view, 0);
    }

    public static void hideKeybord(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.clearFocus();
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private static int LAST_SAVE_KEYBORD_HEIGHT = 0;
    /**
     * 软键盘高度配置
     */
    public static final String KEY_SOFT_KEYBOARD_HEIGHT = "KEY_SOFT_KEYBOARD_HEIGHT";

    public static boolean saveKeybordHeight(Context context, int keybordHeight) {
        if (LAST_SAVE_KEYBORD_HEIGHT == keybordHeight) {
            return false;
        }

        if (keybordHeight < 0) {
            return false;
        }

        LAST_SAVE_KEYBORD_HEIGHT = keybordHeight;


        getShareP(context).edit().putInt(KEY_SOFT_KEYBOARD_HEIGHT, keybordHeight).apply();
        return true;
//        context.getSharedPreferences()
//
//        return ConfigSharedPrefence.getInstance().putIntConfig(KEY_SOFT_KEYBOARD_HEIGHT, keybordHeight);
    }

    private static SharedPreferences getShareP(Context context) {
        return context.getSharedPreferences("KeyboardUtil", 0);
    }

    public static int getKeybordHeight(Context context) {
        if (LAST_SAVE_KEYBORD_HEIGHT == 0) {
        	if(getShareP(context).contains(KEY_SOFT_KEYBOARD_HEIGHT)) // 保存过键盘高度
        		LAST_SAVE_KEYBORD_HEIGHT = getShareP(context).getInt(KEY_SOFT_KEYBOARD_HEIGHT, 0);
        	else   // 没保存过键盘高度，获取默认高度
        		LAST_SAVE_KEYBORD_HEIGHT = DensityUtil.INSTANCE.dip2px(context, 220); //int)context.getResources().getDimension(R.dimen.emoji_view_default_height);
        }

        return LAST_SAVE_KEYBORD_HEIGHT;
    }

    /**
     * 判断键盘是否弹起或者隐藏
     *
     * @param rootView
     *            根布局，可以通过android.R.id.content来获取
     * @return
     */
    public static boolean isKeyBoardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = dm.heightPixels - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    public static int getValidPanelHeight(Context context) {
        final int maxPanelHeight = getMaxPanelHeight(context);
        final int minPanelHeight = getMinPanelHeight(context);

        int validPanelheight = getKeybordHeight(context);

        validPanelheight = Math.max(minPanelHeight, validPanelheight);
        validPanelheight = Math.min(maxPanelHeight, validPanelheight);
        return validPanelheight;


    }


    private static int MAX_PANEL_HEIGHT = 0;
    private static int MIN_PANEL_HEIGHT = 0;

    public static int getMaxPanelHeight(Context context) {
        if (MAX_PANEL_HEIGHT == 0) {
            MAX_PANEL_HEIGHT = DensityUtil.INSTANCE.dip2px(context, 1000); // context.getResources().getDimensionPixelSize(R.dimen.emoji_view_max_height);
        }

        return MAX_PANEL_HEIGHT;
    }

    public static int getMinPanelHeight(Context context) {
        if (MIN_PANEL_HEIGHT == 0) {
            MIN_PANEL_HEIGHT = DensityUtil.INSTANCE.dip2px(context, 100); //context.getResources().getDimensionPixelSize(R.dimen.emoji_view_min_height);
        }

        return MIN_PANEL_HEIGHT;
    }


}

package com.example.test.transition;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class Scene {

    private Context mContext;
    private int mLayoutId = -1;
    private ViewGroup mSceneRoot;
    private View mLayout; // alternative to layoutId
    Runnable mEnterAction, mExitAction;

    public static Scene getSceneForLayout(ViewGroup sceneRoot, int layoutId, Context context) {
//        SparseArray<Scene> scenes = (SparseArray<Scene>) sceneRoot.getTag(
//                com.android.internal.R.id.scene_layoutid_cache);
//        if (scenes == null) {
//            scenes = new SparseArray<Scene>();
//            sceneRoot.setTagInternal(com.android.internal.R.id.scene_layoutid_cache, scenes);
//        }
//        Scene scene = scenes.get(layoutId);
//        if (scene != null) {
//            return scene;
//        } else {
//            scene = new Scene(sceneRoot, layoutId, context);
//            scenes.put(layoutId, scene);
//            return scene;
//        }

        return null;
    }

    public Scene(ViewGroup sceneRoot) {
        mSceneRoot = sceneRoot;
    }

    private Scene(ViewGroup sceneRoot, int layoutId, Context context) {
        mContext = context;
        mSceneRoot = sceneRoot;
        mLayoutId = layoutId;
    }

    public Scene(ViewGroup sceneRoot, View layout) {
        mSceneRoot = sceneRoot;
        mLayout = layout;
    }

    @Deprecated
    public Scene(ViewGroup sceneRoot, ViewGroup layout) {
        mSceneRoot = sceneRoot;
        mLayout = layout;
    }

    public ViewGroup getSceneRoot() {
        return mSceneRoot;
    }

    public void exit() {
        if (getCurrentScene(mSceneRoot) == this) {
            if (mExitAction != null) {
                mExitAction.run();
            }
        }
    }

    public void enter() {

        // Apply layout change, if any
        if (mLayoutId > 0 || mLayout != null) {
            // empty out parent container before adding to it
            getSceneRoot().removeAllViews();

            if (mLayoutId > 0) {
                LayoutInflater.from(mContext).inflate(mLayoutId, mSceneRoot);
            } else {
                mSceneRoot.addView(mLayout);
            }
        }

        // Notify next scene that it is entering. Subclasses may override to configure scene.
        if (mEnterAction != null) {
            mEnterAction.run();
        }

        setCurrentScene(mSceneRoot, this);
    }

    static void setCurrentScene(View view, Scene scene) {
//        view.setTagInternal(com.android.internal.R.id.current_scene, scene);
    }

    static Scene getCurrentScene(View view) {
        return null; //(Scene) view.getTag(com.android.internal.R.id.current_scene);
    }

    public void setEnterAction(Runnable action) {
        mEnterAction = action;
    }

    public void setExitAction(Runnable action) {
        mExitAction = action;
    }


    boolean isCreatedFromLayoutResource() {
        return (mLayoutId > 0);
    }
}
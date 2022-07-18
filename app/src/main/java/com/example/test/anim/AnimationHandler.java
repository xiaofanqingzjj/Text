package com.example.test.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.view.Choreographer;

import java.util.ArrayList;

/**
 * 可以监听屏幕的刷新帧
 */
public class AnimationHandler {
    /**
     * Internal per-thread collections used to avoid set collisions as animations start and end
     * while being processed.
     * @hide
     */
    private final ArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime =
            new ArrayMap<>();
    private final ArrayList<AnimationFrameCallback> mAnimationCallbacks =
            new ArrayList<>();
    private final ArrayList<AnimationFrameCallback> mCommitCallbacks =
            new ArrayList<>();
    private AnimationFrameCallbackProvider mProvider;

    private final Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            doAnimationFrame(getProvider().getFrameTime());
            if (mAnimationCallbacks.size() > 0) {
                getProvider().postFrameCallback(this);
            }
        }
    };

    public final static ThreadLocal<AnimationHandler> sAnimatorHandler = new ThreadLocal<>();
    private boolean mListDirty = false;

    public static AnimationHandler getInstance() {
        if (sAnimatorHandler.get() == null) {
            sAnimatorHandler.set(new AnimationHandler());
        }
        return sAnimatorHandler.get();
    }

    /**
     * By default, the Choreographer is used to provide timing for frame callbacks. A custom
     * provider can be used here to provide different timing pulse.
     */
    public void setProvider(AnimationFrameCallbackProvider provider) {
        if (provider == null) {
            mProvider = new MyFrameCallbackProvider();
        } else {
            mProvider = provider;
        }
    }

    private AnimationFrameCallbackProvider getProvider() {
        if (mProvider == null) {
            mProvider = new MyFrameCallbackProvider();
        }
        return mProvider;
    }

    /**
     * Register to get a callback on the next frame after the delay.
     */
    public void addAnimationFrameCallback(final AnimationFrameCallback callback, long delay) {
        if (mAnimationCallbacks.size() == 0) {
            getProvider().postFrameCallback(mFrameCallback);
        }
        if (!mAnimationCallbacks.contains(callback)) {
            mAnimationCallbacks.add(callback);
        }

        if (delay > 0) {
            mDelayedCallbackStartTime.put(callback, (SystemClock.uptimeMillis() + delay));
        }
    }

    /**
     * Register to get a one shot callback for frame commit timing. Frame commit timing is the
     * time *after* traversals are done, as opposed to the animation frame timing, which is
     * before any traversals. This timing can be used to adjust the start time of an animation
     * when expensive traversals create big delta between the animation frame timing and the time
     * that animation is first shown on screen.
     *
     * Note this should only be called when the animation has already registered to receive
     * animation frame callbacks. This callback will be guaranteed to happen *after* the next
     * animation frame callback.
     */
    public void addOneShotCommitCallback(final AnimationFrameCallback callback) {
        if (!mCommitCallbacks.contains(callback)) {
            mCommitCallbacks.add(callback);
        }
    }

    /**
     * Removes the given callback from the list, so it will no longer be called for frame related
     * timing.
     */
    public void removeCallback(AnimationFrameCallback callback) {
        mCommitCallbacks.remove(callback);
        mDelayedCallbackStartTime.remove(callback);
        int id = mAnimationCallbacks.indexOf(callback);
        if (id >= 0) {
            mAnimationCallbacks.set(id, null);
            mListDirty = true;
        }
    }

    private void doAnimationFrame(long frameTime) {
        long currentTime = SystemClock.uptimeMillis();
        final int size = mAnimationCallbacks.size();
        for (int i = 0; i < size; i++) {
            final AnimationFrameCallback callback = mAnimationCallbacks.get(i);
            if (callback == null) {
                continue;
            }
            if (isCallbackDue(callback, currentTime)) {
                callback.doAnimationFrame(frameTime);
                if (mCommitCallbacks.contains(callback)) {
                    getProvider().postCommitCallback(new Runnable() {
                        @Override
                        public void run() {
                            commitAnimationFrame(callback, getProvider().getFrameTime());
                        }
                    });
                }
            }
        }
        cleanUpList();
    }

    private void commitAnimationFrame(AnimationFrameCallback callback, long frameTime) {
        if (!mDelayedCallbackStartTime.containsKey(callback) &&
                mCommitCallbacks.contains(callback)) {
            callback.commitAnimationFrame(frameTime);
            mCommitCallbacks.remove(callback);
        }
    }

    /**
     * Remove the callbacks from mDelayedCallbackStartTime once they have passed the initial delay
     * so that they can start getting frame callbacks.
     *
     * @return true if they have passed the initial delay or have no delay, false otherwise.
     */
    private boolean isCallbackDue(AnimationFrameCallback callback, long currentTime) {
        Long startTime = mDelayedCallbackStartTime.get(callback);
        if (startTime == null) {
            return true;
        }
        if (startTime < currentTime) {
            mDelayedCallbackStartTime.remove(callback);
            return true;
        }
        return false;
    }

    /**
     * Return the number of callbacks that have registered for frame callbacks.
     */
    public static int getAnimationCount() {
        AnimationHandler handler = sAnimatorHandler.get();
        if (handler == null) {
            return 0;
        }
        return handler.getCallbackSize();
    }

    public static void setFrameDelay(long delay) {
        getInstance().getProvider().setFrameDelay(delay);
    }

    public static long getFrameDelay() {
        return getInstance().getProvider().getFrameDelay();
    }

    void autoCancelBasedOn(ObjectAnimator objectAnimator) {
        for (int i = mAnimationCallbacks.size() - 1; i >= 0; i--) {
            AnimationFrameCallback cb = mAnimationCallbacks.get(i);
            if (cb == null) {
                continue;
            }
//            if (objectAnimator.shouldAutoCancel(cb)) {
//                ((Animator) mAnimationCallbacks.get(i)).cancel();
//            }
        }
    }

    private void cleanUpList() {
        if (mListDirty) {
            for (int i = mAnimationCallbacks.size() - 1; i >= 0; i--) {
                if (mAnimationCallbacks.get(i) == null) {
                    mAnimationCallbacks.remove(i);
                }
            }
            mListDirty = false;
        }
    }

    private int getCallbackSize() {
        int count = 0;
        int size = mAnimationCallbacks.size();
        for (int i = size - 1; i >= 0; i--) {
            if (mAnimationCallbacks.get(i) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Default provider of timing pulse that uses Choreographer for frame callbacks.
     */
    private class MyFrameCallbackProvider implements AnimationFrameCallbackProvider {

        final Choreographer mChoreographer = Choreographer.getInstance();

        @Override
        public void postFrameCallback(Choreographer.FrameCallback callback) {
            mChoreographer.postFrameCallback(callback);
        }

        @Override
        public void postCommitCallback(Runnable runnable) {
//            mChoreographer.postCallback(Choreographer.CALLBACK_COMMIT, runnable, null);
        }

        @Override
        public long getFrameTime() {
            return 0;
//            mChoreographer
//            return mChoreographer.getFrameTime();
        }

        @Override
        public long getFrameDelay() {
            return 0;
//            return Choreographer.getFrameDelay();
        }

        @Override
        public void setFrameDelay(long delay) {
//            Choreographer.setFrameDelay(delay);
        }
    }

    /**
     * Callbacks that receives notifications for animation timing and frame commit timing.
     */
    public interface AnimationFrameCallback {
        /**
         * Run animation based on the frame time.
         * @param frameTime The frame start time, in the {@link SystemClock#uptimeMillis()} time
         *                  base.
         * @return if the animation has finished.
         */
        boolean doAnimationFrame(long frameTime);

        /**
         * This notifies the callback of frame commit time. Frame commit time is the time after
         * traversals happen, as opposed to the normal animation frame time that is before
         * traversals. This is used to compensate expensive traversals that happen as the
         * animation starts. When traversals take a long time to complete, the rendering of the
         * initial frame will be delayed (by a long time). But since the startTime of the
         * animation is set before the traversal, by the time of next frame, a lot of time would
         * have passed since startTime was set, the animation will consequently skip a few frames
         * to respect the new frameTime. By having the commit time, we can adjust the start time to
         * when the first frame was drawn (after any expensive traversals) so that no frames
         * will be skipped.
         *
         * @param frameTime The frame time after traversals happen, if any, in the
         *                  {@link SystemClock#uptimeMillis()} time base.
         */
        void commitAnimationFrame(long frameTime);
    }

    /**
     * The intention for having this interface is to increase the testability of ValueAnimator.
     * Specifically, we can have a custom implementation of the interface below and provide
     * timing pulse without using Choreographer. That way we could use any arbitrary interval for
     * our timing pulse in the tests.
     *
     * @hide
     */
    public interface AnimationFrameCallbackProvider {
        void postFrameCallback(Choreographer.FrameCallback callback);
        void postCommitCallback(Runnable runnable);
        long getFrameTime();
        long getFrameDelay();
        void setFrameDelay(long delay);
    }
}